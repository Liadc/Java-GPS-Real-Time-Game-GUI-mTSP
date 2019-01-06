package Server;

import Client.Avatar;
import Coords.LatLonAlt;
import Geom.Point3D;
import java.util.Date;

public class Server_Robot implements Avatar
{
  private static int _count = 1;
  private double MAX_SPEED = 1.0D; private double INIT_SHOOT_RADIUS = 1.0D; private double ANGULAR_ERR = 20.0D;
  
  private Point3D _pos;
  private double _speed;
  private long _last_cmd;
  private double _ang;
  private int _lives;
  private int _level;
  private int _points;
  private int _shoot_attempts;
  private int _remain_shoots;
  private double _shoot_radius;
  private Game_Arenna _game;
  private Robot_user _target;
  private int _id;
  public static final int LIVES_INIT = 3;
  
  public Server_Robot(Point3D pos) { this(pos, 3, 0, null); }
  
  public Server_Robot(Point3D pos, int lives, int level, Game_Arenna game) {
    _lives = lives;
    _level = level;
    _ang = 0.0D;
    _speed = 1.0D;
    _shoot_attempts = 0;
    _shoot_radius = INIT_SHOOT_RADIUS;
    _last_cmd = new Date().getTime();
    _id = (_count++);
    _pos = new Point3D(pos);
    setGame(game);
  }
  
  public void setGame(Game_Arenna g) { _game = g;
    if (g != null) _target = _game.myAvater();
  }
  
  public String toString() { String ans = "";
    ans = getClass().getSimpleName() + " , " + getStatus();
    return ans;
  }
  
  public void setSpeed(double speed_ms) {
    update();
    if ((speed_ms >= 0.0D) && (speed_ms < MAX_SPEED)) {
      _speed = speed_ms;
    }
  }
  
  public double rotate(double delta_deg)
  {
    update();
    _ang += delta_deg;
    return _ang;
  }
  
  public String getStatus()
  {
    update();
    String ans = "";
    ans = getClass().getSimpleName() + " , " + _level + "," + _lives + "," + "," + _points + "," + getLocation() + "," + _ang + "," + _speed + "," + _shoot_attempts + "," + _remain_shoots;
    return ans;
  }
  
  public double rotate_to_avater() { return rotate_to_avater(_game.myAvater()); }
  
  public double rotate_to_avater(Avatar target) {
    update();
    double[] aa = getLocation().azimuth_elevation_dist(target.getLocation());
    _ang = (aa[0] + (Math.random() - 0.5D) * ANGULAR_ERR);
    double dist = aa[2];
    double shoot_prob = Math.random() * 10.0D / (dist + 1.0D);
    if ((dist < 50.0D) && (shoot_prob > Math.random())) {
      double dist1 = dist + (Math.random() - 0.5D) * 4.0D;
      shoot(dist1);
    }
    return _ang;
  }
  
  public java.util.ArrayList<Avatar> explor(LatLonAlt cen, double radius) { update();
    return null;
  }
  
  public String shoot(double length)
  {
    update();
    String ans = "";
    _shoot_attempts += 1;
    _remain_shoots -= 1;
    double x = Math.sin(Math.toRadians(_ang)) * length;
    double y = Math.cos(Math.toRadians(_ang)) * length;
    Point3D v = new Point3D(x, y, 0.0D);
    LatLonAlt target = getLocation().tanstale(v);
    double dist = target.GPS_distance(_target.getLocation());
    if (dist < 2.0D) {
      _target.under_fire(1.0D);
      ans = "shoot the Avatar from: " + length;
    }
    return ans;
  }
  

  public String send_Msg(Client.MSG msg)
  {
    return null;
  }
  



  public java.util.ArrayList<Client.MSG> get_MSGs() { return null; }
  
  public LatLonAlt getLocation() {
    update();
    return _game.getMap().getGlobalPosition(_pos);
  }
  
  public double distance3D(Avatar ot) {
    update();
    LatLonAlt otr = ot.getLocation();
    double ans = getLocation().GPS_distance(otr);
    return ans;
  }
  
  private void update() {
    long curr = new Date().getTime();
    double dt = (curr - _last_cmd) / 1000.0D;
    _last_cmd = curr;
    double norm = _speed * dt;
    double dx = norm * Math.sin(Math.toDegrees(_ang));
    double dy = norm * Math.cos(Math.toDegrees(_ang));
    _pos.add(dx, dy);
  }
  
  public int getID()
  {
    return 0;
  }
  
  public double getValue()
  {
    return _points;
  }
}
