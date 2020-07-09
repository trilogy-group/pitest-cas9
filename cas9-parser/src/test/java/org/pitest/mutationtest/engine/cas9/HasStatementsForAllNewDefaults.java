package org.pitest.mutationtest.engine.cas9;

class HasStatementsForAllNewDefaults {

  int doIt(int j) {
    for (int i = 0; i < 10; i++) { // CONDITIONALS_BOUNDARY, INCREMENTS
      j = j << 1; // MATH
    }
    if (j != 2) { // NEGATE_CONDITIONALS
      doNothing(); // VOID_METHOD_CALL
    }
    return -j; // INVERT_NEGS, PRIMITIVE_RETURN_VALS
  }

  Boolean testIt(boolean b) {
    return b; // BOOLEAN_TRUE_RETURN, BOOLEAN_FALSE_RETURN
  }

  String echoIt(String a) {
    return a; // EMPTY_RETURN_VALUES
  }

  Object echoIt(Object o) {
    return o; // NULL_RETURN_VALUES
  }

  void doNothing() {}
}
