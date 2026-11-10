package com.ccsw.tutorial.customer;

import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ccsw.tutorial.customer.model.CustomerDto;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Customer", description = "API of Customer")
@RequestMapping(value = "/customer")
@RestController
@CrossOrigin(origins = "*")
public class CustomerController {

	@Autowired
	CustomerService customerService;

	@Autowired
	ModelMapper mapper;

	@Operation(summary = "Find", description = "Method that return a list of Customers")
	@RequestMapping(path = "", method = RequestMethod.GET)
	public List<CustomerDto> findAll() {
		return this.customerService.findAll().stream().map(e -> mapper.map(e, CustomerDto.class))
				.collect(Collectors.toList());
	}

	@Operation(summary = "Save or Update", description = "Method that saves or updates a Customer")
	@RequestMapping(path = { "", "/{id}" }, method = RequestMethod.PUT)
	public void save(@PathVariable(name = "id", required = false) Long id, @RequestBody CustomerDto dto) {
		this.customerService.save(id, dto);
	}

	@Operation(summary = "Delete", description = "Method that deletes a Customer")
	@RequestMapping(path = "/{id}", method = RequestMethod.DELETE)
	public void delete(@PathVariable("id") Long id) {
		this.customerService.delete(id);
	}
}
