package com.ccsw.tutorial.loan;

import org.springframework.data.jpa.domain.Specification;

import com.ccsw.tutorial.common.criteria.SearchCriteria;
import com.ccsw.tutorial.loan.model.Loan;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public class LoanSpecification implements Specification<Loan> {

	private static final long serialVersionUID = 1L;

	private final SearchCriteria criteria;

	public LoanSpecification(SearchCriteria criteria) {
		this.criteria = criteria;
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public Predicate toPredicate(Root<Loan> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
		if (criteria.getValue() == null) {
			return null;
		}

		Path path = getPath(root);

		switch (criteria.getOperation()) {
		case ":":
			if (path.getJavaType() == String.class) {
				return builder.like(path, "%" + criteria.getValue() + "%");
			}
			return builder.equal(path, criteria.getValue());
		case "<=":
			return builder.lessThanOrEqualTo(path, (Comparable) criteria.getValue());
		case ">=":
			return builder.greaterThanOrEqualTo(path, (Comparable) criteria.getValue());
		default:
			return null;
		}
	}

	@SuppressWarnings("rawtypes")
	private Path getPath(Root<Loan> root) {
		String[] split = criteria.getKey().split("[.]", 0);
		Path expression = root.get(split[0]);
		for (int i = 1; i < split.length; i++) {
			expression = expression.get(split[i]);
		}
		return expression;
	}
}
