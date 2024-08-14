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
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpExchangeAdapter;

/**
 * @author Olga Maciaszek-Sharma
 */
public class RestClientAdapterProvider implements HttpExchangeAdapterProvider {

	private static final Log logger = LogFactory.getLog(RestClientAdapterProvider.class);

	private final RestClient.Builder builder;

	private final ObjectProvider<HttpInterfaceClientsProperties> propertiesProvider;

	public RestClientAdapterProvider(RestClient.Builder builder,
			ObjectProvider<HttpInterfaceClientsProperties> propertiesProvider) {
		this.builder = builder;
		this.propertiesProvider = propertiesProvider;
	}

	@Override
	public HttpExchangeAdapter get(ConfigurableListableBeanFactory beanFactory, String clientId) {
		RestClient userProvidedRestClient = QualifiedBeanProvider.qualifiedBean(beanFactory, RestClient.class,
				clientId);
		if (userProvidedRestClient != null) {
			return RestClientAdapter.create(userProvidedRestClient);
		}
		HttpInterfaceClientsProperties properties = this.propertiesProvider.getObject();
		String baseUrl = properties.getProperties(clientId).getBaseUrl();
		RestClient.Builder userProvidedRestClientBuilder = QualifiedBeanProvider.qualifiedBean(beanFactory,
				RestClient.Builder.class, clientId);
		if (userProvidedRestClientBuilder != null) {
			// If the user wants to set the baseUrl directly on the builder,
			// it should not be set in properties.
			if (baseUrl != null) {
				userProvidedRestClientBuilder.baseUrl(baseUrl);
			}
			return RestClientAdapter.create(userProvidedRestClientBuilder.build());
		}
		// create a RestClientAdapter bean with default implementation
		if (logger.isDebugEnabled()) {
			logger.debug("Creating RestClientAdapter for '" + clientId + "'");
		}
		RestClient restClient = this.builder.baseUrl(baseUrl).build();
		return RestClientAdapter.create(restClient);
	}

}
