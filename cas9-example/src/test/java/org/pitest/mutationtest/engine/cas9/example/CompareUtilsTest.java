package org.pitest.mutationtest.engine.cas9.example;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class CompareUtilsTest {

  @Test
  void compare() {
    int a = 0;
    int b = 1;
    int c = 2;
    boolean actual = CompareUtils.compare(a, b, c);
    assertTrue(actual);
  }
}
