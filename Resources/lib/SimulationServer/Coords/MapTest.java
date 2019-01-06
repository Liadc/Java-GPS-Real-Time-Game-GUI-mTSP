package Coords;

import Geom.Point3D;
import java.awt.Image;
import java.io.PrintStream;
import org.junit.Assert;

public class MapTest
{
  public MapTest() {}
  
  @org.junit.Test
  public void testMap()
  {
    String map_name = "data/Ariel1.png";
    double lat = 32.103813D;
    double lon = 35.207357D;
    double alt = 660.0D;
    double dx = 955.5D;
    double dy = 421.0D;
    LatLonAlt cen = new LatLonAlt(lat, lon, alt);
    Map map = new Map(cen, dx, dy, map_name);
    Image im = map.getImage();
    int w = im.getWidth(null);
    int h = im.getHeight(null);
    Point3D min = new Point3D(0.0D, 0.0D, 0.0D);
    Point3D max = new Point3D(w, h, 0.0D);
    LatLonAlt mmin = map.frame2world(min);
    LatLonAlt mmax = map.frame2world(max);
    System.out.println("min: " + mmin);
    System.out.println("max: " + mmax);
    if (!map.isIn(10.0D, 10.0D)) {
      Assert.fail("ERR: should be inside the map");
    }
    if (!map.isIn(800.0D, 400.0D)) {
      Assert.fail("ERR: should be inside the map");
    }
  }
  
  public void testIsIn()
  {
    Assert.fail("Not yet implemented");
  }
  
  public void testGetGlobalPosition()
  {
    Assert.fail("Not yet implemented");
  }
  
  public void testWorld2frame()
  {
    Assert.fail("Not yet implemented");
  }
  
  public void testFrame2world()
  {
    Assert.fail("Not yet implemented");
  }
}
