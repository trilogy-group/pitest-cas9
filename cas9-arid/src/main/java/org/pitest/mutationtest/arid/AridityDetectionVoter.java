package org.pitest.mutationtest.arid;

import com.github.javaparser.ast.Node;

public interface AridityDetectionVoter {

  NodeAridity vote(Node node);
}
