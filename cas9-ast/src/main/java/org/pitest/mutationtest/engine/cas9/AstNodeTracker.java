package org.pitest.mutationtest.engine.cas9;

import com.github.javaparser.ast.stmt.Statement;
import java.util.Optional;

public interface AstNodeTracker {

  Optional<Statement> getCurrentStatement();
}
