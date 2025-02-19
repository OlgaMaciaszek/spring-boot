package org.springframework.boot.autoconfigure.interfaceclients;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * Container annotation that aggregates several {@link InterfaceClientGroup} annotations.
 *
 * <p>Can be used natively, declaring several nested {@link InterfaceClientGroup} annotations.
 * Can also be used in conjunction with Java 8's support for repeatable annotations,
 * where {@link InterfaceClientGroup} can simply be declared several times on the same method,
 * implicitly generating this container annotation.
 *
 * @author Olga Maciaszek-Sharma
 * @since 4.0
 * @see InterfaceClientGroup
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(HttpInterfaceClientsConfiguration.class)
public @interface EnableInterfaceClients {

	InterfaceClientGroup[] value() default {};
}

