package org.pitest.mutationtest.build.intercept.classrules;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singleton;
import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.pitest.mutationtest.build.intercept.MutationFilterTestUtils.TEST_CLASS_NAME;
import static org.pitest.mutationtest.build.intercept.MutationFilterTestUtils.getLineNumbers;
import static org.pitest.mutationtest.build.intercept.MutationFilterTestUtils.getOperators;
import static org.pitest.mutationtest.build.intercept.MutationFilterTestUtils.mutations;
import static org.pitest.mutationtest.build.intercept.classrules.ClassRulesMutationFilterFactory.FEATURE_NAME;
import static org.pitest.plugin.ToggleStatus.ACTIVATE;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.pitest.classpath.ClassPath;
import org.pitest.mutationtest.build.InterceptorParameters;
import org.pitest.mutationtest.config.ReportOptions;
import org.pitest.plugin.FeatureSetting;
import org.pitest.util.PitError;

class ClassRulesMutationFilterFactoryTest {

  private InterceptorParameters params;

  @BeforeEach
  void setUp() {
    val conf = new FeatureSetting(FEATURE_NAME, ACTIVATE, emptyMap());
    val options = mock(ReportOptions.class);
    params = new InterceptorParameters(conf, options, null);

    val classPath = mock(ClassPath.class);
    when(options.getClassPath()).thenReturn(classPath);
  }

  @Test
  void shouldFilterOutMutationsNotInLineRange(@TempDir Path temp) throws Exception {
    // Arrange
    val mutations = Stream.of(
        mutations(1, "ROR"),
        mutations(2, "SBR"),
        mutations(3, "AOR"))
        .flatMap(identity())
        .collect(toList());
    writeRules("{ ranges: [ { first: 1, last: 2 } ] }", temp);
    // Act
    val actual = new ClassRulesMutationFilterFactory()
        .createInterceptor(params)
        .intercept(mutations, null);
    // Assert
    assertAll(
        () -> assertThat(getOperators(actual), contains("ROR", "SBR")),
        () -> assertThat(getLineNumbers(actual), contains(1, 2))
    );
  }

  @Test
  void shouldThrowIfHasInvalidRangeValues(@TempDir Path temp) throws Exception {
    // Arrange
    val mutations = mutations(1, "ROR").collect(toList());
    val filter = new ClassRulesMutationFilterFactory()
        .createInterceptor(params);
    writeRules("{ ranges: [ { first: 2, last: 1 } ] }", temp);
    // Act
    assertThrows(PitError.class, () -> filter.intercept(mutations, null));
  }

  @Test
  void shouldNotFilterIfRulesFileDoesNotExist(@TempDir Path temp) throws Exception {
    // Arrange
    val mutations = Stream.of(
        mutations(1, "ROR"),
        mutations(2, "SBR"),
        mutations(3, "AOR"))
        .flatMap(identity())
        .collect(toList());
    // Act
    val actual = new ClassRulesMutationFilterFactory()
        .createInterceptor(params)
        .intercept(mutations, null);
    // Assert
    assertAll(
        () -> assertThat(getOperators(actual), contains("ROR", "SBR", "AOR")),
        () -> assertThat(getLineNumbers(actual), contains(1, 2, 3))
    );
  }

  private void writeRules(String rulesAsJson, Path directory) throws IOException {
    val rulesPath = directory.resolve(TEST_CLASS_NAME.asJavaName() + ".json");
    val classPath = params.data().getClassPath();
    when(classPath.findResource(anyString())).thenReturn(rulesPath.toUri().toURL());
    Files.write(rulesPath, singleton(rulesAsJson));
  }
}
