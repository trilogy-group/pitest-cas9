package org.pitest.mutationtest.engine.cas9;

import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import lombok.Value;

@Value(staticConstructor = "of")
public class MethodAstInfo {

  ClassOrInterfaceDeclaration classAst;

  CallableDeclaration<?> methodAst;
}
