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

import java.util.List;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.util.StringValueResolver;
import org.springframework.web.service.invoker.HttpServiceArgumentResolver;

/**
 * @author Olga Maciaszek-Sharma
 */
// TODO: rename as not base for HttpProxyFactoryInterfaceClientAdapter?
abstract class AbstractHttpInterfaceClientAdapter extends AbstractInterfaceClientAdapter {

	protected List<HttpServiceArgumentResolver> customArgumentResolvers;

	protected ConversionService conversionService;

	protected StringValueResolver embeddedValueResolver;

	@Override
	protected void resolveDefaultDependencies(BeanFactory factory, String clientName) {
		if (this.customArgumentResolvers != null || this.conversionService != null
				|| this.embeddedValueResolver != null) {
			return;
		}
		this.customArgumentResolvers = resolveListDependency(factory, clientName, HttpServiceArgumentResolver.class);
		this.conversionService = resolveDependency(factory, clientName, ConversionService.class);
		this.embeddedValueResolver = resolveDependency(factory, clientName, StringValueResolver.class);
	}
}
