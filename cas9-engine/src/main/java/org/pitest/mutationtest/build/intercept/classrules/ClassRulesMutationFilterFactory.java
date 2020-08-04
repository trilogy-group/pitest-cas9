package org.pitest.mutationtest.build.intercept.classrules;

import lombok.val;
import org.pitest.mutationtest.build.InterceptorParameters;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.build.MutationInterceptorFactory;
import org.pitest.plugin.Feature;
import org.pitest.plugin.FeatureParameter;

public class ClassRulesMutationFilterFactory implements MutationInterceptorFactory {

  static final String FEATURE_NAME = "FCINCL";

  static final FeatureParameter ROOT_PARAM = FeatureParameter
      .named("root")
      .withDescription("Name of the root folder of class rule resources");

  private static final String DEFAULT_ROOT_PATH = "/cas9/";

  @Override
  public MutationInterceptor createInterceptor(InterceptorParameters params) {
    val rootPath = params.getString(ROOT_PARAM)
        .map(root -> "/" + root + "/")
        .orElse(DEFAULT_ROOT_PATH);
    return new ClassRulesMutationFilter(rootPath, params.data().getClassPath());
  }

  @Override
  public Feature provides() {
    return Feature.named(FEATURE_NAME)
        .withOnByDefault(true)
        .withDescription("Filters out mutations based on class-specific inclusion rules")
        .withParameter(ROOT_PARAM);
  }

  @Override
  public String description() {
    return "Class-level mutation inclusion rules";
  }
}
