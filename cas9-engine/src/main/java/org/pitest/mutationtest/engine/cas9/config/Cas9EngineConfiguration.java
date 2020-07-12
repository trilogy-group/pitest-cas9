package org.pitest.mutationtest.engine.cas9.config;

import static lombok.AccessLevel.PRIVATE;
import static org.pitest.functional.prelude.Prelude.not;

import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Accessors;
import lombok.val;
import org.pitest.functional.prelude.Prelude;
import org.pitest.mutationtest.EngineArguments;
import org.pitest.mutationtest.engine.gregor.MethodInfo;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;
import org.pitest.mutationtest.engine.gregor.MutationEngineConfiguration;
import org.pitest.util.Glob;

@Getter
@Accessors(fluent = true)
@RequiredArgsConstructor(access = PRIVATE)
class Cas9EngineConfiguration implements MutationEngineConfiguration {

  private final Collection<? extends MethodMutatorFactory> mutators;

  private final Predicate<MethodInfo> methodFilter;

  static MutationEngineConfiguration fromArguments(EngineArguments arguments) {
    val operators = arguments.mutators() == null ? Collections.<String>emptySet() : arguments.mutators();
    val mutators = Cas9Mutators.fromOperators(operators);

    val excludedNames = Prelude.or(Glob.toGlobPredicates(arguments.excludedMethods()));

    return new Cas9EngineConfiguration(mutators, not(info -> excludedNames.test(info.getName())));
  }
}
