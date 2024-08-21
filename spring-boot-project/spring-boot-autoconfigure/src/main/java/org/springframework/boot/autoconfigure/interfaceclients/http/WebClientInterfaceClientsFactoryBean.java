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

import org.springframework.boot.autoconfigure.interfaceclients.QualifiedBeanProvider;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpExchangeAdapter;

/**
 * @author Olga Maciaszek-Sharma
 */
public class WebClientInterfaceClientsFactoryBean extends AbstractHttpInterfaceClientsFactoryBean {

	private static final Log logger = LogFactory.getLog(WebClientInterfaceClientsFactoryBean.class);

	@Override
	protected HttpExchangeAdapter exchangeAdapter() {
		String baseUrl = getBaseUrl();

		WebClient userProvidedWebClient = QualifiedBeanProvider.qualifiedBean(this.applicationContext.getBeanFactory(),
				WebClient.class, this.clientId);
		if (userProvidedWebClient != null) {
			// If the user wants to set the baseUrl directly on the builder,
			// it should not be set in properties.
			if (baseUrl != null) {
				userProvidedWebClient = userProvidedWebClient.mutate().baseUrl(baseUrl).build();
			}
			return WebClientAdapter.create(userProvidedWebClient);
		}

		WebClient.Builder userProvidedWebClientBuilder = QualifiedBeanProvider
			.qualifiedBean(this.applicationContext.getBeanFactory(), WebClient.Builder.class, this.clientId);
		if (userProvidedWebClientBuilder != null) {

			if (baseUrl != null) {
				userProvidedWebClientBuilder.baseUrl(baseUrl);
			}
			return WebClientAdapter.create(userProvidedWebClientBuilder.build());
		}

		// create a WebClientAdapter bean with default implementation
		if (logger.isDebugEnabled()) {
			logger.debug("Creating WebClientAdapter for '" + this.clientId + "'");
		}
		WebClient.Builder builder = this.applicationContext.getBean(WebClient.Builder.class);
		WebClient webClient = builder.baseUrl(baseUrl).build();
		return WebClientAdapter.create(webClient);
	}

}
