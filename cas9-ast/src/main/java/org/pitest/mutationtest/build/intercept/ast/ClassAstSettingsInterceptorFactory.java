package org.pitest.mutationtest.build.intercept.ast;

import static java.util.Collections.emptyList;
import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableCollection;
import static java.util.stream.Collectors.toList;
import static org.pitest.mutationtest.build.intercept.ast.ClassAstSettingsInterceptor.INTERCEPTOR;

import java.io.File;
import java.util.Collection;
import lombok.val;
import org.pitest.mutationtest.build.InterceptorParameters;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.build.MutationInterceptorFactory;
import org.pitest.mutationtest.config.ReportOptions;
import org.pitest.plugin.Feature;

public class ClassAstSettingsInterceptorFactory implements MutationInterceptorFactory {

  public static final String FEATURE_NAME = "AST";

  private static final Collection<File> DEFAULT_SOURCE_DIRS =
      unmodifiableCollection(singleton(new File("src/main/java")));

  private static final Collection<File> DEFAULT_CLASSPATH_ELEMENTS = emptyList();

  @Override
  public MutationInterceptor createInterceptor(InterceptorParameters params) {
    val options = params.data();
    INTERCEPTOR.setAstSource(options == null ? null : createAstSource(options));
    return INTERCEPTOR;
  }

  @Override
  public Feature provides() {
    return Feature.named(FEATURE_NAME)
        .withOnByDefault(true)
        .withDescription("Parses the source code of the target class as an AST object");
  }

  @Override
  public String description() {
    return "Source code AST provider plugin";
  }

  public static void restoreClassAstSettings() {
    val current = INTERCEPTOR.getAstSource().orElse(null);
    if (!(current instanceof CachingClassAstSource)) {
      ClassAstSettingsMinionArguments.restoreOptions()
          .map(ClassAstSettingsInterceptorFactory::createAstSource)
          .ifPresent(INTERCEPTOR::setAstSource);
    }
  }

  private static ClassAstSource createAstSource(final ReportOptions options) {
    val sourceDirs = options.getSourceDirs() == null ? DEFAULT_SOURCE_DIRS : options.getSourceDirs();
    val classpathElements = options.getClassPathElements() == null
        ? DEFAULT_CLASSPATH_ELEMENTS
        : options.getClassPathElements().stream().map(File::new).collect(toList());
    ClassAstSettingsMinionArguments.saveOptions(options);
    return new CachingClassAstSource(new ProjectBuildConfigAstSource(sourceDirs, classpathElements));
  }
}
