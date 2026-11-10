package com.ccsw.tutorial.customer;

import java.util.List;

import com.ccsw.tutorial.customer.model.Customer;
import com.ccsw.tutorial.customer.model.CustomerDto;

public interface CustomerService {

	Customer get(Long id);

	List<Customer> findAll();

	void save(Long id, CustomerDto dto);

	void delete(Long id);
}
