/*
 * Copyright 2012-2025 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.boot.autoconfigure.interfaceclients.http;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import org.jspecify.annotations.Nullable;

import org.springframework.boot.autoconfigure.http.client.HttpClientProperties;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings.Redirects;
import org.springframework.web.client.RestClient;

/**
 * Properties for HTTP Interface Client Groups. Contains group registration properties and
 * HTTP client properties that mirror the default {@link HttpClientProperties}.
 *
 * @author Olga Maciaszek-Sharma
 * @author Phillip Webb
 * @since 4.0.0
 */
public class HttpInterfaceGroupProperties {

	/**
	 * Base url to set in the underlying HTTP client group. By default, set to
	 * {@code null}.
	 */
	private @Nullable String baseUrl;

	/**
	 * Default request headers for interface client group. By default, set to empty
	 * {@link Map}.
	 */
	private Map<String, List<String>> defaultHeaders = Collections.emptyMap();

	/**
	 * Default factory used for a client HTTP request.By default,
	 * falls back to {@link HttpClientProperties#getFactory()}.
	 * Currently only supports {@link RestClient}.
	 */
	private @Nullable Factory factory;

	/**
	 * Handling for HTTP redirects. By default,
	 * falls back to {@link HttpClientProperties#getRedirects()}.
	 * Currently only supports {@link RestClient}.
	 */
	private @Nullable Redirects redirects;

	/**
	 * Default request connect timeout for interface client group. By default,
	 * falls back to {@link HttpClientProperties#getConnectTimeout()}.
	 * Currently only supports {@link RestClient}.
	 */
	@Nullable
	Duration connectTimeout;


	/**
	 * Default request read timeout for interface client group. By default,
	 * falls back to {@link HttpClientProperties#getReadTimeout()}.
	 * Currently only supports {@link RestClient}.
	 */
	private @Nullable Duration readTimeout;


	/**
	 * Default SSL configuration for a client HTTP request.
	 */
	private final Ssl ssl = new Ssl();


	public @Nullable String getBaseUrl() {
		return this.baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public Map<String, List<String>> getDefaultHeaders() {
		return this.defaultHeaders;
	}

	public void setDefaultHeaders(Map<String, List<String>> defaultHeaders) {
		this.defaultHeaders = defaultHeaders;
	}

	public @Nullable Factory getFactory() {
		return this.factory;
	}

	public void setFactory(Factory factory) {
		this.factory = factory;
	}

	public @Nullable Redirects getRedirects() {
		return this.redirects;
	}

	public void setRedirects(Redirects redirects) {
		this.redirects = redirects;
	}

	public @Nullable Duration getConnectTimeout() {
		return this.connectTimeout;
	}

	public void setConnectTimeout(Duration connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public @Nullable Duration getReadTimeout() {
		return this.readTimeout;
	}

	public void setReadTimeout(Duration readTimeout) {
		this.readTimeout = readTimeout;
	}

	public Ssl getSsl() {
		return this.ssl;
	}

	/**
	 * Supported factory types.
	 */
	public enum Factory {

		/**
		 * Apache HttpComponents HttpClient.
		 */
		HTTP_COMPONENTS(ClientHttpRequestFactoryBuilder::httpComponents),

		/**
		 * Jetty's HttpClient.
		 */
		JETTY(ClientHttpRequestFactoryBuilder::jetty),

		/**
		 * Reactor-Netty.
		 */
		REACTOR(ClientHttpRequestFactoryBuilder::reactor),

		/**
		 * Java's HttpClient.
		 */
		JDK(ClientHttpRequestFactoryBuilder::jdk),

		/**
		 * Standard JDK facilities.
		 */
		SIMPLE(ClientHttpRequestFactoryBuilder::simple);

		private final Supplier<ClientHttpRequestFactoryBuilder<?>> builderSupplier;

		Factory(Supplier<ClientHttpRequestFactoryBuilder<?>> builderSupplier) {
			this.builderSupplier = builderSupplier;
		}

		ClientHttpRequestFactoryBuilder<?> builder() {
			return this.builderSupplier.get();
		}

	}

	/**
	 * SSL configuration.
	 */
	public static class Ssl {

		/**
		 * SSL bundle to use.
		 */
		private @Nullable String bundle;

		public @Nullable String getBundle() {
			return this.bundle;
		}

		public void setBundle(String bundle) {
			this.bundle = bundle;
		}

	}

}
