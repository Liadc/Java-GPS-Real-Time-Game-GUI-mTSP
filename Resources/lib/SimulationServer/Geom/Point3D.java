package Geom;

import java.io.Serializable;

public class Point3D implements Geom_element, Serializable {
  private static final long serialVersionUID = 1L;
  private double _x;
  private double _y;
  private double _z;
  public static final int ONSEGMENT = 0;
  public static final int LEFT = 1;
  
  public Point3D(double x, double y, double z) {
    _x = x;
    _y = y;
    _z = z;
  }
  
  public Point3D(Point3D p)
  {
    _x = p.x();
    _y = p.y();
    _z = p.z();
  }
  
  public Point3D(double x, double y) { this(x, y, 0.0D); }
  
  public Point3D(String s) { String[] a = s.split(",");
    _x = Double.parseDouble(a[0]);
    _y = Double.parseDouble(a[1]);
    _z = Double.parseDouble(a[2]);
  }
  




  public double x() { return _x; }
  public double y() { return _y; }
  public double z() { return _z; }
  public int ix() { return (int)_x; }
  public int iy() { return (int)_y; }
  public int iz() { return (int)_z; }
  
  public void add(Point3D p) { add(_x, _y, _z); }
  
  public void add(double dx, double dy, double dz) { _x += dx;_y += dy;_z += dz; }
  
  public void add(double x, double y) { add(x, y, 0.0D); }
  
  public String toString()
  {
    return _x + "," + _y + "," + _z;
  }
  
  public double distance2D(Point3D p2) { return distance3D(p2.x(), p2.y(), z()); }
  

  public double distance3D(Point3D p2) { return distance3D(p2.x(), p2.y(), p2.z()); }
  
  public double distance3D(double x, double y, double z) {
    double dx = _x - x;
    double dy = _y - y;
    double dz = _z - z;
    double t = dx * dx + dy * dy + dz * dz;
    return Math.sqrt(t);
  }
  
  public boolean equals(Point3D p2)
  {
    return (_x == _x) && (_y == _y) && (_z == _z);
  }
  
  public boolean close2equals(Point3D p2, double dist) {
    return distance3D(p2) < dist;
  }
  
  public boolean equalsXY(Point3D p) { return (_x == _x) && (_y == _y); }
  
  public String toFile() { return _x + "," + _y + "," + _z; }
  
  public String toFile1() { return "Point3D " + _x + " " + _y + " " + _z; }
  





  public int pointLineTest2(Point3D a, Point3D b)
  {
    int flag = pointLineTest(a, b);
    if (_x < _x) {
      if ((_x <= _x) && (_x > _x)) {
        if (flag == 1) return 6;
        if (flag == 2) { return 7;
        }
      }
    }
    else if ((_x > _x) && 
      (_x <= _x) && (_x > _x)) {
      if (flag == 2) return 6;
      if (flag == 1) { return 7;
      }
    }
    return flag;
  }
  


  public static final int RIGHT = 2;
  

  public static final int INFRONTOFA = 3;
  

  public static final int BEHINDB = 4;
  

  public static final int ERROR = 5;
  

  public static final int DOWN = 6;
  
  public static final int UP = 7;
  
  public int pointLineTest(Point3D a, Point3D b)
  {
    if ((a == null) || (b == null) || (a.equalsXY(b))) { return 5;
    }
    double dx = _x - _x;
    double dy = _y - _y;
    double res = dy * (_x - _x) - dx * (_y - _y);
    
    if (res < 0.0D) return 1;
    if (res > 0.0D) { return 2;
    }
    if (dx > 0.0D) {
      if (_x < _x) return 3;
      if (_x < _x) return 4;
      return 0;
    }
    if (dx < 0.0D) {
      if (_x > _x) return 3;
      if (_x > _x) return 4;
      return 0;
    }
    if (dy > 0.0D) {
      if (_y < _y) return 3;
      if (_y < _y) return 4;
      return 0;
    }
    if (dy < 0.0D) {
      if (_y > _y) return 3;
      if (_y > _y) return 4;
      return 0;
    }
    return 5;
  }
  

  public void rescale(Point3D center, Point3D vec)
  {
    if ((center != null) && (vec != null))
      rescale(center, vec.x(), vec.y(), vec.z());
  }
  
  public void rescale(Point3D center, double size) {
    if ((center != null) && (size > 0.0D))
      rescale(center, size, size, size);
  }
  
  private void rescale(Point3D center, double sizeX, double sizeY, double sizeZ) { _x += (_x - _x) * sizeX;
    _y += (_y - _y) * sizeY;
    _z += (_z - _z) * sizeZ;
  }
  
  public void rotate2D(Point3D center, double angle)
  {
    _x -= center.x();
    _y -= center.y();
    double a = Math.atan2(_y, _x);
    
    double radius = Math.sqrt(_x * _x + _y * _y);
    _x = (center.x() + radius * Math.cos(a + angle));
    _y = (center.y() + radius * Math.sin(a + angle));
  }
  
  public double angleXY(Point3D p)
  {
    if (p == null) throw new RuntimeException("** Error: Point3D angle got null **");
    return Math.atan2(_y - _y, _x - _x);
  }
  
  public double angleXY_2PI(Point3D p)
  {
    if (p == null) throw new RuntimeException("** Error: Point3D angle got null **");
    double ans = Math.atan2(_y - _y, _x - _x);
    if (ans < 0.0D) ans += 6.283185307179586D;
    return ans;
  }
  
  public double angleZ(Point3D p) {
    if (p == null) throw new RuntimeException("** Error: Point3D angleZ got null **");
    return Math.atan2(_z - _z, distance2D(p));
  }
  



  public double north_angle(Point3D p)
  {
    double ans = 0.0D;
    double a_rad = Math.atan2(_y - _y, _x - _x);
    double a_deg = Math.toDegrees(a_rad);
    if (a_deg <= 90.0D) ans = 90.0D - a_deg; else
      ans = 450.0D - a_deg;
    return ans;
  }
  

  public double up_angle(Point3D p)
  {
    double ans = 0.0D;
    ans = Math.atan2(_z - _z, distance2D(p));
    return Math.toDegrees(ans);
  }
  

  public double up_angle(Point3D p, double h)
  {
    double ans = 0.0D;
    ans = Math.atan2(_z + h - _z, distance2D(p));
    return Math.toDegrees(ans);
  }
  
  public static double r2d(double a) { return Math.toDegrees(a); }
  
  public static double d2r(double a) { return Math.toRadians(a); }
}
