package org.pitest.mutationtest.engine.cas9.mutators.sbr;

import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.pitest.mutationtest.testing.mutators.MutantMatcher.replaces;

import com.google.gson.Gson;
import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.stream.Stream;
import lombok.SneakyThrows;
import lombok.val;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.pitest.classinfo.ClassName;
import org.pitest.classpath.ClassPathByteArraySource;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.mutationtest.engine.MutationIdentifier;
import org.pitest.mutationtest.engine.cas9.AstGregorMutater;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;
import org.pitest.mutationtest.testing.ast.ClassAstSourceExtension;

@ExtendWith(ClassAstSourceExtension.class)
class SBRMutatorTest {

  public static final String TARGET_MUTATIONS = "/sbr/SBRMutationTarget.json";

  @Test
  void shouldFindMutationsForSbrOperator() {
    // Arrange
    val byteSource = new ClassPathByteArraySource();
    val className = ClassName.fromClass(SBRMutationTarget.class);
    val mutators = singleton((MethodMutatorFactory) SBRMutator.SBR_MUTATOR);
    // Act
    val actual = new AstGregorMutater(any -> true, byteSource, mutators)
        .findMutations(className)
        .stream()
        .map(MutationDetails::getLineNumber)
        .collect(toList());
    // Assert
    assertThat(actual, contains(11, 14, 16, 17));
  }

  static Stream<Arguments> getMutationFixture() {
    val details = loadTargetDetails()
        .map(MutationDetails::getId)
        .collect(toCollection(LinkedList::new));
    assert details.size() == 4;
    return Stream.of(
        Arguments.of(details.pop(), "x", "y"),
        Arguments.of(details.pop(), "x", "y"),
        Arguments.of(details.pop(), "x", "y"),
        Arguments.of(details.pop(), "x", "y")
    );
  }

  @Disabled
  @ParameterizedTest(name = "{index}: ({1}) => ({2})")
  @MethodSource("getMutationFixture")
  void shouldGetMutationInClassFromMutatorId(MutationIdentifier mutationId, String original, String mutated) {
    // Arrange
    val byteSource = new ClassPathByteArraySource();
    val mutators = singleton((MethodMutatorFactory) SBRMutator.SBR_MUTATOR);
    // Act
    val actual = new AstGregorMutater(any -> true, byteSource, mutators)
        .getMutation(mutationId);
    // Assert
    assertThat(actual, replaces(original, mutated));
  }

  @SneakyThrows
  private static Stream<MutationDetails> loadTargetDetails() {
    val targetMutationsPath = Paths.get(SBRMutatorTest.class.getResource(TARGET_MUTATIONS).toURI());
    try (Reader reader = new FileReader(targetMutationsPath.toFile())) {
      return Stream.of(new Gson().fromJson(reader, MutationDetails[].class));
    }
  }
}
