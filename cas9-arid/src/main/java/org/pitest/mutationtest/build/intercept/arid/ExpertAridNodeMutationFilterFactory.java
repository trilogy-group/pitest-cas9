package org.pitest.mutationtest.build.intercept.arid;

import lombok.val;
import org.pitest.mutationtest.arid.AridityDetectionManagerFactory;
import org.pitest.mutationtest.arid.managers.AridityDetectionMode;
import org.pitest.mutationtest.arid.managers.ExpertAridityDetectionManagerFactory;
import org.pitest.plugin.Feature;
import org.pitest.plugin.FeatureParameter;
import org.pitest.plugin.FeatureSetting;
import org.pitest.util.IsolationUtils;

public class ExpertAridNodeMutationFilterFactory extends AbstractAridNodeMutationFilterFactory {

  static final String FEATURE_NAME = "FEARID";

  static final FeatureParameter MODE_PARAM = FeatureParameter
      .named("mode")
      .withDescription("Arid node detection mode");

  @Override
  protected AridityDetectionManagerFactory createManagerFactory(FeatureSetting feature) {
    val loader = IsolationUtils.getContextClassLoader();
    val mode = feature.getString(MODE_PARAM.name())
        .map(AridityDetectionMode::valueOf)
        .orElse(AridityDetectionMode.AFFIRMATIVE);
    return new ExpertAridityDetectionManagerFactory(loader, mode);
  }

  @Override
  public Feature provides() {
    return Feature.named(FEATURE_NAME)
        .withParameter(MODE_PARAM)
        .withDescription("Filters out mutations based on custom rules for arid nodes detection");
  }

  @Override
  public String description() {
    return "Arid nodes standard rules filter";
  }
}
