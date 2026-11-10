package com.ccsw.tutorial.customer;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

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

import com.ccsw.tutorial.customer.model.CustomerDto;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestRestTemplate
public class CustomerIT {

	public static final String LOCALHOST = "http://localhost:";
	public static final String SERVICE_PATH = "/customer";
	private static final int TOTAL_CUSTOMERS = 4;

	@LocalServerPort
	private int port;

	@Autowired
	private TestRestTemplate restTemplate;

	ParameterizedTypeReference<List<CustomerDto>> responseType = new ParameterizedTypeReference<List<CustomerDto>>() {
	};

	private int count() {
		return restTemplate.exchange(LOCALHOST + port + SERVICE_PATH, HttpMethod.GET, null, responseType).getBody()
				.size();
	}

	private ResponseEntity<Void> put(String path, String name) {
		CustomerDto dto = new CustomerDto();
		dto.setName(name);
		return restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + path, HttpMethod.PUT, new HttpEntity<>(dto),
				Void.class);
	}

	@Test
	public void findAllShouldReturnAllCustomers() {
		assertEquals(TOTAL_CUSTOMERS, count());
	}

	@Test
	public void saveWithoutIdShouldCreateCustomer() {
		assertEquals(HttpStatus.OK, put("", "Cliente nuevo").getStatusCode());
		assertEquals(TOTAL_CUSTOMERS + 1, count());
	}

	@Test
	public void saveWithExistingNameShouldFail() {
		assertEquals(HttpStatus.CONFLICT, put("", "Marta Gil").getStatusCode());
		assertEquals(HttpStatus.CONFLICT, put("", "marta gil").getStatusCode());
		assertEquals(TOTAL_CUSTOMERS, count());
	}

	@Test
	public void modifyKeepingOwnNameShouldWork() {
		assertEquals(HttpStatus.OK, put("/2", "Marta Gil").getStatusCode());
	}

	@Test
	public void modifyWithNameOfAnotherCustomerShouldFail() {
		assertEquals(HttpStatus.CONFLICT, put("/2", "Ana Ruiz").getStatusCode());
	}

	@Test
	public void modifyWithNotExistIdShouldFail() {
		assertEquals(HttpStatus.NOT_FOUND, put("/99", "Otro nombre").getStatusCode());
	}

	@Test
	public void deleteWithoutLoansShouldDeleteCustomer() {
		restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/4", HttpMethod.DELETE, null, Void.class);
		assertEquals(TOTAL_CUSTOMERS - 1, count());
	}

	@Test
	public void deleteWithNotExistIdShouldFail() {
		ResponseEntity<Void> response = restTemplate.exchange(LOCALHOST + port + SERVICE_PATH + "/99",
				HttpMethod.DELETE, null, Void.class);
		assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
	}
}
