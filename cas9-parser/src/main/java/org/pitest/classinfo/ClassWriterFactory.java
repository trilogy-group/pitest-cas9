package org.pitest.classinfo;

import org.pitest.reloc.asm.ClassWriter;

public interface ClassWriterFactory {

  ClassWriter createWriter(String className);
}
