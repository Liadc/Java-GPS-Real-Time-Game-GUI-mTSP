package GIS;

import Coords.LatLonAlt;
import Geom.Geom_element;
import Geom.Point3D;
import java.util.ArrayList;

public class Path implements GIS_element
{
  private ArrayList<LatLonAlt> _path;
  private String _name;
  
  public Path(String name)
  {
    _name = name;
    _path = new ArrayList();
  }
  
  public String toString() {
    String ans = "";
    for (int i = 0; i < _path.size(); i++) {
      ans = ans + i + ") " + _path.get(i) + "\n";
    }
    return ans;
  }
  
  public double distance3D() { double ans = 0.0D;
    
    for (int i = 0; i < size() - 1; i++) {
      LatLonAlt l0 = getPoint(i);
      LatLonAlt l1 = getPoint(i + 1);
      double seg = l0.distance3D(l1);
      ans += seg;
    }
    
    return ans;
  }
  
  public void add(LatLonAlt e) { _path.add(e); }
  


  public int size() { return _path.size(); }
  public LatLonAlt getPoint(int i) { return (LatLonAlt)_path.get(i); }
  
  public Geom_element getGeom()
  {
    return null;
  }
  

  public Meta_data getData()
  {
    return null;
  }
  
  public void translate(Point3D vec) {}
}
