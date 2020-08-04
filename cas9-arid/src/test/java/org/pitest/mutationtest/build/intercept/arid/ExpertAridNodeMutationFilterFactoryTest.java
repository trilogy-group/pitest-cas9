package org.pitest.mutationtest.build.intercept.arid;

import static java.lang.System.getProperty;
import static java.util.Collections.singleton;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.pitest.mutationtest.build.intercept.arid.ExpertAridMutationTarget.MUTATED_LINE;
import static org.pitest.mutationtest.build.intercept.arid.ExpertAridNodeMutationFilterFactory.FEATURE_NAME;
import static org.pitest.mutationtest.build.intercept.arid.ExpertAridNodeMutationFilterFactory.MODE_PARAM;
import static org.pitest.plugin.ToggleStatus.ACTIVATE;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.stmt.Statement;
import java.io.File;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.pitest.classinfo.ClassName;
import org.pitest.classpath.ClassPathByteArraySource;
import org.pitest.mutationtest.arid.AridityDetectionVoter;
import org.pitest.mutationtest.arid.NodeAridity;
import org.pitest.mutationtest.arid.managers.AridityDetectionMode;
import org.pitest.mutationtest.build.InterceptorParameters;
import org.pitest.mutationtest.build.intercept.ast.ClassAstSource;
import org.pitest.mutationtest.config.ReportOptions;
import org.pitest.mutationtest.engine.Location;
import org.pitest.mutationtest.engine.MethodName;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.mutationtest.engine.MutationIdentifier;
import org.pitest.plugin.FeatureSetting;

@SuppressWarnings("unused")
class ExpertAridNodeMutationFilterFactoryTest {

  private static final String SOURCE_DIR_PROPERTY = "cas9-test.sources";

  private static final String DEFAULT_SOURCE_DIR = "src/test/java";

  public static final String MUTATION_TARGET_FILE = "ExpertAridMutationTarget.java";

  @AfterEach
  void tearDown() {
    ClassAstSource.getDefault()
        .getAst(ExpertAridMutationTarget.class.getName(), MUTATION_TARGET_FILE)
        .ifPresent(node -> node.walk(Statement.class,
            stmt -> stmt.getDataKeys().stream()
                .filter(key -> stmt.getData(key) instanceof NodeAridity)
                .findFirst()
                .ifPresent(stmt::removeData)));
  }

  static Stream<Arguments> getModeAndRemoveFixture() {
    return Stream.of(
        arguments(AridityDetectionMode.AFFIRMATIVE, true),
        arguments(AridityDetectionMode.CONSENSUS, false),
        arguments(AridityDetectionMode.UNANIMOUS, false));
  }

  @ParameterizedTest
  @MethodSource("getModeAndRemoveFixture")
  void createdInterceptorShouldFilterMutationUsingMode(final AridityDetectionMode mode, final boolean removeMutation) {
    // Arrange
    val feature = new FeatureSetting(FEATURE_NAME, ACTIVATE,
        singletonMap(MODE_PARAM.name(), singletonList(mode.toString())));
    val options = new ReportOptions();
    val sourceDir = new File(getProperty(SOURCE_DIR_PROPERTY, DEFAULT_SOURCE_DIR));
    val byteSource = new ClassPathByteArraySource();
    val params = new InterceptorParameters(feature, options, byteSource);
    val mutation = new MutationDetails(new MutationIdentifier(new Location(
        ClassName.fromClass(ExpertAridMutationTarget.class), MethodName.fromString("any"), "V()"), 0, "MUTATOR_ID"),
        MUTATION_TARGET_FILE, "Single mutation", MUTATED_LINE, 0);
    val expected = removeMutation ? 0 : 1;
    options.setFeatures(singleton("+" + FEATURE_NAME));
    options.setSourceDirs(singleton(sourceDir));
    // Act
    val actual = new ExpertAridNodeMutationFilterFactory().createInterceptor(params)
        .intercept(singleton(mutation), null);
    // Assert
    assertThat(actual, hasSize(expected));
  }

  public static class AlwaysAbstainVoter implements AridityDetectionVoter {

    @Override
    public NodeAridity vote(Node node) {
      return NodeAridity.ABSTAIN;
    }
  }

  public static class AlwaysAridVoter implements AridityDetectionVoter {

    @Override
    public NodeAridity vote(Node node) {
      return NodeAridity.ARID;
    }
  }

  public static class AlwaysRelevantVoter implements AridityDetectionVoter {

    @Override
    public NodeAridity vote(Node node) {
      return NodeAridity.RELEVANT;
    }
  }
}
