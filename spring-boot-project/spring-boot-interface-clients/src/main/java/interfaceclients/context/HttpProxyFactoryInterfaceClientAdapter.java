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
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * @author Olga Maciaszek-Sharma
 */
public class HttpProxyFactoryInterfaceClientAdapter extends AbstractInterfaceClientAdapter {

	private HttpServiceProxyFactory proxyFactory;

	@Override
	public <T> T createClient(BeanFactory factory, String clientName, Class<T> type) {
		resolveDefaultDependencies(factory);
		HttpServiceProxyFactory proxyFactory = resolveDependency(factory, clientName,
				HttpServiceProxyFactory.class);
		return proxyFactory != null ? proxyFactory.createClient(type) :
				this.proxyFactory.createClient(type);
	}

	@Override
	public boolean canCreateClient(BeanFactory factory, String clientName) {
		resolveDefaultDependencies(factory);
		HttpServiceProxyFactory proxyFactory = resolveDependency(factory, clientName,
				HttpServiceProxyFactory.class);
		return this.proxyFactory != null || proxyFactory != null;
	}

	@Override
	protected void resolveDefaultDependencies(BeanFactory factory) {
		if (this.proxyFactory != null) {
			return;
		}
		this.proxyFactory = resolveDependency(factory, HttpServiceProxyFactory.class);
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE + 10;
	}
}
