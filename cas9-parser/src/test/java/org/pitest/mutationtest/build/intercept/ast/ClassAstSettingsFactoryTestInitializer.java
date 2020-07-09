package org.pitest.mutationtest.build.intercept.ast;

import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toSet;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Properties;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.pitest.mutationtest.config.ReportOptions;

@UtilityClass
public class ClassAstSettingsFactoryTestInitializer {

  private static final String BUILD_PROPERTIES_FILE = "/build.properties";

  private static final Properties BUILD_PROPERTIES = loadBuildProperties();

  private static final String SOURCE_DIRS_PROPERTY = "sourceDirectory";

  private static final String CLASSES_DIRS_PROPERTY = "outputDirectory";

  private static final String CLASSPATH_FILE = "/classpath.txt";

  public void setUp() {
    val options = new ReportOptions();
    options.setSourceDirs(singleton(new File(BUILD_PROPERTIES.getProperty(SOURCE_DIRS_PROPERTY))));
    options.setClassPathElements(loadClassPathElements());
    ClassAstSettingsFactory.initialize(options);
  }

  public void tearDown() {
    ClassAstSettingsFactory.initialize(null);
  }

  @SneakyThrows
  private Properties loadBuildProperties() {
    val buildProps = new Properties();
    try (InputStream is = ClassAstSettingsFactoryTestInitializer.class.getResourceAsStream(BUILD_PROPERTIES_FILE)) {
      buildProps.load(is);
    }
    return buildProps;
  }

  @SneakyThrows
  private Collection<String> loadClassPathElements() {
    val classPathFile = Paths.get(ClassAstSettingsFactoryTestInitializer.class.getResource(CLASSPATH_FILE).toURI());
    val dependencies = Files.readAllLines(classPathFile).stream()
        .flatMap(line -> Stream.of(line.split(":")))
        .collect(toSet());
    dependencies.add(BUILD_PROPERTIES.getProperty(CLASSES_DIRS_PROPERTY));
    return dependencies;
  }
}
