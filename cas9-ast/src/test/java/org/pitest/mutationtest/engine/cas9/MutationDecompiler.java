package org.pitest.mutationtest.engine.cas9;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.printer.PrettyPrinterConfiguration;
import lombok.Value;
import lombok.val;
import org.jd.core.v1.ClassFileToJavaSourceDecompiler;
import org.jd.core.v1.api.Decompiler;
import org.jd.core.v1.api.loader.Loader;
import org.jd.core.v1.api.loader.LoaderException;
import org.jd.core.v1.api.printer.Printer;
import org.pitest.classinfo.ClassName;
import org.pitest.mutationtest.engine.Mutant;

@Value(staticConstructor = "of")
class MutationDecompiler {

  static final PrettyPrinterConfiguration CODE_PRINT_CONFIG = new PrettyPrinterConfiguration()
      .setPrintComments(false)
      .setIndentSize(2);
  ClassName targetName;

  Decompiler decompiler = new ClassFileToJavaSourceDecompiler();

  String decompile(Mutant mutant) throws Exception {
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
    decompiler.decompile(loader, printer, targetName.asJavaName());
    return StaticJavaParser.parse(builder.toString())
        .getClassByName(targetName.getNameWithoutPackage().asJavaName())
        .map(node -> node.toString(CODE_PRINT_CONFIG))
        .orElseThrow(IllegalArgumentException::new);
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
