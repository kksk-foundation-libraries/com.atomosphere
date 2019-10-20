package com.atomosphere.colfer;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.UUID;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

public class ColferGenerateProcessor extends AbstractProcessor {
	@Override
	public Set<String> getSupportedAnnotationTypes() {
		return new HashSet<>();
	}

	@Override
	public SourceVersion getSupportedSourceVersion() {
		return SourceVersion.latest();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		if (!roundEnv.processingOver()) {
			process(roundEnv);
		}
		return true;
	}

	void process(RoundEnvironment roundEnv) {
		try {
			FileObject confFile = processingEnv.getFiler().getResource(StandardLocation.CLASS_OUTPUT, null, "colfer/colfer.conf");
//			confFile.openInputStream()
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static class ColferGenerator {
		private ProcessingEnvironment processingEnv;
		/**
		 * The target language.
		 */
		String lang = "Java";

		/**
		 * The source files. Directories are scanned for
		 * files with the colf extension.
		 */
		File schema = null;

		/**
		 * Normalizes schemas on the fly.
		 */
		boolean formatSchemas = false;

		/**
		 * Adds a package prefix. Use slash as a separator when nesting.
		 */
		String packagePrefix = "";

		/**
		 * Sets the default upper limit for serial byte sizes. The
		 * expression is applied to the target language under the name
		 * ColferSizeMax. (default "16 * 1024 * 1024")
		 */
		String sizeMax = "16 * 1024 * 1024";

		/**
		 * Sets the default upper limit for the number of elements in a
		 * list. The expression is applied to the target language under
		 * the name ColferListMax. (default "64 * 1024")
		 */
		String listMax = "64 * 1024";

		/**
		 * Makes all generated classes extend a super class. Use slash as
		 * a package separator. Java only.
		 */
		String superClass = ColferObject.class.getName().replaceAll("\\.", "/");

		public ColferGenerator(ProcessingEnvironment processingEnv) {
			this.processingEnv = processingEnv;
		}

		void _execute() throws Exception {
			Path colf = compiler();

			try {
				Process proc = launch(colf);

				Scanner stderr = new Scanner(proc.getErrorStream());
				while (stderr.hasNext())
					processingEnv.getMessager().printMessage(Kind.ERROR, stderr.nextLine());
				stderr.close();

				int exit = proc.waitFor();
				if (exit != 0)
					throw new Exception("colf: exit " + exit);

				//			project.addCompileSourceRoot(sourceTarget.toString());
			} catch (Exception e) {
				throw new Exception("compiler command execution", e);
			}
		}

		Process launch(Path colf) throws IOException {
			List<String> args = new ArrayList<>();
			args.add(colf.toString());
			//		args.add("-b=" + sourceTarget);
			if (packagePrefix != null)
				args.add("-p=" + packagePrefix);
			if (sizeMax != null)
				args.add("-s=" + sizeMax);
			if (listMax != null)
				args.add("-l=" + listMax);
			if (superClass != null)
				args.add("-x=" + superClass);
			if (formatSchemas)
				args.add("-f");
			args.add(lang);
			args.add(schema.toString());

			processingEnv.getMessager().printMessage(Kind.NOTE, "compile command arguments: " + args);
			ProcessBuilder builder = new ProcessBuilder(args);
			//		builder.directory(project.getBasedir());
			return builder.start();
		}

		/** Installs the executable. */
		Path compiler() throws Exception {
			String command = "colf";
			String resource;
			{
				String arch = System.getProperty("os.arch").toLowerCase();
				if ("x86_64".equals(arch))
					arch = "amd64";
				if (!"amd64".equals(arch))
					throw new Exception("unsupported hardware architecture: " + arch);

				String os = System.getProperty("os.name", "generic").toLowerCase();
				if (os.startsWith("mac") || os.startsWith("darwin")) {
					resource = "/" + arch + "/colf-darwin";
				} else if (os.startsWith("windows")) {
					resource = "/" + arch + "/colf.exe";
					command = "colf.exe";
				} else {
					resource = "/" + arch + "/colf-" + os;
				}
			}
			String tmpDir = System.getProperty("java.io.tmpdir");
			String tmpFolder = UUID.randomUUID().toString();

			Path path = new File(new File(new File(tmpDir), tmpFolder), command).toPath();

			if (Files.exists(path))
				return path;

			// install resource to path
			InputStream stream = ColferGenerateProcessor.class.getResourceAsStream(resource);
			if (stream == null)
				throw new Exception(resource + ": no such resource - platform not supported");
			try {
				Files.createDirectories(path.getParent());
				Files.copy(stream, path);
				stream.close();
			} catch (Exception e) {
				processingEnv.getMessager().printMessage(Kind.ERROR, "compiler command installation:" + e.getLocalizedMessage());
				throw new Exception(path.toString() + ": installation failed");
			}

			try {
				if (path.getFileSystem().supportedFileAttributeViews().contains("posix")) {
					// ensure execution permission
					Set<PosixFilePermission> perms = Files.getPosixFilePermissions(path);
					if (!perms.contains(PosixFilePermission.OWNER_EXECUTE)) {
						perms.add(PosixFilePermission.OWNER_EXECUTE);
						Files.setPosixFilePermissions(path, perms);
					}
				}
			} catch (Exception e) {
				processingEnv.getMessager().printMessage(Kind.WARNING, "compiler executable permission:" + e.getLocalizedMessage());
			}

			return path;
		}

	}
}
