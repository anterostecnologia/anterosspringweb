package br.com.anteros.spring.web.config;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import br.com.anteros.core.log.Logger;
import br.com.anteros.core.log.LoggerProvider;
import br.com.anteros.spring.web.support.OpenSQLSessionInViewFilter;

public class AnterosResponseHeaderFilter implements Filter {
	
	
	private static Logger LOG = LoggerProvider.getInstance().getLogger(AnterosResponseHeaderFilter.class);
 
    public AnterosResponseHeaderFilter() {
		super();
		LOG.info("Inicializou filtro do AnterosResponseHeaderFilter");  
	}

	@Override
    public void doFilter(ServletRequest request, ServletResponse response, 
      FilterChain chain) throws IOException, ServletException {
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        httpServletResponse.setHeader(
          "Anteros-JSESSION-ID", httpServletRequest.getSession().getId());
        chain.doFilter(request, response);
    }
 
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        
    }
 
    @Override
    public void destroy() {
       
    }
}