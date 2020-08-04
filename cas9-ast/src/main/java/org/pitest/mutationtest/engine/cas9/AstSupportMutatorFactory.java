package org.pitest.mutationtest.engine.cas9;

import lombok.val;
import org.pitest.mutationtest.engine.gregor.MethodInfo;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;
import org.pitest.mutationtest.engine.gregor.MutationContext;
import org.pitest.reloc.asm.MethodVisitor;

public interface AstSupportMutatorFactory extends MethodMutatorFactory {

  MethodVisitor create(MutationContext context, AstNodeTracker astTracker, MethodAstInfo astInfo,
      MethodInfo methodInfo, MethodVisitor visitor);

  default MethodVisitor create(MutationContext context, MethodInfo methodInfo, MethodVisitor visitor) {
    if (context instanceof MethodAstInfoSource) {
      val astContext = (MethodAstInfoSource) context;
      val astTracker = astContext.getAstNodeTracker(methodInfo);
      return astContext.getMethodAstInfo(methodInfo)
          .map(astInfo -> create(context, astTracker, astInfo, methodInfo, visitor))
          .orElse(visitor);
    }
    return visitor;
  }
}
