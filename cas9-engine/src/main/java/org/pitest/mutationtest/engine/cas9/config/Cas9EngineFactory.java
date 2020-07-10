package org.pitest.mutationtest.engine.cas9.config;

import static org.pitest.mutationtest.engine.cas9.config.EngineArgumentsConfiguration.config;

import org.pitest.mutationtest.EngineArguments;
import org.pitest.mutationtest.MutationEngineFactory;
import org.pitest.mutationtest.engine.MutationEngine;
import org.pitest.mutationtest.engine.cas9.Cas9MutationEngine;

public class Cas9EngineFactory implements MutationEngineFactory {

  @Override
  public MutationEngine createEngine(EngineArguments arguments) {
    return Cas9MutationEngine.of(config(arguments));
  }

  @Override
  public String name() {
    return Cas9MutationEngine.ENGINE_NAME;
  }

  @Override
  public String description() {
    return "Enhanced mutation targeting engine";
  }

}
