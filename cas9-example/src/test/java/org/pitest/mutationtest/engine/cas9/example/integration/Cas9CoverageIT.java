package org.pitest.mutationtest.engine.cas9.example.integration;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.Map;
import org.junit.jupiter.api.Test;

class Cas9CoverageIT {

  @Test
  void shouldGenerateExpectedCoverageReport() throws Exception {
    final File file = new File(System.getProperty("pitest.report.file"));
    final Map<String, Map<String, Long>> coverage = Mutations.loadFromXml(file);
    assertAll(
        () -> assertEquals(1, coverage.get("AOR").get("KILLED")),
        () -> assertEquals(4, coverage.get("ROR").get("KILLED")),
        () -> assertEquals(3, coverage.get("ROR").get("SURVIVED")),
        () -> assertEquals(3, coverage.get("UOI").get("SURVIVED")));
  }
}
