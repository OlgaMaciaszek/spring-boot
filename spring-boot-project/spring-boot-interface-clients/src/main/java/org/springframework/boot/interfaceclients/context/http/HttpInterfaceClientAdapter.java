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
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * @author Olga Maciaszek-Sharma
 */
public class HttpInterfaceClientAdapter implements InterfaceClientAdapter {


	@Override
	public <T> T createClient(ListableBeanFactory beanFactory, String clientName, Class<T> type,
			String httpProxyFactoryBeanName, String httpClientBeanName) {
		// Allow using different proxyFactory instances for different client beans
		HttpServiceProxyFactory proxyFactory = proxyFactory(beanFactory, httpProxyFactoryBeanName);

		// TODO: autoconfigure webclient/ resttamplate/ restclient resolvers (all implementing
		//  HttpClientResolver), depending on the classpath, allowing for reordering

		throw new UnsupportedOperationException("Please, implement me.");
	}

	private static HttpServiceProxyFactory proxyFactory(ListableBeanFactory beanFactory, String httpProxyFactoryBeanName) {
		if (!httpProxyFactoryBeanName.isEmpty()) {
			return Optional.of(beanFactory.getBeansOfType(HttpServiceProxyFactory.class)
					.get(httpProxyFactoryBeanName)).orElseThrow(() ->
					new IllegalArgumentException("There is no HttpServiceProxyFactory bean with name '"
							+ httpProxyFactoryBeanName + "'"));
		}
		// TODO: rethink relying on naming
		// TODO: create a default in AutoConfiguration
		return beanFactory.getBeansOfType(HttpServiceProxyFactory.class)
				.get("defaultHttpServiceProxyFactory");
	}


	@Override
	public boolean canCreateClient(String clientName) {
		throw new UnsupportedOperationException("Please, implement me.");
	}

	@Override
	public int getOrder() {
		throw new UnsupportedOperationException("Please, implement me.");
	}
}
