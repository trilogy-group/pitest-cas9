package org.pitest.mutationtest.engine.cas9;

import java.util.Optional;
import org.pitest.mutationtest.engine.gregor.MethodInfo;

interface MethodAstInfoSource {

  Optional<MethodAstInfo> getMethodAstInfo(MethodInfo methodInfo);

  // TODO: move out from this interface
  AstNodeTracker getAstNodeTracker(MethodInfo methodInfo);
}
