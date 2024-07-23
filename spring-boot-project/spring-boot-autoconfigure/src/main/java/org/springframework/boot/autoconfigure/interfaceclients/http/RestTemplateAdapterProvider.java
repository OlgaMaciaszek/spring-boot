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

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.support.RestTemplateAdapter;
import org.springframework.web.service.invoker.HttpExchangeAdapter;

/**
 * @author Olga Maciaszek-Sharma
 */
public class RestTemplateAdapterProvider implements HttpExchangeAdapterProvider {

	private final RestTemplateBuilder restTemplateBuilder;

	private final HttpInterfaceClientsProperties properties;

	public RestTemplateAdapterProvider(RestTemplateBuilder restTemplateBuilder, HttpInterfaceClientsProperties properties) {
		this.restTemplateBuilder = restTemplateBuilder;
		this.properties = properties;
	}

	@Override
	public HttpExchangeAdapter get(String clientName) {
		RestTemplate restTemplate = this.restTemplateBuilder
				.rootUri(this.properties.getProperties(clientName).getBaseUrl())
				.build();
		return RestTemplateAdapter.create(restTemplate);
	}
}
