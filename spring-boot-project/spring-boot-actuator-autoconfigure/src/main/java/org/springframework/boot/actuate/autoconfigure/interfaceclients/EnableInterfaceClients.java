/*
 * Copyright 2012-2025 the original author or authors.
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

package org.springframework.boot.actuate.autoconfigure.interfaceclients;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.boot.actuate.web.exchanges.HttpExchange;
import org.springframework.web.service.registry.AbstractHttpServiceGroup;

/**
 * Registers an HTTP service group along with associated interface clients.
 * Scans the listed packages for {@link @HttpExchange}-annotated interfaces
 * to add to the group or adds the directly provided interfaces to the group.
 *
 * @author Olga Maciaszek-Sharma
 * @since 4.0.0
 * @see AbstractHttpServiceGroup
 * TODO: javadoc
 * TODO: * probably move to FW
 * TODO: * should the name be general like the current one or `EnableHttpInterfaceClients`?
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface EnableInterfaceClients {

	/**
	 * The url of the host or service the clients in this group will communicate with.
	 * TODO
	 * @return An absolute URL or resolvable serviceId.
	 */
	String value();



	/**
	 * Base packages to scan for annotated components.
	 * Use {@link #basePackageClasses()} for a type-safe alternative to String-based
	 * package names.
	 * @return the array of 'basePackages'.
	 */
	String[] basePackages() default {};

	/**
	 * Type-safe alternative to {@link #basePackages()} for specifying the packages to
	 * scan for annotated components. The package of each class specified will be scanned.
	 * <p>
	 * Consider creating a special no-op marker class or interface in each package that
	 * serves no purpose other than being referenced by this attribute.
	 * @return the array of 'basePackageClasses'.
	 */
	Class<?>[] basePackageClasses() default {};

	/**
	 * List of interfaces to instantiate for the group. If not empty, disables classpath
	 * scanning.
	 * @return an array of {@link HttpExchange} classes
	 */
	Class<?>[] clients() default {};
}
