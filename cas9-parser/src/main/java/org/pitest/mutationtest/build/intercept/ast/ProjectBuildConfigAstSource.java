package org.pitest.mutationtest.build.intercept.ast;

import static java.util.Collections.singleton;
import static org.apache.commons.lang3.Functions.apply;

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
import lombok.Value;
import lombok.val;
import org.pitest.classinfo.ClassName;
import org.pitest.mutationtest.SourceLocator;
import org.pitest.mutationtest.tooling.SmartSourceLocator;

@Value
class ProjectBuildConfigAstSource implements ClassAstSource {

  SourceLocator locator;

  JavaParser parser;

  static ProjectBuildConfigAstSource of(@NonNull final Collection<File> sourceDirs,
      @NonNull final Collection<File> classPathElements) {
    val parser = createParser(sourceDirs, classPathElements);
    val locator = new SmartSourceLocator(sourceDirs);
    return new ProjectBuildConfigAstSource(locator, parser);
  }

  @Override
  public Optional<ClassOrInterfaceDeclaration> getAst(String clazz, String fileName) {
    val className = ClassName.fromString(clazz)
        .getNameWithoutPackage()
        .asJavaName();

    return locator.locate(singleton(clazz), fileName)
        .map(parser::parse)
        .map(result -> result.getResult()
            .orElseThrow(() -> new ParseProblemException(result.getProblems())))
        .flatMap(unit -> unit.getClassByName(className));
  }

  private static JavaParser createParser(@NonNull Collection<File> sourceDirs,
      @NonNull Collection<File> classPathElements) {
    val reflectionSolver = new ReflectionTypeSolver();

    val dependencyUrls = classPathElements.stream()
        .map(elem -> apply(file -> file.toURI().toURL(), elem))
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
}
