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

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.core.Ordered;

/**
 * @author Olga Maciaszek-Sharma
 */
public interface InterfaceClientAdapter extends Ordered {

	String DEFAULT_QUALIFIER = "interfaceClients";

	<T> T createClient(ListableBeanFactory beanFactory, String clientName, Class<T> type,
			String httpProxyFactoryBeanName, String httpClientBeanName);

	boolean canCreateClient(String clientName);

}
