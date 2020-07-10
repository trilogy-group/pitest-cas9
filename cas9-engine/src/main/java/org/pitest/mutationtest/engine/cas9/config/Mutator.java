package org.pitest.mutationtest.engine.cas9.config;

import static java.util.Collections.unmodifiableCollection;
import static java.util.stream.Collectors.toList;
import static org.pitest.mutationtest.engine.cas9.mutators.LogicalConnectorReplacementMutator.LCR_MUTATOR;
import static org.pitest.mutationtest.engine.cas9.mutators.StatementBlockRemovalMutator.SBR_MUTATOR;
import static org.pitest.mutationtest.engine.gregor.config.Mutator.aor;
import static org.pitest.mutationtest.engine.gregor.config.Mutator.ror;
import static org.pitest.mutationtest.engine.gregor.config.Mutator.uoi;

import java.util.ArrayList;
import java.util.Collection;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;

@UtilityClass
public class Mutator {

  private final Collection<MethodMutatorFactory> ALL_MUTATORS = getSupportedMutators();

  public Collection<MethodMutatorFactory> all() {
    return ALL_MUTATORS;
  }

  public Collection<MethodMutatorFactory> fromNames(@NonNull Collection<String> mutators) {
    if (mutators.isEmpty()) {
      return all();
    }

    return mutators.stream()
        .map(Mutator::fromName)
        .collect(toList());
  }

  private MethodMutatorFactory fromName(@NonNull String name) {
    return ALL_MUTATORS.stream()
        .filter(op -> name.equals(op.getGloballyUniqueId()))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Invalid operator id: " + name));
  }

  private Collection<MethodMutatorFactory> getSupportedMutators() {
    Collection<MethodMutatorFactory> mutators = new ArrayList<>();
    mutators.add(LCR_MUTATOR);
    mutators.addAll(uoi());
    mutators.addAll(ror());
    mutators.add(SBR_MUTATOR);
    mutators.addAll(aor());
    return unmodifiableCollection(mutators);
  }
}
