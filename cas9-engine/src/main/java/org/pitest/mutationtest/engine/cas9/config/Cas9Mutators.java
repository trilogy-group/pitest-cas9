package org.pitest.mutationtest.engine.cas9.config;

import static java.util.Collections.singleton;
import static java.util.Collections.unmodifiableMap;
import static java.util.stream.Collectors.toSet;
import static org.pitest.mutationtest.engine.cas9.mutators.LogicalConnectorReplacementMutator.LCR_MUTATOR;
import static org.pitest.mutationtest.engine.cas9.mutators.StatementBlockRemovalMutator.SBR_MUTATOR;
import static org.pitest.mutationtest.engine.gregor.config.Mutator.aor;
import static org.pitest.mutationtest.engine.gregor.config.Mutator.ror;
import static org.pitest.mutationtest.engine.gregor.config.Mutator.uoi;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import lombok.val;
import org.pitest.help.Help;
import org.pitest.help.PitHelpError;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;

@UtilityClass
public class Cas9Mutators {

  private final Map<String, Collection<MethodMutatorFactory>> MUTATORS = getMutatorMap();

  public Collection<MethodMutatorFactory> fromOperator(@NonNull String name) {
    if (!MUTATORS.containsKey(name)) {
      throw new PitHelpError(Help.UNKNOWN_MUTATOR, name);
    }
    return MUTATORS.get(name);
  }

  public Collection<MethodMutatorFactory> fromOperators(@NonNull Collection<String> operators) {
    if (operators.isEmpty()) {
      return fromOperators(MUTATORS.keySet());
    }

    return operators.stream()
        .map(Cas9Mutators::fromOperator)
        .flatMap(Collection::stream)
        .collect(toSet());
  }

  private Map<String, Collection<MethodMutatorFactory>> getMutatorMap() {
    val mutatorByName = new HashMap<String, Collection<MethodMutatorFactory>>();
    mutatorByName.put("LCR", singleton(LCR_MUTATOR));
    mutatorByName.put("UOI", uoi());
    mutatorByName.put("ROR", ror());
    mutatorByName.put("SBR", singleton(SBR_MUTATOR));
    mutatorByName.put("AOR", aor());
    return unmodifiableMap(mutatorByName);
  }
}
