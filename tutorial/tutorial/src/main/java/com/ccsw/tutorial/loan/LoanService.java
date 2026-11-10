package com.ccsw.tutorial.loan;

import org.springframework.data.domain.Page;

import com.ccsw.tutorial.loan.model.Loan;
import com.ccsw.tutorial.loan.model.LoanDto;
import com.ccsw.tutorial.loan.model.LoanSearchDto;

public interface LoanService {

	Loan get(Long id);

	Page<Loan> findPage(LoanSearchDto dto);

	void save(Long id, LoanDto dto);

	void delete(Long id);
}
