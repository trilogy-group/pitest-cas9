package org.pitest.mutationtest.engine.cas9;

import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.CallableDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedParameterDeclaration;
import java.util.Arrays;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.Value;
import lombok.experimental.Delegate;
import lombok.val;
import org.objectweb.asm.Type;
import org.pitest.mutationtest.build.intercept.ast.ClassAstSource;
import org.pitest.mutationtest.engine.gregor.GregorClassContext;
import org.pitest.mutationtest.engine.gregor.MethodInfo;
import org.pitest.mutationtest.engine.gregor.MutationContext;

@Value(staticConstructor = "of")
class AstSourceMutationContext implements MutationContext, MethodAstInfoSource {

  @NonNull
  @Delegate(types = MutationContext.class)
  MutationContext mutationContext;

  @NonNull
  GregorClassContext classContext;

  @NonNull
  ClassAstSource source;

  @Override
  public Optional<MethodAstInfo> getMethodAstInfo(MethodInfo methodInfo) {
    if (methodInfo.isGeneratedEnumMethod() || methodInfo.isStaticInitializer() || methodInfo.isSynthetic()) {
      return Optional.empty();
    }

    val classInfo = mutationContext.getClassInfo();
    val fileName = classContext.getFileName();

    if (classInfo == null || fileName == null) {
      return Optional.empty();
    }

    return source.getAst(classInfo.getName(), fileName)
        .flatMap(classAst -> findMemberByMethodInfo(classAst, methodInfo)
            .map(methodAst -> MethodAstInfo.of(classAst, methodAst)));
  }

  private static Optional<CallableDeclaration<?>> findMemberByMethodInfo(
      final ClassOrInterfaceDeclaration type, final MethodInfo methodInfo) {
    val paramTypes = Stream.of(Type.getArgumentTypes(methodInfo.getMethodDescriptor()))
        .map(Type::getClassName)
        .toArray(String[]::new);

    Predicate<BodyDeclaration<?>> isMethodDeclaration = BodyDeclaration::isMethodDeclaration;
    Predicate<BodyDeclaration<?>> isMethodWithName =
        ((Function<BodyDeclaration<?>, MethodDeclaration>) BodyDeclaration::asMethodDeclaration)
            .andThen(MethodDeclaration::getNameAsString)
            .andThen(methodInfo.getName()::equals)::apply;

    Predicate<BodyDeclaration<?>> matchesName =
        isMethodDeclaration.and(isMethodWithName).or(BodyDeclaration::isConstructorDeclaration);
    Predicate<BodyDeclaration<?>> matchesParameters =
        ((Function<BodyDeclaration<?>, CallableDeclaration<?>>) CallableDeclaration.class::cast)
            .andThen(member -> isCallableWithParameters(member, paramTypes))::apply;

    return type.getMembers().stream()
        .filter(matchesName.and(matchesParameters))
        .findFirst()
        .map(member -> (CallableDeclaration<?>) member);
  }

  private static boolean isCallableWithParameters(final CallableDeclaration<?> member, final String[] paramTypes) {
    if (member.getParameters().size() != paramTypes.length) {
      return false;
    }

    try {
      String[] resolvedTypes = member.getParameters().stream()
          .map(Parameter::resolve)
          .map(ResolvedParameterDeclaration::describeType)
          .toArray(String[]::new);
      return Arrays.equals(paramTypes, resolvedTypes);
    } catch (UnsolvedSymbolException e) {
      return false;
    }
  }
}
