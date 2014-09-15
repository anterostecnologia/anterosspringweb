package br.com.anteros.springWeb.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

import br.com.anteros.persistence.serialization.jackson.AnterosObjectMapper;
import br.com.anteros.persistence.session.SQLSessionFactory;


@Component
public class AnterosHttpMessageConverter extends MappingJackson2HttpMessageConverter {

	private SQLSessionFactory sessionFactory;

	public AnterosHttpMessageConverter() {
	}

	public AnterosHttpMessageConverter(SQLSessionFactory sessionFactory) {
		this.setSessionFactory(sessionFactory);
	}

	@Autowired
	public void setSessionFactory(SQLSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		this.setObjectMapper(new AnterosObjectMapper(sessionFactory));
	}

	public SQLSessionFactory getSessionFactory() {
		return sessionFactory;
	}
}
