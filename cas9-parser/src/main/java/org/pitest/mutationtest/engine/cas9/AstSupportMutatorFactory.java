package org.pitest.mutationtest.engine.cas9;

import org.pitest.mutationtest.engine.gregor.MethodInfo;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;
import org.pitest.mutationtest.engine.gregor.MutationContext;
import org.pitest.reloc.asm.MethodVisitor;

public interface AstSupportMutatorFactory extends MethodMutatorFactory {

  MethodVisitor create(MutationContext context, MethodAstInfo astInfo, MethodInfo methodInfo, MethodVisitor visitor);

  default MethodVisitor create(MutationContext context, MethodInfo methodInfo, MethodVisitor visitor) {
    if (context instanceof MethodAstInfoSource) {
      return ((MethodAstInfoSource) context)
          .getMethodAstInfo(methodInfo)
          .map(astInfo -> create(context, astInfo, methodInfo, visitor))
          .orElse(visitor);
    }
    return visitor;
  }
}
