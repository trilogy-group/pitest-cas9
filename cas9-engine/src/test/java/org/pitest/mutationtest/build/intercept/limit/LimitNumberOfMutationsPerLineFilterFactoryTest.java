package org.pitest.mutationtest.build.intercept.limit;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static java.util.function.UnaryOperator.identity;
import static java.util.stream.Collectors.toList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.pitest.mutationtest.build.intercept.MutationFilterTestUtils.getLineNumbers;
import static org.pitest.mutationtest.build.intercept.MutationFilterTestUtils.getOperators;
import static org.pitest.mutationtest.build.intercept.MutationFilterTestUtils.mutations;
import static org.pitest.mutationtest.build.intercept.limit.LimitNumberOfMutationsPerLineFilterFactory.FEATURE_NAME;
import static org.pitest.mutationtest.build.intercept.limit.LimitNumberOfMutationsPerLineFilterFactory.LIMIT_PARAM;
import static org.pitest.plugin.ToggleStatus.ACTIVATE;

import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.Test;
import org.pitest.mutationtest.build.InterceptorParameters;
import org.pitest.plugin.FeatureSetting;

class LimitNumberOfMutationsPerLineFilterFactoryTest {

  @Test
  void shouldFilterToSingleMutationPerLine() {
    // Arrange
    val conf = new FeatureSetting(FEATURE_NAME, ACTIVATE, emptyMap());
    val params = new InterceptorParameters(conf, null, null);
    val mutations = Stream.of(
        mutations(1, "ROR", "UOI", "LCR"),
        mutations(2, "AOR", "UOI"),
        mutations(3, "UOI"))
        .flatMap(identity())
        .collect(toList());
    // Act
    val actual = new LimitNumberOfMutationsPerLineFilterFactory()
        .createInterceptor(params)
        .intercept(mutations, null);
    // Assert
    assertAll(
        () -> assertThat(getOperators(actual), contains("ROR", "AOR", "UOI")),
        () -> assertThat(getLineNumbers(actual), contains(1, 2, 3))
    );
  }

  @Test
  void shouldFilterUpToThreeMutationsPerLine() {
    // Arrange
    val conf = new FeatureSetting(FEATURE_NAME, ACTIVATE,
        singletonMap(LIMIT_PARAM.name(), singletonList("3")));
    val params = new InterceptorParameters(conf, null, null);
    val mutations = Stream.of(
        mutations(1, "UOI", "AOR", "SBR", "LCR", "ROR"),
        mutations(2, "ROR", "ROR", "ROR", "ROR", "LCR", "LCR"),
        mutations(3, "UOI", "AOR"),
        mutations(4, "ROR"))
        .flatMap(identity())
        .collect(toList());
    // Act
    val actual = new LimitNumberOfMutationsPerLineFilterFactory()
        .createInterceptor(params)
        .intercept(mutations, null);
    // Assert
    assertAll(
        () -> assertThat(getOperators(actual), contains("ROR", "LCR", "SBR", "ROR", "ROR", "ROR", "AOR", "UOI", "ROR")),
        () -> assertThat(getLineNumbers(actual), contains(1, 1 , 1, 2, 2, 2, 3, 3, 4)));
  }
}
