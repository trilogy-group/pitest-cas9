package org.pitest.mutationtest.build.intercept.classrules;

import com.google.gson.Gson;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import org.pitest.mutationtest.engine.MutationDetails;

class ClassRules {

  public static final Gson GSON = new Gson();

  private List<LineRange> ranges = new ArrayList<>();

  void checkIsValid() {
    if (ranges == null || ranges.isEmpty()) {
      throw new RuleException("At least one line range must be specified");
    }
    ranges.forEach(LineRange::checkValid);
  }

  static ClassRules fromResourceName(String name) {
    InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(name);
    if (input == null) {
      throw new RuleException("Cannot find class rules resource with name: " + name);
    }
    try (Reader reader = new InputStreamReader(input)) {
      ClassRules rules = GSON.fromJson(reader, ClassRules.class);
      rules.checkIsValid();
      return rules;
    } catch (IOException e) {
      throw new RuleException("Cannot load class rules resource with name: " + name);
    }
  }

  boolean validate(MutationDetails details) {
    return ranges.stream()
        .anyMatch(range -> range.validate(details));
  }
}
