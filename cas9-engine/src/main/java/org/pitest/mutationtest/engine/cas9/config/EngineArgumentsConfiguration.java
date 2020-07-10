package org.pitest.mutationtest.engine.cas9.config;

import static lombok.AccessLevel.PRIVATE;
import static org.pitest.functional.prelude.Prelude.not;

import java.util.Collection;
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
class EngineArgumentsConfiguration implements MutationEngineConfiguration {

  private final Collection<? extends MethodMutatorFactory> mutators;

  private final Predicate<MethodInfo> methodFilter;

  static MutationEngineConfiguration config(EngineArguments arguments) {
    val mutators = arguments.mutators() == null
        ? Mutator.all() : Mutator.fromNames(arguments.mutators());

    val excludedNames = Prelude.or(Glob.toGlobPredicates(arguments.excludedMethods()));

    return new EngineArgumentsConfiguration(mutators, not(info -> excludedNames.test(info.getName())));
  }
}
