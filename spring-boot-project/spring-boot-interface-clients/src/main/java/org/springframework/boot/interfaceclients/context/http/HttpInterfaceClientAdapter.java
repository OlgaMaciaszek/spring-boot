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

package org.springframework.boot.interfaceclients.context.http;

import java.util.Optional;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.boot.interfaceclients.context.InterfaceClientAdapter;
import org.springframework.web.service.invoker.HttpExchangeAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * @author Olga Maciaszek-Sharma
 */
public class HttpInterfaceClientAdapter implements InterfaceClientAdapter {

	private final HttpExchangeAdapterProvider adapterProvider;

	public HttpInterfaceClientAdapter(HttpExchangeAdapterProvider adapterProvider) {
		this.adapterProvider = adapterProvider;
	}


	// TODO: get bean names and base url from properties per client
	@Override
	public <T> T createClient(ListableBeanFactory beanFactory, String clientName, Class<T> type,
			String httpProxyFactoryBeanName, String httpExchangeAdapterBeanName) {
		// Allow using different proxyFactory instances for different client beans
		HttpServiceProxyFactory proxyFactory = proxyFactory(beanFactory, clientName,
				httpProxyFactoryBeanName, httpExchangeAdapterBeanName);

		return proxyFactory.createClient(type);
	}

	private HttpServiceProxyFactory proxyFactory(ListableBeanFactory beanFactory,
			String clientName, String httpProxyFactoryBeanName, String httpExchangeAdapterBeanName) {
		// we assume that if the user has specified the bean name, the bean is required
		// try getting HttpServiceProxyFactory bean specified by the user
		if (!httpProxyFactoryBeanName.isEmpty()) {
			return Optional.of(beanFactory.getBeansOfType(HttpServiceProxyFactory.class)
					.get(httpProxyFactoryBeanName)).orElseThrow(() ->
					new IllegalArgumentException("There is no HttpServiceProxyFactory bean with name '"
							+ httpProxyFactoryBeanName + "'"));
		}
		// create an HttpServiceProxyFactory bean with default implementation
		HttpExchangeAdapter adapter = exchangeAdapter(beanFactory, clientName, httpExchangeAdapterBeanName);
		return HttpServiceProxyFactory.builderFor(adapter).build();
	}

	private HttpExchangeAdapter exchangeAdapter(ListableBeanFactory beanFactory, String clientName, String httpExchangeAdapterBeanName) {
		// we assume that if the user has specified the bean name, the bean is required
		// try getting HttpExchangeAdapter bean specified by the user
		if (!httpExchangeAdapterBeanName.isEmpty()) {
			return Optional.of(beanFactory.getBeansOfType(HttpExchangeAdapter.class)
					.get(httpExchangeAdapterBeanName)).orElseThrow(() -> new IllegalArgumentException(
					"There is no HttpExchangeAdapter bean with name '" + httpExchangeAdapterBeanName
							+ "'"));
		}
		// create an HttpExchangeAdapter bean with default implementation
		return this.adapterProvider.get(clientName);
	}

	@Override
	public int getOrder() {
		throw new UnsupportedOperationException("Please, implement me.");
	}
}
