package org.pitest.mutationtest.arid.managers;

import static java.util.stream.Collectors.toList;

import com.github.javaparser.ast.DataKey;
import com.github.javaparser.ast.Node;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.val;
import org.pitest.mutationtest.arid.AridityDetectionManager;
import org.pitest.mutationtest.arid.AridityDetectionVoter;
import org.pitest.mutationtest.arid.NodeAridity;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.mutationtest.engine.MutationIdentifier;

abstract class AbstractAridityDetectionManager implements AridityDetectionManager {

  private static final DataKey<NodeAridity> NODE_ARIDITY_KEY = new DataKey<NodeAridity>() { };

  private final Function<MutationIdentifier, Node> mapper;

  private final Collection<AridityDetectionVoter> voters;

  protected AbstractAridityDetectionManager(@NonNull Function<MutationIdentifier, Node> mapper,
      @NonNull Collection<AridityDetectionVoter> voters) {
    this.mapper = mapper;
    this.voters = voters.stream()
        .map(NodeVisitorAridityDetectionVoter::new)
        .collect(toList());
  }

  @Override
  public boolean decide(@NonNull MutationDetails details) {
    val node = mapper.apply(details.getId());
    if (node == null) {
      return defaultAridity(details) == NodeAridity.ARID;
    }
    if (node.containsData(NODE_ARIDITY_KEY)) {
      return node.getData(NODE_ARIDITY_KEY) == NodeAridity.ARID;
    }
    val decisions = voters.stream()
        .map(voter -> voter.vote(node))
        .peek(decision -> node.setData(NODE_ARIDITY_KEY, decision))
        .filter(decision -> decision != NodeAridity.ABSTAIN);
    return decide(decisions);
  }

  protected abstract boolean decide(Stream<NodeAridity> votes);

  protected abstract NodeAridity defaultAridity(MutationDetails details);
}
