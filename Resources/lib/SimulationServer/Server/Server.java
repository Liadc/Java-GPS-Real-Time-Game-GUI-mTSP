package Server;

import Client.Avatar;
import Coords.LatLonAlt;
import Coords.Map;
import Geom.Point3D;
import java.util.ArrayList;







public class Server
{
  private Game_Arenna _game = null;
  

  public Server() { init(); }
  
  private void init() {
    _game = new Game_Arenna();
    String map_name = "data/Ariel1.png";
    double lat = 32.103813D;
    double lon = 35.207357D;
    double alt = 0.0D;
    double dx = 955.5D;
    double dy = 421.0D;
    LatLonAlt cen = new LatLonAlt(lat, lon, alt);
    Map map = new Map(cen, dx, dy, map_name);
    _game.setMap(map);
    Robot_user robot = new Robot_user(new Point3D(0.0D, 0.0D, 0.0D), 3, 0);
    robot.setGame(_game);
    
    _game.setUserRobot(robot);
    int size = 4;
    for (int i = 0; i < size; i++) {
      double x = (Math.random() - 0.5D) * 400.0D;
      double y = (Math.random() - 0.5D) * 200.0D;
      Point3D p = new Point3D(x, y);
      Server_Robot r = new Server_Robot(p);
      r.setGame(_game);
      _game.addAvatar(r);
    }
  }
  
  public void move() { myAvatar().setSpeed(2.5D); }
  

  public ArrayList<Avatar> getRobots(double dist)
  {
    LatLonAlt cen = myAvatar().getLocation();
    return _game.explor(cen, dist);
  }
  
  public Robot_user myAvatar() { return _game.myAvater(); }
  
  public Map getMap() { return _game.getMap(); }
}
