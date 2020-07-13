package org.pitest.mutationtest.build.intercept.limit;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.Comparator.comparingInt;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import lombok.Value;
import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.mutationtest.build.InterceptorParameters;
import org.pitest.mutationtest.build.InterceptorType;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.build.MutationInterceptorFactory;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.mutationtest.engine.cas9.config.Cas9Mutators;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;
import org.pitest.plugin.Feature;
import org.pitest.plugin.FeatureParameter;

public class LimitNumberOfMutationsPerLineFilterFactory implements MutationInterceptorFactory {

  private static final FeatureParameter LIMIT_PARAM = FeatureParameter
      .named("limit")
      .withDescription("Integer value for maximum mutations to create per line");

  private static final Integer DEFAULT_LIMIT = 1;

  private static final List<String> OPERATOR_PRIORITY = unmodifiableList(asList("ROR", "LCR", "SBR", "AOR", "UOI"));

  private static final List<String> ORDERED_MUTATORS = getMutatorsOrderedByPriority();

  @Override
  public MutationInterceptor createInterceptor(InterceptorParameters params) {
    int limit = params.getInteger(LIMIT_PARAM).orElse(DEFAULT_LIMIT);
    return LimitNumberOfMutationsPerLineFilter.of(limit);
  }

  @Override
  public Feature provides() {
    return Feature.named("LINELIMIT")
        .withOnByDefault(true)
        .withDescription("Limits the maximum number of mutations per line")
        .withParameter(LIMIT_PARAM);
  }

  @Override
  public String description() {
    return "Max mutations per line limit";
  }

  private static List<String> getMutatorsOrderedByPriority() {
    return OPERATOR_PRIORITY.stream()
        .map(Cas9Mutators::fromOperator)
        .flatMap(Collection::stream)
        .map(MethodMutatorFactory::getGloballyUniqueId)
        .collect(toList());
  }

  @Value(staticConstructor = "of")
  static class LimitNumberOfMutationsPerLineFilter implements MutationInterceptor {

    int maxMutationsPerLine;

    @Override
    public InterceptorType type() {
      return InterceptorType.FILTER;
    }

    @Override
    public void begin(ClassTree clazz) { }

    @Override
    public Collection<MutationDetails> intercept(Collection<MutationDetails> mutations, Mutater mutater) {
      Comparator<? super MutationDetails> byMutatorPriority =
          comparingInt(detail -> ORDERED_MUTATORS.indexOf(detail.getMutator()));

      return mutations.stream()
          .collect(groupingBy(MutationDetails::getLineNumber))
          .values().stream()
          .peek(details -> details.sort(byMutatorPriority))
          .flatMap(details -> details.stream().limit(maxMutationsPerLine))
          .collect(toList());
    }

    @Override
    public void end() { }
  }
}
