// TODO: remove if not needed?
///*
// * Copyright 2012-2024 the original author or authors.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// *      https://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
//package interfaceclients.context;
//
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.FactoryBean;
//import org.springframework.beans.factory.ListableBeanFactory;
//import org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils;
//import org.springframework.beans.factory.support.BeanDefinitionRegistry;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.ApplicationContextAware;
//import org.springframework.util.Assert;
//
///**
// * @author Olga Maciaszek-Sharma
// */
//class InterfaceClientFactoryBean implements FactoryBean<Object>, ApplicationContextAware {
//
//	private String name;
//
//	private Class<?> type;
//
//	private ApplicationContext applicationContext;
//
//	@Override
//	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
//		this.applicationContext = applicationContext;
//	}
//
//	@Override
//	public Object getObject() throws Exception {
//		throw new UnsupportedOperationException("Please, implement me.");
//	}
//
//	@Override
//	public Class<?> getObjectType() {
//		throw new UnsupportedOperationException("Please, implement me.");
//	}
//
//	private <T> T resolveHttpClient(BeanDefinitionRegistry registry, String clientName,
//			Class<T> type){
//			applicationContext.getClassLoader()
//
//	}
//
//	private  <T> T resolveDependency(BeanDefinitionRegistry registry, String clientName,
//			Class<T> type) {
//		Assert.isInstanceOf(ListableBeanFactory.class, registry, registry.getClass().getSimpleName()
//				+ " is not an instance of " + ListableBeanFactory.class.getSimpleName());
//		ListableBeanFactory beanFactory = (ListableBeanFactory) registry;
//		return BeanFactoryAnnotationUtils.qualifiedBeanOfType(beanFactory, type, clientName);
//	}
//}
