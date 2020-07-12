package org.pitest.mutationtest.build.intercept.coverage;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.val;
import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.coverage.CoverageDatabase;
import org.pitest.mutationtest.build.InterceptorType;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationDetails;

enum CoveredLinesOnlyFilter implements MutationInterceptor {

  FILTER;

  private final List<CoverageDatabase> coverages = new ArrayList<>();

  synchronized void addCoverage(CoverageDatabase coverage) {
    coverages.add(coverage);
  }

  boolean hasCoverage(MutationDetails details) {
    val line = details.getClassLine();
    return coverages.stream()
        .map(coverage -> coverage.getTestsForClassLine(line))
        .anyMatch(tests -> !tests.isEmpty());
  }

  @Override
  public InterceptorType type() {
    return InterceptorType.FILTER;
  }

  @Override
  public void begin(ClassTree clazz) { }

  @Override
  public Collection<MutationDetails> intercept(Collection<MutationDetails> mutations, Mutater mutater) {
    return mutations.stream()
        .filter(this::hasCoverage)
        .collect(Collectors.toList());
  }

  @Override
  public void end() { }
}
