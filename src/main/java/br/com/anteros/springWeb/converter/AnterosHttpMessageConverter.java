package br.com.anteros.springWeb.converter;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

import br.com.anteros.persistence.serialization.jackson.AnterosObjectMapper;
import br.com.anteros.persistence.session.SQLSessionFactory;

@Component(value="singleton")
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
	
	@Override
	protected void writeInternal(Object object, HttpOutputMessage outputMessage) throws IOException,
			HttpMessageNotWritableException {
		super.writeInternal(object, outputMessage);
	}
	
	@Override
	public boolean canRead(Class<?> clazz, MediaType mediaType) {
		return super.canRead(clazz, mediaType);
	}
	
	@Override
	public boolean canWrite(Class<?> clazz, MediaType mediaType) {
		return super.canWrite(clazz, mediaType);
	}

}
