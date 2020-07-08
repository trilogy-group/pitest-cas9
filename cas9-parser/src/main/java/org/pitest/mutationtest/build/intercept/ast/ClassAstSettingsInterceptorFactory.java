package org.pitest.mutationtest.build.intercept.ast;

import java.util.Collection;
import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.mutationtest.build.InterceptorParameters;
import org.pitest.mutationtest.build.InterceptorType;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.build.MutationInterceptorFactory;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.plugin.Feature;

public class ClassAstSettingsInterceptorFactory implements MutationInterceptorFactory {

  @Override
  public MutationInterceptor createInterceptor(InterceptorParameters params) {
    ClassAstSettingsFactory.initialize(params.data());
    return NoOpMutationInterceptor.INTERCEPTOR;
  }

  @Override
  public Feature provides() {
    return Feature.named("AST")
        .withOnByDefault(true)
        .withDescription("Parses the source code of the target class as an AST object");
  }

  @Override
  public String description() {
    return "Source code AST provider plugin";
  }

  private enum NoOpMutationInterceptor implements MutationInterceptor {

    INTERCEPTOR;

    @Override
    public InterceptorType type() {
      return InterceptorType.OTHER;
    }

    @Override
    public void begin(ClassTree clazz) { }

    @Override
    public Collection<MutationDetails> intercept(Collection<MutationDetails> mutations, Mutater m) {
      return mutations;
    }

    @Override
    public void end() { }
  }
}
