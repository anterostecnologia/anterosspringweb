package br.com.anteros.springWeb.converter;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

import br.com.anteros.persistence.serialization.jackson.AnterosObjectMapper;
import br.com.anteros.persistence.serialization.jackson.AnterosPersistenceJacksonModule.Feature;

@Component
public class AnterosForceLazyHttpMessageConverter extends MappingJackson2HttpMessageConverter {

	public AnterosForceLazyHttpMessageConverter() {
		AnterosObjectMapper mapper = new AnterosObjectMapper();
		mapper.enable(Feature.FORCE_LAZY_LOADING);
		this.setObjectMapper(mapper);
	}

}
