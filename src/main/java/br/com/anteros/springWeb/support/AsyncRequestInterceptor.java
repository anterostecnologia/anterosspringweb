package br.com.anteros.springWeb.support;

import java.util.concurrent.Callable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.async.CallableProcessingInterceptorAdapter;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.context.request.async.DeferredResultProcessingInterceptor;

import br.com.anteros.persistence.session.SQLSessionFactory;
import br.com.anteros.spring.transaction.SQLSessionFactoryUtils;
import br.com.anteros.spring.transaction.SQLSessionHolder;

class AsyncRequestInterceptor extends CallableProcessingInterceptorAdapter
		implements DeferredResultProcessingInterceptor {

	private static final Log logger = LogFactory.getLog(AsyncRequestInterceptor.class);

	private final SQLSessionFactory sessionFactory;

	private final SQLSessionHolder sessionHolder;

	private volatile boolean timeoutInProgress;


	public AsyncRequestInterceptor(SQLSessionFactory sessionFactory, SQLSessionHolder sessionHolder) {
		this.sessionFactory = sessionFactory;
		this.sessionHolder = sessionHolder;
	}

	@Override
	public <T> void preProcess(NativeWebRequest request, Callable<T> task) {
		bindSession();
	}

	public void bindSession() {
		this.timeoutInProgress = false;
		TransactionSynchronizationManager.bindResource(this.sessionFactory, this.sessionHolder);
	}

	@Override
	public <T> void postProcess(NativeWebRequest request, Callable<T> task, Object concurrentResult) {
		TransactionSynchronizationManager.unbindResource(this.sessionFactory);
	}

	@Override
	public <T> Object handleTimeout(NativeWebRequest request, Callable<T> task) {
		this.timeoutInProgress = true;
		return RESULT_NONE; // give other interceptors a chance to handle the timeout
	}

	@Override
	public <T> void afterCompletion(NativeWebRequest request, Callable<T> task) throws Exception {
		closeAfterTimeout();
	}

	private void closeAfterTimeout() {
		if (this.timeoutInProgress) {
			logger.debug("Closing Anteros SQLSession after async request timeout");
			SQLSessionFactoryUtils.closeSession(sessionHolder.getSQLSession());
		}
	}

	// Implementation of DeferredResultProcessingInterceptor methods

	public <T> void beforeConcurrentHandling(NativeWebRequest request, DeferredResult<T> deferredResult) {}
	public <T> void preProcess(NativeWebRequest request, DeferredResult<T> deferredResult) {}
	public <T> void postProcess(NativeWebRequest request, DeferredResult<T> deferredResult, Object result) {}

	@Override
	public <T> boolean handleTimeout(NativeWebRequest request, DeferredResult<T> deferredResult) {
		this.timeoutInProgress = true;
		return true; // give other interceptors a chance to handle the timeout
	}

	@Override
	public <T> void afterCompletion(NativeWebRequest request, DeferredResult<T> deferredResult) {
		closeAfterTimeout();
	}

}
