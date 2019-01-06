package Server;

import Client.Avatar;
import Client.MSG;
import Coords.LatLonAlt;
import Coords.Map;
import Geom.Point3D;
import java.util.ArrayList;
import java.util.Date;


public class Robot_user
  implements Avatar
{
  private double MAX_SPEED = 3.0D; private double INIT_SHOOT_RADIUS = 2.0D; private double FIRE_TIME_OUT = 5.0D;
  private int MAX_EXPLOR = 3;
  private int INIT_NUM_OF_SHOTS = 10;
  

  private Point3D _pos;
  

  private double _speed;
  
  private long _last_cmd;
  
  private long _last_fire;
  
  private double _ang;
  private int _kills;
  private int _lives;
  
  public Robot_user(Point3D pos) { this(pos, 3, 0); }
  
  public Robot_user(Point3D pos, int lives, int level) {
    _kills = 0;
    _last_fire = new Date().getTime();
    _lives = lives;
    _level = level;
    _ang = 0.0D;
    _speed = 0.0D;
    _remain_shoots = INIT_NUM_OF_SHOTS;
    _shoot_attempts = 0;
    _shoot_radius = INIT_SHOOT_RADIUS;
    _last_cmd = new Date().getTime();
    _pos = new Point3D(pos);
  }
  
  public void setGame(Game_Arenna g) { _game = g; }
  
  public void under_fire(double d) { update();
    if (!isSafe())
    {
      _last_fire = _last_cmd;
      _points = ((int)(_points - d));
      if (_points < 0) _points = 0;
      _lives -= 1;
      if (_lives < 0) _lives = 0;
    }
  }
  
  public boolean isSafe() { double dt = (_last_cmd - _last_fire) / 1000.0D;
    return dt < FIRE_TIME_OUT;
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
    ans = getClass().getSimpleName() + " , " + _level + "," + _lives + "," + _kills + "," + _points + "," + getLocation() + "," + _ang + "," + _speed + "," + _shoot_attempts + "," + _remain_shoots;
    return ans;
  }
  
  public ArrayList<Avatar> explor(LatLonAlt cen, double radius)
  {
    update();
    ArrayList<Avatar> ans = _game.explor(cen, radius, MAX_EXPLOR);
    
    return ans;
  }
  
  public String shoot(double length)
  {
    update();
    _shoot_attempts += 1;
    _remain_shoots -= 1;
    double x = Math.sin(Math.toRadians(_ang)) * length;
    double y = Math.cos(Math.toRadians(_ang)) * length;
    Point3D v = new Point3D(x, y, 0.0D);
    LatLonAlt target = getLocation().tanstale(v);
    ArrayList<Avatar> res = explor(target, _shoot_radius);
    String ans = "";
    for (int i = 0; i < res.size(); i++) {
      _points = ((int)(_points + ((Avatar)res.get(i)).getValue()));
      ans = ans + ((Avatar)res.get(i)).getStatus();
    }
    return ans;
  }
  

  public String send_Msg(MSG msg)
  {
    return null;
  }
  
  private int _level;
  private int _points;
  
  public ArrayList<MSG> get_MSGs() { return null; }
  
  public LatLonAlt getLocation() {
    update();
    return _game.getMap().getGlobalPosition(_pos); }
  
  private int _shoot_attempts;
  
  public double distance3D(Avatar ot) { update();
    LatLonAlt otr = ot.getLocation();
    double ans = getLocation().GPS_distance(otr);
    return ans; }
  
  private int _remain_shoots;
  
  private void update() { long curr = new Date().getTime();
    double dt = (curr - _last_cmd) / 1000.0D;
    _last_cmd = curr;
    double norm = _speed * dt;
    double dx = norm * Math.sin(Math.toDegrees(_ang));
    double dy = norm * Math.cos(Math.toDegrees(_ang));
    _pos.add(dx, dy); }
  
  private double _shoot_radius;
  private Game_Arenna _game;
  
  public int getID() { return 0; }
  
  public static final int LIVES_INIT = 3;
  private ArrayList<MSG> _in_box;
  public double getValue() {
    return _points;
  }
}
