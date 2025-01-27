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

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.web.service.registry.HttpServiceProxyRegistry;

/**
 * @author Olga Maciaszek-Sharma
 */
public class HttpInterfaceClientsRegistrar implements ImportBeanDefinitionRegistrar {

	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		Assert.isInstanceOf(ListableBeanFactory.class, registry,
				"Registry must be an instance of " + ListableBeanFactory.class.getSimpleName());
		ListableBeanFactory beanFactory = (ListableBeanFactory) registry;
		// TODO - consider separate Registrars for various registries
		HttpServiceProxyRegistry.Builder registryBuilder = beanFactory.getBean(HttpServiceProxyRegistry.Builder.class);

		registryBuilder.discoverAndAddClients(AutoConfigurationPackages.get(beanFactory), clientBuilder -> { // dosth
		}, proxyFactoryBuilderConsumer -> {
			// do sth
		});

	}

}
