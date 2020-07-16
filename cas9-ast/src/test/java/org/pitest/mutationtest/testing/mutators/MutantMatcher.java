package org.pitest.mutationtest.testing.mutators;

import static java.lang.System.getProperty;
import static java.util.stream.Collectors.joining;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.printer.PrettyPrinterConfiguration;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.concurrent.atomic.LongAdder;
import java.util.function.UnaryOperator;
import lombok.SneakyThrows;
import lombok.val;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsEqual;
import org.pitest.classinfo.ClassName;
import org.pitest.mutationtest.engine.Mutant;

/**
 * Tests if the mutant corresponds to the replacement of the statement in mutated line
 * of the corresponding source file with a given expression.
 * <p>
 *   Example: {@code assertThat(mutant, replaces("a > b", "a >= b"))}
 * </p>
 */
public class MutantMatcher extends TypeSafeMatcher<Mutant> {

  public static final String SOURCE_DIR_PROPERTY = "cas9-test.sources";

  static final PrettyPrinterConfiguration CODE_PRINT_CONFIG = new PrettyPrinterConfiguration()
      .setPrintComments(false)
      .setIndentSize(2);

  private final UnaryOperator<String> replacer;

  private Matcher<String> equalsMatcher;

  public MutantMatcher(final String original, final String mutation) {
    replacer = line -> line.replace(original, mutation);
  }

  public static Matcher<Mutant> replaces(String original, String mutation) {
    return new MutantMatcher(original, mutation);
  }

  @Override
  public void describeTo(Description description) {
    if (equalsMatcher == null) {
      throw new IllegalStateException("Cannot describe before calling matches");
    }
    equalsMatcher.describeTo(description);
  }

  @Override
  protected boolean matchesSafely(Mutant mutant) {
    val actual = decompile(mutant);
    val expected = replaceCode(mutant, replacer);

    equalsMatcher = IsEqual.equalTo(expected);
    return equalsMatcher.matches(actual);
  }

  @Override
  protected void describeMismatchSafely(Mutant mutant, Description description) {
    if (equalsMatcher == null) {
      throw new IllegalStateException("Cannot describe before calling matches");
    }
    val actual = decompile(mutant);
    equalsMatcher
        .describeMismatch(actual, description);
  }

  private static String decompile(Mutant mutant) {
    return normalized(mutant.getDetails().getClassName(), MutantDecompiler.decompile(mutant));
  }

  @SneakyThrows
  private static String replaceCode(final Mutant mutant, final UnaryOperator<String> replacer) {
    val details = mutant.getDetails();
    val packagePath = details.getClassName()
        .getPackage()
        .asInternalName();
    val filePath = Paths.get(getProperty(SOURCE_DIR_PROPERTY), packagePath, details.getFilename());
    val index = new LongAdder();
    val modified = Files.readAllLines(filePath).stream()
        .peek(line -> index.increment())
        .map(line -> index.intValue() == details.getLineNumber() ? replacer.apply(line) : line)
        .collect(joining(System.lineSeparator()));
    return normalized(details.getClassName(), modified);
  }

  private static String normalized(ClassName name, String code) {
    val simpleName = name
        .getNameWithoutPackage()
        .asJavaName();

    return StaticJavaParser.parse(code)
        .getClassByName(simpleName)
        .map(type -> type.setAnnotations(NodeList.nodeList()))
        .map(node -> node.toString(CODE_PRINT_CONFIG))
        .orElse(code);
  }
}
