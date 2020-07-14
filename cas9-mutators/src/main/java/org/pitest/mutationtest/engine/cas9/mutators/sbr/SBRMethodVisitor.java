package org.pitest.mutationtest.engine.cas9.mutators.sbr;

import lombok.Getter;
import org.pitest.bytecode.ASMVersion;
import org.pitest.mutationtest.engine.cas9.MethodAstInfo;
import org.pitest.reloc.asm.MethodVisitor;

class SBRMethodVisitor extends MethodVisitor {

  @Getter
  private final MethodAstInfo astInfo;

  public SBRMethodVisitor(MethodVisitor methodVisitor, MethodAstInfo astInfo) {
    super(ASMVersion.ASM_VERSION, methodVisitor);
    this.astInfo = astInfo;
  }
}
