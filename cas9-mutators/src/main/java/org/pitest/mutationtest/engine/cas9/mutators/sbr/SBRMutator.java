package org.pitest.mutationtest.engine.cas9.mutators.sbr;

import org.pitest.mutationtest.engine.cas9.AstNodeTracker;
import org.pitest.mutationtest.engine.cas9.AstSupportMutatorFactory;
import org.pitest.mutationtest.engine.cas9.MethodAstInfo;
import org.pitest.mutationtest.engine.gregor.MethodInfo;
import org.pitest.mutationtest.engine.gregor.MutationContext;
import org.pitest.reloc.asm.MethodVisitor;

public enum SBRMutator implements AstSupportMutatorFactory {

  SBR_MUTATOR;

  @Override
  public MethodVisitor create(MutationContext context, AstNodeTracker astTracker, MethodAstInfo astInfo,
      MethodInfo methodInfo, MethodVisitor visitor) {
    return new SBRMethodVisitor(this, context, astTracker, visitor);
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
