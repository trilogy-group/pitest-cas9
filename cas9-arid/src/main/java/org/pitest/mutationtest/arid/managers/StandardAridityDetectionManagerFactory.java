package org.pitest.mutationtest.arid.managers;

import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableCollection;

import com.github.javaparser.ast.Node;
import java.util.Collection;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.pitest.mutationtest.arid.AridityDetectionManager;
import org.pitest.mutationtest.arid.AridityDetectionManagerFactory;
import org.pitest.mutationtest.arid.AridityDetectionVoter;
import org.pitest.mutationtest.arid.voters.AccessorStmtAridityVoter;
import org.pitest.mutationtest.arid.voters.MethodCallAridityVoter;
import org.pitest.mutationtest.arid.voters.ThrowStmtAridityVoter;
import org.pitest.mutationtest.engine.MutationIdentifier;

@RequiredArgsConstructor
public class StandardAridityDetectionManagerFactory implements AridityDetectionManagerFactory {

  private static final Collection<AridityDetectionVoter> VOTERS = unmodifiableCollection(asList(
      new AccessorStmtAridityVoter(),
      new MethodCallAridityVoter(),
      new ThrowStmtAridityVoter()));

  @Override
  public AridityDetectionManager createManager(Function<MutationIdentifier, Node> mapper) {
    return new AffirmativeManager(mapper, VOTERS);
  }
}
