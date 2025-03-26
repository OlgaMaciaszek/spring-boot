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

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.client.RestClientAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.function.client.WebClientAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.support.WebClientAdapter;
import org.springframework.web.reactive.function.client.support.WebClientHttpServiceGroupConfigurer;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.service.registry.ImportHttpServices;

/**
 * {@link EnableAutoConfiguration Auto-configuration} for HTTP Interface Clients.
 * <p>
 * FIXME This will result in the creation of Interface Client beans defined by
 * {@link ImportHttpServices} annotations.
 *
 * @author Olga Maciaszek-Sharma
 * @since 4.0.0
 */
@AutoConfiguration(after = { RestClientAutoConfiguration.class, WebClientAutoConfiguration.class })
@ConditionalOnProperty(value = "spring.interface-clients.enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(HttpInterfaceGroupsProperties.class)
public class HttpInterfaceClientsAutoConfiguration {

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass({ RestClient.class, RestClientAdapter.class, HttpServiceProxyFactory.class })
	@ConditionalOnMissingClass("org.springframework.web.reactive.function.client.WebClient")
	protected static class RestClientInterfaceClientsConfiguration {

		@Bean
		RestClientPropertyBasedHttpServiceGroupConfigurer restClientPropertyBasedHttpServiceGroupConfigurer(
				HttpInterfaceGroupsProperties properties) {
			return new RestClientPropertyBasedHttpServiceGroupConfigurer(properties);
		}

	}

	@Configuration(proxyBeanMethods = false)
	@ConditionalOnClass({ WebClient.class, WebClientAdapter.class, HttpServiceProxyFactory.class })
	protected static class WebClientInterfaceClientsConfiguration {

		@Bean
		WebClientHttpServiceGroupConfigurer webClientHttpServiceGroupConfigurer(
				HttpInterfaceGroupsProperties properties) {
			return new WebClientPropertyBasedConfigurer(properties);
		}

	}

}
