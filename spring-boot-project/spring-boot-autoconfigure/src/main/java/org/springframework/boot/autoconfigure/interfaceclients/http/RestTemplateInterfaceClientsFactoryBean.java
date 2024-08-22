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
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.support.RestTemplateAdapter;
import org.springframework.web.service.invoker.HttpExchangeAdapter;
import org.springframework.web.util.DefaultUriBuilderFactory;

/**
 * @author Olga Maciaszek-Sharma
 */
public class RestTemplateInterfaceClientsFactoryBean extends AbstractHttpInterfaceClientsFactoryBean {

	private static final Log logger = LogFactory.getLog(RestTemplateInterfaceClientsFactoryBean.class);

	@Override
	protected HttpExchangeAdapter exchangeAdapter() {
		String baseUrl = getBaseUrl();

		RestTemplate userProvidedRestTemplate = QualifiedBeanProvider
			.qualifiedBean(this.applicationContext.getBeanFactory(), RestTemplate.class, this.clientId);
		if (userProvidedRestTemplate != null) {
			// If the user wants to set the baseUrl directly on the builder,
			// it should not be set in properties.
			if (baseUrl != null) {
				userProvidedRestTemplate.setUriTemplateHandler(new DefaultUriBuilderFactory(baseUrl));
			}
			return RestTemplateAdapter.create(userProvidedRestTemplate);
		}

		RestTemplateBuilder userProvidedRestTemplateBuilder = QualifiedBeanProvider
			.qualifiedBean(this.applicationContext.getBeanFactory(), RestTemplateBuilder.class, this.clientId);
		if (userProvidedRestTemplateBuilder != null) {

			if (baseUrl != null) {
				userProvidedRestTemplateBuilder.rootUri(baseUrl);
			}
			return RestTemplateAdapter.create(userProvidedRestTemplateBuilder.build());
		}

		// create a RestTemplateAdapter bean with default implementation
		if (logger.isDebugEnabled()) {
			logger.debug("Creating RestTemplateAdapter for '" + this.clientId + "'");
		}
		RestTemplateBuilder builder = this.applicationContext.getBean(RestTemplateBuilder.class);
		RestTemplate restTemplate = builder.rootUri(baseUrl).build();
		return RestTemplateAdapter.create(restTemplate);
	}

}
