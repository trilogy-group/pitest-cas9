package org.pitest.mutationtest.engine.cas9.example;

public class BusinessObject {

  private int bv;

  public int getBv() {
    return bv;
  }

  public void setBv(int bv) {
    this.bv = bv;
  }

  int doIt(int a, int b) {
    System.out.println("begin");
    if (a == 0)
      a = b;
    if (a > 0) {
      System.out.println("a = " + a);
      while (a <= b) {
        if (a == bv) {
          System.out.println("break!");
          break;
        }
        a++;
      }
    }
    System.out.println("done");
    return a;
  }
}
