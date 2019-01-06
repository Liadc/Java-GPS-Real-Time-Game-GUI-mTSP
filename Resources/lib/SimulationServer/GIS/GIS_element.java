package GIS;

import Geom.Geom_element;
import Geom.Point3D;

public abstract interface GIS_element
{
  public abstract Geom_element getGeom();
  
  public abstract Meta_data getData();
  
  public abstract void translate(Point3D paramPoint3D);
}
