package org.pitest.mutationtest.build.intercept.classrules;

import lombok.Value;
import org.pitest.mutationtest.engine.MutationDetails;

@Value
class LineRange {

  Integer first;

  Integer last;

  boolean validate(MutationDetails details) {
    int line = details.getLineNumber();
    return line >= first && line <= last;
  }
}
