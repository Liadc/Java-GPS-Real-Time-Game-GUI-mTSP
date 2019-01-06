package Coords;

import Geom.Point3D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;



public class Map
{
  private LatLonAlt _center;
  private double _dx;
  private double _dy;
  private String _map_file = "data/Ariel1.png";
  private BufferedImage _image;
  private int _w;
  
  public Map(LatLonAlt cen, double dx, double dy, String map) {
    _map_file = map;
    try {
      _image = ImageIO.read(new File(_map_file));
      _w = _image.getWidth();
      _h = _image.getHeight();
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    
    _center = new LatLonAlt(cen);
    _dx = dx;_dy = dy;
    _ll = new double[3];
    _ll[0] = _center.x();
    _ll[1] = _center.y();
    _ll[2] = _center.z(); }
  
  public BufferedImage getImage() { return _image; }
  
  public boolean isIn(double x, double y) { return (Math.abs(x) < _dx) && (Math.abs(y) < _dy); }
  
  private int _h;
  private double[] _ll;
  public LatLonAlt getGlobalPosition(Point3D v) { double[] vec = { v.x(), v.y(), v.z() };
    double[] a = Cords.offsetLatLonAlt(_ll, vec);
    LatLonAlt ans = new LatLonAlt(a[0], a[1], a[2]);
    return ans;
  }
  
  public Point3D world2frame(LatLonAlt p) { double w = _image.getWidth(null);
    double h = _image.getHeight(null);
    Point3D vec = _center.vector3D(p);
    double dw = vec.x() * 2.0D / _dx;
    double dh = vec.y() * 2.0D / _dy;
    double x = w / 2.0D + w / 2.0D * dw;
    double y = h / 2.0D - h / 2.0D * dh;
    return new Point3D(x, y, p.z());
  }
  
  public Point3D image2frame(Point3D p, int w, int h) { double dx = p.x() / _w;
    double dy = p.y() / _h;
    double x = dx * w;
    double y = dy * h;
    return new Point3D(x, y, p.z());
  }
  
  public LatLonAlt frame2world(Point3D p) { double w = _image.getWidth(null);
    double h = _image.getHeight(null);
    
    double dw = p.x() - w / 2.0D;
    double dh = h / 2.0D - p.y();
    double xn = dw * 2.0D / w;
    double yn = dh * 2.0D / h;
    double x = xn * _dx / 2.0D;
    double y = yn * _dy / 2.0D;
    Point3D v = new Point3D(x, y, p.z());
    LatLonAlt l = _center.tanstale(v);
    return l;
  }
}
