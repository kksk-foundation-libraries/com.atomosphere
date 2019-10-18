package com.atomosphere.annotation;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface Column {
	String name();

	ColferType type() default ColferType.OTHER;

	String typeName() default Schema.UNDEFINED;

	boolean isArray() default false;
}
