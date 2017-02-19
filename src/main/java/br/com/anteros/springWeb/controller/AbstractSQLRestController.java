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
import java.util.ArrayList;
import java.util.List;

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
import br.com.anteros.persistence.session.query.filter.Filter;
import br.com.anteros.persistence.session.repository.Page;
import br.com.anteros.persistence.session.repository.PageRequest;
import br.com.anteros.persistence.session.service.SQLService;

/**
 * Classe base para uso de serviços REST de persistência usando Anteros.
 * 
 * @author Edson Martins edsonmartins2005@gmail.com
 *
 * @param <T>
 *            Tipo
 * @param <ID>
 *            ID
 */
@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true)
@SuppressWarnings("unchecked")
public abstract class AbstractSQLRestController<T, ID extends Serializable> {

	protected static Logger log = LoggerProvider.getInstance().getLogger(AbstractSQLRestController.class.getName());


	/**
	 * Insere ou atualiza um objeto.
	 * @param object Objeto a ser salvo
	 * @return Objeto salvo
	 * @throws Exception
	 */
	
	@RequestMapping(value = "/", method = { RequestMethod.POST, RequestMethod.PUT })
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = false)
	public T save(@RequestBody T object) throws Exception {
		return getService().save(object);
	}

	/**
	 * Remove um objeto pelo ID.
	 * 
	 * @param id
	 *            Identificador do objeto
	 * @return Objeto removido.
	 * @throws Exception
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = false)
	public T removeById(@PathVariable(value = "id") String id)
			throws Exception {
		ID castID = (ID) id;
		T result = getService().findOne(castID);
		getService().remove(result);
		return result;
	}

	/**
	 * Remove todos os objetos da classe.
	 * 
	 * @param ids
	 *            Lista dos id's a serem removidos.
	 * @return Verdadeiro se removeu todos.
	 * @throws Exception
	 */
	@RequestMapping(value = "/", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = false)
	public Boolean removeAll(@RequestParam(required = true) List<String> ids) throws Exception {
		List<ID> newIds = new ArrayList<ID>();
		for (String id : ids) {
			ID castID = (ID) id;
			newIds.add(castID);
		}
		return getService().removeAll(newIds);
	}

	/**
	 * Busca um objeto pelo seu ID.
	 * 
	 * @param id
	 *            Identificador do objeto.
	 * @return Objeto encontrado.
	 * @throws Exception
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true)
	public T findOne(@PathVariable(value = "id") String id)
			throws Exception {
		ID castID = (ID) id;
		return getService().findOne(castID);
	}

	/**
	 * Busca os objetos da classe com paginação.
	 * 
	 * @param page
	 *            Número da página
	 * @param size
	 *            Tamanho da página
	 * @return Página
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/findAll", params = { "page", "size" })
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true)
	public Page<T> findAll(@RequestParam("page") int page, @RequestParam("size") int size) {
		PageRequest pageRequest = new PageRequest(page, size);
		return getService().findAll(pageRequest);
	}

	/**
	 * Busca os objetos da classe contido na lista de ID's.
	 * 
	 * @param ids
	 *            Lista de ID's
	 * @return Lista de objetos encontrados.
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/findAll")
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true)
	public List<T> findAll(@RequestParam(required = true) List<String> ids) {
		List<ID> newIds = new ArrayList<ID>();
		for (String id : ids) {
			ID castID = (ID) id;
			newIds.add(castID);
		}
		return getService().findAll(newIds);
	}

	/**
	 * Busca os objetos da classe de acordo com o objeto filtro.
	 * 
	 * @param filter
	 *            Objeto filtro
	 * @param page
	 *            Número da página
	 * @param size
	 *            Tamanho da página
	 * @return Página
	 */
	@RequestMapping(value = "/findWithFilter", params = { "page", "size" }, method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true)
	public Page<T> find(@RequestBody Filter filter, @RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "size", required = true) int size) {
		PageRequest pageRequest = new PageRequest(page, size);
		return getService().find("", pageRequest);
	}

	/**
	 * Queries nomeadas
	 */

	/**
	 * Busca os objetos da classe usando uma consulta nomeada.
	 * 
	 * @param queryName
	 *            Nome da consulta
	 * @param page
	 *            Número da página
	 * @param size
	 *            Tamanho da página
	 * @return Página
	 */
	@RequestMapping(value = "/findByNamedQuery/{queryName}", params = { "page", "size" }, method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true)
	public Page<T> findByNamedQuery(@PathVariable("queryName") String queryName,
			@RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "size", required = true) int size) {
		PageRequest pageRequest = new PageRequest(page, size);
		return getService().findByNamedQuery(queryName, pageRequest);
	}

	/**
	 * Busca os objetos da classe usando uma consulta nomeada e um filtro.
	 * 
	 * @param filter
	 *            Objeto filtro
	 * @param queryName
	 *            Nome da consulta
	 * @param page
	 *            Número da página
	 * @param size
	 *            Tamanho da página
	 * @return Página
	 */
	@RequestMapping(value = "/findByNamedQueryWithFilter/{queryName}", params = { "page",
			"size" }, method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true)
	public Page<T> findByNamedQuery(@RequestBody Filter filter, @PathVariable("queryName") String queryName,
			@RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "size", required = true) int size) {
		PageRequest pageRequest = new PageRequest(page, size);
		return getService().findByNamedQuery(queryName, pageRequest);
	}

	/**
	 * Busca os objetos da classe usando uma consulta nomeada de acordo com os
	 * parâmetros.
	 * 
	 * @param queryName
	 *            Nome da consulta
	 * @param page
	 *            Número da página
	 * @param size
	 *            Tamanho da página
	 * @param parameters
	 *            Lista de parâmetros
	 * @return Página
	 */
	@RequestMapping(value = "/findByNamedQueryWithParams/{queryName}", params = { "page", "size",
			"parameters" }, method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true)
	public Page<T> findByNamedQuery(@PathVariable("queryName") String queryName,
			@RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "size", required = true) int size,
			@RequestParam(value = "parameters", required = true) List<String> parameters) {
		PageRequest pageRequest = new PageRequest(page, size);
		return getService().findByNamedQuery(queryName, parameters, pageRequest);
	}

	/**
	 * Busca os objetos da classe usando uma consulta nomeada de acordo com os
	 * parâmetros e filtro.
	 * 
	 * @param filter
	 *            Objeto filtro
	 * @param queryName
	 *            Nome da consulta
	 * @param page
	 *            Número da página
	 * @param size
	 *            Tamanho da página
	 * @param parameters
	 *            Lista de parâmetros
	 * @return Página
	 */
	@RequestMapping(value = "/findByNamedQueryWithParamsAndFilter/{queryName}", params = { "page", "size",
			"parameters" }, method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true)
	public Page<T> findByNamedQuery(@RequestBody Filter filter, @PathVariable("queryName") String queryName,
			@RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "size", required = true) int size,
			@RequestParam(value = "parameters", required = true) List<String> parameters) {
		PageRequest pageRequest = new PageRequest(page, size);
		return getService().findByNamedQuery(queryName, parameters, pageRequest);
	}

	/**
	 * Count
	 */

	/**
	 * Retorna a quantidade de objetos da classe.
	 * 
	 * @return Número total de objetos
	 */
	@RequestMapping(value = "/count", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true)
	public long count() {
		return getService().count();
	}

	/**
	 * Verifica a existência de um objeto com o ID.
	 * 
	 * @param id
	 *            Id do objeto
	 * @return Verdadeiro se existir.
	 */
	@RequestMapping(value = "/exists/{id}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true)
	public boolean exists(@PathVariable String id) {
		ID castID = (ID) id;
		return getService().exists(castID);
	}

	/**
	 * Verifica a existência dos objetos contidos na lista.
	 * 
	 * @param ids Lista de id's para verificar a existência.
	 * @return Verdadeiro se existir algum id.
	 */
	@RequestMapping(value = "/exists", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true)
	public boolean exists(@RequestParam(required = true) List<String> ids) {
		List<ID> newIds = new ArrayList<ID>();
		for (String id : ids) {
			ID castID = (ID) id;
			newIds.add(castID);
		}
		return getService().exists(newIds);
	}

	/**
	 * Método abstrato que irá fornecer a classe de serviço para ser usada no
	 * controller.
	 * 
	 * @return
	 */
	public abstract SQLService<T, ID> getService();

}
