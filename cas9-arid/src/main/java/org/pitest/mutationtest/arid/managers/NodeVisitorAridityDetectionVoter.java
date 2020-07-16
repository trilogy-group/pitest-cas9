package org.pitest.mutationtest.arid.managers;

import static org.pitest.functional.prelude.Prelude.not;
import static org.pitest.mutationtest.arid.NodeAridity.ABSTAIN;
import static org.pitest.mutationtest.arid.NodeAridity.ARID;
import static org.pitest.mutationtest.arid.NodeAridity.RELEVANT;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.visitor.VoidVisitorWithDefaults;
import java.util.Collection;
import java.util.HashSet;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.pitest.mutationtest.arid.AridityDetectionVoter;
import org.pitest.mutationtest.arid.NodeAridity;

/**
 * A visitor that implements the simple/compound arid detection heuristic.
 * <p>
 * <table><tr><td rowspan="2"><i>arid</i>(N) =</td>
 *   <td><i>expert</i>(N)</td><td>if <i>simple</i>(N)</td></tr><tr>
 *   <td>1 if &and;(<i>arid</i>(b)) = 1, &forall;b &isin; N</td><td><i>otherwise</i></td></tr>
 * </table>
 * </p>
 */
@RequiredArgsConstructor
class NodeVisitorAridityDetectionVoter extends VoidVisitorWithDefaults<Consumer<NodeAridity>>
    implements AridityDetectionVoter {

  private final AridityDetectionVoter voter;

  @Override
  public NodeAridity vote(Node node) {
    val expressions = node.findAll(Expression.class);
    return expressions.isEmpty() ? voter.vote(node) : arid(expressions);
  }

  @Override
  public void defaultAction(Node node, Consumer<NodeAridity> action) {
    action.accept(vote(node));
  }

  @Override
  public void defaultAction(NodeList n, Consumer<NodeAridity> arg) {
    throw new UnsupportedOperationException();
  }

  private NodeAridity arid(Collection<? extends Node> nodes) {
    val decisions = new HashSet<NodeAridity>();
    nodes.forEach(node -> node.accept(this, decisions::add));
    val hasRelevant = decisions.stream()
        .filter(not(ABSTAIN::equals))
        .anyMatch(RELEVANT::equals);
    val defaultAridity = decisions.isEmpty() ? ABSTAIN : ARID;
    return hasRelevant ? RELEVANT : defaultAridity;
  }
}
