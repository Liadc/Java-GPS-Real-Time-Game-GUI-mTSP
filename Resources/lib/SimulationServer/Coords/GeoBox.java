package Coords;

import java.io.Serializable;





public class GeoBox
  implements Serializable
{
  private int _id = 0;
  private LatLonAlt _min;
  private LatLonAlt _max;
  private double _weight; private static int _count = 0;
  
  public GeoBox(LatLonAlt p1, LatLonAlt p2) { _min = new LatLonAlt(Math.min(p1.lat(), p2.lat()), 
      Math.min(p1.lon(), p2.lon()), Math.min(p1.alt(), p2.alt()));
    _max = new LatLonAlt(Math.max(p1.lat(), p2.lat()), 
      Math.max(p1.lon(), p2.lon()), Math.max(p1.alt(), p2.alt()));
    _id = (_count++);
    _weight = 1.0D;
  }
  
  public GeoBox(String line) {
    String[] arr = line.split(",");
    _id = Integer.parseInt(arr[1]);
    String p = arr[2] + "," + arr[3] + "," + arr[4];
    _min = new LatLonAlt(p);
    p = arr[5] + "," + arr[6] + "," + arr[7];
    _max = new LatLonAlt(p);
    _weight = Double.parseDouble(arr[8]);
  }
  
  public GeoBox(GeoBox geoBox)
  {
    this(geoBox.toString());
  }
  
  public String toString() {
    String ans = "B," + _id + "," + _min + "," + _max + "," + _weight;
    return ans; }
  
  public double getWeight() { return _weight; }
  public void setWeight(double w) { _weight = w; }
  
  public boolean isIn2D(LatLonAlt q) { boolean ans = false;
    if ((q.lat() >= _min.lat()) && (q.lon() >= _min.lon()) && (q.lat() <= _max.lat()) && (q.lon() <= _max.lon())) {
      ans = true;
    }
    return ans;
  }
  
  public boolean isIn3D(LatLonAlt q) { boolean ans = isIn2D(q);
    ans = (ans) && (q.alt() >= _min.alt()) && (q.alt() <= _max.alt());
    return ans; }
  
  public LatLonAlt getMin() { return _min; }
  public LatLonAlt getMax() { return _max; }
}
