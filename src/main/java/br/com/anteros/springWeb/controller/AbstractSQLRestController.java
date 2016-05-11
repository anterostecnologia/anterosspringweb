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
package br.com.anteros.springWeb.controller;

import java.io.Serializable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import br.com.anteros.core.log.Logger;
import br.com.anteros.core.log.LoggerProvider;
import br.com.anteros.persistence.session.repository.Page;
import br.com.anteros.persistence.session.repository.PageRequest;
import br.com.anteros.persistence.session.service.SQLService;

/**
 * Classe base para uso de serviços REST de persistência usando Anteros.
 *  
 * @author Edson Martins edsonmartins2005@gmail.com
 *
 * @param <T> Tipo
 * @param <ID> ID
 */
@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true)
@SuppressWarnings("unchecked")
public abstract class AbstractSQLRestController<T, ID extends Serializable> {

	protected static Logger log = LoggerProvider.getInstance().getLogger(AbstractSQLRestController.class.getName());

	/**
	 * Insere ou atualiza objeto no banco de dados  via método POST ou PUT.;
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/", method = { RequestMethod.POST, RequestMethod.PUT })
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = false)
	public T save(HttpServletRequest request, HttpServletResponse response, @RequestBody T object) throws Exception {
		return getService().save(object);
	}

	/**
	 * Remove objeto por ID no banco de dados via método DELETE.
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = false)
	public T removeById(HttpServletRequest request, HttpServletResponse response, @PathVariable(value = "id") String id)
			throws Exception {
		ID castID = (ID) id;
		T result = getService().findOne(castID);
		getService().remove(result);
		return result;
	}
	
	/**
	 *  Remove objeto no banco de dados via método DELETE.
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = false)
	public void remove(HttpServletRequest request, HttpServletResponse response, @RequestBody T object)
			throws Exception {
		getService().remove(object);
	}

	/**
	 * Retorna objeto do banco de dados via método GET.
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true)
	public T findOne(HttpServletRequest request, HttpServletResponse response, @PathVariable(value = "id") String id)
			throws Exception {
		ID castID = (ID) id;
		return getService().findOne(castID);
	}

	/**
	 * Retorna todos os objetos do banco de dados via método GET.
	 * @return
	 */
	@RequestMapping(value = {"/findAll","/"}, method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true)
	public List<T> findAll() {
		return getService().findAll();
	}

	/**
	 * Retorna todos os objetos do banco de dados com paginação via método GET.
	 * @param page
	 * @param size
	 * @return
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/findAllWithPage", params = { "page", "size" })
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true)
	public Page<T> findAll(@RequestParam("page") int page, @RequestParam("size") int size) {
		PageRequest pageRequest = new PageRequest(page, size);
		return getService().findAll(pageRequest);
	}

	@RequestMapping(value = "/find/{sql}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true)
	public List<T> find(HttpServletRequest request, HttpServletResponse response,
			@PathVariable(value = "sql") String sql) throws Exception {
		return getService().find(sql);
	}

	@RequestMapping(value = "/findWithPage/{sql}", params = { "page", "size" }, method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true)
	public Page<T> find(@PathVariable("sql") String sql, @RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "size", required = true) int size) {
		PageRequest pageRequest = new PageRequest(page, size);
		return getService().find(sql, pageRequest);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/findWithParameters/{sql}", params = { "parameters" })
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true)
	public List<T> find(@PathVariable String sql,
			@RequestParam(value = "parameters", required = true) Object... parameters) {
		return getService().find(sql, parameters);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/findWithParametersAndPage/{sql}", params = { "page", "size",
			"parameters" })
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true)
	public Page<T> find(@PathVariable("sql") String sql, @RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "size", required = true) int size,
			@RequestParam(value = "parameters", required = true) Object... parameters) {
		PageRequest pageRequest = new PageRequest(page, size);
		return getService().find(sql, parameters, pageRequest);
	}

	@RequestMapping(value = "/findByNamedQuery/{queryName}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true)
	public List<T> findByNamedQuery(@PathVariable("queryName") String queryName) {
		return getService().findByNamedQuery(queryName);
	}

	@RequestMapping(value = "/findByNamedQueryWithPage/{queryName}", params = { "page", "size" }, method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true)
	public Page<T> findByNamedQuery(@PathVariable("queryName") String queryName,
			@RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "size", required = true) int size) {
		PageRequest pageRequest = new PageRequest(page, size);
		return getService().findByNamedQuery(queryName, pageRequest);
	}

	@RequestMapping(value = "/findByNamedQueryWithParameters/{queryName}", params = { "parameters" }, method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true)
	public List<T> findByNamedQuery(@PathVariable("queryName") String queryName,
			@RequestParam(value = "parameters", required = true) Object... parameters) {
		return getService().findByNamedQuery(queryName, parameters);
	}

	@RequestMapping(value = "/findByNamedQueryWithParamsAndPage/{queryName}", params = { "page", "size", "parameters" }, method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true)
	public Page<T> findByNamedQuery(@PathVariable("queryName") String queryName,
			@RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "size", required = true) int size,
			@RequestParam(value = "parameters", required = true) Object... parameters) {
		PageRequest pageRequest = new PageRequest(page, size);
		return getService().findByNamedQuery(queryName, parameters, pageRequest);
	}

	@RequestMapping(value = "/count", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true)
	public long count() {
		return getService().count();
	}

	/**
	 * Método abstrato que irá fornecer a classe de serviço para ser usada no
	 * controller.
	 * 
	 * @return
	 */
	public abstract SQLService<T, ID> getService();

}
