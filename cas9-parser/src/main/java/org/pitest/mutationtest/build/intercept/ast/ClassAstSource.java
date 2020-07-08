package org.pitest.mutationtest.build.intercept.ast;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import java.util.Optional;

public interface ClassAstSource {

  Optional<ClassOrInterfaceDeclaration> getAst(String clazz, String fileName);
}
