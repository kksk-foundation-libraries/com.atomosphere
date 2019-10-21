package com.atomosphere.colfer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;

import com.google.auto.service.AutoService;
import com.sun.source.tree.CompilationUnitTree;
import com.sun.source.util.TreePath;
import com.sun.source.util.Trees;

@AutoService(Processor.class)
public class SampleProcessor extends AbstractProcessor {
	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return new HashSet<>(Arrays.asList("*"));
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latest();
	}
	private Trees trees;

	@Override
	public void init(ProcessingEnvironment procEnv) {
		trees = Trees.instance(procEnv);
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		roundEnv.getRootElements().stream().map(this::toUnit).forEach(u -> {
			System.out.println("----- CompilationUnitTree -----");
			// AST の内容を出力
			System.out.println(u);
		});

		return false;
	}

	// AST の取得
	private CompilationUnitTree toUnit(Element el) {
		TreePath path = trees.getPath(el);
		return path.getCompilationUnit();
	}

}
