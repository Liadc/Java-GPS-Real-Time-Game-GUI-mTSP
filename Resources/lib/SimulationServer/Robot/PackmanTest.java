package Robot;

import java.io.PrintStream;
import org.junit.Assert;

public class PackmanTest
{
  public PackmanTest() {}
  
  public static Coords.LatLonAlt POS = new Coords.LatLonAlt(32.0D, 35.0D, 0.0D);
  
  @org.junit.Test
  public void testPackmanLatLonAltDouble() { Packman pk = new Packman(POS, 1.0D);
    System.out.println(pk);
    pk.move(100.0D);
    System.out.println(pk);
    pk.setOrientation(30.0D);
    pk.move(100.0D);
    System.out.println(pk);
    
    pk.move(-100.0D);
    System.out.println(pk);
  }
  


  public void testToString()
  {
    Assert.fail("Not yet implemented");
  }
  
  public void testGetLocation()
  {
    Assert.fail("Not yet implemented");
  }
  
  public void testSet_speed()
  {
    Assert.fail("Not yet implemented");
  }
  
  public void testMove()
  {
    Assert.fail("Not yet implemented");
  }
}
