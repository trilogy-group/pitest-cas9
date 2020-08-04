package org.pitest.mutationtest.build.intercept.classrules;

import com.google.gson.Gson;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.val;
import org.pitest.bytecode.analysis.ClassTree;
import org.pitest.classinfo.ClassName;
import org.pitest.classpath.ClassPath;
import org.pitest.mutationtest.build.InterceptorType;
import org.pitest.mutationtest.build.MutationInterceptor;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.util.PitError;

@RequiredArgsConstructor
class ClassRulesMutationFilter implements MutationInterceptor {

  private static final Predicate<MutationDetails> NON_FILTERED_VALIDATOR = details -> true;

  private static final Gson GSON = new Gson();

  @NonNull
  private final String rootPath;

  @NonNull
  private final ClassPath classPath;

  private final Map<ClassName, Predicate<MutationDetails>> filterByClass = new HashMap<>();

  @Override
  public InterceptorType type() {
    return InterceptorType.FILTER;
  }

  @Override
  public void begin(ClassTree clazz) { }

  @Override
  public Collection<MutationDetails> intercept(Collection<MutationDetails> mutations, Mutater mutater) {
    return mutations.stream()
        .filter(this::validate)
        .collect(Collectors.toList());
  }

  @Override
  public void end() { }

  private synchronized boolean validate(MutationDetails details) {
    return filterByClass.computeIfAbsent(details.getClassName(),
        name -> findRulesFile(name)
            .map(this::loadRulesFromFile)
            .orElse(NON_FILTERED_VALIDATOR))
        .test(details);
  }

  @SneakyThrows
  private Optional<Path> findRulesFile(ClassName name) {
    val resourceName = rootPath + name.asJavaName() + ".json";
    val resourceUrl = classPath.findResource(resourceName);
    return resourceUrl == null
        ? Optional.empty()
        : Optional.of(Paths.get(resourceUrl.toURI()));
  }

  @SneakyThrows
  private Predicate<MutationDetails> loadRulesFromFile(Path path) {
    try (Reader reader = Files.newBufferedReader(path)) {
      val rules = GSON.fromJson(reader, ClassRules.class);
      rules.checkValid();
      return rules::validate;
    } catch (InvalidClassRuleException e) {
      throw new PitError("Error while loading file " + path, e);
    }
  }
}
