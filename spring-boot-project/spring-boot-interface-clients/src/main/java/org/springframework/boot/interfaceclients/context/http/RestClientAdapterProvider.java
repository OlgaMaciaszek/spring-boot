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

import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpExchangeAdapter;

/**
 * @author Olga Maciaszek-Sharma
 */
public class RestClientAdapterProvider implements HttpExchangeAdapterProvider {

	private final RestClient.Builder builder;

	private final HttpInterfaceClientsProperties properties;

	public RestClientAdapterProvider(RestClient.Builder builder, HttpInterfaceClientsProperties properties) {
		this.builder = builder;
		this.properties = properties;
	}

	@Override
	public HttpExchangeAdapter get(String clientName) {
		RestClient restClient = this.builder
				// TODO: get baseUrl per client
				.baseUrl(this.properties.getBaseUrl())
				.build();
		return RestClientAdapter.create(restClient);
	}
}
