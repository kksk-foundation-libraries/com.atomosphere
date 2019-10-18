package com.atomosphere;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;

import com.atomosphere.annotation.Struct;

public class ColferObjectGenerator extends AbstractProcessor {
	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return new HashSet<>(Arrays.asList(Struct.class.getName()));
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.RELEASE_8;
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		return true;
	}

}
