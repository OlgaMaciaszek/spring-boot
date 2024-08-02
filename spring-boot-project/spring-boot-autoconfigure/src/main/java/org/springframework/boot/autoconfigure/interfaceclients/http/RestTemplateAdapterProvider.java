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
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.support.RestTemplateAdapter;
import org.springframework.web.service.invoker.HttpExchangeAdapter;

/**
 * @author Olga Maciaszek-Sharma
 */
public class RestTemplateAdapterProvider implements HttpExchangeAdapterProvider {

	private static final Log logger = LogFactory.getLog(RestTemplateAdapterProvider.class);

	private final RestTemplateBuilder restTemplateBuilder;

	private final ObjectProvider<HttpInterfaceClientsProperties> propertiesProvider;

	public RestTemplateAdapterProvider(RestTemplateBuilder restTemplateBuilder,
			ObjectProvider<HttpInterfaceClientsProperties> propertiesProvider) {
		this.restTemplateBuilder = restTemplateBuilder;
		this.propertiesProvider = propertiesProvider;
	}

	@Override
	public HttpExchangeAdapter get(ListableBeanFactory beanFactory, String clientId) {
		RestTemplate userProvidedRestTemplate = QualifiedBeanProvider.qualifiedBean(beanFactory, RestTemplate.class,
				clientId);
		if (userProvidedRestTemplate != null) {
			return RestTemplateAdapter.create(userProvidedRestTemplate);
		}
		HttpInterfaceClientsProperties properties = this.propertiesProvider.getObject();
		String baseUrl = properties.getProperties(clientId).getBaseUrl();
		RestTemplateBuilder userProvidedRestTemplateBuilder = QualifiedBeanProvider.qualifiedBean(beanFactory,
				RestTemplateBuilder.class, clientId);
		if (userProvidedRestTemplateBuilder != null) {
			// If the user wants to set the baseUrl directly on the builder,
			// it should not be set in properties.
			if (baseUrl != null) {
				userProvidedRestTemplateBuilder.rootUri(baseUrl);
			}
			return RestTemplateAdapter.create(userProvidedRestTemplateBuilder.build());
		}
		// create a RestTemplateAdapter bean with default implementation
		if (logger.isDebugEnabled()) {
			logger.debug("Creating RestTemplateAdapter for '" + clientId + "'");
		}
		RestTemplate restTemplate = this.restTemplateBuilder.rootUri(baseUrl).build();
		return RestTemplateAdapter.create(restTemplate);
	}

}
