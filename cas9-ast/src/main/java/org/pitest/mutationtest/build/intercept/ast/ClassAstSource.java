package org.pitest.mutationtest.build.intercept.ast;

import static org.pitest.mutationtest.build.intercept.ast.ClassAstSettingsInterceptor.INTERCEPTOR;

import com.github.javaparser.ast.body.TypeDeclaration;
import java.util.Optional;
import org.pitest.classinfo.ClassName;
import org.pitest.mutationtest.engine.MutationDetails;

public interface ClassAstSource {

  Optional<TypeDeclaration<?>> getAst(ClassName className, String fileName);

  default Optional<TypeDeclaration<?>> getAst(String clazz, String fileName) {
    return getAst(ClassName.fromString(clazz), fileName);
  }

  default Optional<TypeDeclaration<?>> getAst(MutationDetails details) {
    return getAst(details.getClassName(), details.getFilename());
  }

  static ClassAstSource getDefault() {
    return INTERCEPTOR.getAstSource()
        .orElse((name, file) -> Optional.empty());
  }
}
