package com.ccsw.tutorial.loan;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import com.ccsw.tutorial.common.pagination.PageableRequest;
import com.ccsw.tutorial.config.ResponsePage;
import com.ccsw.tutorial.customer.model.CustomerDto;
import com.ccsw.tutorial.game.model.GameDto;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.LoanSearchDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestRestTemplate
public class LoanIT {

	public static final String LOCALHOST = "http://localhost:";
	public static final String SERVICE_PATH = "/loan";
	private static final int TOTAL_LOANS = 7;

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	ParameterizedTypeReference<ResponsePage<LoanDto>> responseTypePage = new ParameterizedTypeReference<ResponsePage<LoanDto>>() {
	};

	private ResponsePage<LoanDto> search(int page, int size, Long idGame, Long idCustomer, LocalDate date) {
		LoanSearchDto dto = new LoanSearchDto();
		dto.setPageable(new PageableRequest(page, size));
		dto.setIdGame(idGame);
		dto.setIdCustomer(idCustomer);
		dto.setDate(date);
		return restTemplate
				.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.POST, new HttpEntity<>(dto), responseTypePage)
				.getBody();
	}

	private LoanDto loan(long gameId, long customerId, String start, String end) {
		GameDto game = new GameDto();
		game.setId(gameId);
		CustomerDto customer = new CustomerDto();
		customer.setId(customerId);
		LoanDto dto = new LoanDto();
		dto.setGame(game);
		dto.setCustomer(customer);
		dto.setStartDate(LocalDate.parse(start));
		dto.setEndDate(LocalDate.parse(end));
		return dto;
	}

	private ResponseEntity<Void> put(Long id, LoanDto dto) {
		String url = LOCALHOST + port + SERVICE_PATH + (id == null ? "" : "/" + id);
		return restTemplate.exchange(url, HttpMethod.PUT, new HttpEntity<>(dto), Void.class);
	}

	@Test
	public void findWithoutFiltersShouldReturnAll() {
		assertEquals(TOTAL_LOANS, search(0, 20, null, null, null).getTotalElements());
	}

	@Test
	public void findIsPaginated() {
		ResponsePage<LoanDto> page = search(1, 5, null, null, null);
		assertEquals(TOTAL_LOANS, page.getTotalElements());
		assertEquals(2, page.getContent().size());
	}

	@Test
	public void findByGameShouldFilter() {
		assertEquals(2, search(0, 20, 1L, null, null).getTotalElements());
	}

	@Test
	public void findByCustomerShouldFilter() {
		assertEquals(3, search(0, 20, null, 1L, null).getTotalElements());
	}

	@Test
	public void findByIntermediateDateShouldReturnLoansThatCoverIt() {
		// 07/10 está dentro de los préstamos 1, 2 y 4
		assertEquals(3, search(0, 20, null, null, LocalDate.parse("2026-10-07")).getTotalElements());
	}

	@Test
	public void findByBoundaryDatesShouldIncludeStartAndEnd() {
		assertEquals(2, search(0, 20, null, null, LocalDate.parse("2026-10-01")).getTotalElements()); // 1 y 4
		assertEquals(2, search(0, 20, null, null, LocalDate.parse("2026-10-12")).getTotalElements()); // 2 y 4
	}

	@Test
	public void findByDateWithoutLoansShouldReturnEmpty() {
		assertEquals(0, search(0, 20, null, null, LocalDate.parse("2027-01-01")).getTotalElements());
	}

	@Test
	public void findCombiningFiltersShouldApplyAll() {
		assertEquals(1, search(0, 20, 1L, 1L, LocalDate.parse("2026-10-07")).getTotalElements());
		assertEquals(0, search(0, 20, 1L, 2L, LocalDate.parse("2026-10-07")).getTotalElements());
	}

	@Test
	public void saveValidLoanShouldCreateIt() {
		assertEquals(HttpStatus.OK, put(null, loan(6, 4, "2026-10-01", "2026-10-05")).getStatusCode());
		assertEquals(TOTAL_LOANS + 1, search(0, 20, null, null, null).getTotalElements());
	}

	@Test
	public void saveWithEndBeforeStartShouldFail() {
		assertEquals(HttpStatus.BAD_REQUEST, put(null, loan(6, 4, "2026-10-05", "2026-10-01")).getStatusCode());
	}

	@Test
	public void saveWithMoreThan14DaysShouldFail() {
		assertEquals(HttpStatus.BAD_REQUEST, put(null, loan(6, 4, "2026-10-01", "2026-10-20")).getStatusCode());
		assertEquals(HttpStatus.OK, put(null, loan(6, 4, "2026-10-01", "2026-10-15")).getStatusCode());
	}

	@Test
	public void saveGameAlreadyLoanedAnyDayOfRangeShouldFail() {
		assertEquals(HttpStatus.BAD_REQUEST, put(null, loan(1, 4, "2026-10-10", "2026-10-12")).getStatusCode());
		assertEquals(HttpStatus.BAD_REQUEST, put(null, loan(1, 4, "2026-09-28", "2026-10-01")).getStatusCode());
		assertEquals(HttpStatus.OK, put(null, loan(1, 4, "2026-10-11", "2026-10-12")).getStatusCode());
	}

	@Test
	public void saveCustomerWithTwoLoansThatDayShouldFail() {
		assertEquals(HttpStatus.BAD_REQUEST, put(null, loan(6, 1, "2026-10-08", "2026-10-09")).getStatusCode());
		assertEquals(HttpStatus.OK, put(null, loan(6, 1, "2026-10-11", "2026-10-13")).getStatusCode());
	}

	@Test
	public void modifyLoanShouldNotConflictWithItself() {
		assertEquals(HttpStatus.OK, put(1L, loan(1, 1, "2026-10-01", "2026-10-12")).getStatusCode());
	}

	@Test
	public void modifyWithNotExistIdShouldFail() {
		assertEquals(HttpStatus.NOT_FOUND, put(99L, loan(6, 4, "2026-10-01", "2026-10-05")).getStatusCode());
	}

	@Test
	public void deleteShouldRemoveLoan() {
		restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/1", HttpMethod.DELETE, null, Void.class);
		assertEquals(TOTAL_LOANS - 1, search(0, 20, null, null, null).getTotalElements());
	}

	@Test
	public void deleteWithNotExistIdShouldFail() {
		ResponseEntity<Void> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/99",
				HttpMethod.DELETE, null, Void.class);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
}
