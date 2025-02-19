package org.springframework.boot.autoconfigure.interfaceclients;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.MergedAnnotation;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.support.RestClientProxyRegistry;
import org.springframework.web.service.registry.HttpServiceProxyGroup;
import org.springframework.web.service.registry.HttpServiceProxyRegistry;

// TODO - Boot: add separate packages for RestClient and WebClient based implementations?
// TODO: handle AOT
/**
 * @author Olga Maciaszek-Sharma
 */
public class HttpClientsRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
		Assert.isInstanceOf(ListableBeanFactory.class, registry,
				"Registry must be an instance of " + ListableBeanFactory.class.getSimpleName());
		ListableBeanFactory beanFactory = (ListableBeanFactory) registry;

		// TODO: also support configuring proxy factory
		// TODO: support bot RestClient and WebClient registries in different levels
		RestClientProxyRegistry.Builder restClientProxyRegistryBuilder = beanFactory.getBean(RestClientProxyRegistry.Builder.class);
		beanFactory.getBeansOfType(RestClientHttpServiceGroupConfigurer.class).values()
				.forEach(restClientProxyRegistryBuilder::apply);

		Map<String, Set<MergedAnnotation<InterfaceClientGroup>>> annotationsMap = getAnnotations(beanFactory, registry);

		registerBeanDefinitions(registry, annotationsMap, restClientProxyRegistryBuilder);
	}

	protected String[] getBasePackages(String[] basePackages,
			Class<?>[] basePackageClasses, String importingClassName, boolean serviceTypesListed) {
		Set<String> packages = new HashSet<>();
		for (String pkg : basePackages) {
			if (StringUtils.hasText(pkg)) {
				packages.add(pkg);
			}
		}

		for (Class<?> clazz : basePackageClasses) {
			packages.add(ClassUtils.getPackageName(clazz));
		}

		if (packages.isEmpty() && !serviceTypesListed) {
			packages.add(ClassUtils.getPackageName(importingClassName));
		}
		return packages.toArray(String[]::new);
	}

	private void registerBeanDefinitions(BeanDefinitionRegistry registry, Map<String, Set<MergedAnnotation<InterfaceClientGroup>>> annotationsMap, RestClientProxyRegistry.Builder registryBuilder) {
		addClientGroups(annotationsMap, registryBuilder);


		HttpServiceProxyRegistry interfaceClientRegistry = registryBuilder.build();

		registerBeanDefinitions(registry, "httpInterfaceClientRegistry", HttpServiceProxyRegistry.class,
				interfaceClientRegistry);

		for (HttpServiceProxyGroup clientGroup : interfaceClientRegistry.getProxyGroups()) {
			Map<Class<?>, Object> proxies = clientGroup.proxies();
			for (Class<?> proxyClass : proxies.keySet()) {
				// TODO: improve bean naming:
				// - better handle missing group names (set to null and check
				// for that while constructing group lookup in registry) and
				// name clashes (use just simple name to begin with, but proactively use
				// a more advanced naming strategy: groupName + FQN if required)
				String beanName = clientGroup.name() + proxyClass.getSimpleName();
				registerBeanDefinitions(registry, beanName, proxyClass, proxies.get(proxyClass));
			}
		}
	}

	private void addClientGroups(Map<String, Set<MergedAnnotation<InterfaceClientGroup>>> annotationsMap, RestClientProxyRegistry.Builder registryBuilder) {
		for (String key : annotationsMap.keySet()) {
			Set<MergedAnnotation<InterfaceClientGroup>> annotations = annotationsMap.get(key);
			for (MergedAnnotation<InterfaceClientGroup> annotation : annotations) {
				Class<?>[] serviceTypes = annotation.getClassArray("httpServiceTypes");
				registryBuilder.addClient(annotation.getString(MergedAnnotation.VALUE),
						annotation.getString("name"),
						httpServiceConfigurer -> httpServiceConfigurer
								.addServiceTypes(serviceTypes)
								.discoverServiceTypes(getBasePackages(annotation.getStringArray("basePackages"),
//										FIXME: refactor checking for service types while resolving base packages
										annotation.getClassArray("basePackageClasses"), key, serviceTypes.length > 0)),
						clientBuilder -> {
						},
						proxyFactoryBuilder -> {
						});
			}
		}
	}

	private static void registerBeanDefinitions(BeanDefinitionRegistry registry, String beanName, Class<?> beanClass,
			Object object) {
		BeanDefinition definition = BeanDefinitionBuilder
				.rootBeanDefinition(ResolvableType.forClass(beanClass), () -> object)
				.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE)
				.getBeanDefinition();
		BeanDefinitionHolder holder = new BeanDefinitionHolder(definition, beanName);
		BeanDefinitionReaderUtils.registerBeanDefinition(holder, registry);
	}

	private static Map<String, Set<MergedAnnotation<InterfaceClientGroup>>> getAnnotations(ListableBeanFactory beanFactory,
			BeanDefinitionRegistry registry) {
		String[] annotatedBeanNames = beanFactory.getBeanNamesForAnnotation(InterfaceClientGroup.class);
		Map<String, Set<MergedAnnotation<InterfaceClientGroup>>> annotations = new HashMap<>();
		for (String beanName : annotatedBeanNames) {
			BeanDefinition beanDefinition = registry.getBeanDefinition(beanName);
			Assert.isInstanceOf(AnnotatedBeanDefinition.class, beanDefinition);
			AnnotatedBeanDefinition annotatedBeanDefinition = (AnnotatedBeanDefinition) beanDefinition;
			AnnotationMetadata metadata = annotatedBeanDefinition.getMetadata();
			Set<MergedAnnotation<InterfaceClientGroup>> annotationSet = new HashSet<>();
			MergedAnnotation<EnableInterfaceClients> containerAnnotation = annotatedBeanDefinition.getMetadata()
					.getAnnotations()
					.get(EnableInterfaceClients.class);
			if (containerAnnotation.isPresent()) {
				Collections.addAll(annotationSet, containerAnnotation
						.getAnnotationArray(MergedAnnotation.VALUE, InterfaceClientGroup.class));
			}
			MergedAnnotation<InterfaceClientGroup> annotation = annotatedBeanDefinition.getMetadata()
					.getAnnotations()
					.get(InterfaceClientGroup.class);
			if (annotation.isPresent()) {
				annotationSet.add(annotation);
			}
			annotations.put(metadata.getClassName(), annotationSet);
		}
		return annotations;
	}
}


