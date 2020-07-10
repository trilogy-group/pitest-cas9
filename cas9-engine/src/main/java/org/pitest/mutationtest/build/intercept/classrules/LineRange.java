package org.pitest.mutationtest.build.intercept.classrules;

import lombok.Value;
import org.pitest.mutationtest.engine.MutationDetails;

@Value
class LineRange {

  Integer first;

  Integer last;

  void checkValid() {
    if (first == null || last == null) {
      throw new RuleException("Line ranges must specify first and last lines");
    }

    if (first <= 0) {
      throw new RuleException("First line in line ranges must be greater than zero");
    }

    if (last < first) {
      throw new RuleException("Last line in line ranges must be greater or equal to the first line");
    }
  }

  boolean validate(MutationDetails details) {
    int line = details.getLineNumber();
    return line >= first && line <= last;
  }
}
