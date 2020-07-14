package org.pitest.mutationtest.build.intercept.classrules;

import lombok.Value;
import org.pitest.mutationtest.engine.MutationDetails;

@Value
class LineRange {

  Integer first;

  Integer last;

  boolean contains(MutationDetails details) {
    int line = details.getLineNumber();
    return line >= first && line <= last;
  }

  void checkValid() throws InvalidClassRuleException {
    if (first == null || last == null) {
      throw new InvalidClassRuleException("Line ranges must specify the first and last lines.");
    }

    if (first < 0) {
      throw new InvalidClassRuleException("The first line in a range must be greater than zero.");
    }

    if (first > last) {
      throw new InvalidClassRuleException("The last line in a range must be greater than or equal to the first line.");
    }
  }
}
