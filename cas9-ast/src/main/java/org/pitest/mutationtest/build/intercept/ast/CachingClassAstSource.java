package org.pitest.mutationtest.build.intercept.ast;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import lombok.Value;

@Value
class CachingClassAstSource implements ClassAstSource {

  private static final int MAX_ENTRIES = 200;

  Map<String, ClassOrInterfaceDeclaration> cache = new LinkedHashMap<String, ClassOrInterfaceDeclaration>() {
    @Override
    protected boolean removeEldestEntry(Entry eldest) {
      return size() > MAX_ENTRIES;
    }
  };

  ClassAstSource child;

  @Override
  public Optional<ClassOrInterfaceDeclaration> getAst(String clazz, String fileName) {
    if (!cache.containsKey(clazz)) {
      child.getAst(clazz, fileName)
          .ifPresent(type -> cache.put(clazz, type));
    }
    return Optional.ofNullable(cache.get(clazz));
  }
}
