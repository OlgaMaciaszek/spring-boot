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

import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigurationPackages;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.ResolvableType;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.web.service.registry.HttpServiceProxyGroup;
import org.springframework.web.service.registry.HttpServiceProxyRegistry;
import org.springframework.web.service.registry.InterfaceClientData;

/**
 * @author Olga Maciaszek-Sharma
 */
public class HttpInterfaceClientsRegistrar<CB> implements ImportBeanDefinitionRegistrar {

	@SuppressWarnings("unchecked")
	@Override
	public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
		Assert.isInstanceOf(ListableBeanFactory.class, registry,
				"Registry must be an instance of " + ListableBeanFactory.class.getSimpleName());
		ListableBeanFactory beanFactory = (ListableBeanFactory) registry;
		HttpServiceProxyRegistry.Builder<?, CB> registryBuilder = beanFactory
			.getBean(HttpServiceProxyRegistry.Builder.class);

		Set<InterfaceClientData> clientData = registryBuilder
			.discoverClients(AutoConfigurationPackages.get(beanFactory));

		InterfaceClientsBuilderConfigurer<CB> configurer = beanFactory.getBean(InterfaceClientsBuilderConfigurer.class);

		for (InterfaceClientData interfaceClientData : clientData) {
			registryBuilder.addClient(interfaceClientData, configurer.buildClientConsumer(interfaceClientData.name()));
		}

		HttpServiceProxyRegistry interfaceClientRegistry = registryBuilder.build();
		registerBeanDefinition(registry, "httpInterfaceClientRegistry", HttpServiceProxyRegistry.class,
				interfaceClientRegistry);

		for (HttpServiceProxyGroup clientGroup : interfaceClientRegistry.getProxyGroups()) {
			Map<Class<?>, Object> proxies = clientGroup.proxies();
			for (Class<?> proxyClass : proxies.keySet()) {
				// TODO: * create better bean names from urls?
				String beanName = clientGroup.name() + proxyClass.getSimpleName();
				registerBeanDefinition(registry, beanName, proxyClass, proxies.get(proxyClass));
			}
		}

	}

	private static void registerBeanDefinition(BeanDefinitionRegistry registry, String beanName, Class<?> beanClass,
			Object object) {
		BeanDefinition definition = BeanDefinitionBuilder
			.rootBeanDefinition(ResolvableType.forClass(beanClass), () -> object)
			.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE)
			.getBeanDefinition();
		BeanDefinitionHolder holder = new BeanDefinitionHolder(definition, beanName);
		BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
	}

}
