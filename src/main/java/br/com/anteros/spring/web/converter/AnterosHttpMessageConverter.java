/*******************************************************************************
 * Copyright 2012 Anteros Tecnologia
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package br.com.anteros.spring.web.converter;

import java.io.IOException;
import java.lang.reflect.Type;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.stereotype.Component;
import org.springframework.util.TypeUtils;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.ser.FilterProvider;

import br.com.anteros.persistence.dsl.osql.group.GroupExpression;
import br.com.anteros.persistence.serialization.jackson.AnterosObjectMapper;
import br.com.anteros.persistence.session.SQLSessionFactory;
import br.com.anteros.persistence.session.query.filter.JacksonBase;


/**
 * 
 * @author Edson Martins edsonmartins2005@gmail.com
 *
 */
@Component(value="singleton")
public class AnterosHttpMessageConverter extends MappingJackson2HttpMessageConverter {

	private SQLSessionFactory sessionFactory;

	public AnterosHttpMessageConverter() {
	}

	public AnterosHttpMessageConverter(SQLSessionFactory sessionFactory) {
		this.setSessionFactory(sessionFactory);
		AnterosObjectMapper mapper = new AnterosObjectMapper(sessionFactory);
		mapper.addMixInAnnotations(JacksonBase.class, JacksonBaseMixin.class);
		mapper.addMixInAnnotations(GroupExpression.class, GroupExpressionMixin.class);		
		this.setObjectMapper(mapper);
	}

	@Autowired
	public void setSessionFactory(SQLSessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
		AnterosObjectMapper mapper = new AnterosObjectMapper(sessionFactory);
		mapper.addMixInAnnotations(JacksonBase.class, JacksonBaseMixin.class);
		mapper.addMixInAnnotations(GroupExpression.class, GroupExpressionMixin.class);	
		this.setObjectMapper(mapper);
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
	
	
	@Override
	protected void writeInternal(Object object, Type type, HttpOutputMessage outputMessage)	
			throws IOException, HttpMessageNotWritableException {
		super.writeInternal(object, type, outputMessage);
	}
//
//		MediaType contentType = outputMessage.getHeaders().getContentType();
//		JsonEncoding encoding = getJsonEncoding(contentType);
//		JsonGenerator generator = this.objectMapper.getFactory().createGenerator(outputMessage.getBody(), encoding);
//		try {
//			writePrefix(generator, object);
//
//			Class<?> serializationView = null;
//			FilterProvider filters = null;
//			Object value = object;
//			JavaType javaType = null;
//			if (object instanceof MappingJacksonValue) {
//				MappingJacksonValue container = (MappingJacksonValue) object;
//				value = container.getValue();
//				serializationView = container.getSerializationView();
//				filters = container.getFilters();
//			}
//			if (type != null && TypeUtils.isAssignable(type, value.getClass())) {
//				javaType = getJavaType(type, null);
//			}
//			ObjectWriter objectWriter;
//			if (serializationView != null) {
//				objectWriter = this.objectMapper.writerWithView(serializationView);
//			}
//			else if (filters != null) {
//				objectWriter = this.objectMapper.writer(filters);
//			}
//			else {
//				objectWriter = this.objectMapper.writer();
//			}
//			if (javaType != null && javaType.isContainerType()) {
//				objectWriter = objectWriter.forType(javaType);
//			}
//			SerializationConfig config = objectWriter.getConfig();
//			if (contentType != null && contentType.isCompatibleWith(MediaType.TEXT_EVENT_STREAM) &&
//					config.isEnabled(SerializationFeature.INDENT_OUTPUT)) {
////				objectWriter = objectWriter.with(this.ssePrettyPrinter);
//			}
//			objectWriter.writeValue(generator, value);
//
//			writeSuffix(generator, object);
//			generator.flush();
//
//		}
//		catch (InvalidDefinitionException ex) {
//			throw new HttpMessageConversionException("Type definition error: " + ex.getType(), ex);
//		}
//		catch (JsonProcessingException ex) {
//			throw new HttpMessageNotWritableException("Could not write JSON: " + ex.getOriginalMessage(), ex);
//		}
//	}
	
	

}
