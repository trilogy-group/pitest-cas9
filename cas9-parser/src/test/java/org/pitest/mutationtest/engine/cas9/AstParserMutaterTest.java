package org.pitest.mutationtest.engine.cas9;

import static java.util.Arrays.asList;
import static java.util.Collections.emptySet;
import static java.util.Collections.singleton;
import static java.util.stream.Collectors.toSet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.pitest.mutationtest.engine.gregor.mutators.ConditionalsBoundaryMutator.CONDITIONALS_BOUNDARY_MUTATOR;
import static org.pitest.mutationtest.engine.gregor.mutators.IncrementsMutator.INCREMENTS_MUTATOR;
import static org.pitest.mutationtest.engine.gregor.mutators.InvertNegsMutator.INVERT_NEGS_MUTATOR;
import static org.pitest.mutationtest.engine.gregor.mutators.MathMutator.MATH_MUTATOR;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.pitest.classinfo.ClassName;
import org.pitest.classpath.ClassPathByteArraySource;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.mutationtest.engine.gregor.MethodInfo;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;
import org.pitest.mutationtest.engine.gregor.config.Mutator;

@SuppressWarnings("unused")
class AstParserMutaterTest {

  static final Predicate<MethodInfo> ALL_METHODS = any -> true;

  static final Collection<MethodMutatorFactory> NEW_DEFAULTS = Mutator.newDefaults();

  static final Collection<MethodMutatorFactory> ALL_MUTATORS = Mutator.all();

  static final String TARGET_CLASS_INTERNAL_NAME = HasStatementsForAllNewDefaults.class.getName().replace('.', '/');

  enum HasEnumConstructor {

    VALUE(0);

    int value;

    HasEnumConstructor(int value) {
      this.value = ++value;
    }
  }

  static class HasComparisonInAssertAndNegativeInReturn {

    int doIt(int i) {
      assert (i > 10);
      return -i;
    }
  }

  static class HasExpressionInLambda {

    void print(Collection<Integer> numbers) {
      numbers.stream().map(n -> n + 1).forEach(System.out::println);
    }
  }

  static Stream<Arguments> findMutationsFixture() {
    return Stream.of(
        Arguments.of(HasStatementsForAllNewDefaults.class, NEW_DEFAULTS, getMutatorIds(NEW_DEFAULTS)),
        Arguments.of(HasEnumConstructor.class, singleton(INCREMENTS_MUTATOR), getMutatorIds(INCREMENTS_MUTATOR)),
        Arguments.of(HasComparisonInAssertAndNegativeInReturn.class,
            asList(CONDITIONALS_BOUNDARY_MUTATOR, INVERT_NEGS_MUTATOR), getMutatorIds(INVERT_NEGS_MUTATOR)),
        Arguments.of(HasExpressionInLambda.class, singleton(MATH_MUTATOR), getMutatorIds(MATH_MUTATOR)));
  }

  @ParameterizedTest
  @MethodSource("findMutationsFixture")
  void shouldFindMutationsInClassFromMutatorsWithExpectedIds(Class<?> classToMutate,
      Collection<MethodMutatorFactory> mutators, Collection<String> expectedIds) {
    // Arrange
    val byteSource = new ClassPathByteArraySource();
    val className = ClassName.fromClass(classToMutate);
    val expected = expectedIds.toArray(new String[0]);
    // Act
    val actual = new AstParserMutater(ALL_METHODS, byteSource, mutators)
        .findMutations(className)
        .stream()
        .map(MutationDetails::getMutator)
        .collect(toSet());
    // Assert
    assertThat(actual, containsInAnyOrder(expected));
  }

  static class HasStatementSuitableForMathMutation {

    int sum(int a, int b) {
      return a + b;
    }
  }

  enum HasGeneratedCodeOnly { FOO, BAR }

  static class HasExpressionInAssert {

    void doIt(int i) {
      assert ((i + 20) > 10);
    }
  }

  static Stream<Arguments> doNotFindMutationsFixture() {
    return Stream.of(
        Arguments.of(HasStatementSuitableForMathMutation.class, emptySet()),
        Arguments.of(HasGeneratedCodeOnly.class, ALL_MUTATORS),
        Arguments.of(HasExpressionInAssert.class, asList(MATH_MUTATOR, CONDITIONALS_BOUNDARY_MUTATOR))
    );
  }

  @ParameterizedTest
  @MethodSource("doNotFindMutationsFixture")
  void shouldFindNoMutationsInClassFromMutators(Class<?> classToMutate, Collection<MethodMutatorFactory> mutators) {
    // Arrange
    val byteSource = new ClassPathByteArraySource();
    val className = ClassName.fromClass(classToMutate);
    // Act
    val actual = new AstParserMutater(ALL_METHODS, byteSource, mutators)
        .findMutations(className);
    // Assert
    assertThat(actual, is(empty()));
  }

  private static Collection<String> getMutatorIds(MethodMutatorFactory... mutators) {
    return getMutatorIds(asList(mutators));
  }

  private static Collection<String> getMutatorIds(Collection<MethodMutatorFactory> mutators) {
    return mutators.stream()
        .map(MethodMutatorFactory::getGloballyUniqueId)
        .collect(Collectors.toSet());
  }
}
