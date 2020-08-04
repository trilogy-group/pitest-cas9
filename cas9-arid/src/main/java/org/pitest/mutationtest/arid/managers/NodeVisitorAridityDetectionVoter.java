package org.pitest.mutationtest.arid.managers;

import static com.github.javaparser.ast.Node.TreeTraversal.DIRECT_CHILDREN;
import static java.util.Collections.singleton;
import static org.pitest.mutationtest.arid.NodeAridity.ABSTAIN;
import static org.pitest.mutationtest.arid.NodeAridity.ARID;
import static org.pitest.mutationtest.arid.NodeAridity.RELEVANT;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorWithDefaults;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
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

  private static final Predicate<Node> IS_STATEMENT = Statement.class::isInstance;

  private static final Set<NodeAridity> ARID_ONLY = singleton(ARID);

  private final AridityDetectionVoter voter;

  @Override
  public NodeAridity vote(Node node) {
    val expressions = new ArrayList<Node>();
    node.walk(DIRECT_CHILDREN, IS_STATEMENT.and(expressions::add)::test);
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
    val hasOnlyArid = decisions.equals(ARID_ONLY);
    val defaultAridity = decisions.contains(RELEVANT) ? RELEVANT : ABSTAIN;
    return hasOnlyArid ? ARID : defaultAridity;
  }
}
