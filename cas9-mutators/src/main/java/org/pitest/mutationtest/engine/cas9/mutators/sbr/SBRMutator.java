package org.pitest.mutationtest.engine.cas9.mutators.sbr;

import org.pitest.mutationtest.engine.cas9.AstSupportMutatorFactory;
import org.pitest.mutationtest.engine.cas9.MethodAstInfo;
import org.pitest.mutationtest.engine.gregor.MethodInfo;
import org.pitest.mutationtest.engine.gregor.MutationContext;
import org.pitest.reloc.asm.MethodVisitor;

public enum SBRMutator implements AstSupportMutatorFactory {
  SBR_MUTATOR;

  @Override
  public MethodVisitor create(MutationContext context, MethodAstInfo astInfo, MethodInfo methodInfo,
      MethodVisitor visitor) {
    return new SBRMethodVisitor(visitor, astInfo);
  }

  @Override
  public String getGloballyUniqueId() {
    return this.getClass().getName();
  }

  @Override
  public String getName() {
    return name();
  }
}
