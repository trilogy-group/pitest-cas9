package org.pitest.mutationtest.build.intercept.coverage;

import java.util.Collection;
import java.util.stream.Collectors;
import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.mutationtest.build.InterceptorParameters;
import org.pitest.mutationtest.build.InterceptorType;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.build.MutationInterceptorFactory;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.plugin.Feature;

public class NoCoverageMutationFilterFactory implements MutationInterceptorFactory {

  @Override
  public MutationInterceptor createInterceptor(InterceptorParameters params) {
    return null;
  }

  @Override
  public Feature provides() {
    return Feature.named("FCCOVL")
        .withOnByDefault(true)
        .withDescription("Filters out mutations in lines not covered by any unit test");
  }

  @Override
  public String description() {
    return "Uncovered lines filter";
  }

  static class NoCoverageMutationFilter implements MutationInterceptor {

    @Override
    public InterceptorType type() {
      return InterceptorType.FILTER;
    }

    @Override
    public void begin(ClassTree clazz) { }

    @Override
    public Collection<MutationDetails> intercept(Collection<MutationDetails> mutations, Mutater m) {
      return mutations.stream()
          .filter(details -> details.getTestsInOrder().isEmpty())
          .collect(Collectors.toList());
    }

    @Override
    public void end() { }
  }
}
