package com.atomosphere.annotation;

import static java.lang.annotation.ElementType.PACKAGE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

@Documented
@Retention(RUNTIME)
@Target(PACKAGE)
public @interface Schema {

	String[] language() default { "java" };

	boolean formatSchemas() default false;

	String sizeMax() default UNDEFINED;

	String listMax() default UNDEFINED;

	public static final String UNDEFINED = "##UNDEFINED##";
}
