package com.atomosphere.colfer;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

import org.junit.Test;

import com.google.common.io.Resources;
import com.google.testing.compile.JavaFileObjects;

public class Test001 {

	@Test
	public void test() {
		//com/atomosphere/colfer/TestObject.
		SampleProcessor processor = new SampleProcessor();
		assert_() //
				.about(javaSource()) //
				.that(JavaFileObjects.forResource(Resources.getResource("com/atomosphere/colfer/TestObject.java"))) //
				.processedWith(processor) //
				.compilesWithoutError();
	}

}
