package com.ccsw.tutorial.loan;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ccsw.tutorial.common.criteria.SearchCriteria;
import com.ccsw.tutorial.common.exception.BusinessException;
import com.ccsw.tutorial.customer.CustomerService;
import com.ccsw.tutorial.customer.model.Customer;
import com.ccsw.tutorial.game.GameService;
import com.ccsw.tutorial.game.model.Game;
import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.LoanSearchDto;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class LoanServiceImpl implements LoanService {

	static final int MAX_LOAN_DAYS = 14;

	static final int MAX_LOANS_PER_CUSTOMER_PER_DAY = 2;

	@Autowired
	LoanRepository loanRepository;

	@Autowired
	GameService gameService;

	@Autowired
	CustomerService customerService;

	@Override
	public Loan get(Long id) {
		return this.loanRepository.findById(id).orElse(null);
	}

	@Override
	public Page<Loan> findPage(LoanSearchDto dto) {
		LoanSpecification gameSpec = new LoanSpecification(new SearchCriteria("game.id", ":", dto.getIdGame()));
		LoanSpecification customerSpec = new LoanSpecification(
				new SearchCriteria("customer.id", ":", dto.getIdCustomer()));

		LoanSpecification startSpec = new LoanSpecification(new SearchCriteria("startDate", "<=", dto.getDate()));
		LoanSpecification endSpec = new LoanSpecification(new SearchCriteria("endDate", ">=", dto.getDate()));

		Specification<Loan> spec = gameSpec.and(customerSpec).and(startSpec).and(endSpec);

		return this.loanRepository.findAll(spec, dto.getPageable().getPageable());
	}

	@Override
	public void save(Long id, LoanDto dto) {
		if (dto.getGame() == null || dto.getGame().getId() == null) {
			throw new BusinessException("El juego es obligatorio");
		}
		if (dto.getCustomer() == null || dto.getCustomer().getId() == null) {
			throw new BusinessException("El cliente es obligatorio");
		}
		if (dto.getStartDate() == null || dto.getEndDate() == null) {
			throw new BusinessException("Las fechas de inicio y fin son obligatorias");
		}

		Game game = this.gameService.get(dto.getGame().getId());
		if (game == null) {
			throw new BusinessException("El juego indicado no existe");
		}
		Customer customer = this.customerService.get(dto.getCustomer().getId());
		if (customer == null) {
			throw new BusinessException("El cliente indicado no existe");
		}

		LocalDate start = dto.getStartDate();
		LocalDate end = dto.getEndDate();

		validateDates(start, end);
		validateGameAvailable(id, game, start, end);
		validateCustomerLimit(id, customer, start, end);

		Loan loan;
		if (id == null) {
			loan = new Loan();
		} else {
			loan = this.get(id);
			if (loan == null) {
				throw new BusinessException(HttpStatus.NOT_FOUND, "El préstamo no existe");
			}
		}

		loan.setGame(game);
		loan.setCustomer(customer);
		loan.setStartDate(start);
		loan.setEndDate(end);

		this.loanRepository.save(loan);
	}

	@Override
	public void delete(Long id) {
		if (this.get(id) == null) {
			throw new BusinessException(HttpStatus.NOT_FOUND, "El préstamo no existe");
		}
		this.loanRepository.deleteById(id);
	}

	private void validateDates(LocalDate start, LocalDate end) {
		if (end.isBefore(start)) {
			throw new BusinessException("La fecha de fin no puede ser anterior a la fecha de inicio");
		}
		if (ChronoUnit.DAYS.between(start, end) > MAX_LOAN_DAYS) {
			throw new BusinessException("El periodo máximo de préstamo es de " + MAX_LOAN_DAYS + " días");
		}
	}

	private void validateGameAvailable(Long id, Game game, LocalDate start, LocalDate end) {
		List<Loan> overlapping = this.loanRepository
				.findByGameIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(game.getId(), end, start).stream()
				.filter(l -> !l.getId().equals(id)).collect(Collectors.toList());

		if (!overlapping.isEmpty()) {
			throw new BusinessException("El juego ya está prestado en alguno de los días del periodo indicado");
		}
	}

	private void validateCustomerLimit(Long id, Customer customer, LocalDate start, LocalDate end) {
		List<Loan> overlapping = this.loanRepository
				.findByCustomerIdAndStartDateLessThanEqualAndEndDateGreaterThanEqual(customer.getId(), end, start)
				.stream().filter(l -> !l.getId().equals(id)).collect(Collectors.toList());

		for (LocalDate day = start; !day.isAfter(end); day = day.plusDays(1)) {
			final LocalDate current = day;
			long loansThatDay = overlapping.stream()
					.filter(l -> !l.getStartDate().isAfter(current) && !l.getEndDate().isBefore(current)).count();

			if (loansThatDay >= MAX_LOANS_PER_CUSTOMER_PER_DAY) {
				throw new BusinessException("El cliente ya tiene " + MAX_LOANS_PER_CUSTOMER_PER_DAY
						+ " juegos prestados el día " + current);
			}
		}
	}
}
