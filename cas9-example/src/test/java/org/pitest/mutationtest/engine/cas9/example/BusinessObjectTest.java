package org.pitest.mutationtest.engine.cas9.example;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class BusinessObjectTest {

  @Test
  void doIt() {
    BusinessObject bo = new BusinessObject();
    bo.setBv(20);
    int actual = bo.doIt(10, 30);
    assertEquals(bo.getBv(), actual);
  }
}
