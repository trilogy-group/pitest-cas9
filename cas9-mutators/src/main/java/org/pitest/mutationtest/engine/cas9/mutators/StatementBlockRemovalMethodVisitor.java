package org.pitest.mutationtest.engine.cas9.mutators;

import lombok.Getter;
import org.pitest.bytecode.ASMVersion;
import org.pitest.mutationtest.engine.cas9.MethodAstInfo;
import org.pitest.reloc.asm.MethodVisitor;

class StatementBlockRemovalMethodVisitor extends MethodVisitor {

  @Getter
  private final MethodAstInfo astInfo;

  public StatementBlockRemovalMethodVisitor(MethodVisitor methodVisitor, MethodAstInfo astInfo) {
    super(ASMVersion.ASM_VERSION, methodVisitor);
    this.astInfo = astInfo;
  }
}
