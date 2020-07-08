package org.pitest.mutationtest.engine.gregor;

import java.util.HashMap;
import java.util.Map;
import lombok.NonNull;
import lombok.Value;
import org.pitest.bytecode.FrameOptions;
import org.pitest.classinfo.ClassByteArraySource;
import org.pitest.classinfo.ClassWriterFactory;
import org.pitest.classinfo.ComputeClassWriter;
import org.pitest.reloc.asm.ClassWriter;

/***
 * A Gregor compatible factory of {@link ClassWriter} objects.
 * See {@link AbstractGregorMutater#createWriter(String)}.
 */
@Value
public class GregorClassWriterFactory implements ClassWriterFactory {

  @NonNull
  ClassByteArraySource byteSource;

  Map<String, String> computeCache = new HashMap<>();

  @Override
  public ClassWriter createWriter(String className) {
    int flags = byteSource.getBytes(className)
        .map(FrameOptions::pickFlags)
        .orElseThrow(IllegalArgumentException::new);
    return new ComputeClassWriter(byteSource, computeCache, flags);
  }
}
