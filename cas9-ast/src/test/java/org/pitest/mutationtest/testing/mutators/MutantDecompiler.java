package org.pitest.mutationtest.testing.mutators;

import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.Value;
import lombok.val;
import org.jd.core.v1.ClassFileToJavaSourceDecompiler;
import org.jd.core.v1.api.Decompiler;
import org.jd.core.v1.api.loader.Loader;
import org.jd.core.v1.api.loader.LoaderException;
import org.jd.core.v1.api.printer.Printer;
import org.pitest.mutationtest.engine.Mutant;

final class MutantDecompiler {

  private static final Decompiler DECOMPILER = new ClassFileToJavaSourceDecompiler();

  private MutantDecompiler() {
    throw new UnsupportedOperationException();
  }

  @SneakyThrows
  static String decompile(@NonNull final Mutant mutant) {
    val targetName = mutant.getDetails().getClassName();
    val loader = new Loader() {
      @Override
      public boolean canLoad(String internalName) {
        return targetName.asInternalName().equals(internalName);
      }

      @Override
      public byte[] load(String internalName) throws LoaderException {
        return mutant.getBytes();
      }
    };
    val builder = new StringBuilder();
    val printer = new FlatCodePrinter(builder);
    DECOMPILER.decompile(loader, printer, targetName.asJavaName());
    return builder.toString();
  }

  @Value
  static class FlatCodePrinter implements Printer {

    StringBuilder builder;

    @Override
    public void start(int maxLineNumber, int majorVersion, int minorVersion) { }

    @Override
    public void end() { }

    @Override
    public void printText(String text) {
      builder.append(text);
    }

    @Override
    public void printNumericConstant(String constant) {
      builder.append(constant);
    }

    @Override
    public void printStringConstant(String constant, String ownerInternalName) {
      builder.append(constant);
    }

    @Override
    public void printKeyword(String keyword) {
      builder.append(keyword);
    }

    @Override
    public void printDeclaration(int type, String internalTypeName, String name, String descriptor) {
      builder.append(name);
    }

    @Override
    public void printReference(int type, String internalTypeName, String name, String descriptor,
        String ownerInternalName) {
      builder.append(name);
    }

    @Override
    public void indent() { }

    @Override
    public void unindent() { }

    @Override
    public void startLine(int lineNumber) { }

    @Override
    public void endLine() {
      builder.append(System.lineSeparator());
    }

    @Override
    public void extraLine(int count) { builder.append(System.lineSeparator()); }

    @Override
    public void startMarker(int type) { }

    @Override
    public void endMarker(int type) { }
  }
}
