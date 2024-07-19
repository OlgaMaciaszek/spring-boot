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

import java.util.List;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.StringValueResolver;
import org.springframework.web.service.invoker.HttpServiceArgumentResolver;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

/**
 * @author Olga Maciaszek-Sharma
 */
// TODO: rename as not base for HttpProxyFactoryInterfaceClientAdapter?
abstract class AbstractHttpInterfaceClientAdapter extends AbstractInterfaceClientAdapter {

	private List<HttpServiceArgumentResolver> customArgumentResolvers;

	private ConversionService conversionService;

	private StringValueResolver embeddedValueResolver;

	@Override
	protected void resolveDefaultDependencies(BeanFactory factory) {
		// TODO: this is not good enough - ensure init once at the beginning, check if constructor would not be too early
		if (this.customArgumentResolvers != null || this.conversionService != null
				|| this.embeddedValueResolver != null) {
			return;
		}
		this.customArgumentResolvers = resolveListDependency(factory, HttpServiceArgumentResolver.class);
		this.conversionService = resolveDependency(factory, ConversionService.class);
		this.embeddedValueResolver = resolveDependency(factory, StringValueResolver.class);
	}

	protected HttpServiceProxyFactory.Builder getServiceProxyFactoryBuilder(BeanFactory beanFactory, String clientName) {
		resolveDefaultDependencies(beanFactory);
		List<HttpServiceArgumentResolver> customArgumentResolvers = !resolveListDependency(beanFactory, clientName, HttpServiceArgumentResolver.class).isEmpty()
				? resolveListDependency(beanFactory, clientName, HttpServiceArgumentResolver.class) :
				this.customArgumentResolvers;
		ConversionService conversionService = resolveDependency(beanFactory, clientName, ConversionService.class) != null
				? resolveDependency(beanFactory, clientName, ConversionService.class) : this.conversionService;
		StringValueResolver embeddedValueResolver = resolveDependency(beanFactory, clientName, StringValueResolver.class) != null
				? resolveDependency(beanFactory, clientName, StringValueResolver.class) : this.embeddedValueResolver;
		HttpServiceProxyFactory.Builder builder = HttpServiceProxyFactory.builder();
		for (HttpServiceArgumentResolver resolver : customArgumentResolvers) {
			builder.customArgumentResolver(resolver);
		}
		builder.conversionService(conversionService)
				.embeddedValueResolver(embeddedValueResolver);
		return builder;
	}
}
