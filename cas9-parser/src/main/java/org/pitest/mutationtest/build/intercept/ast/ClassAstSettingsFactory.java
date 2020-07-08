package org.pitest.mutationtest.build.intercept.ast;

import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableCollection;
import static org.apache.commons.lang3.ObjectUtils.defaultIfNull;

import java.io.File;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.pitest.mutationtest.config.ReportOptions;

@UtilityClass
public class ClassAstSettingsFactory {

  private static final Collection<File> DEFAULT_SOURCE_DIRS =
      unmodifiableCollection(singleton(new File("src/main/java")));

  private static final Collection<String> DEFAULT_CLASSPATH_ELEMENTS = emptyList();

  private ClassAstSource classAstSource = (name, file) -> Optional.empty();

  synchronized void initialize(@NonNull final ReportOptions options) {
    val sourceDirs = defaultIfNull(options.getSourceDirs(), DEFAULT_SOURCE_DIRS);
    val classpathElementsAsIs = defaultIfNull(options.getClassPathElements(), DEFAULT_CLASSPATH_ELEMENTS);
    val classpathElements = classpathElementsAsIs.stream()
        .map(File::new)
        .filter(File::exists)
        .collect(Collectors.toList());
    classAstSource = new CachingClassAstSource(ProjectBuildConfigAstSource.of(sourceDirs, classpathElements));
  }

  public ClassAstSource getAstSource() {
    return classAstSource;
  }
}
