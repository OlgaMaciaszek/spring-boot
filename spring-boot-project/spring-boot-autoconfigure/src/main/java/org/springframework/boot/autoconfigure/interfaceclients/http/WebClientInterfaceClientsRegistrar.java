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
// import reactor.netty.http.client.HttpClient;
//
// import org.springframework.beans.factory.BeanFactory;
// import org.springframework.http.client.reactive.ReactorClientHttpConnector;
// import org.springframework.web.reactive.function.client.WebClient;
//
/// **
// * @author Olga Maciaszek-Sharma
// */
// public class WebClientInterfaceClientsRegistrar extends
/// AbstractHttpInterfaceClientsRegistrar<WebClient.Builder> {
//
// @Override
// protected Consumer<WebClient.Builder> buildClientConsumer(String name, BeanFactory
/// beanFactory) {
// // TODO: needs provider instead?
// HttpInterfaceClientsProperties properties =
/// beanFactory.getBean(HttpInterfaceClientsProperties.class);
// HttpInterfaceClientGroupProperties clientGroupProperties =
/// properties.getProperties(name);
// return (builder) -> {
// builder.clientConnector(new
/// ReactorClientHttpConnector(buildHttpClient(clientGroupProperties)));
// Map<String, List<String>> defaultHeaders = clientGroupProperties.getDefaultHeaders();
// for (String headerName : defaultHeaders.keySet()) {
// builder.defaultHeader(headerName,
/// defaultHeaders.get(headerName).toArray(String[]::new));
// }
// };
// }
//
// private HttpClient buildHttpClient(HttpInterfaceClientGroupProperties
/// clientGroupProperties) {
// // TODO
// return HttpClient.create();
// }
//
// }
