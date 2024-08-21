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

import java.lang.annotation.Annotation;

import org.springframework.boot.autoconfigure.interfaceclients.AbstractInterfaceClientsImportRegistrar;

/**
 * @author Olga Maciaszek-Sharma
 */
public class RestTemplateInterfaceClientsImportRegistrar extends AbstractInterfaceClientsImportRegistrar {

	@Override
	protected Class<? extends Annotation> getAnnotation() {
		return HttpClient.class;
	}

	@Override
	protected Class<?> getFactoryBeanClass() {
		return RestTemplateInterfaceClientsFactoryBean.class;
	}

}
