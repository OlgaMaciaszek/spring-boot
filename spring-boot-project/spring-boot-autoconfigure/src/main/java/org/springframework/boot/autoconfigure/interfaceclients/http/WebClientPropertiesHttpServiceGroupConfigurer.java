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

import org.springframework.core.Ordered;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.Builder;
import org.springframework.web.reactive.function.client.support.WebClientHttpServiceGroupConfigurer;

/**
 * A {@link WebClientHttpServiceGroupConfigurer} that configures the group and its
 * underlying {@link WebClient .Builder} using property values.
 *
 * @author Olga Maciaszek-Sharma
 * @since 4.0.0
 */
public class WebClientPropertiesHttpServiceGroupConfigurer implements WebClientHttpServiceGroupConfigurer, Ordered {

	private final HttpInterfaceGroupsProperties clientGroupProperties;

	public WebClientPropertiesHttpServiceGroupConfigurer(HttpInterfaceGroupsProperties clientGroupProperties) {
		this.clientGroupProperties = clientGroupProperties;
	}

	@Override
	public void configureGroups(Groups<Builder> groups) {
		groups.configureClient((group, builder) -> {
			HttpInterfaceGroupProperties clientGroupProperties = this.clientGroupProperties.getProperties(group.name());
			if (clientGroupProperties == null) {
				return;
			}
			if (clientGroupProperties.getBaseUrl() != null) {
				builder.baseUrl(clientGroupProperties.getBaseUrl());
			}
			Map<String, List<String>> defaultHeaders = clientGroupProperties.getDefaultHeaders();
			for (String headerName : defaultHeaders.keySet()) {
				builder.defaultHeader(headerName, defaultHeaders.get(headerName).toArray(String[]::new));
			}
		});
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}

}
