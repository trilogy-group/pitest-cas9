package org.pitest.mutationtest.build.intercept.ast;

import static java.util.stream.Collectors.toMap;

import com.github.javaparser.ast.Node;
import java.util.AbstractMap.SimpleEntry;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import lombok.val;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.mutationtest.engine.MutationIdentifier;

public interface AstSupportMutationInterceptor extends MutationInterceptor {

  default Collection<MutationDetails> intercept(Collection<MutationDetails> mutations, Mutater mutater) {
    ClassAstSource source = ClassAstSource.getDefault();

    BiFunction<Node, Integer, Boolean> containsLine = (node, line) ->
        node.getRange()
            .filter(range -> range.begin.line == line)
            .isPresent();
    BiPredicate<Node, MutationDetails> matchesMutation = (node, mutation) ->
        containsLine.apply(node, mutation.getLineNumber());

    Function<MutationDetails, Optional<Node>> nodeForMutation = mutation -> source.getAst(mutation)
        .flatMap(type -> type.findFirst(Node.class, node -> matchesMutation.test(node, mutation)));

    val nodeById = mutations.stream()
        .map(mutation -> new SimpleEntry<>(mutation.getId(), nodeForMutation.apply(mutation)))
        .filter(entry -> entry.getValue().isPresent())
        .collect(toMap(
            SimpleEntry::getKey,
            entry -> entry.getValue().get()));

    return intercept(mutations, nodeById, mutater);
  }

  Collection<MutationDetails> intercept(Collection<MutationDetails> mutations,
      Map<MutationIdentifier, Node> nodeById, Mutater mutater);
}
