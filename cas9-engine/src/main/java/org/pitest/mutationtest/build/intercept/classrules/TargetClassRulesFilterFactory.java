package org.pitest.mutationtest.build.intercept.classrules;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.mutationtest.build.InterceptorParameters;
import org.pitest.mutationtest.build.InterceptorType;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.build.MutationInterceptorFactory;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.plugin.Feature;
import org.pitest.plugin.FeatureParameter;

public class TargetClassRulesFilterFactory implements MutationInterceptorFactory {

  private static final FeatureParameter ROOT_PARAM = FeatureParameter
      .named("root")
      .withDescription("Name of the root folder of class rule resources");

  private static final String DEFAULT_ROOT = "cas9";

  @Override
  public MutationInterceptor createInterceptor(InterceptorParameters params) {
    val root = params.getString(ROOT_PARAM).orElse(DEFAULT_ROOT);
    return new TargetClassRulesFilter(root);
  }

  @Override
  public Feature provides() {
    return Feature.named("FCINCL")
        .withOnByDefault(true)
        .withDescription("Filters out mutations based on class-specific inclusion rules")
        .withParameter(ROOT_PARAM);
  }

  @Override
  public String description() {
    return "Class-level mutation inclusion rules";
  }

  @RequiredArgsConstructor
  static class TargetClassRulesFilter implements MutationInterceptor {

    @NonNull
    private final String root;

    Map<String, ClassRules> rulesMap = new HashMap<>();

    @Override
    public InterceptorType type() {
      return InterceptorType.FILTER;
    }

    @Override
    public void begin(ClassTree clazz) { }

    @Override
    public Collection<MutationDetails> intercept(Collection<MutationDetails> mutations, Mutater m) {
      return mutations.stream()
          .filter(this::validate)
          .collect(Collectors.toList());
    }

    @Override
    public void end() { }

    private synchronized boolean validate(MutationDetails details) {
      val className = details.getClassName().asJavaName();
      val resourceName = "/" + root + "/" + className;
      val classRules = rulesMap.computeIfAbsent(resourceName, ClassRules::fromResourceName);
      return classRules.validate(details);
    }
  }
}
