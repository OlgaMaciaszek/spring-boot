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

package org.springframework.boot.interfaceclients.context.http;

import java.lang.annotation.Annotation;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.interfaceclients.context.AbstractInterfaceClientsImportRegistrar;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * @author Olga Maciaszek-Sharma
 */
// TODO: move over to spring-boot-autoconfigure
// TODO: Handle AOT
public class HttpInterfaceClientsImportRegistrar extends AbstractInterfaceClientsImportRegistrar {


	private static final Log logger = LogFactory.getLog(HttpInterfaceClientsImportRegistrar.class);

	@Override
	public void registerBeanDefinitions(AnnotationMetadata metadata, BeanDefinitionRegistry registry) {
		Assert.isInstanceOf(ListableBeanFactory.class, registry, "Registry must be an instance of "
				+ ListableBeanFactory.class.getSimpleName());
		Set<BeanDefinition> candidateComponents = discoverCandidateComponents(metadata);
		for (BeanDefinition candidateComponent : candidateComponents) {
			if (candidateComponent instanceof AnnotatedBeanDefinition beanDefinition) {
				AnnotationMetadata annotationMetadata = beanDefinition.getMetadata();
				Assert.isTrue(annotationMetadata.isInterface(), getAnnotation().getSimpleName()
						+ "can only be placed on an interface.");
				MergedAnnotation<? extends Annotation> annotation = annotationMetadata.getAnnotations()
						.get(getAnnotation());
				String beanClassName = annotationMetadata.getClassName();
				Class<?> beanClass;
				try {
					beanClass = Class.forName(beanClassName);
				}
				catch (ClassNotFoundException e) {
					if (logger.isDebugEnabled()) {
						logger.debug("Class not found for interface client "
								+ beanClassName + ": " + e.getMessage());
					}
					throw new RuntimeException(e);
				}
				String clientName = !ObjectUtils.isEmpty(annotation.getString(MergedAnnotation.VALUE))
						? annotation.getString(MergedAnnotation.VALUE) : StringUtils.uncapitalize(beanClassName);
				ListableBeanFactory beanFactory = (ListableBeanFactory) registry;
				HttpInterfaceClientAdapter adapter = beanFactory.getBean(HttpInterfaceClientAdapter.class);
				BeanDefinition definition = BeanDefinitionBuilder
						.rootBeanDefinition(ResolvableType.forClass(beanClass),
								() -> adapter.createClient(beanFactory, clientName, beanClass))
						.getBeanDefinition();
				registry.registerBeanDefinition(clientName, definition);

			}
		}
	}




	@Override
	protected Class<? extends Annotation> getAnnotation() {
		return HttpClient.class;
	}

}


