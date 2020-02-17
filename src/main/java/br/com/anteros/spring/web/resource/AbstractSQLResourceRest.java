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
package br.com.anteros.spring.web.resource;

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
import br.com.anteros.core.utils.Assert;
import br.com.anteros.core.utils.StringUtils;
import br.com.anteros.persistence.dsl.osql.BooleanBuilder;
import br.com.anteros.persistence.dsl.osql.DynamicEntityPath;
import br.com.anteros.persistence.dsl.osql.types.OrderSpecifier;
import br.com.anteros.persistence.metadata.EntityCache;
import br.com.anteros.persistence.metadata.descriptor.DescriptionField;
import br.com.anteros.persistence.session.exception.SQLSessionException;
import br.com.anteros.persistence.session.query.SQLQueryException;
import br.com.anteros.persistence.session.query.filter.AnterosFilterDsl;
import br.com.anteros.persistence.session.query.filter.AnterosMultipleFieldsFilter;
import br.com.anteros.persistence.session.query.filter.AnterosSortFieldsHelper;
import br.com.anteros.persistence.session.query.filter.DefaultFilterBuilder;
import br.com.anteros.persistence.session.query.filter.Filter;
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
@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true, transactionManager = "transactionManagerSQL")
@SuppressWarnings("unchecked")
public abstract class AbstractSQLResourceRest<T, ID extends Serializable> {

	protected static Logger log = LoggerProvider.getInstance().getLogger(AbstractSQLResourceRest.class.getName());

	/**
	 * Insere ou atualiza um objeto.
	 * 
	 * @param object Objeto a ser salvo
	 * @return Objeto salvo
	 * @throws Exception
	 */

	@RequestMapping(value = "/", method = { RequestMethod.POST, RequestMethod.PUT })
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = false, transactionManager = "transactionManagerSQL")
	public T save(@RequestBody T object) throws Exception {
		return getService().save(object);
	}

	/**
	 * Valida um objeto.
	 * 
	 * @param object Objeto a ser validado
	 * @return Objeto validado
	 * @throws Exception
	 */

	@RequestMapping(value = "/validate", method = { RequestMethod.POST, RequestMethod.PUT })
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = false, transactionManager = "transactionManagerSQL")
	public void validate(@RequestBody T object) throws Exception {
		getService().validate(object);
	}

	/**
	 * Valida um objeto.
	 * 
	 * @param object Objeto a ser validado
	 * @param groups Grupo de validação
	 * @return Objeto validado
	 * @throws Exception
	 */

	@RequestMapping(value = "/validateGroup", method = { RequestMethod.POST, RequestMethod.PUT })
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = false, transactionManager = "transactionManagerSQL")
	public void validateGroup(@RequestBody T object, Class<?>... groups) throws Exception {
		getService().validate(object, groups);
	}

	/**
	 * Remove um objeto pelo ID.
	 * 
	 * @param id Identificador do objeto
	 * @return Objeto removido.
	 * @throws Exception
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = false, transactionManager = "transactionManagerSQL")
	public T removeById(@PathVariable(value = "id") String id) throws Exception {
		ID castID = (ID) id;
		T result = getService().findOne(castID,null);
		if (result == null) {
			throw new SQLSessionException("ID " + id + " não foi encontrado.");
		}
		getService().remove(result);
		return result;
	}

	/**
	 * Remove todos os objetos da classe.
	 * 
	 * @param ids Lista dos id's a serem removidos.
	 * @return Verdadeiro se removeu todos.
	 * @throws Exception
	 */
	@RequestMapping(value = "/", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = false, transactionManager = "transactionManagerSQL")
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
	 * @param id Identificador do objeto.
	 * @return Objeto encontrado.
	 * @throws Exception
	 */
	@RequestMapping(value = "/{id}", params = { "fieldsToForceLazy" }, method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true, transactionManager = "transactionManagerSQL")
	public T findOne(@PathVariable(value = "id") String id, @RequestParam("fieldsToForceLazy") String fieldsToForceLazy) throws Exception {
		ID castID = (ID) id;
		return getService().findOne(castID,true,fieldsToForceLazy);
	}

	/**
	 * Busca os objetos da classe com paginação.
	 * 
	 * @param page Número da página
	 * @param size Tamanho da página
	 * @return Página
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/findAll", params = { "page", "size", "fieldsToForceLazy" })
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true, transactionManager = "transactionManagerSQL")
	public Page<T> findAll(@RequestParam("page") int page, @RequestParam("size") int size, @RequestParam("fieldsToForceLazy") String fieldsToForceLazy) {
		PageRequest pageRequest = new PageRequest(page, size);
		Page<T> result = getService().findAll(pageRequest, true, fieldsToForceLazy);
		Page<T> concretePage = this.createConcretePage(result.getContent(), pageRequest, result.getTotalElements());
		if (concretePage!=null) {
			return concretePage;
		}
		return result;
	}

	/**
	 * Busca os objetos da classe com paginação e ordenado
	 * 
	 * @param page Número da página
	 * @param size Tamanho da página
	 * @param sort Campos para ordenação
	 * @return Página
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/findAll", params = { "page", "size", "sort", "fieldsToForceLazy" })
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true, transactionManager = "transactionManagerSQL")
	public Page<T> findAll(@RequestParam("page") int page, @RequestParam("size") int size,
			@RequestParam("sort") String sort, @RequestParam("fieldsToForceLazy") String fieldsToForceLazy) {
		PageRequest pageRequest = new PageRequest(page, size);

		BooleanBuilder builder = new BooleanBuilder();
		EntityCache[] entityCaches = getService().getSession().getEntityCacheManager().getEntitiesBySuperClassIncluding(this.getService().getResultClass());

		List<OrderSpecifier> orderBy = AnterosSortFieldsHelper.convertFieldsToOrderby(getService().getSession(),
				(DynamicEntityPath) this.getService().getEntityPath(), entityCaches, sort);
		Page<T> result = getService().findAll(builder, true, pageRequest, fieldsToForceLazy, orderBy.toArray(new OrderSpecifier[] {}));
		Page<T> concretePage = this.createConcretePage(result.getContent(), pageRequest, result.getTotalElements());
		if (concretePage!=null) {
			return concretePage;
		}
		return result;
	}

	protected Page<T> createConcretePage(List<T> content, PageRequest pageRequest, long totalElements){
		return null;
	}

	/**
	 * Busca os objetos da classe contido na lista de ID's.
	 * 
	 * @param ids Lista de ID's
	 * @return Lista de objetos encontrados.
	 */
	@RequestMapping(method = RequestMethod.GET, value = "/findAll", params = { "fieldsToForceLazy" })
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true, transactionManager = "transactionManagerSQL")
	public List<T> findAll(@RequestParam(required = true) List<String> ids, @RequestParam("fieldsToForceLazy") String fieldsToForceLazy) {
		List<ID> newIds = new ArrayList<ID>();
		for (String id : ids) {
			ID castID = (ID) id;
			newIds.add(castID);
		}
		List<T> result = getService().findAll(newIds,true, fieldsToForceLazy);
		List<T> concreteList = this.createConcreteList(result);
		if (concreteList!=null) {
			return concreteList;
		}
		return result;
	}

	protected List<T> createConcreteList(List<T> result) {
		return null;
	}

	/**
	 * Busca os objetos da classe de acordo com o objeto filtro.
	 * 
	 * @param filter Objeto filtro
	 * @param page   Número da página
	 * @param size   Tamanho da página
	 * @return Página
	 * @throws Exception
	 */
	@RequestMapping(value = "/findWithFilter", params = { "page", "size","fieldsToForceLazy" }, method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true, transactionManager = "transactionManagerSQL")
	public Page<T> find(@RequestBody Filter filter, @RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "size", required = true) int size, @RequestParam("fieldsToForceLazy") String fieldsToForceLazy) throws Exception {
		PageRequest pageRequest = new PageRequest(page, size);

		DefaultFilterBuilder builder = AnterosFilterDsl.getFilterBuilder();

		String sort = builder.toSortSql(filter, getService().getSession(), getService().getResultClass());

		String sql = builder.toSql(filter, getService().getSession(), getService().getResultClass());
		
		EntityCache entityCache = getService().getSession().getEntityCacheManager().getEntityCache(getService().getResultClass());
		DescriptionField tenantId = entityCache.getTenantId();
		
		if (tenantId!=null) {
			if (this.getService().getSession().getTenantId()==null) {
				throw new SQLQueryException("Informe o Tenant ID para realizar consulta na entidade "+entityCache.getEntityClass().getName());
			}
			sql = sql + " AND "+tenantId.getSimpleColumn().getColumnName()+"="+'"'+getService().getSession().getTenantId()+'"';
		}

		Page<T> result = getService().find("select * from " + getService().getTableName() + " where " + sql
				+ (StringUtils.isNotEmpty(sort) ? " ORDER BY " + sort : ""), builder.getParams(), pageRequest, true, fieldsToForceLazy);
		Page<T> concretePage = this.createConcretePage(result.getContent(), pageRequest, result.getTotalElements());
		if (concretePage!=null) {
			return concretePage;
		}
		return result;
	}

	/**
	 * Busca os objetos da classe de acordo com a string de filtro e os campos.
	 * 
	 * @param filter String filter
	 * @param fields String fields
	 * @param page   Número da página
	 * @param size   Tamanho da página
	 * @return Página
	 * @throws Exception
	 */
	@RequestMapping(value = "/findMultipleFields", params = { "filter", "fields", "page", "size",
			"sort","fieldsToForceLazy" }, method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true, transactionManager = "transactionManagerSQL")
	public Page<T> find(@RequestParam(value = "filter", required = true) String filter,
			@RequestParam(value = "fields", required = true) String fields,
			@RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "size", required = true) int size, @RequestParam(value = "sort") String sort,
			@RequestParam("fieldsToForceLazy") String fieldsToForceLazy)
			throws Exception {
		PageRequest pageRequest = new PageRequest(page, size);
		
		Page<T> result = new AnterosMultipleFieldsFilter<T>().filter(filter).fields(fields).session(getService().getSession()).readOnly(true)
				.resultClass(getService().getResultClass()).fieldsSort(sort).page(pageRequest).fieldsToForceLazy(fieldsToForceLazy).buildAndGetPage();
		Page<T> concretePage = this.createConcretePage(result.getContent(), pageRequest, result.getTotalElements());
		if (concretePage!=null) {
			return concretePage;
		}
		return result;
	}

	/**
	 * Queries nomeadas
	 */

	/**
	 * Busca os objetos da classe usando uma consulta nomeada.
	 * 
	 * @param queryName Nome da consulta
	 * @param page      Número da página
	 * @param size      Tamanho da página
	 * @return Página
	 */
	@RequestMapping(value = "/findByNamedQuery/{queryName}", params = { "page", "size", "fieldsToForceLazy" }, method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true, transactionManager = "transactionManagerSQL")
	public Page<T> findByNamedQuery(@PathVariable("queryName") String queryName,
			@RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "size", required = true) int size, @RequestParam("fieldsToForceLazy") String fieldsToForceLazy) {
		PageRequest pageRequest = new PageRequest(page, size);
		Page<T> result = getService().findByNamedQuery(queryName, pageRequest, true, fieldsToForceLazy);
		Page<T> concretePage = this.createConcretePage(result.getContent(), pageRequest, result.getTotalElements());
		if (concretePage!=null) {
			return concretePage;
		}
		return result;
	}

	/**
	 * Busca os objetos da classe usando uma consulta nomeada e um filtro.
	 * 
	 * @param filter    Objeto filtro
	 * @param queryName Nome da consulta
	 * @param page      Número da página
	 * @param size      Tamanho da página
	 * @return Página
	 */
	@RequestMapping(value = "/findByNamedQueryWithFilter/{queryName}", params = { "page",
			"size", "fieldsToForceLazy" }, method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true, transactionManager = "transactionManagerSQL")
	public Page<T> findByNamedQuery(@RequestBody Filter filter, @PathVariable("queryName") String queryName,
			@RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "size", required = true) int size, @RequestParam("fieldsToForceLazy") String fieldsToForceLazy) {
		PageRequest pageRequest = new PageRequest(page, size);
		DefaultFilterBuilder builder = AnterosFilterDsl.getFilterBuilder();
		Assert.notNull(queryName, "O nome da query não pode ser nulo.");
		String query;
		Page<T> result = null;
		try {
			String sort = builder.toSortSql(filter, getService().getSession(), getService().getResultClass());
			String sql = builder.toSql(filter, getService().getSession(), getService().getResultClass());
			query = getService().getNamedQuery(queryName).getQuery() + " WHERE " + sql
					+ (StringUtils.isNotEmpty(sort) ? " ORDER BY " + sort : "") + sort;
			result = getService().find(query, builder.getParams(), pageRequest, true, fieldsToForceLazy);
		} catch (Exception e) {
			throw new SQLSessionException("Não foi possível executar a query nomeada " + queryName, e);
		}
		Page<T> concretePage = this.createConcretePage(result.getContent(), pageRequest, result.getTotalElements());
		if (concretePage!=null) {
			return concretePage;
		}
		return result;
	}

	/**
	 * Busca os objetos da classe usando uma consulta nomeada de acordo com os
	 * parâmetros.
	 * 
	 * @param queryName  Nome da consulta
	 * @param page       Número da página
	 * @param size       Tamanho da página
	 * @param parameters Lista de parâmetros
	 * @return Página
	 */
	@RequestMapping(value = "/findByNamedQueryWithParams/{queryName}", params = { "page", "size",
			"parameters", "fieldsToForceLazy" }, method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true, transactionManager = "transactionManagerSQL")
	public Page<T> findByNamedQuery(@PathVariable("queryName") String queryName,
			@RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "size", required = true) int size,
			@RequestParam(value = "parameters", required = true) List<String> parameters,
			@RequestParam("fieldsToForceLazy") String fieldsToForceLazy) {
		PageRequest pageRequest = new PageRequest(page, size);
		Page<T> result = getService().findByNamedQuery(queryName, parameters, pageRequest, true, fieldsToForceLazy);
		Page<T> concretePage = this.createConcretePage(result.getContent(), pageRequest, result.getTotalElements());
		if (concretePage!=null) {
			return concretePage;
		}
		return result;
	}

	/**
	 * Busca os objetos da classe usando uma consulta nomeada de acordo com os
	 * parâmetros e filtro.
	 * 
	 * @param filter     Objeto filtro
	 * @param queryName  Nome da consulta
	 * @param page       Número da página
	 * @param size       Tamanho da página
	 * @param parameters Lista de parâmetros
	 * @return Página
	 */
	@RequestMapping(value = "/findByNamedQueryWithParamsAndFilter/{queryName}", params = { "page", "size",
			"parameters","fieldsToForceLazy" }, method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true, transactionManager = "transactionManagerSQL")
	public Page<T> findByNamedQuery(@RequestBody Filter filter, @PathVariable("queryName") String queryName,
			@RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "size", required = true) int size,
			@RequestParam(value = "parameters", required = true) List<String> parameters,
			@RequestParam("fieldsToForceLazy") String fieldsToForceLazy) {
		PageRequest pageRequest = new PageRequest(page, size);
		Page<T> result = getService().findByNamedQuery(queryName, parameters, pageRequest, true, fieldsToForceLazy);
		Page<T> concretePage = this.createConcretePage(result.getContent(), pageRequest, result.getTotalElements());
		if (concretePage!=null) {
			return concretePage;
		}
		return result;
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
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true, transactionManager = "transactionManagerSQL")
	public long count() {
		return getService().count();
	}

	/**
	 * Verifica a existência de um objeto com o ID.
	 * 
	 * @param id Id do objeto
	 * @return Verdadeiro se existir.
	 */
	@RequestMapping(value = "/exists/{id}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true, transactionManager = "transactionManagerSQL")
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
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true, transactionManager = "transactionManagerSQL")
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
