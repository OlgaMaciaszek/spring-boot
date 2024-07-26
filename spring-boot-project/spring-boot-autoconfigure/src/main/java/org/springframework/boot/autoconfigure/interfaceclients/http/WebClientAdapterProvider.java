/*
 * Copyright 2012-2024 the original author or authors.
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpExchangeAdapter;

/**
 * @author Olga Maciaszek-Sharma
 */
public class WebClientAdapterProvider implements HttpExchangeAdapterProvider {

	private static final Log logger = LogFactory.getLog(WebClientAdapterProvider.class);

	private final WebClient.Builder builder;

	private final HttpInterfaceClientsProperties properties;

	public WebClientAdapterProvider(WebClient.Builder builder, HttpInterfaceClientsProperties properties) {
		this.builder = builder;
		this.properties = properties;
	}

	@Override
	public HttpExchangeAdapter get(ListableBeanFactory beanFactory, String clientName) {
		WebClient userProvidedWebClient = QualifiedBeanProvider.qualifiedBean(beanFactory, WebClient.class, clientName);
		if (userProvidedWebClient != null) {
			return WebClientAdapter.create(userProvidedWebClient);
		}
		WebClient.Builder userProvidedWebClientBuilder = QualifiedBeanProvider.qualifiedBean(beanFactory,
				WebClient.Builder.class, clientName);
		if (userProvidedWebClientBuilder != null) {
			// TODO: should we do this or get it from the user?
			userProvidedWebClientBuilder.baseUrl(this.properties.getProperties(clientName).getBaseUrl());
			return WebClientAdapter.create(userProvidedWebClientBuilder.build());
		}
		// create a WebClientAdapter bean with default implementation
		if (logger.isDebugEnabled()) {
			logger.debug("Creating WebClientAdapter for '" + clientName + "'");
		}
		WebClient webClient = this.builder.baseUrl(this.properties.getProperties(clientName).getBaseUrl()).build();
		return WebClientAdapter.create(webClient);
	}

}
