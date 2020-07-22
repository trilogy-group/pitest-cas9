package org.pitest.mutationtest.engine.gregor;

import static org.pitest.functional.prelude.Prelude.not;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.NonNull;
import lombok.val;
import org.pitest.bytecode.NullVisitor;
import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.classinfo.ClassName;
import org.pitest.mutationtest.engine.Mutant;
import org.pitest.mutationtest.engine.Mutater;
import org.pitest.mutationtest.engine.MutationDetails;
import org.pitest.mutationtest.engine.MutationIdentifier;
import org.pitest.reloc.asm.ClassReader;
import org.pitest.reloc.asm.ClassVisitor;
import org.pitest.reloc.asm.ClassWriter;

/**
 * A replacement for the original {@code GregorMutater} that allows to change
 * the {@link ClassContext} and {@link ClassWriter} created during mutation generation.
 */
public abstract class AbstractGregorMutater implements Mutater {

  public static final NullVisitor NOOP_VISITOR = new NullVisitor();

  private final Predicate<MethodInfo> filter;

  private final ClassByteArraySource byteSource;

  private final Set<MethodMutatorFactory> mutators;

  protected AbstractGregorMutater(@NonNull Predicate<MethodInfo> filter, @NonNull ClassByteArraySource byteSource,
      @NonNull Collection<MethodMutatorFactory> mutators) {
    this.byteSource = byteSource;
    this.mutators = Collections.unmodifiableSet(new HashSet<>(mutators));
    this.filter = filter
        .and(not(MethodInfo::isSynthetic).or(info -> info.getName().startsWith("lambda$")))
        .and(not(MethodInfo::isGeneratedEnumMethod))
        .and(not(MethodInfo::isInGroovyClass));
  }

  @Override
  public List<MutationDetails> findMutations(ClassName classToMutate) {
    val className = classToMutate.asInternalName();
    return byteSource.getBytes(className)
        .map(bytes -> mutations(null, bytes, mutators, NOOP_VISITOR))
        .orElse(Stream.empty())
        .collect(Collectors.toList());
  }

  @Override
  public Mutant getMutation(MutationIdentifier id) {
    val className = id.getClassName().asInternalName();
    val writer = createWriter(className);
    val mutatorForId = mutators.stream()
        .filter(mutator -> id.getMutator().equals(mutator.getGloballyUniqueId()))
        .collect(Collectors.toList());
    return byteSource.getBytes(className)
        .flatMap(bytes -> mutations(id, bytes, mutatorForId, writer)
            .filter(details -> details.matchesId(id))
            .findFirst()
            .map(details -> new Mutant(details, writer.toByteArray())))
        .orElseThrow(IllegalArgumentException::new);
  }

  protected abstract GregorClassContext createContext();

  protected abstract GregorMutatingClassVisitor createMutatingVisitor(ClassVisitor visitor, GregorClassContext context,
      Predicate<MethodInfo> filter, Collection<MethodMutatorFactory> mutators);

  protected abstract ClassWriter createWriter(String className);

  private Stream<MutationDetails> mutations(MutationIdentifier id, byte[] classBytes,
      Collection<MethodMutatorFactory> mutators, ClassVisitor visitor) {
    val reader = new ClassReader(classBytes);
    val context = createContext();
    context.setTargetMutation(Optional.ofNullable(id));
    val mutatingVisitor = createMutatingVisitor(visitor, context, filter, mutators);
    reader.accept(mutatingVisitor, ClassReader.EXPAND_FRAMES);
    return context.getCollectedMutations().stream();
  }
}
