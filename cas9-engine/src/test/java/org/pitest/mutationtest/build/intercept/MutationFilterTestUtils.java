package org.pitest.mutationtest.build.intercept;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.substringAfterLast;

import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.stream.Stream;
import org.pitest.classinfo.ClassName;
import org.pitest.mutationtest.engine.Location;
import org.pitest.mutationtest.engine.MethodName;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.mutationtest.engine.MutationIdentifier;
import org.pitest.mutationtest.engine.cas9.mutators.Cas9Mutators;

public class MutationFilterTestUtils {

  public static final ClassName TEST_CLASS_NAME = ClassName.fromString("com.test.Xyz");

  private MutationFilterTestUtils() {
    throw new UnsupportedOperationException();
  }

  public static Stream<MutationDetails> mutations(int lineNumber, String... operators) {
    Location location = new Location(TEST_CLASS_NAME, MethodName.fromString("doIt"), "()V");
    return Stream.of(operators)
        .map(Cas9Mutators::fromOperator)
        .map(Iterables::getLast)
        .map(factory -> new MutationIdentifier(location, emptyList(), factory.getGloballyUniqueId()))
        .map(id -> new MutationDetails(id, null, "", lineNumber, 0));
  }

  public static Iterable<String> getOperators(Collection<MutationDetails> details) {
    return details.stream()
        .map(detail -> substringAfterLast(detail.getMutator(), ".").substring(0, 3))
        .collect(toList());
  }

  public static Iterable<String> getMethods(Collection<MutationDetails> details) {
    return details.stream()
        .map(detail -> detail.getMethod().name())
        .collect(toList());
  }

  public static Iterable<Integer> getLineNumbers(Collection<MutationDetails> details) {
    return details.stream()
        .map(MutationDetails::getLineNumber)
        .collect(toList());
  }
}
