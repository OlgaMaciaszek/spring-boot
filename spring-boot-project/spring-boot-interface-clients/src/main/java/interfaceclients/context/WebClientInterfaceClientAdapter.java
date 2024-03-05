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

package interfaceclients.context;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.Ordered;
import org.springframework.util.Assert;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.service.invoker.HttpServiceArgumentResolver;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * @author Olga Maciaszek-Sharma
 */
public class WebClientInterfaceClientAdapter extends AbstractHttpInterfaceClientAdapter {

	private HttpServiceProxyFactory proxyFactory;

	private WebClient webClient;

	private WebClient.Builder webClientBuilder;

	@Override
	public <T> T createClient(BeanFactory factory, String clientName, Class<T> type) {
		if (this.proxyFactory == null) {
			this.proxyFactory = buildProxyFactory(factory, clientName);
		}
		return this.proxyFactory.createClient(type);
	}


	// TODO: later move up
	private HttpServiceProxyFactory buildProxyFactory(BeanFactory factory, String clientName) {
		resolveDefaultDependencies(factory, clientName);
		Assert.isTrue(this.webClient != null || this.webClientBuilder != null,
				"No qualified WebClient or WebClient.Builder instance found.");
		// TODO: test with load-balanced WebClient.Builder
		// TODO: customise adapters
		WebClientAdapter webClientAdapter = this.webClient != null ?
				WebClientAdapter.create(this.webClient) : WebClientAdapter.create(this.webClientBuilder.build());
		HttpServiceProxyFactory.Builder builder = HttpServiceProxyFactory.builderFor(webClientAdapter);
		for (HttpServiceArgumentResolver resolver : this.customArgumentResolvers) {
			builder.customArgumentResolver(resolver);
		}
		return builder.conversionService(this.conversionService).embeddedValueResolver(this.embeddedValueResolver)
				.build();
	}

	@Override
	public boolean canCreateClient(BeanFactory factory, String clientName) {
		resolveDefaultDependencies(factory, clientName);
		return this.webClient != null || this.webClientBuilder != null;
	}

	protected void resolveDefaultDependencies(BeanFactory factory, String clientName) {
		if (this.webClient != null || this.webClientBuilder != null) {
			return;
		}
		this.webClient = resolveDependency(factory, clientName, WebClient.class);
		this.webClientBuilder = resolveDependency(factory, clientName, WebClient.Builder.class);
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE + 11;
	}
}


