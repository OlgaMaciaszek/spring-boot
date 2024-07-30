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
import java.text.Normalizer;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.text.CaseUtils;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.interfaceclients.AbstractInterfaceClientsImportRegistrar;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * @author Olga Maciaszek-Sharma
 */
// TODO: Handle AOT
public class HttpInterfaceClientsImportRegistrar extends AbstractInterfaceClientsImportRegistrar {

	// TODO: work on IntelliJ plugin /other plugins/ to show that the client beans are
	//  autoconfigured
	private static final Log logger = LogFactory.getLog(HttpInterfaceClientsImportRegistrar.class);

	private static final String INTERFACE_CLIENT_SUFFIX = "InterfaceClient";

	private static final String BEAN_NAME_ATTRIBUTE_NAME = "beanName";

	@Override
	public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
		Assert.isInstanceOf(ListableBeanFactory.class, registry,
				"Registry must be an instance of " + ListableBeanFactory.class.getSimpleName());
		ListableBeanFactory beanFactory = (ListableBeanFactory) registry;
		Set<BeanDefinition> candidateComponents = discoverCandidateComponents(beanFactory);
		for (BeanDefinition candidateComponent : candidateComponents) {
			if (candidateComponent instanceof AnnotatedBeanDefinition beanDefinition) {
				AnnotationMetadata annotatedBeanMetadata = beanDefinition.getMetadata();
				Assert.isTrue(annotatedBeanMetadata.isInterface(),
						getAnnotation().getSimpleName() + "can only be placed on an interface.");
				MergedAnnotation<? extends Annotation> annotation = annotatedBeanMetadata.getAnnotations()
					.get(getAnnotation());
				String beanClassName = annotatedBeanMetadata.getClassName();
				Class<?> beanClass;
				try {
					beanClass = Class.forName(beanClassName);
				}
				catch (ClassNotFoundException e) {
					if (logger.isDebugEnabled()) {
						logger.debug("Class not found for interface client " + beanClassName + ": " + e.getMessage());
					}
					throw new RuntimeException(e);
				}
				// TODO: consider naming conventions: value of the annotation is the
				// qualifier to look for related beans
				// TODO: while the actual beanName corresponds to the simple class name
				// suffixed with InterfaceClient
				String clientId = annotation.getString(MergedAnnotation.VALUE);
				String beanName = !ObjectUtils.isEmpty(annotation.getString(BEAN_NAME_ATTRIBUTE_NAME))
						? annotation.getString(BEAN_NAME_ATTRIBUTE_NAME) : buildBeanName(clientId);
				HttpInterfaceClientsAdapter adapter = beanFactory.getBean(HttpInterfaceClientsAdapter.class);
				BeanDefinition definition = BeanDefinitionBuilder
					.rootBeanDefinition(ResolvableType.forClass(beanClass),
							() -> adapter.createClient(beanFactory, clientId, beanClass))
					.getBeanDefinition();
				registry.registerBeanDefinition(beanName, definition);

			}
		}
	}

	private String buildBeanName(String clientId) {
		// TODO: research Normalizer form types
		String normalised = Normalizer.normalize(clientId, Normalizer.Form.NFD);
		String camelCased = CaseUtils.toCamelCase(normalised, false, '-', '_');
		return camelCased + INTERFACE_CLIENT_SUFFIX;
	}

	@Override
	protected Class<? extends Annotation> getAnnotation() {
		return HttpClient.class;
	}

}
