package br.com.anteros.springWeb.support;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
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


public class OpenSQLSessionInViewFilter extends OncePerRequestFilter {

	public static final String DEFAULT_SESSION_FACTORY_BEAN_NAME = "sessionFactory";

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
	protected void doFilterInternal(
			HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		SQLSessionFactory sessionFactory = lookupSessionFactory(request);
		boolean participate = false;

		WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);
		String key = getAlreadyFilteredAttributeName();

		if (isSingleSession()) {
			if (TransactionSynchronizationManager.hasResource(sessionFactory)) {
				participate = true;
			}
			else {
				boolean isFirstRequest = !isAsyncDispatch(request);
				if (isFirstRequest || !applySessionBindingInterceptor(asyncManager, key)) {
					logger.debug("Opening single Anteros SQLSession in OpenSQLSessionInViewFilter");
					SQLSession session = getSession(sessionFactory);
					SQLSessionHolder sessionHolder = new SQLSessionHolder(session);
					TransactionSynchronizationManager.bindResource(sessionFactory, sessionHolder);

					AsyncRequestInterceptor interceptor = new AsyncRequestInterceptor(sessionFactory, sessionHolder);
					asyncManager.registerCallableInterceptor(key, interceptor);
					asyncManager.registerDeferredResultInterceptor(key, interceptor);
				}
			}
		}
		else {
			Assert.state(!isAsyncStarted(request), "Deferred close mode is not supported on async dispatches");
			if (SQLSessionFactoryUtils.isDeferredCloseActive(sessionFactory)) {
				participate = true;
			}
			else {
				SQLSessionFactoryUtils.initDeferredClose(sessionFactory);
			}
		}

		try {
			LOG.debug("Before execute doFilter");
			filterChain.doFilter(request, response);
			LOG.debug("After execute doFilter");
		}
		finally {
			if (!participate) {
				if (isSingleSession()) {
					// single session mode
					SQLSessionHolder sessionHolder =
							(SQLSessionHolder) TransactionSynchronizationManager.unbindResource(sessionFactory);
					if (!isAsyncStarted(request)) {
						logger.debug("Closing single Anteros SQLSession in OpenSQLSessionInViewFilter");
						closeSession(sessionHolder.getSession(), sessionFactory);
					}
				}
				else {
					// deferred close mode
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
			logger.debug("Using SQLSessionFactory '" + getSessionFactoryBeanName() + "' for OpenSQLSessionInViewFilter");
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
