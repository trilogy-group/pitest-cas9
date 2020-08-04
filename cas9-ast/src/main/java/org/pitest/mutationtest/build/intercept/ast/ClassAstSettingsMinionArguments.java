package org.pitest.mutationtest.build.intercept.ast;

import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.io.File;
import java.util.Optional;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.pitest.mutationtest.config.ReportOptions;
import org.pitest.util.EnvironmentUtils;

/**
 * Shares the input data used to initialize the AST in the main project
 * so it can be restored in minion process.
 * <p>
 *   This is required as the minion doesn't initiate the interceptors in the forked process.
 * </p>
 */
@UtilityClass
class ClassAstSettingsMinionArguments {

  public final String SOURCE_DIRS_ENV = "org.pitest.mutationtest.config.ReportOptionsContext#SRCDIRS";

  public final String CLASSPATH_ELEMENTS_ENV = "org.pitest.mutationtest.config.ReportOptionsContext#CLASSPATH";

  Optional<ReportOptions> restoreOptions() {
    val sourceDirsValue = System.getenv(SOURCE_DIRS_ENV);
    val classpathElementsValue = System.getenv(CLASSPATH_ELEMENTS_ENV);
    if (sourceDirsValue == null || classpathElementsValue == null) {
      return Optional.empty();
    }

    val sourceDirs = stream(sourceDirsValue.split(",")).map(File::new).collect(toList());
    val classpathElements = asList(classpathElementsValue.split(","));

    val options = new ReportOptions();
    options.setSourceDirs(sourceDirs);
    options.setClassPathElements(classpathElements);
    return Optional.of(options);
  }

  void saveOptions(@NonNull ReportOptions options) {
    val sourceDirs = options.getSourceDirs();
    val classpathElements = options.getClassPathElements();

    if (sourceDirs != null) {
      val sourceDirsValue = sourceDirs.stream()
          .map(File::toString)
          .collect(joining(","));
      EnvironmentUtils.setenv(SOURCE_DIRS_ENV, sourceDirsValue);
    }

    if (classpathElements != null) {
      val classpathElementsValue = String.join(",", classpathElements);
      EnvironmentUtils.setenv(CLASSPATH_ELEMENTS_ENV, classpathElementsValue);
    }
  }
}
