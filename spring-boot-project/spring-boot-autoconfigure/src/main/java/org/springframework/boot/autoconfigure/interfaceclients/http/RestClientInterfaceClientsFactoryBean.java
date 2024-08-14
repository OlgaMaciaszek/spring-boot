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

import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpExchangeAdapter;

/**
 * @author Olga Maciaszek-Sharma
 */
public class RestClientInterfaceClientsFactoryBean extends AbstractHttpInterfaceClientsFactoryBean {

	private static final Log logger = LogFactory.getLog(RestClientInterfaceClientsFactoryBean.class);

	@Override
	protected HttpExchangeAdapter exchangeAdapter() {
		HttpInterfaceClientsProperties properties = this.applicationContext
			.getBean(HttpInterfaceClientsProperties.class);
		// If the user wants to set the baseUrl directly on the builder,
		// it should not be set in properties.
		String baseUrl = properties.getProperties(this.clientId).getBaseUrl();

		RestClient userProvidedRestClient = QualifiedBeanProvider
			.qualifiedBean(this.applicationContext.getBeanFactory(), RestClient.class, this.clientId);
		if (userProvidedRestClient != null) {
			if (baseUrl != null) {
				userProvidedRestClient = userProvidedRestClient.mutate().baseUrl(baseUrl).build();
			}
			return RestClientAdapter.create(userProvidedRestClient);
		}

		RestClient.Builder userProvidedRestClientBuilder = QualifiedBeanProvider
			.qualifiedBean(this.applicationContext.getBeanFactory(), RestClient.Builder.class, this.clientId);
		if (userProvidedRestClientBuilder != null) {

			if (baseUrl != null) {
				userProvidedRestClientBuilder.baseUrl(baseUrl);
			}
			return RestClientAdapter.create(userProvidedRestClientBuilder.build());
		}

		// create a RestClientAdapter bean with default implementation
		if (logger.isDebugEnabled()) {
			logger.debug("Creating RestClientAdapter for '" + this.clientId + "'");
		}
		RestClient.Builder builder = this.applicationContext.getBean(RestClient.Builder.class);
		RestClient restClient = builder.baseUrl(baseUrl).build();
		return RestClientAdapter.create(restClient);
	}

}
