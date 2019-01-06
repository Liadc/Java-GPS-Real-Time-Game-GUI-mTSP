package GIS;

import java.util.Set;

public abstract interface GIS_layer
  extends Set<GIS_element>
{
  public abstract Meta_data get_Meta_data();
}
