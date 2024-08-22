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

package org.springframework.boot.autoconfigure.interfaceclients.rsocket;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.boot.autoconfigure.interfaceclients.AbstractInterfaceClientsFactoryBean;
import org.springframework.boot.autoconfigure.interfaceclients.QualifiedBeanProvider;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.service.RSocketServiceProxyFactory;

/**
 * @author Olga Maciaszek-Sharma
 */
public class RSocketInterfaceClientsFactoryBean extends AbstractInterfaceClientsFactoryBean {

	private static final Log logger = LogFactory.getLog(RSocketInterfaceClientsFactoryBean.class);

	@Override
	public Object getObject() throws Exception {
		RSocketServiceProxyFactory factory = proxyFactory();
		return factory.createClient(this.type);
	}

	private RSocketServiceProxyFactory proxyFactory() {
		RSocketServiceProxyFactory userProvidedProxyFactory = QualifiedBeanProvider
			.qualifiedBean(this.applicationContext.getBeanFactory(), RSocketServiceProxyFactory.class, this.clientId);
		if (userProvidedProxyFactory != null) {
			return userProvidedProxyFactory;
		}
		// create an RSocketServiceProxyFactory bean with default implementation
		if (logger.isDebugEnabled()) {
			logger.debug("Creating RSocketServiceProxyFactory for '" + this.clientId + "'");
		}
		RSocketRequester requester = rsocketRequester();
		return RSocketServiceProxyFactory.builder(requester).build();
	}

	private RSocketRequester rsocketRequester() {
		RSocketRequester userProvidedRequester = QualifiedBeanProvider
			.qualifiedBean(this.applicationContext.getBeanFactory(), RSocketRequester.class, this.clientId);
		if (userProvidedRequester != null) {
			return userProvidedRequester;
		}

		RSocketRequester.Builder userProvidedRSocketRequesterBuilder = QualifiedBeanProvider
			.qualifiedBean(this.applicationContext.getBeanFactory(), RSocketRequester.Builder.class, this.clientId);
		if (userProvidedRSocketRequesterBuilder != null) {
			return toRequester(userProvidedRSocketRequesterBuilder);
		}

		// create an RSocketRequester bean with default implementation
		if (logger.isDebugEnabled()) {
			logger.debug("Creating RSocketRequester for '" + this.clientId + "'");
		}
		RSocketRequester.Builder builder = this.applicationContext.getBean(RSocketRequester.Builder.class);
		return toRequester(builder);
	}

	private RSocketRequester toRequester(RSocketRequester.Builder requesterBuilder) {
		// If the user wants to set the baseUrl directly on the builder,
		// it should not be set in properties.
		RSocketInterfaceClientsProperties properties = this.applicationContext
			.getBean(RSocketInterfaceClientsProperties.class);
		if (properties.getHost() != null && properties.getPort() != null) {
			return requesterBuilder.tcp(properties.getHost(), properties.getPort());
		}
		if (properties.getUri() != null) {
			return requesterBuilder.websocket(properties.getUri());
		}
		throw new IllegalArgumentException("No Host and Port or URI provided in RSocketInterfaceClientsProperties");
	}

}
