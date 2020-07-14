package org.pitest.mutationtest.build.intercept.ast;

import static java.util.Collections.singleton;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseProblemException;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.model.resolution.TypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ClassLoaderTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Optional;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.val;
import org.pitest.classinfo.ClassName;
import org.pitest.mutationtest.SourceLocator;
import org.pitest.mutationtest.tooling.SmartSourceLocator;

class ProjectBuildConfigAstSource implements ClassAstSource {

  private final JavaParser parser;

  private final SourceLocator locator;

  ProjectBuildConfigAstSource(@NonNull final Collection<File> sourceDirs,
      @NonNull final Collection<File> classPathElements) {
    this.parser = createParser(sourceDirs, classPathElements);
    this.locator = new SmartSourceLocator(sourceDirs);
  }

  @Override
  public Optional<ClassOrInterfaceDeclaration> getAst(ClassName className, String fileName) {
    val internalName = className.asInternalName();
    val simpleName = className
        .getNameWithoutPackage()
        .asJavaName();

    return locator.locate(singleton(internalName), fileName)
        .map(parser::parse)
        .map(result -> result.getResult()
            .orElseThrow(() -> new ParseProblemException(result.getProblems())))
        .flatMap(unit -> unit.getClassByName(simpleName));
  }

  private static JavaParser createParser(@NonNull Collection<File> sourceDirs,
      @NonNull Collection<File> classPathElements) {
    val reflectionSolver = new ReflectionTypeSolver();

    val dependencyUrls = classPathElements.stream()
        .map(ProjectBuildConfigAstSource::toUrl)
        .toArray(URL[]::new);
    val dependencySolver = new ClassLoaderTypeSolver(new URLClassLoader(dependencyUrls));

    val sourceDirSolvers = sourceDirs.stream()
        .map(JavaParserTypeSolver::new)
        .toArray(TypeSolver[]::new);
    val sourcesSolver = sourceDirSolvers.length == 1 ? sourceDirSolvers[0] : new CombinedTypeSolver(sourceDirSolvers);

    val typeSolver = new CombinedTypeSolver(reflectionSolver, dependencySolver, sourcesSolver);
    val symbolSolver = new JavaSymbolSolver(typeSolver);
    val configuration = new ParserConfiguration().setSymbolResolver(symbolSolver);

    return new JavaParser(configuration);
  }

  @SneakyThrows
  private static URL toUrl(File file) {
    return file.toURI().toURL();
  }
}
