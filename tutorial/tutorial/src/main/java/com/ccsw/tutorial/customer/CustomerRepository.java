package com.ccsw.tutorial.customer;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.ccsw.tutorial.customer.model.Customer;

public interface CustomerRepository extends CrudRepository<Customer, Long> {

	Optional<Customer> findByNameIgnoreCase(String name);
}
