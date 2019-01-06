package GIS;

import Geom.Point3D;

public abstract interface Meta_data
{
  public abstract long getUTC();
  
  public abstract String toString();
  
  public abstract Point3D get_Orientation();
}
