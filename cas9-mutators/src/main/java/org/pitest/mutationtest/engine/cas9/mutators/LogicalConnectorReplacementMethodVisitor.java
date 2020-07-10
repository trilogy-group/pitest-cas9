package org.pitest.mutationtest.engine.cas9.mutators;

import java.util.Collections;
import java.util.Map;
import lombok.Getter;
import org.pitest.mutationtest.engine.cas9.MethodAstInfo;
import org.pitest.mutationtest.engine.gregor.AbstractInsnMutator;
import org.pitest.mutationtest.engine.gregor.MethodInfo;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;
import org.pitest.mutationtest.engine.gregor.MutationContext;
import org.pitest.mutationtest.engine.gregor.ZeroOperandMutation;
import org.pitest.reloc.asm.MethodVisitor;

class LogicalConnectorReplacementMethodVisitor extends AbstractInsnMutator {

  @Getter
  private final MethodAstInfo astInfo;

  public LogicalConnectorReplacementMethodVisitor(MethodMutatorFactory factory, MethodAstInfo astInfo,
      MethodInfo methodInfo, MutationContext context, MethodVisitor delegateMethodVisitor) {
    super(factory, methodInfo, context, delegateMethodVisitor);
    this.astInfo = astInfo;
  }

  @Override
  protected Map<Integer, ZeroOperandMutation> getMutations() {
    return Collections.emptyMap();
  }
}
