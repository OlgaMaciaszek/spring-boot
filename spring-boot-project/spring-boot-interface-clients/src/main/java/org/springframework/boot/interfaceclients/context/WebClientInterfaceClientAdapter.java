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

package org.springframework.boot.interfaceclients.context;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * @author Olga Maciaszek-Sharma
 */
public class WebClientInterfaceClientAdapter extends AbstractHttpInterfaceClientAdapter {

	private HttpServiceProxyFactory proxyFactory;

	private WebClient webClient;

	private WebClient.Builder webClientBuilder;

	@Override
	public <T> T createClient(BeanFactory beanFactory, String clientName, Class<T> type, String httpProxyFactoryBeanName, String httpClientBeanName) {
		if (this.proxyFactory == null) {
			this.proxyFactory = buildProxyFactory(beanFactory, clientName);
		}
		return this.proxyFactory.createClient(type);
	}


	// TODO: later move up
	private HttpServiceProxyFactory buildProxyFactory(BeanFactory beanFactory, String clientName) {
		resolveDefaultDependencies(beanFactory);
		WebClient webClient = resolveDependency(beanFactory, clientName, WebClient.class) != null
				? resolveDependency(beanFactory, clientName, WebClient.class) : this.webClient;
		WebClient.Builder webClientBuilder = resolveDependency(beanFactory, WebClient.Builder.class) != null
				? resolveDependency(beanFactory, WebClient.Builder.class) : this.webClientBuilder;
		Assert.isTrue(webClient != null || webClientBuilder != null,
				"No qualified WebClient or WebClient.Builder instance found.");
		// TODO: test with load-balanced WebClient.Builder
		// TODO: customise adapters
		WebClientAdapter webClientAdapter = webClient != null ?
				WebClientAdapter.create(webClient) : WebClientAdapter.create(webClientBuilder.build());
		HttpServiceProxyFactory.Builder builder = getServiceProxyFactoryBuilder(beanFactory, clientName);
		return builder.exchangeAdapter(webClientAdapter).build();
	}

	protected void resolveDefaultDependencies(BeanFactory factory, String clientName) {

	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE + 11;
	}

	@Override
	protected void resolveDefaultDependencies(BeanFactory factory) {
		if (this.webClient != null || this.webClientBuilder != null) {
			return;
		}
		this.webClient = resolveDependency(factory, WebClient.class);
		this.webClientBuilder = resolveDependency(factory, WebClient.Builder.class);
	}
}


