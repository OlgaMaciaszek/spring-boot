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

/**
 * Properties for HTTP Interface Clients.
 *
 * @author Olga Maciaszek-Sharma
 * @since 4.0.0
 */
public class HttpInterfaceClientGroupProperties {

	// FIXME

	/**
	 * Base url to set in the underlying HTTP client. By default, set to null.
	 */
	private String baseUrl = null;

	/**
	 * Default request connect timeout for interface client group.
	 */
	private Duration connectTimeout = null;

	/**
	 * Default request read timeout for interface client group.
	 */
	private Duration readTimeout = null;

	/**
	 * Default request headers for interface client group.
	 */
	private Map<String, List<String>> defaultHeaders = Collections.emptyMap();

	// TODO: response timeout

	public String getBaseUrl() {
		return this.baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public Duration getConnectTimeout() {
		return this.connectTimeout;
	}

	public void setConnectTimeout(Duration connectTimeout) {
		this.connectTimeout = connectTimeout;
	}

	public Duration getReadTimeout() {
		return this.readTimeout;
	}

	public void setReadTimeout(Duration readTimeout) {
		this.readTimeout = readTimeout;
	}

	public Map<String, List<String>> getDefaultHeaders() {
		return this.defaultHeaders;
	}

	public void setDefaultHeaders(Map<String, List<String>> defaultHeaders) {
		this.defaultHeaders = defaultHeaders;
	}

}
