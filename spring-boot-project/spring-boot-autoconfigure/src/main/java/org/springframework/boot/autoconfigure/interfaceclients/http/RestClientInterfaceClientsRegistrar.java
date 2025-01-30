/// *
// * Copyright 2012-2025 the original author or authors.
// *
// * Licensed under the Apache License, Version 2.0 (the "License");
// * you may not use this file except in compliance with the License.
// * You may obtain a copy of the License at
// *
// * https://www.apache.org/licenses/LICENSE-2.0
// *
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS,
// * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// * See the License for the specific language governing permissions and
// * limitations under the License.
// */
//
// package org.springframework.boot.autoconfigure.interfaceclients.http;
//
// import java.util.List;
// import java.util.Map;
// import java.util.function.Consumer;
//
// import org.springframework.beans.factory.BeanFactory;
// import org.springframework.http.client.ClientHttpRequestFactory;
// import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
// import org.springframework.web.client.RestClient;
//
/// **
// * @author Olga Maciaszek-Sharma
// */
// public class RestClientInterfaceClientsRegistrar extends
/// AbstractHttpInterfaceClientsRegistrar<RestClient.Builder> {
//
// @Override
// protected Consumer<RestClient.Builder> buildClientConsumer(String name, BeanFactory
/// beanFactory) {
// // TODO: needs provider instead?
// HttpInterfaceClientsProperties properties =
/// beanFactory.getBean(HttpInterfaceClientsProperties.class);
// HttpInterfaceClientGroupProperties clientGroupProperties =
/// properties.getProperties(name);
// return (builder) -> {
// builder.requestFactory(buildClientHttpRequestFactory(clientGroupProperties));
// Map<String, List<String>> defaultHeaders = clientGroupProperties.getDefaultHeaders();
// for (String headerName : defaultHeaders.keySet()) {
// builder.defaultHeader(headerName,
/// defaultHeaders.get(headerName).toArray(String[]::new));
// }
// };
// }
//
// private ClientHttpRequestFactory buildClientHttpRequestFactory(
// HttpInterfaceClientGroupProperties clientProperties) {
// HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = new
/// HttpComponentsClientHttpRequestFactory();
// clientHttpRequestFactory.setConnectTimeout(clientProperties.getConnectTimeout());
// clientHttpRequestFactory.setReadTimeout(clientProperties.getReadTimeout());
// return clientHttpRequestFactory;
// }
//
// }
