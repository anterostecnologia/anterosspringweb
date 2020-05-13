package br.com.anteros.spring.web.support;

import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpMethod;

import com.google.common.base.Function;
import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.io.ByteStreams;
import com.google.common.net.HttpHeaders;

public class AnterosGzipBodyDecompressFilter implements Filter {

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	 /**
	 * Analisa a solicitação de servlet para um possível corpo(body) compactado com gzip. Quando codificação de conteúdo
	 * O cabeçalho tem o valor "gzip" e o método de solicitação é POST. Lemos todos os gzipados
	 * stream e é haz quaisquer dados descompactá-lo. Caso gzip Content-Encoding
	 * cabeçalho especificado, mas o corpo não está realmente no formato gzip, lançaremos
	 * ZipException.
	 *
	 * @param servletRequest  servlet request
	 * @param servletResponse servlet response
	 * @param chain           filter chain
	 * @throws IOException      lançada quando falhar
	 * @throws ServletException lançada quando falhar
	 */
	@Override
	public final void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse,
			final FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		boolean isGzipped = request.getHeader(HttpHeaders.CONTENT_ENCODING) != null
				&& request.getHeader(HttpHeaders.CONTENT_ENCODING).contains("gzip");
		boolean requestTypeSupported = HttpMethod.POST.equals(request.getMethod());
		if (isGzipped && !requestTypeSupported) {
			throw new IllegalStateException(request.getMethod() + " não suporta corpo(BODY) de parâmetros compactado com gzip."
					+ " Atualmente, apenas solicitações POST são suportadas.");
		}
		if (isGzipped && requestTypeSupported) {
			request = new GzippedInputStreamWrapper((HttpServletRequest) servletRequest);
		}
		chain.doFilter(request, response);

	}

	/**
	 * @inheritDoc
	 */
	@Override
	public final void destroy() {
	}

	/**
	 * Classe de invólucro que detecta se a solicitação é compactada e descompactada.
	 */
	final class GzippedInputStreamWrapper extends HttpServletRequestWrapper {
		/**
		 * Codificação padrão usada quando os parâmetros de postagem são analisados.
		 */
		public static final String DEFAULT_ENCODING = "ISO-8859-1";

		/**
		 * Matriz de bytes serializados resultante de descompactar o corpo compactado com gzip.
		 */
		private byte[] bytes;

		/**
		 * Constrói um objeto de solicitação que envolve a solicitação fornecida. Caso a
		 * codificação de conteúdo contenha "gzip", agrupamos o fluxo de entrada na matriz de bytes para
		 * o fluxo de entrada original que não tem nada, mas o fluxo de entrada envolvido sempre
		 * retorna o fluxo de entrada descompactado reproduzível.
		 *
		 * @param request solicitar qual fluxo de entrada será quebrado.
		 * @throws java.io.IOException quando a recuperação do fluxo de entrada falhou.
		 */
		public GzippedInputStreamWrapper(final HttpServletRequest request) throws IOException {
			super(request);
			try {
				final InputStream in = new GZIPInputStream(request.getInputStream());
				bytes = ByteStreams.toByteArray(in);
			} catch (EOFException e) {
				bytes = new byte[0];
			}
		}

		/**
		 * @return reproduceable fluxo de entrada igual ao servlet inicial
		 *         inputstream (se não foi compactado) ou retorna o fluxo de entrada descompactado.
		 * @throws IOException se falhar.
		 */
		@Override
		public ServletInputStream getInputStream() throws IOException {
			final ByteArrayInputStream sourceStream = new ByteArrayInputStream(bytes);
			return new ServletInputStream() {
				public int read() throws IOException {
					return sourceStream.read();
				}

				public void close() throws IOException {
					super.close();
					sourceStream.close();
				}
			};
		}

		/**
		 * É necessário substituir o getParametersMap porque lemos inicialmente toda a entrada.
		 * O container stream e servlet não terá acesso aos dados do stream de entrada.
		 *
		 * @return parsed lista de parâmetros. Os parâmetros são analisados ​​apenas quando o Content-Type
		 *         "application/x-www-form-urlencoded" está definido.
		 */
		@Override
		public Map getParameterMap() {
			String contentEncodingHeader = getHeader(HttpHeaders.CONTENT_TYPE);
			if (!Strings.isNullOrEmpty(contentEncodingHeader)
					&& contentEncodingHeader.contains("application/x-www-form-urlencoded")) {
				Map params = new HashMap(super.getParameterMap());
				try {
					params.putAll(parseParams(new String(bytes)));
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				return params;
			} else {
				return super.getParameterMap();
			}
		}

		/**
		 * Analisa os parâmetros do fluxo de entrada de bytes.
		 *
		 * @param body corpo da solicitação serializado para string.
		 * @return parsed mapa de parâmetros.
		 * @throws UnsupportedEncodingException se a codificação fornecida não for suportada.
		 */
		private Map<String, String[]> parseParams(final String body) throws UnsupportedEncodingException {
			String characterEncoding = getCharacterEncoding();
			if (null == characterEncoding) {
				characterEncoding = DEFAULT_ENCODING;
			}
			final Multimap<String, String> parameters = ArrayListMultimap.create();
			for (String pair : body.split("&")) {
				if (Strings.isNullOrEmpty(pair)) {
					continue;
				}
				int idx = pair.indexOf("=");

				String key = null;
				if (idx > 0) {
					key = URLDecoder.decode(pair.substring(0, idx), characterEncoding);
				} else {
					key = pair;
				}
				String value = null;
				if (idx > 0 && pair.length() > idx + 1) {
					value = URLDecoder.decode(pair.substring(idx + 1), characterEncoding);
				} else {
					value = null;
				}
				parameters.put(key, value);
			}
			return Maps.transformValues(parameters.asMap(), new Function<Collection<String>, String[]>() {
				@Override
				public String[] apply(final Collection<String> input) {
					return Iterables.toArray(input, String.class);
				}
			});
		}
	}
}