package com.java.Rubik;

public class Demo {
    public static void main(String ...args) throws Exception {
      Cube c = new Cube();

      int side = 0;
      int layer = 1;
      int n = 1;
      boolean clockWise = true;

      c.show();
      c.rotate(side, layer, n, clockWise);
      System.out.println("After 1st rotation:");
      c.show();
      c.rotate(side, layer, n + 2, clockWise);
      System.out.println("After 2nd rotation:");
      c.show();

      System.out.println(c.isAssembled());

    }
}
