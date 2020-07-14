package org.pitest.mutationtest.build.intercept.ast;

import java.util.Collection;
import java.util.Optional;
import lombok.Setter;
import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.mutationtest.build.InterceptorType;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationDetails;

enum ClassAstSettingsInterceptor implements MutationInterceptor {

  INTERCEPTOR;

  @Setter
  private ClassAstSource astSource;

  Optional<ClassAstSource> getAstSource() {
    return Optional.ofNullable(astSource);
  }

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
