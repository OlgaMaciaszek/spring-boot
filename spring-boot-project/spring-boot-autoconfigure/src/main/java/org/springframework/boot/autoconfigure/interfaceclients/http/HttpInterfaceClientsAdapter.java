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
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.interfaceclients.InterfaceClientsAdapter;
import org.springframework.web.service.invoker.HttpExchangeAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * @author Olga Maciaszek-Sharma
 */
public class HttpInterfaceClientsAdapter implements InterfaceClientsAdapter {

	private static final Log logger = LogFactory.getLog(HttpInterfaceClientsAdapter.class);

	private final HttpExchangeAdapterProvider adapterProvider;

	public HttpInterfaceClientsAdapter(HttpExchangeAdapterProvider adapterProvider) {
		this.adapterProvider = adapterProvider;
	}

	@Override
	public <T> T createClient(ConfigurableListableBeanFactory beanFactory, String clientId, Class<T> type) {
		HttpServiceProxyFactory proxyFactory = proxyFactory(beanFactory, clientId);

		return proxyFactory.createClient(type);
	}

	private HttpServiceProxyFactory proxyFactory(ConfigurableListableBeanFactory beanFactory, String clientId) {
		HttpServiceProxyFactory userProvidedProxyFactory = QualifiedBeanProvider.qualifiedBean(beanFactory,
				HttpServiceProxyFactory.class, clientId);
		if (userProvidedProxyFactory != null) {
			return userProvidedProxyFactory;
		}
		// create an HttpServiceProxyFactory bean with default implementation
		if (logger.isDebugEnabled()) {
			logger.debug("Creating HttpServiceProxyFactory for '" + clientId + "'");
		}
		HttpExchangeAdapter adapter = this.adapterProvider.get(beanFactory, clientId);
		return HttpServiceProxyFactory.builderFor(adapter).build();
	}

	// TODO: check if needed
	@Override
	public int getOrder() {
		throw new UnsupportedOperationException("Please, implement me.");
	}

}
