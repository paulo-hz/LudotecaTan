package com.ccsw.tutorial.loan.model;

import java.time.LocalDate;

import com.ccsw.tutorial.customer.model.CustomerDto;
import com.ccsw.tutorial.game.model.GameDto;

public class LoanDto {

	private Long id;

	private GameDto game;

	private CustomerDto customer;

	private LocalDate startDate;

	private LocalDate endDate;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public GameDto getGame() {
		return this.game;
	}

	public void setGame(GameDto game) {
		this.game = game;
	}

	public CustomerDto getCustomer() {
		return this.customer;
	}

	public void setCustomer(CustomerDto customer) {
		this.customer = customer;
	}

	public LocalDate getStartDate() {
		return this.startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return this.endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
}
