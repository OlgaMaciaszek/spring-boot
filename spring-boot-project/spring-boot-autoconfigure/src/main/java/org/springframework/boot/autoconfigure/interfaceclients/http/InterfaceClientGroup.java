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

package org.springframework.boot.autoconfigure.interfaceclients.http;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.annotation.AliasFor;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.registry.HttpServiceGroup;
import org.springframework.web.service.registry.HttpServiceGroup.ScanSpec;

/**
 * Registers an HTTP service client along with associated interface clients. Scans the
 * listed packages for {@link @HttpExchange}-annotated interfaces to add or adds the
 * directly provided interfaces to the client.
 *
 * @author Olga Maciaszek-Sharma
 * @since 4.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Repeatable(EnableInterfaceClients.class)
public @interface InterfaceClientGroup {

	/**
	 * The {@code id} of the interface client group.
	 * Set to {@code default} if none provided.
	 *
	 * @see HttpServiceGroup#id()
	 * @return interface client group id
	 */
	@AliasFor("id")
	String value() default "";

	/**
	 * The {@code id} of the interface client group.
	 * Alias for {@link #value() value}.
	 * Set to {@code default} if none provided.
	 *
	 * @see HttpServiceGroup#id()
	 * @return interface client group id
	 */
	@AliasFor("value")
	String name() default "";


	/**
	 * Base packages to scan for annotated components. Use {@link #basePackageClasses()}
	 * for a type-safe alternative to String-based package names.
	 * Uses the package of the annotated class if none provided.
	 *
	 * @return the array of base packages
	 */
	String[] basePackages() default {};

	/**
	 * Type-safe alternative to {@link #basePackages()} for specifying the packages to
	 * scan for annotated components. The package of each class specified will be scanned.
	 * Uses the package of the annotated class if none provided.
	 * <p>
	 * Consider creating a special no-op marker class or interface in each package that
	 * serves no purpose other than being referenced by this attribute.
	 *
	 * @return the array of base package classes
	 */
	Class<?>[] basePackageClasses() default {};

	/**
	 * Array of interface types to instantiate for the client.
	 *
	 * @return an array of {@link org.springframework.web.service.annotation.HttpExchange}
	 * interfaces
	 */
	Class<?>[] httpServiceTypes() default {};

}
