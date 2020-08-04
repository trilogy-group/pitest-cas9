package org.pitest.mutationtest.build.intercept.arid;

import org.pitest.mutationtest.arid.AridityDetectionManagerFactory;
import org.pitest.mutationtest.arid.managers.StandardAridityDetectionManagerFactory;
import org.pitest.plugin.Feature;
import org.pitest.plugin.FeatureSetting;

public class StandardAridNodeMutationFilterFactory extends AbstractAridNodeMutationFilterFactory {

  static final String FEATURE_NAME = "FSARID";

  @Override
  protected AridityDetectionManagerFactory createManagerFactory(FeatureSetting feature) {
    return new StandardAridityDetectionManagerFactory();
  }

  @Override
  public Feature provides() {
    return Feature
        .named(FEATURE_NAME)
        .withOnByDefault(true)
        .withDescription("Filters out mutations based on predefined rules for arid nodes detection");
  }

  @Override
  public String description() {
    return "Arid nodes standard rules filter";
  }
}
