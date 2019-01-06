package Coords;

import java.io.PrintStream;

public class CordsTest
{
  public CordsTest() {}
  
  public static void main(String[] a) {}
  
  public static void test3()
  {
    double k1 = 0.001D;
    double[] p1 = { 35.0D, 30.0D, 0.0D };
    double[] p2 = { 35.0D + k1, 30.0D + 2.0D * k1, 0.0D };
    double[] p3 = { 35.0D + k1 * 2.0D, 30.0D + k1, 0.0D };
    

    double[] vec12 = Cords.flatWorldDist(p1, p2);
    double[] vec13 = Cords.flatWorldDist(p1, p3);
    double[] vec23 = Cords.flatWorldDist(p2, p3);
    double[] ll2a = Cords.offsetLatLonAlt(p1, vec12);
    double[] d22 = Cords.flatWorldDist(p2, ll2a);
    System.out.println("Vec22:" + d22[0] + ", " + d22[1] + ", " + d22[2]);
    
    double[] ad12 = Cords.azmDist(p1, p2);
    double[] ad13 = Cords.azmDist(p1, p3);
    double[] ad23 = Cords.azmDist(p2, p3);
    
    System.out.println("Vec12:" + ad12[0] + ", " + ad12[1]);
    System.out.println("Vec13:" + ad13[0] + ", " + ad13[1]);
    System.out.println("Vec23:" + ad23[0] + ", " + ad23[1]);
    
    double[] sc12 = Cords.screen_cordes(vec12, 0.0D, 0.0D);
    double[] sc13 = Cords.screen_cordes(vec13, 0.0D, 0.0D);
    
    double[] sc12a = Cords.screen_cordes(vec12, 10.0D, -2.0D);
    double[] sc13a = Cords.screen_cordes(vec13, 10.0D, -2.0D);
    
    double[] sc12b = Cords.screen_cordes(vec12, 345.0D, 5.0D);
    double[] sc13b = Cords.screen_cordes(vec13, 345.0D, 5.0D);
    
    System.out.println("YP12:" + sc12[0] + ", " + sc12[1]);
    System.out.println("YP13:" + sc13[0] + ", " + sc13[1]);
    
    System.out.println("YP12a:" + sc12a[0] + ", " + sc12a[1]);
    System.out.println("YP13a:" + sc13a[0] + ", " + sc13a[1]);
    
    System.out.println("YP12b:" + sc12b[0] + ", " + sc12b[1]);
    System.out.println("YP13b:" + sc13b[0] + ", " + sc13b[1]);
  }
  
  public static void test1() { double[] ll1 = { 32.166916D, 34.803395D, 100.0D };
    double[] ll2 = { 32.166917D, 34.81401D, 120.0D };
    double[] vec = Cords.flatWorldDist(ll1, ll2);
    
    double[] ll2a = Cords.offsetLatLonAlt(ll1, vec);
    System.out.println("Vec:" + vec[0] + ", " + vec[1] + ", " + vec[2]);
    System.out.println("ll2a:" + ll2a[0] + ", " + ll2a[1] + ", " + ll2a[2]);
    if ((((Math.abs(vec[1]) > 1.0D ? 1 : 0) | (Math.abs(vec[0] - 1000.0D) > 1.0D ? 1 : 0)) != 0) || (Math.abs(vec[2] - 20.0D) > 0.001D)) {
      System.err.println("Wrong Coords to dVec convertor");
    }
    if ((((Math.abs(ll2[0] - ll2a[0]) > 1.0E-4D ? 1 : 0) | (Math.abs(ll2[1] - ll2a[1]) > 1.0E-4D ? 1 : 0)) != 0) || (Math.abs(ll2[2] - ll2a[2]) > 1.0E-4D)) {
      System.err.println("Wrong Coords to dVec2 convertor");
    }
  }
  





  @org.junit.Test
  public void testFlatWorldDist2()
  {
    double[] ll1 = { 32.166916D, 34.803395D, 100.0D };
    double[] vec = { 300.0D, 400.0D, 0.0D };
    double[] ll2 = Cords.offsetLatLonAlt(ll1, vec);
    
    double[] vec2 = Cords.flatWorldDist(ll1, ll2);
    System.out.println("x=" + vec2[0] + " y=" + vec2[1]);
    if ((((Math.abs(vec2[0] - vec[0]) > 0.1D ? 1 : 0) | (Math.abs(vec[1] - vec2[1]) > 0.1D ? 1 : 0)) != 0) || (Math.abs(vec[2] - vec2[2]) > 0.001D))
    {
      System.err.println("Wrong Coords to dVec convertor");
      org.junit.Assert.fail("Wrong Coords to dVec convertor");
    }
  }
  
  @org.junit.Test
  public void testFlatWorldDist() { double[] ll1 = { 32.166916D, 34.803395D, 100.0D };
    double[] ll2 = { 32.166917D, 34.81401D, 120.0D };
    double[] vec = Cords.flatWorldDist(ll1, ll2);
    
    double[] ll2a = Cords.offsetLatLonAlt(ll1, vec);
    System.out.println("Vec:" + vec[0] + ", " + vec[1] + ", " + vec[2]);
    System.out.println("ll2a:" + ll2a[0] + ", " + ll2a[1] + ", " + ll2a[2]);
    
    if ((Math.abs(vec[0] - 999.0D) > 1.0D) || (Math.abs(vec[1]) > 1.0D) || (Math.abs(vec[2] - 20.0D) > 0.001D))
    {
      System.err.println("Wrong Coords to dVec convertor " + vec[2]);
      org.junit.Assert.fail("Wrong Coords to dVec convertor");
    }
    if ((((Math.abs(ll2[0] - ll2a[0]) > 0.001D ? 1 : 0) | (Math.abs(ll2[1] - ll2a[1]) > 0.001D ? 1 : 0)) != 0) || (Math.abs(ll2[2] - ll2a[2]) > 0.001D)) {
      System.err.println("Wrong Coords to dVec2 convertor");
      org.junit.Assert.fail("Wrong Coords to dVec2 convertor");
    }
  }
  
  @org.junit.Test
  public void testAzmDist() {
    for (double i = 0.0D; i < 360.0D; i += 7.89D) {
      double a = Math.toRadians(i);
      double x = Math.sin(a);double y = Math.cos(a);
      double ang = Cords.angXY(x, y);
      if (Math.abs(ang - i) > 1.0E-4D) {
        org.junit.Assert.fail("Wrong azm!");
      }
    }
  }
  
  @org.junit.Test
  public void test2()
  {
    double lon = 35.0D;double lat = 30.0D;double k1 = 0.001D;
    double[] p1 = { lat, lon, 0.0D };
    double[] p2 = { lat + k1 * 2.0D, lon + k1, 0.0D };
    double[] p3 = { lat + k1, lon + k1 * 2.0D, 0.0D };
    

    double[] vec13 = Cords.flatWorldDist(p1, p3);
    double[] ll3a = Cords.offsetLatLonAlt(p1, vec13);
    double[] vec13a = Cords.flatWorldDist(p1, ll3a);
    
    double[] vec33 = Cords.flatWorldDist(p3, ll3a);
    System.out.println("Vec13:" + vec13[0] + ", " + vec13[1] + ", " + vec13[2]);
    System.out.println("Vec13a:" + vec13a[0] + ", " + vec13a[1] + ", " + vec13a[2]);
    System.out.println("Vec33:" + vec33[0] + ", " + vec33[1] + ", " + vec33[2]);
    double[] ad12 = Cords.azmDist(p1, p2);
    double[] ad13 = Cords.azmDist(p1, p3);
    double[] ad23 = Cords.azmDist(p2, p3);
    
    System.out.println("Vec12:" + ad12[0] + ", " + ad12[1]);
    System.out.println("Vec13:" + ad13[0] + ", " + ad13[1]);
    System.out.println("Vec23:" + ad23[0] + ", " + ad23[1]);
  }
}
