package br.com.anteros.spring.web.converter;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.anteros.persistence.session.query.filter.Operator;



public abstract class GroupExpressionMixin {

	@JsonIgnore
	public abstract Operator getOperator();

}
