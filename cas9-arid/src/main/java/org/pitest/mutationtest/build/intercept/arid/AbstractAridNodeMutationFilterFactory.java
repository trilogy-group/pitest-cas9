package org.pitest.mutationtest.build.intercept.arid;

import static java.util.Collections.emptyMap;
import static org.pitest.plugin.ToggleStatus.ACTIVATE;
import static org.pitest.plugin.ToggleStatus.DEACTIVATE;

import lombok.val;
import org.pitest.mutationtest.arid.AridityDetectionManagerFactory;
import org.pitest.mutationtest.build.InterceptorParameters;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.build.MutationInterceptorFactory;
import org.pitest.mutationtest.build.intercept.ast.ClassAstSettingsInterceptorFactory;
import org.pitest.plugin.FeatureParser;
import org.pitest.plugin.FeatureSetting;

public abstract class AbstractAridNodeMutationFilterFactory implements MutationInterceptorFactory {

  private static final FeatureSetting EMPTY_FEATURE = new FeatureSetting("EMPTY", DEACTIVATE, emptyMap());

  private static final String AST_FEATURE_NAME = ClassAstSettingsInterceptorFactory.FEATURE_NAME;

  private static MutationInterceptor classAstInterceptor;

  @Override
  public MutationInterceptor createInterceptor(InterceptorParameters params) {
    ensureAstReady(params);
    val feature = params.settings().orElse(EMPTY_FEATURE);
    val factory = createManagerFactory(feature);
    return new AridNodeMutationFilter(factory);
  }

  protected abstract AridityDetectionManagerFactory createManagerFactory(FeatureSetting feature);

  private static synchronized void ensureAstReady(InterceptorParameters params) {
    if (classAstInterceptor == null) {
      val features = params.data().getFeatures();
      val hasAstFeature = features != null && !features.isEmpty() && new FeatureParser()
          .parseFeatures(features).stream()
          .anyMatch(setting -> AST_FEATURE_NAME.equals(setting.feature()) && setting.status() == ACTIVATE);
      if (!hasAstFeature) {
        classAstInterceptor = new ClassAstSettingsInterceptorFactory().createInterceptor(params);
      }
    }
  }
}
