package org.pitest.mutationtest.engine.cas9;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationEngine;
import org.pitest.mutationtest.engine.cas9.config.Cas9Mutater;
import org.pitest.mutationtest.engine.gregor.MethodInfo;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;
import org.pitest.mutationtest.engine.gregor.MutationEngineConfiguration;

@RequiredArgsConstructor
public class Cas9MutationEngine implements MutationEngine {

  public static final String ENGINE_NAME = "cas9";

  private final Set<MethodMutatorFactory> operators;

  private final Predicate<MethodInfo> filter;

  public static MutationEngine withConfig(MutationEngineConfiguration config) {
    Set<MethodMutatorFactory> operators = new HashSet<>(config.mutators());
    return new Cas9MutationEngine(operators, config.methodFilter());
  }

  @Override
  public Mutater createMutator(ClassByteArraySource source) {
    return new Cas9Mutater(filter, source, operators);
  }

  @Override
  public Collection<String> getMutatorNames() {
    return operators.stream()
        .map(MethodMutatorFactory::getName)
        .collect(Collectors.toSet());
  }

  @Override
  public String getName() {
    return ENGINE_NAME;
  }
}
