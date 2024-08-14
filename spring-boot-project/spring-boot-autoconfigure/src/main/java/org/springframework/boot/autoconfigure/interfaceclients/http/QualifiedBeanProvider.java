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

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;

import static org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils.qualifiedBeansOfType;

/**
 * @author Olga Maciaszek-Sharma
 */
final class QualifiedBeanProvider {

	private static final Log logger = LogFactory.getLog(QualifiedBeanProvider.class);

	static <T> T qualifiedBean(ListableBeanFactory beanFactory, Class<T> type, String clientId) {
		Map<String, T> matchingClientBeans = qualifiedBeansOfType(beanFactory, type, clientId);
		if (matchingClientBeans.size() > 1) {
			throw new NoUniqueBeanDefinitionException(type, matchingClientBeans.keySet());
		}
		if (matchingClientBeans.isEmpty()) {
			if (logger.isDebugEnabled()) {
				logger.debug("No qualified bean of type " + type + " found for " + clientId);
			}
			Map<String, T> matchingDefaultBeans = qualifiedBeansOfType(beanFactory, type, clientId);
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

}
