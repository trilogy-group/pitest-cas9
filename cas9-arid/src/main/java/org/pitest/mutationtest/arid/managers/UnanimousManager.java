package org.pitest.mutationtest.arid.managers;

import com.github.javaparser.ast.Node;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;
import org.pitest.mutationtest.arid.AridityDetectionVoter;
import org.pitest.mutationtest.arid.NodeAridity;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.mutationtest.engine.MutationIdentifier;

class UnanimousManager extends AbstractAridityDetectionManager {

  public UnanimousManager(Function<MutationIdentifier, Node> mapper, Collection<AridityDetectionVoter> voters) {
    super(mapper, voters);
  }

  @Override
  protected boolean decide(Stream<NodeAridity> votes) {
    return votes.allMatch(NodeAridity.ARID::equals);
  }

  @Override
  protected NodeAridity defaultAridity(MutationDetails details) {
    return NodeAridity.ARID;
  }
}
