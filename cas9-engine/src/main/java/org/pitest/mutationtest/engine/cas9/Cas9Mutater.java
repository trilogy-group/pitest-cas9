package org.pitest.mutationtest.engine.cas9;

import java.util.Collection;
import java.util.function.Predicate;
import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.mutationtest.engine.gregor.MethodInfo;
import org.pitest.mutationtest.engine.gregor.MethodMutatorFactory;

public class Cas9Mutater extends AstParserMutater {

  public Cas9Mutater(Predicate<MethodInfo> filter, ClassByteArraySource byteSource,
      Collection<MethodMutatorFactory> mutators) {
    super(filter, byteSource, mutators);
  }
}
