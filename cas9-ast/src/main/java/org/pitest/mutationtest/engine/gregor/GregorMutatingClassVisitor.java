package org.pitest.mutationtest.engine.gregor;

import java.util.Collection;
import java.util.function.Predicate;
import org.pitest.reloc.asm.ClassVisitor;

/***
 * Exposes {@link MutatingClassVisitor} for extending Gregor behavior.
 */
public class GregorMutatingClassVisitor extends MutatingClassVisitor {

  public GregorMutatingClassVisitor(ClassVisitor delegateClassVisitor, ClassContext context,
      Predicate<MethodInfo> filter, Collection<MethodMutatorFactory> mutators) {
    super(delegateClassVisitor, context, filter, mutators);
  }
}
