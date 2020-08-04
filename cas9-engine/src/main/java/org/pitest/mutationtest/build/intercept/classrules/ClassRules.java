package org.pitest.mutationtest.build.intercept.classrules;

import java.util.ArrayList;
import java.util.List;
import lombok.Value;
import org.pitest.mutationtest.engine.MutationDetails;

@Value
class ClassRules {

  List<LineRange> ranges = new ArrayList<>();

  boolean validate(MutationDetails details) {
    return ranges.stream()
        .anyMatch(range -> range.contains(details));
  }

  void checkValid() throws InvalidClassRuleException {
    for (LineRange range : ranges) {
      range.checkValid();
    }
  }
}
