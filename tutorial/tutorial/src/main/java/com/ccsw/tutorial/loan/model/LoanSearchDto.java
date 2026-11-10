package com.ccsw.tutorial.loan.model;

import java.time.LocalDate;

import com.ccsw.tutorial.common.pagination.PageableRequest;

public class LoanSearchDto {

	private PageableRequest pageable;

	private Long idGame;

	private Long idCustomer;

	private LocalDate date;

	public PageableRequest getPageable() {
		return pageable;
	}

	public void setPageable(PageableRequest pageable) {
		this.pageable = pageable;
	}

	public Long getIdGame() {
		return idGame;
	}

	public void setIdGame(Long idGame) {
		this.idGame = idGame;
	}

	public Long getIdCustomer() {
		return idCustomer;
	}

	public void setIdCustomer(Long idCustomer) {
		this.idCustomer = idCustomer;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}
}
