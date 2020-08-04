package org.pitest.mutationtest.engine.cas9.example.integration;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.Map;
import org.junit.jupiter.api.Test;

public class GregorCoverageIT {

  @Test
  void shouldGenerateExpectedCoverageReport() throws Exception {
    final File file = new File(System.getProperty("pitest.report.file"));
    final Map<String, Map<String, Long>> coverage = Mutations.loadFromXml(file);
    assertAll(
        () -> assertEquals(4, coverage.get("AOR").get("KILLED")),
        () -> assertEquals(37, coverage.get("ROR").get("KILLED")),
        () -> assertEquals(23, coverage.get("ROR").get("SURVIVED")),
        () -> assertEquals(39, coverage.get("UOI").get("KILLED")),
        () -> assertEquals(53, coverage.get("UOI").get("SURVIVED")),
        () -> assertEquals(8, coverage.get("UOI").get("NO_COVERAGE")));
  }
}
