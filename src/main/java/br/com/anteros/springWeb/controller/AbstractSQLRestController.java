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
import br.com.anteros.persistence.dsl.osql.AbstractOSQLQuery;
import br.com.anteros.persistence.session.repository.Page;
import br.com.anteros.persistence.session.repository.PageRequest;
import br.com.anteros.persistence.session.service.SQLService;


@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true)
public abstract class AbstractSQLRestController<T, ID extends Serializable> {

	private static Logger log = LoggerProvider.getInstance().getLogger(AbstractOSQLQuery.class.getName());

	/**
	 * Insert or Update object in database via POST or PUT methods;
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
	 * Remove object in database via DELETE method
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = false)
	public T remove(HttpServletRequest request, HttpServletResponse response, @PathVariable(value = "id") ID id)
			throws Exception {
		T result = getService().findOne(id);
		getService().remove(result);
		return result;
	}

	/**
	 * Get object in database via GET method
	 * 
	 * @param id
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true)
	public T findOne(HttpServletRequest request, HttpServletResponse response, @PathVariable(value = "id") ID id)
			throws Exception {
		return getService().findOne(id);
	}

	@RequestMapping(value = "/findAll", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	@Transactional(rollbackFor = Throwable.class, propagation = Propagation.REQUIRED, readOnly = true)
	public List<T> findAll() {
		return getService().findAll();
	}

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
