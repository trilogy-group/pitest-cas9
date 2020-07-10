package org.pitest.mutationtest.engine.cas9.example;

class MathUtils {

  static int calcutate(int a, int b) {
    if (a == 0 || a > b) {
      a = b++;
    }
    int c = a * b;
    return -c;
  }
}
