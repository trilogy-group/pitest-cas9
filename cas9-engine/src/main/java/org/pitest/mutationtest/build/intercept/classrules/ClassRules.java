package org.pitest.mutationtest.build.intercept.classrules;

import java.util.ArrayList;
import java.util.List;
import org.pitest.mutationtest.engine.MutationDetails;

class ClassRules {

  private List<LineRange> ranges = new ArrayList<>();

  boolean validate(MutationDetails details) {
    return ranges.stream()
        .anyMatch(range -> range.validate(details));
  }
}
