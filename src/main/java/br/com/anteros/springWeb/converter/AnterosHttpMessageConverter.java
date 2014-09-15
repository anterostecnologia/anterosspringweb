package br.com.anteros.springWeb.converter;

import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

import br.com.anteros.persistence.serialization.jackson.AnterosObjectMapper;

@Component
public class AnterosHttpMessageConverter extends MappingJackson2HttpMessageConverter {

	public AnterosHttpMessageConverter() {
		this.setObjectMapper(new AnterosObjectMapper());
	}

}
