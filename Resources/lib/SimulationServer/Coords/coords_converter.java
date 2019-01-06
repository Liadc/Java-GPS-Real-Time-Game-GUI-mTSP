package Coords;

import Geom.Point3D;

public abstract interface coords_converter
{
  public abstract Point3D add(Point3D paramPoint3D1, Point3D paramPoint3D2);
  
  public abstract double distance3d(Point3D paramPoint3D1, Point3D paramPoint3D2);
  
  public abstract Point3D vector3D(Point3D paramPoint3D1, Point3D paramPoint3D2);
  
  public abstract double[] azimuth_elevation_dist(Point3D paramPoint3D1, Point3D paramPoint3D2);
  
  public abstract boolean isValid_GPS_Point(Point3D paramPoint3D);
}
