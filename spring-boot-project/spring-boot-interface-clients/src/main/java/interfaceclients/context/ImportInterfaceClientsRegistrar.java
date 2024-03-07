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

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.OrderComparator;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * @author Olga Maciaszek-Sharma
 */
abstract class ImportInterfaceClientsRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware,
		ResourceLoaderAware {

	//TODO: handle AOT

	private static final Log logger = LogFactory.getLog(ImportInterfaceClientsRegistrar.class);

	private Environment environment;

	private ResourceLoader resourceLoader;

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
						logger.debug("Could not register interface client for class "
								+ beanClassName + ": " + e.getMessage());
					}
					throw new RuntimeException(e);
				}
				String clientName = !ObjectUtils.isEmpty(annotation.getString(MergedAnnotation.VALUE))
						? annotation.getString(MergedAnnotation.VALUE) : StringUtils.uncapitalize(beanClassName);
				ListableBeanFactory beanFactory = (ListableBeanFactory) registry;
				// TODO: initialise in autoconfig
				InterfaceClientAdapter adapter = beanFactory.getBean(AggregateInterfaceClientAdapter.class);
				BeanDefinition definition = BeanDefinitionBuilder
						.rootBeanDefinition(ResolvableType.forClass(beanClass),
								() -> adapter.createClient(beanFactory, clientName, beanClass))
						.getBeanDefinition();
				registry.registerBeanDefinition(clientName, definition);
			}
		}
	}

	private static InterfaceClientAdapter getInterfaceClientAdapter(ListableBeanFactory beanFactory,
			String clientName) {
		// TODO: test overriding order in user configurations
		List<InterfaceClientAdapter> adapters = beanFactory.getBeansOfType(InterfaceClientAdapter.class).values().stream()
				// TODO: if too early, filter by annotations on bean definitions
				// TODO: try to avoid duplicate dependency resolution for client
				.filter(adapter -> adapter.canCreateClient(beanFactory, clientName))
				.toList();
		Assert.notEmpty(adapters, "No suitable "
				+ InterfaceClientAdapter.class.getSimpleName() +" available.");
		OrderComparator.sort(adapters);
		return adapters.get(0);
	}

	private Set<BeanDefinition> discoverCandidateComponents(AnnotationMetadata metadata) {
		Set<BeanDefinition> candidateComponents = new HashSet<>();
		MergedAnnotation<AutoConfigureInterfaceClients> annotation = metadata.getAnnotations()
				.get(AutoConfigureInterfaceClients.class);
		Class<?>[] clients = annotation.getClassArray(MergedAnnotation.VALUE);
		if (clients.length == 0) {
			ClassPathScanningCandidateComponentProvider scanner = getScanner();
			scanner.setResourceLoader(this.resourceLoader);
			scanner.addIncludeFilter(new AnnotationTypeFilter(getAnnotation()));
			Set<String> basePackages = Set.of(ClassUtils.getPackageName(metadata.getClassName()));
			for (String basePackage : basePackages) {
				candidateComponents.addAll(scanner.findCandidateComponents(basePackage));
			}
		}
		else {
			for (Class<?> clazz : clients) {
				candidateComponents.add(new AnnotatedGenericBeanDefinition(clazz));
			}
		}
		return candidateComponents;
	}

	private ClassPathScanningCandidateComponentProvider getScanner() {
		return new ClassPathScanningCandidateComponentProvider(false, this.environment) {
			@Override
			protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
				boolean isCandidate = false;
				if (beanDefinition.getMetadata().isIndependent()) {
					if (!beanDefinition.getMetadata().isAnnotation()) {
						isCandidate = true;
					}
				}
				return isCandidate;
			}
		};
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.environment = environment;
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	protected abstract Class<? extends Annotation> getAnnotation();

	protected <T> Supplier<T> supplyClient(BeanDefinitionRegistry registry, String beanClassName, Class<T> beanClass) {
		return new Supplier<T>() {
			@Override
			public T get() {
				return createClient(registry, beanClassName, beanClass);
			}
		};
	}

	protected abstract <T> T createClient();

	protected <T> T createClient(BeanDefinitionRegistry registry, String beanClassName, Class<T> beanClass) {
		Assert.isInstanceOf(ListableBeanFactory.class, registry, registry.getClass().getSimpleName()
				+ " is not an instance of " + ListableBeanFactory.class.getSimpleName());
		ListableBeanFactory beanFactory = (ListableBeanFactory) registry;
		// TODO: handle many beans
		// FIXME
		InterfaceClientAdapter adapter = beanFactory.getBeansOfType(InterfaceClientAdapter.class).values().stream().findAny().get();
		return adapter.createClient(registry, beanClassName, beanClass);
	}

}
