package com.ccsw.tutorial.customer;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.ccsw.tutorial.common.exception.BusinessException;
import com.ccsw.tutorial.customer.model.Customer;
import com.ccsw.tutorial.customer.model.CustomerDto;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CustomerServiceImpl implements CustomerService {

	@Autowired
	CustomerRepository customerRepository;

	@Override
	public Customer get(Long id) {
		return this.customerRepository.findById(id).orElse(null);
	}

	@Override
	public List<Customer> findAll() {
		return (List<Customer>) this.customerRepository.findAll();
	}

	@Override
	public void save(Long id, CustomerDto dto) {
		String name = dto.getName() == null ? "" : dto.getName().trim();
		if (name.isEmpty()) {
			throw new BusinessException("El nombre no puede estar vacío");
		}

		Optional<Customer> existing = this.customerRepository.findByNameIgnoreCase(name);
		if (existing.isPresent() && !existing.get().getId().equals(id)) {
			throw new BusinessException(HttpStatus.CONFLICT, "Ya existe un cliente con ese nombre");
		}

		Customer customer;
		if (id == null) {
			customer = new Customer();
		} else {
			customer = this.get(id);
			if (customer == null) {
				throw new BusinessException(HttpStatus.NOT_FOUND, "El cliente no existe");
			}
		}

		customer.setName(name);
		this.customerRepository.save(customer);
	}

	@Override
	public void delete(Long id) {
		if (this.get(id) == null) {
			throw new BusinessException(HttpStatus.NOT_FOUND, "El cliente no existe");
		}
		this.customerRepository.deleteById(id);
	}
}
