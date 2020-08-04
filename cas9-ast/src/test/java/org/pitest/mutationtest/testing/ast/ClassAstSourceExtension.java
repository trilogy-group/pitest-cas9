package org.pitest.mutationtest.testing.ast;

import static java.util.Arrays.asList;
import static java.util.Collections.singleton;

import java.io.File;
import java.util.HashSet;
import lombok.val;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.pitest.mutationtest.build.InterceptorParameters;
import org.pitest.mutationtest.build.MutationInterceptorFactory;
import org.pitest.mutationtest.build.intercept.ast.ClassAstSettingsInterceptorFactory;
import org.pitest.mutationtest.config.ReportOptions;

public class ClassAstSourceExtension implements BeforeEachCallback, AfterEachCallback {

  private static final String SOURCE_DIRS_PROPERTY = "cas9-test.sources";

  private static final String CLASSES_DIRS_PROPERTY = "cas9-test.classes";

  private static final String CLASSPATH_PROPERTY = "cas9-test.classpath";

  private static final MutationInterceptorFactory INTERCEPTOR_FACTORY = new ClassAstSettingsInterceptorFactory();

  @Override
  public void beforeEach(ExtensionContext context) {
    val sourcesDir = System.getProperty(SOURCE_DIRS_PROPERTY, "src/test/java");
    val classesDir = System.getProperty(CLASSES_DIRS_PROPERTY, "target/classes");
    val classpath = new HashSet<>(asList(System.getProperty(CLASSPATH_PROPERTY, "").split(":")));

    classpath.add(classesDir);

    val options = new ReportOptions();
    options.setSourceDirs(singleton(new File(sourcesDir)));
    options.setClassPathElements(classpath);
    val params = new InterceptorParameters(null, options, null);

    INTERCEPTOR_FACTORY.createInterceptor(params);
  }

  @Override
  public void afterEach(ExtensionContext context) {
    val params = new InterceptorParameters(null, null, null);
    INTERCEPTOR_FACTORY.createInterceptor(params);
  }
}
