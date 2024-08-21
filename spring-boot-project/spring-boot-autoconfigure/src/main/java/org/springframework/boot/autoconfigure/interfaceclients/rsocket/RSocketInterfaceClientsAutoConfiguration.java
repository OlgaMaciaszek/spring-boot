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

package org.springframework.boot.autoconfigure.interfaceclients.rsocket;

import io.rsocket.transport.netty.server.TcpServerTransport;
import reactor.netty.http.server.HttpServer;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.rsocket.RSocketRequesterAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.messaging.rsocket.RSocketRequester;
import org.springframework.messaging.rsocket.service.RSocketServiceProxyFactory;

/**
 * @author Olga Maciaszek-Sharma
 */
@AutoConfiguration(after = RSocketRequesterAutoConfiguration.class)
@ConditionalOnClass({ RSocketRequester.class, io.rsocket.RSocket.class, HttpServer.class,
		TcpServerTransport.class, RSocketServiceProxyFactory.class })
@EnableConfigurationProperties(RSocketInterfaceClientsProperties.class)
@ConditionalOnProperty(value = "spring.interfaceclients.enabled", havingValue = "true")
@Import(RSocketInterfaceClientsImportRegistrar.class)
public class RSocketInterfaceClientsAutoConfiguration {
}
