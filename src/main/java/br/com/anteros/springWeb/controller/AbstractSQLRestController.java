package br.com.anteros.springWeb.controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.WebRequest;

import br.com.anteros.persistence.session.repository.Page;
import br.com.anteros.persistence.session.repository.PageRequest;
import br.com.anteros.persistence.session.service.SQLService;

public abstract class AbstractSQLRestController<T, ID extends Serializable> {

	protected SQLService<T, ID> service;

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
	public T save(HttpServletRequest request, HttpServletResponse response, @RequestBody T object) throws Exception {
		return service.save(object);
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
	public T remove(HttpServletRequest request, HttpServletResponse response, @PathVariable(value = "id") ID id)
			throws Exception {
		T result = service.findOne(id);
		service.remove(result);
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
	public T findOne(HttpServletRequest request, HttpServletResponse response, @PathVariable(value = "id") ID id)
			throws Exception {
		return service.findOne(id);
	}

	@RequestMapping(value = "/findAll", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public List<T> findAll() {
		return service.findAll();
	}

	@RequestMapping(method = RequestMethod.GET, value = "/findAllPaginated", params = { "page", "size" })
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public Page<T> findAll(@RequestParam("page") int page, @RequestParam("size") int size) {
		PageRequest pageRequest = new PageRequest(page, size);
		return service.findAll(pageRequest);
	}

	@RequestMapping(value = "/find/{sql}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public List<T> find(HttpServletRequest request, HttpServletResponse response,
			@PathVariable(value = "sql") String sql) throws Exception {
		return service.find(sql);
	}

	@RequestMapping(value = "/findWithPage/{sql}", params = { "page", "size" }, method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public Page<T> find(@PathVariable("sql") String sql, @RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "size", required = true) int size) {
		PageRequest pageRequest = new PageRequest(page, size);
		return service.find(sql, pageRequest);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/findWithParameters/{sql}", params={"parameters"})
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public List<T> find(@PathVariable String sql, @RequestParam(value="parameters", required=true) Object... parameters) {
		return service.find(sql, parameters);
	}

	@RequestMapping(method = RequestMethod.GET, value = "/findWithParametersAndPage/{sql}", params = { "page", "size", "parameters" })
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public Page<T> find(@PathVariable("sql") String sql, @RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "size", required = true) int size, @RequestParam(value="parameters", required=true) Object... parameters) {
		PageRequest pageRequest = new PageRequest(page, size);
		return service.find(sql, parameters, pageRequest);
	}

	@RequestMapping(value = "/findByNamedQuery/{queryName}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public List<T> findByNamedQuery(@PathVariable("queryName") String queryName) {
		return service.findByNamedQuery(queryName);
	}

	@RequestMapping(value = "/findByNamedQueryWithPage/{queryName}", params = { "page", "size" }, method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public Page<T> findByNamedQuery(@PathVariable("queryName") String queryName,
			@RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "size", required = true) int size) {
		PageRequest pageRequest = new PageRequest(page, size);
		return service.findByNamedQuery(queryName, pageRequest);
	}

	@RequestMapping(value = "/findByNamedQueryWithParameters/{queryName}", params ={"parameters"}, method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public List<T> findByNamedQuery(@PathVariable("queryName") String queryName, @RequestParam(value="parameters", required=true) Object... parameters) {
		return service.findByNamedQuery(queryName, parameters);
	}

	@RequestMapping(value = "/findByNamedQueryWithParamsAndPage/{queryName}", params = { "page", "size", "parameters" }, method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public Page<T> findByNamedQuery(@PathVariable("queryName")String queryName, @RequestParam(value = "page", required = true) int page,
			@RequestParam(value = "size", required = true) int size, @RequestParam(value="parameters", required=true) Object... parameters) {
		PageRequest pageRequest = new PageRequest(page, size);
		return service.findByNamedQuery(queryName, parameters, pageRequest);
	}

	@RequestMapping(value = "/count", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	@ResponseBody
	public long count() {
		return service.count();
	}

	/**
	 * Método abstrato que irá fornecer a classe de serviço para ser usada no
	 * controller.
	 * 
	 * @return
	 */
	public abstract SQLService<T, ID> getService();

	/**
	 * Método para tratamento de exceções ocorridas durante a requisição REST.
	 * 
	 * @param ex
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@ExceptionHandler(HttpClientErrorException.class)
	public @ResponseBody String handleUncaughtException(HttpClientErrorException ex, WebRequest request,
			HttpServletResponse response) throws IOException {
		response.sendError(ex.getStatusCode().value(), ex.getMessage());
		ex.printStackTrace();
		return ex.getMessage();
	}

}
