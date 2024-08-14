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

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.type.MethodMetadata;

/**
 * @author Olga Maciaszek-Sharma
 */
final class QualifiedBeanProvider {

	private static final Log logger = LogFactory.getLog(QualifiedBeanProvider.class);

	static <T> T qualifiedBean(ConfigurableListableBeanFactory beanFactory, Class<T> type, String clientId) {
		Map<String, T> matchingClientBeans = getQualifiedBeansOfType(beanFactory, type, clientId);
		if (matchingClientBeans.size() > 1) {
			throw new NoUniqueBeanDefinitionException(type, matchingClientBeans.keySet());
		}
		if (matchingClientBeans.isEmpty()) {
			if (logger.isDebugEnabled()) {
				logger.debug("No qualified bean of type " + type + " found for " + clientId);
			}
			Map<String, T> matchingDefaultBeans = getQualifiedBeansOfType(beanFactory, type, clientId);
			if (matchingDefaultBeans.size() > 1) {
				throw new NoUniqueBeanDefinitionException(type, matchingDefaultBeans.keySet());
			}
			if (matchingDefaultBeans.isEmpty()) {
				if (logger.isDebugEnabled()) {
					logger.debug("No qualified bean of type " + type + " found for default id");
				}
				return null;
			}
		}
		return matchingClientBeans.values().iterator().next();
	}

	private static <T> Map<String, T> getQualifiedBeansOfType(ConfigurableListableBeanFactory beanFactory,
			Class<T> type, String clientId) {
		Map<String, T> beansOfType = BeanFactoryUtils.beansOfTypeIncludingAncestors(beanFactory, type);
		Map<String, T> matchingClientBeans = new HashMap<>();
		for (String beanName : beansOfType.keySet()) {
			BeanDefinition beanDefinition = beanFactory.getBeanDefinition(beanName);
			if (beanDefinition instanceof AnnotatedBeanDefinition definition) {
				// TODO: try refactoring this
				// TODO: beans defined in a different way
				// AnnotationAttributes annotationAttributes = AnnotationAttributes
				// .fromMap(definition.getMetadata().getAnnotationAttributes(Qualifier.class.getName()));
				MethodMetadata methodMetadata = definition.getFactoryMethodMetadata();
				AnnotationAttributes annotationAttributes = methodMetadata != null ? AnnotationAttributes
					.fromMap(methodMetadata.getAnnotationAttributes(Qualifier.class.getName())) : null;
				if (annotationAttributes != null
						&& clientId.equals(annotationAttributes.getString(MergedAnnotation.VALUE))) {
					matchingClientBeans.put(beanName, beansOfType.get(beanName));
				}

			}
		}
		return matchingClientBeans;
	}

}
