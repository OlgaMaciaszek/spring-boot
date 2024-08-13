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

import org.springframework.boot.autoconfigure.interfaceclients.AbstractInterfaceClientsFactoryBean;
import org.springframework.web.service.invoker.HttpExchangeAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * @author Olga Maciaszek-Sharma
 */
public abstract class AbstractHttpInterfaceClientsFactoryBean extends AbstractInterfaceClientsFactoryBean {

	private static final Log logger = LogFactory.getLog(AbstractHttpInterfaceClientsFactoryBean.class);

	@Override
	public Object getObject() throws Exception {
		HttpServiceProxyFactory proxyFactory = proxyFactory();
		return proxyFactory.createClient(this.type);
	}

	private HttpServiceProxyFactory proxyFactory() {
		HttpServiceProxyFactory userProvidedProxyFactory = QualifiedBeanProvider.qualifiedBean(this.applicationContext,
				HttpServiceProxyFactory.class, this.clientId);
		if (userProvidedProxyFactory != null) {
			return userProvidedProxyFactory;
		}
		// create an HttpServiceProxyFactory bean with default implementation
		if (logger.isDebugEnabled()) {
			logger.debug("Creating HttpServiceProxyFactory for '" + this.clientId + "'");
		}
		HttpExchangeAdapter adapter = exchangeAdapter();
		return HttpServiceProxyFactory.builderFor(adapter).build();
	}

	protected abstract HttpExchangeAdapter exchangeAdapter();

}
