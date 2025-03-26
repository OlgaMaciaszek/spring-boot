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

import java.util.List;
import java.util.Map;

import org.springframework.boot.http.client.ClientHttpRequestFactoryBuilder;
import org.springframework.boot.http.client.ClientHttpRequestFactorySettings;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.web.client.RestClient.Builder;
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer;

// TODO: add corresponding WebClient-based implementation

/**
 * @author Olga Maciaszek-Sharma
 */
public class RestClientPropertyBasedHttpServiceGroupConfigurer implements RestClientHttpServiceGroupConfigurer {

	private final HttpInterfaceGroupsProperties properties;

	public RestClientPropertyBasedHttpServiceGroupConfigurer(HttpInterfaceGroupsProperties properties) {
		this.properties = properties;
	}

	private ClientHttpRequestFactory buildClientHttpRequestFactory(HttpInterfaceGroupProperties clientProperties) {
		// FIXME - retrieve default Boot Builder level settings
		// FIXME - handle other properties
		ClientHttpRequestFactorySettings factorySettings = ClientHttpRequestFactorySettings.defaults()
			.withConnectTimeout(clientProperties.getConnectTimeout())
			.withReadTimeout(clientProperties.getReadTimeout());
		return ClientHttpRequestFactoryBuilder.detect().build(factorySettings);
	}

	@Override
	public void configureGroups(Groups<Builder> groups) {
		groups.configureClient((group, builder) -> {
			HttpInterfaceGroupProperties clientGroupProperties = this.properties.getProperties(group.name());
			if (clientGroupProperties == null) {
				return;
			}
			if (clientGroupProperties.getBaseUrl() != null) {
				builder.baseUrl(clientGroupProperties.getBaseUrl());
			}
			builder.requestFactory(buildClientHttpRequestFactory(clientGroupProperties));
			Map<String, List<String>> defaultHeaders = clientGroupProperties.getDefaultHeaders();
			for (String headerName : defaultHeaders.keySet()) {
				builder.defaultHeader(headerName, defaultHeaders.get(headerName).toArray(String[]::new));
			}
		});

	}

}
