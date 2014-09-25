package br.com.anteros.springWeb.converter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

import br.com.anteros.persistence.serialization.jackson.AnterosObjectMapper;
import br.com.anteros.persistence.serialization.jackson.AnterosPersistenceJacksonModule.Feature;
import br.com.anteros.persistence.session.SQLSessionFactory;

@Component
public class AnterosForceLazyHttpMessageConverter extends MappingJackson2HttpMessageConverter {

	private SQLSessionFactory sessionFactory;

	public AnterosForceLazyHttpMessageConverter(SQLSessionFactory sessionFactory) {
		this.setSessionFactory(sessionFactory);
	}

	@Autowired
	public void setSessionFactory(SQLSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		AnterosObjectMapper mapper = new AnterosObjectMapper(sessionFactory);
		mapper.enable(Feature.FORCE_LAZY_LOADING);
		this.setObjectMapper(mapper);
	}

	public SQLSessionFactory getSessionFactory() {
		return sessionFactory;
	}


}
