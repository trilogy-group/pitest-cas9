package org.pitest.mutationtest.arid.voters;

import static org.pitest.mutationtest.arid.NodeAridity.ABSTAIN;
import static org.pitest.mutationtest.arid.NodeAridity.ARID;
import static org.pitest.util.Log.getLogger;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import java.util.logging.Level;
import lombok.val;
import org.pitest.mutationtest.arid.AridityDetectionVoter;
import org.pitest.mutationtest.arid.NodeAridity;

public class MethodCallAridityVoter implements AridityDetectionVoter {

  @Override
  public NodeAridity vote(Node node) {
    return node instanceof MethodCallExpr
        ? arid((MethodCallExpr) node)
        : ABSTAIN;
  }

  private static NodeAridity arid(MethodCallExpr expr) {
    try {
      val declaringType = expr.resolve().declaringType();
      return declaringType.isJavaLangObject() || declaringType.isJavaLangEnum() ? ARID : ABSTAIN;
    } catch (UnsolvedSymbolException e) {
      getLogger().log(Level.WARNING, "Could not resolve expression.", e);
      return ABSTAIN;
    }
  }
}
