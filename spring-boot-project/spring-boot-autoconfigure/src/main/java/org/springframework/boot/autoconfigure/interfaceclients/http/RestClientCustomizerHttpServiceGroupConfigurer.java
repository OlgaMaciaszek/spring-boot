/*
 * Copyright 2012-2025 the original author or authors.
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

import java.util.List;

import org.springframework.boot.web.client.RestClientCustomizer;
import org.springframework.web.client.RestClient.Builder;
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer;

/**
 * @author Olga Maciaszek-Sharma
 * @author Phillip Webb
 */
public class RestClientCustomizerHttpServiceGroupConfigurer implements RestClientHttpServiceGroupConfigurer {

	private final List<RestClientCustomizer> customizers;

	public RestClientCustomizerHttpServiceGroupConfigurer(List<RestClientCustomizer> customizers) {
		this.customizers = customizers;
	}

	@Override
	public void configureGroups(Groups<Builder> groups) {
		groups.configureClient(builder -> {
			for (RestClientCustomizer customizer : this.customizers) {
				customizer.customize(builder);
			}
		});

	}

	// Allow plugging in  user-provided configurers
	// between the properties-based configurers and this one
	@Override
	public int getOrder() {
		return 0;
	}
}
