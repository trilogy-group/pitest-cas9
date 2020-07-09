package org.pitest.mutationtest.build.intercept.ast;

import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableCollection;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import java.io.File;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.pitest.mutationtest.config.ReportOptions;

@UtilityClass
public class ClassAstSettingsFactory {

  private static final Collection<File> DEFAULT_SOURCE_DIRS =
      unmodifiableCollection(singleton(new File("src/main/java")));

  private static final Collection<String> DEFAULT_CLASSPATH_ELEMENTS = emptyList();

  private static final ClassAstSource EMPTY_AST_SOURCE = (name, file) -> Optional.empty();

  private ClassAstSource classAstSource = EMPTY_AST_SOURCE;

  synchronized void initialize(ReportOptions options) {
    classAstSource = options == null ? EMPTY_AST_SOURCE : createAstSource(options);
  }

  public ClassAstSource getAstSource() {
    return classAstSource;
  }

  private ClassAstSource createAstSource(final ReportOptions options) {
    val sourceDirs = defaultIfNull(options.getSourceDirs(), DEFAULT_SOURCE_DIRS);
    val classpathElementsAsIs = defaultIfNull(options.getClassPathElements(), DEFAULT_CLASSPATH_ELEMENTS);
    val classpathElements = classpathElementsAsIs.stream()
        .map(File::new)
        .filter(File::exists)
        .collect(Collectors.toList());
    return new CachingClassAstSource(ProjectBuildConfigAstSource.of(sourceDirs, classpathElements));
  }
}
