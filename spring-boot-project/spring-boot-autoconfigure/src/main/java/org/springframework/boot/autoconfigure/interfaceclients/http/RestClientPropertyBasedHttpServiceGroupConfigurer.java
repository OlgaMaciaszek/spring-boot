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
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.http.client.HttpClientProperties;
import org.springframework.boot.autoconfigure.web.client.RestClientBuilderConfigurer;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings.Redirects;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient.Builder;
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer;

// TODO: add corresponding WebClient-based implementation

/**
 * @author Olga Maciaszek-Sharma
 */
public class RestClientPropertyBasedHttpServiceGroupConfigurer implements RestClientHttpServiceGroupConfigurer {

	private final HttpClientProperties httpClientProperties;

	private final HttpInterfaceGroupsProperties properties;

	private final RestClientBuilderConfigurer restClientBuilderConfigurer;

	private final ObjectProvider<SslBundles> sslBundles;

	public RestClientPropertyBasedHttpServiceGroupConfigurer(
			HttpClientProperties httpClientProperties,
			HttpInterfaceGroupsProperties properties,
			RestClientBuilderConfigurer restClientBuilderConfigurer,
			ObjectProvider<SslBundles> sslBundles) {
		this.httpClientProperties = httpClientProperties;
		this.properties = properties;
		this.restClientBuilderConfigurer = restClientBuilderConfigurer;
		this.sslBundles = sslBundles;
	}

	private ClientHttpRequestFactory buildClientHttpRequestFactory(HttpInterfaceGroupProperties clientGroupProperties) {
		// Rebuild entire request factory
		SslBundle sslBundle = getSslBundle(clientGroupProperties.getSsl(), this.sslBundles);
		ClientHttpRequestFactorySettings factorySettings = new ClientHttpRequestFactorySettings(getRedirects(clientGroupProperties),
				getConnectTimeout(clientGroupProperties), getReadTimeout(clientGroupProperties), sslBundle);
		return ClientHttpRequestFactoryBuilder.detect().build(factorySettings);
	}

	private boolean requiresNewRequestFactory(HttpInterfaceGroupProperties clientGroupProperties) {
		return clientGroupProperties.getConnectTimeout() != null
				|| clientGroupProperties.getReadTimeout() != null || clientGroupProperties.getSsl().getBundle() != null;
	}

	private Redirects getRedirects(HttpInterfaceGroupProperties clientGroupProperties) {
		return clientGroupProperties.getRedirects() != null ? clientGroupProperties.getRedirects()
				: this.httpClientProperties.getRedirects();
	}

	private Duration getReadTimeout(HttpInterfaceGroupProperties clientGroupProperties) {
		return clientGroupProperties.getReadTimeout() != null ?
				clientGroupProperties.getReadTimeout() : this.httpClientProperties.getReadTimeout();
	}

	private Duration getConnectTimeout(HttpInterfaceGroupProperties clientGroupProperties) {
		return clientGroupProperties.getConnectTimeout() != null ? clientGroupProperties.getConnectTimeout()
				: this.httpClientProperties.getConnectTimeout();
	}

	@Override
	public void configureGroups(Groups<Builder> groups) {
		groups.configureClient((group, builder) -> {
			this.restClientBuilderConfigurer.configure(builder);
			HttpInterfaceGroupProperties clientGroupProperties = this.properties.getProperties(group.name());
			if (clientGroupProperties == null) {
				return;
			}
			if (clientGroupProperties.getBaseUrl() != null) {
				builder.baseUrl(clientGroupProperties.getBaseUrl());
			}
			if (requiresNewRequestFactory(clientGroupProperties)) {
				builder.requestFactory(buildClientHttpRequestFactory(clientGroupProperties));
			}
			Map<String, List<String>> defaultHeaders = clientGroupProperties.getDefaultHeaders();
			for (String headerName : defaultHeaders.keySet()) {
				builder.defaultHeader(headerName, defaultHeaders.get(headerName).toArray(String[]::new));
			}
		});

	}

	private SslBundle getSslBundle(HttpClientProperties.Ssl properties, ObjectProvider<SslBundles> sslBundles) {
		String name = properties.getBundle();
		return (StringUtils.hasLength(name)) ? sslBundles.getObject().getBundle(name) : null;
	}

}
