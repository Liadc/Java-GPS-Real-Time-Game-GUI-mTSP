package Coords;

import Geom.Point3D;
import java.io.Serializable;

public class LatLonAlt extends Point3D implements Serializable
{
  public static final Point3D OO = new Point3D(0.0D, 0.0D, 0.0D);
  
  public LatLonAlt(double lat, double lon, double alt) { super(lat, lon, alt); }
  
  public LatLonAlt(LatLonAlt cen) {
    super(cen.lat(), cen.lon(), cen.alt());
  }
  
  public LatLonAlt(String s) { super(s); }
  
  public double lat() { return x(); }
  public double lon() { return y(); }
  public double alt() { return z(); }
  
  public boolean isValid() { return (Math.abs(lat()) < 90.0D) && (Math.abs(lon()) < 180.0D); }
  
  public LatLonAlt tanstale(Point3D v) {
    double[] vec = { v.x(), v.y(), v.z() };
    double[] ll1 = { x(), y(), z() };
    double[] a = Cords.offsetLatLonAlt(ll1, vec);
    LatLonAlt ans = new LatLonAlt(a[0], a[1], a[2]);
    return ans;
  }
  
  public double GPS_distance(LatLonAlt gps1) {
    Point3D a = vector3D(gps1);
    return OO.distance3D(a);
  }
  
  public Point3D vector3D(LatLonAlt gps) { double[] gp0 = { x(), y(), z() };
    double[] gp1 = { gps.x(), gps.y(), gps.z() };
    double[] a = Cords.flatWorldDist(gp0, gp1);
    Point3D ans = new Point3D(a[0], a[1], a[2]);
    return ans;
  }
  
  public double[] azimuth_elevation_dist(LatLonAlt gps) { double[] gp0 = { x(), y(), z() };
    double[] gp1 = { gps.x(), gps.y(), gps.z() };
    double[] a = Cords.azmDist(gp0, gp1);
    return a;
  }
}
