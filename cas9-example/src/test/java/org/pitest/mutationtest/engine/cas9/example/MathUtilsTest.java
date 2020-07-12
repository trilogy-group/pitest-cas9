package org.pitest.mutationtest.engine.cas9.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MathUtilsTest {

  @Test
  void calcutate() {
    int a = 2;
    int b = 4;
    int actual = MathUtils.calcutate(a, b);
    assertEquals(-8, actual);
  }
}
