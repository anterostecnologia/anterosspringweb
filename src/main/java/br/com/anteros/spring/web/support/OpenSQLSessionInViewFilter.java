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
package br.com.anteros.spring.web.support;

import java.io.IOException;
import java.util.Enumeration;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.async.WebAsyncManager;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.anteros.core.log.Logger;
import br.com.anteros.core.log.LoggerProvider;
import br.com.anteros.persistence.session.SQLSession;
import br.com.anteros.persistence.session.SQLSessionFactory;
import br.com.anteros.spring.transaction.SQLSessionFactoryUtils;
import br.com.anteros.spring.transaction.SQLSessionHolder;

/**
 * 
 * @author Edson Martins edsonmartins2005@gmail.com
 *
 */
public class OpenSQLSessionInViewFilter extends OncePerRequestFilter {

	public static final String DEFAULT_SESSION_FACTORY_BEAN_NAME = "sessionFactorySQL";

	private static Logger LOG = LoggerProvider.getInstance().getLogger(OpenSQLSessionInViewFilter.class);

	private String sessionFactoryBeanName = DEFAULT_SESSION_FACTORY_BEAN_NAME;

	private boolean singleSession = true;

	public void setSessionFactoryBeanName(String sessionFactoryBeanName) {
		this.sessionFactoryBeanName = sessionFactoryBeanName;
	}

	protected String getSessionFactoryBeanName() {
		return this.sessionFactoryBeanName;
	}

	public void setSingleSession(boolean singleSession) {
		this.singleSession = singleSession;
	}

	protected boolean isSingleSession() {
		return this.singleSession;
	}

	@Override
	protected boolean shouldNotFilterAsyncDispatch() {
		return false;
	}

	@Override
	protected boolean shouldNotFilterErrorDispatch() {
		return false;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
	    String tenantID = request.getHeader("x-tenant-id");
	    String companyID = request.getHeader("x-company-id"); 
	    
		LOG.info("Tenant ID: "+tenantID+" Company ID: "+companyID);
		SQLSessionFactory sessionFactory = lookupSessionFactory(request);
		boolean participate = false;

		WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);
		
		WebApplicationContext springContext = 
		        WebApplicationContextUtils.getWebApplicationContext(this.getServletContext());	
		
		String key = getAlreadyFilteredAttributeName();

		if (isSingleSession()) {
			if (TransactionSynchronizationManager.hasResource(sessionFactory)) {
				participate = true;
			} else {
				boolean isFirstRequest = !isAsyncDispatch(request);
				if (isFirstRequest || !applySessionBindingInterceptor(asyncManager, key)) {
					logger.debug("Opening single Anteros SQLSession in OpenSQLSessionInViewFilter");
					SQLSession session = getSession(sessionFactory);
					session.setTenantId(tenantID);
					session.setCompanyId(companyID);
					SQLSessionHolder sessionHolder = new SQLSessionHolder(session);
					TransactionSynchronizationManager.bindResource(sessionFactory, sessionHolder);

					AsyncRequestInterceptor interceptor = new AsyncRequestInterceptor(sessionFactory, sessionHolder);
					asyncManager.registerCallableInterceptor(key, interceptor);
					asyncManager.registerDeferredResultInterceptor(key, interceptor);
				}
			}
		} else {
			Assert.state(!isAsyncStarted(request), "Deferred close mode is not supported on async dispatches");
			if (SQLSessionFactoryUtils.isDeferredCloseActive(sessionFactory)) {
				participate = true;
			} else {
				SQLSessionFactoryUtils.initDeferredClose(sessionFactory);
			}
		}

		try {
			LOG.debug("Before execute doFilter");
			filterChain.doFilter(request, response);

			Enumeration<String> hds = request.getHeaderNames();
			String header = request.getHeader("x-tenant-id");
			
			LOG.debug("After execute doFilter");
		} finally {
			if (!participate) {
				if (isSingleSession()) {
					SQLSessionHolder sessionHolder = (SQLSessionHolder) TransactionSynchronizationManager
							.unbindResource(sessionFactory);
					if (!isAsyncStarted(request)) {
						logger.debug("Closing single Anteros SQLSession in OpenSQLSessionInViewFilter");
						closeSession(sessionHolder.getSession(), sessionFactory);
						sessionHolder.removeSession(sessionHolder.getSession());
					}
				} else {
					SQLSessionFactoryUtils.processDeferredClose(sessionFactory);
				}
			}
		}
	}

	protected SQLSessionFactory lookupSessionFactory(HttpServletRequest request) {
		return lookupSessionFactory();
	}

	protected SQLSessionFactory lookupSessionFactory() {
		if (logger.isDebugEnabled()) {
			logger.debug(
					"Using SQLSessionFactory '" + getSessionFactoryBeanName() + "' for OpenSQLSessionInViewFilter");
		}
		WebApplicationContext wac = WebApplicationContextUtils.getRequiredWebApplicationContext(getServletContext());
		return wac.getBean(getSessionFactoryBeanName(), SQLSessionFactory.class);
	}

	protected SQLSession getSession(SQLSessionFactory sessionFactory) throws DataAccessResourceFailureException {
		SQLSession session = SQLSessionFactoryUtils.getSession(sessionFactory, true);
		return session;
	}

	protected void closeSession(SQLSession session, SQLSessionFactory sessionFactory) {
		SQLSessionFactoryUtils.closeSession(session);
	}

	private boolean applySessionBindingInterceptor(WebAsyncManager asyncManager, String key) {
		if (asyncManager.getCallableInterceptor(key) == null) {
			return false;
		}
		((AsyncRequestInterceptor) asyncManager.getCallableInterceptor(key)).bindSession();
		return true;
	}

}
