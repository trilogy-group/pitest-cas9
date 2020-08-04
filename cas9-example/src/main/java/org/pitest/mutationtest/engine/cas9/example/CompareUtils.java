package org.pitest.mutationtest.engine.cas9.example;

class CompareUtils {

  static boolean compare(int a, int b, int c) {
    boolean tA = a < b;
    boolean tB = b < c;
    boolean tC = c > 0;
    return tA && tB && tC;
  }
}
