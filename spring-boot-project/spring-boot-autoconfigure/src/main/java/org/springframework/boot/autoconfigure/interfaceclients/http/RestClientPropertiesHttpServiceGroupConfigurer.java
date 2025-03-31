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

import org.jspecify.annotations.Nullable;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.http.client.HttpClientProperties;
import org.springframework.boot.autoconfigure.interfaceclients.http.HttpInterfaceGroupProperties.Ssl;
import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings.Redirects;
import org.springframework.boot.ssl.SslBundle;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.core.Ordered;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClient.Builder;
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer;

/**
 * A {@link RestClientHttpServiceGroupConfigurer} that configures the group and its
 * underlying {@link RestClient .Builder} using property values. For
 * {@link ClientHttpRequestFactorySettings}, the configuration falls back to
 * {@link HttpClientProperties} if the property is not set for the group.
 *
 * @author Olga Maciaszek-Sharma
 * @since 4.0.0
 */
public class RestClientPropertiesHttpServiceGroupConfigurer implements RestClientHttpServiceGroupConfigurer {

	private final HttpClientProperties httpClientProperties;

	private final HttpInterfaceGroupsProperties clientGroupProperties;

	private final ClientHttpRequestFactoryBuilder<?> requestFactoryBuilder;

	private final ClientHttpRequestFactorySettings requestFactorySettings;

	private final ObjectProvider<SslBundles> sslBundles;

	public RestClientPropertiesHttpServiceGroupConfigurer(HttpClientProperties httpClientProperties,
			HttpInterfaceGroupsProperties clientGroupProperties,
			@Nullable ClientHttpRequestFactoryBuilder<?> requestFactoryBuilder,
			@Nullable ClientHttpRequestFactorySettings requestFactorySettings, ObjectProvider<SslBundles> sslBundles) {
		this.httpClientProperties = httpClientProperties;
		this.clientGroupProperties = clientGroupProperties;
		this.requestFactoryBuilder = (requestFactoryBuilder != null) ? requestFactoryBuilder
				: ClientHttpRequestFactoryBuilder.detect();
		this.requestFactorySettings = (requestFactorySettings != null) ? requestFactorySettings
				: ClientHttpRequestFactorySettings.defaults();
		this.sslBundles = sslBundles;
	}

	@Override
	public void configureGroups(Groups<Builder> groups) {
		groups.configureClient((group, builder) -> {
			HttpInterfaceGroupProperties clientGroupProperties = this.clientGroupProperties.getProperties(group.name());
			if (clientGroupProperties == null) {
				this.requestFactoryBuilder.build(this.requestFactorySettings);
				return;
			}
			if (clientGroupProperties.getBaseUrl() != null) {
				builder.baseUrl(clientGroupProperties.getBaseUrl());
			}
			Map<String, List<String>> defaultHeaders = clientGroupProperties.getDefaultHeaders();
			for (String headerName : defaultHeaders.keySet()) {
				builder.defaultHeader(headerName, defaultHeaders.get(headerName).toArray(String[]::new));
			}
			builder.requestFactory(buildRequestFactory(clientGroupProperties));
		});
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}

	private ClientHttpRequestFactory buildRequestFactory(HttpInterfaceGroupProperties clientGroupProperties) {
		ClientHttpRequestFactoryBuilder<?> requestFactoryBuilder = (clientGroupProperties.getFactory() != null)
				? clientGroupProperties.getFactory().builder() : this.requestFactoryBuilder;
		if (customRequestFactorySettings(clientGroupProperties)) {
			ClientHttpRequestFactorySettings requestFactorySettings = buildClientHttpRequestFactorySettings(
					clientGroupProperties);
			return requestFactoryBuilder.build(requestFactorySettings);
		}
		// Fall back ClientHttpRequestFactorySettings provided by the user
		// or created from HttpClientProperties
		return requestFactoryBuilder.build(this.requestFactorySettings);
	}

	private boolean customRequestFactorySettings(HttpInterfaceGroupProperties clientGroupProperties) {
		return clientGroupProperties.getRedirects() != null || clientGroupProperties.getConnectTimeout() != null
				|| clientGroupProperties.getReadTimeout() != null || clientGroupProperties.getSsl().getBundle() != null;
	}

	private ClientHttpRequestFactorySettings buildClientHttpRequestFactorySettings(
			HttpInterfaceGroupProperties clientGroupProperties) {
		// Rebuild entire request factory
		SslBundle sslBundle = getSslBundle(getSsl(clientGroupProperties), this.sslBundles);
		return new ClientHttpRequestFactorySettings(getRedirects(clientGroupProperties),
				getConnectTimeout(clientGroupProperties), getReadTimeout(clientGroupProperties), sslBundle);
	}

	private Ssl getSsl(HttpInterfaceGroupProperties clientGroupProperties) {
		if (clientGroupProperties.getSsl().getBundle() != null) {
			return clientGroupProperties.getSsl();
		}
		Ssl ssl = new Ssl();
		ssl.setBundle(clientGroupProperties.getSsl().getBundle());
		return ssl;
	}

	private @Nullable SslBundle getSslBundle(HttpInterfaceGroupProperties.Ssl properties,
			ObjectProvider<SslBundles> sslBundles) {
		String name = properties.getBundle();
		return (StringUtils.hasLength(name)) ? sslBundles.getObject().getBundle(name) : null;
	}

	private Redirects getRedirects(HttpInterfaceGroupProperties clientGroupProperties) {
		return (clientGroupProperties.getRedirects() != null) ? clientGroupProperties.getRedirects()
				: this.httpClientProperties.getRedirects();
	}

	private Duration getConnectTimeout(HttpInterfaceGroupProperties clientGroupProperties) {
		return (clientGroupProperties.getConnectTimeout() != null) ? clientGroupProperties.getConnectTimeout()
				: this.httpClientProperties.getConnectTimeout();
	}

	private Duration getReadTimeout(HttpInterfaceGroupProperties clientGroupProperties) {
		return (clientGroupProperties.getReadTimeout() != null) ? clientGroupProperties.getReadTimeout()
				: this.httpClientProperties.getReadTimeout();
	}

}
