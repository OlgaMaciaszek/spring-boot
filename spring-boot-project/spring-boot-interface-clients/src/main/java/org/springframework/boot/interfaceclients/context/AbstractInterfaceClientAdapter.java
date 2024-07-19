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

import java.util.Collections;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
import org.springframework.util.Assert;

/**
 * @author Olga Maciaszek-Sharma
 */
abstract class AbstractInterfaceClientAdapter implements InterfaceClientAdapter {

	// TODO: handle default qualifier for all interface clients to be used in conjunction
	// TODO: with the per client ones

	private static final Log logger = LogFactory.getLog(AbstractInterfaceClientAdapter.class);

	protected <T> T resolveDependency(BeanFactory factory, Class<T> type) {
		return resolveDependency(factory, DEFAULT_QUALIFIER, type);
	}

	protected <T> List<T> resolveListDependency(BeanFactory factory, Class<T> type) {
		return resolveListDependency(factory, DEFAULT_QUALIFIER, type);
	}

	// TODO: move to utility class?
	protected <T> T resolveDependency(BeanFactory factory, String clientName,
			Class<T> type) {
		Assert.isInstanceOf(ListableBeanFactory.class, factory, factory.getClass().getSimpleName()
				+ " is not an instance of " + ListableBeanFactory.class.getSimpleName());
		ListableBeanFactory beanFactory = (ListableBeanFactory) factory;
		try {
			return BeanFactoryAnnotationUtils.qualifiedBeanOfType(beanFactory, type, clientName);
		}
		catch (BeansException e) {
			if (logger.isDebugEnabled()) {
				logger.debug("Has not found qualified " + type.getSimpleName() + " instance for "
						+ clientName + " interface client.");
			}
		}
		return null;
	}

	protected <
			T> List<T> resolveListDependency(BeanFactory factory, String clientName,
			Class<T> type) {
		Assert.isInstanceOf(ListableBeanFactory.class, factory, factory.getClass().getSimpleName()
				+ " is not an instance of " + ListableBeanFactory.class.getSimpleName());
		ListableBeanFactory beanFactory = (ListableBeanFactory) factory;
		try {
			return BeanFactoryAnnotationUtils.qualifiedBeansOfType(beanFactory, type, clientName)
					.values().stream().toList();
		}
		catch (BeansException e) {
			if (logger.isDebugEnabled()) {
				logger.debug("Has not found qualified " + type.getSimpleName() + " instances for "
						+ clientName + " interface client.");
			}
		}
		return Collections.emptyList();
	}

	protected abstract void resolveDefaultDependencies(BeanFactory factory);
}
