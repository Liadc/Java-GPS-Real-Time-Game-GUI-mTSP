package GIS;

import java.util.Set;

public abstract interface GIS_project
  extends Set<GIS_layer>
{
  public abstract Meta_data get_Meta_data();
}
