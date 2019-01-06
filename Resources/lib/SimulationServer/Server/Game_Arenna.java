package Server;

import Client.Avatar;
import Coords.LatLonAlt;
import Coords.Map;
import java.util.ArrayList;
import java.util.Collections;


public class Game_Arenna
{
  private ArrayList<Avatar> _robots;
  private Map _map;
  private Robot_user _myRobot;
  public static final int MAX_EXPLOR = 3;
  
  public Game_Arenna()
  {
    _robots = new ArrayList();
  }
  
  public void setMap(Map m) { _map = m; }
  
  public void setUserRobot(Robot_user r) { _myRobot = r; }
  
  public void addAvatar(Avatar a) {
    if (a != null) _robots.add(a); }
  
  public ArrayList<Avatar> explor(LatLonAlt p, double rad) { return explor(p, rad, 3); }
  
  public ArrayList<Avatar> explor(LatLonAlt p, double rad, int mAX_EXPLOR2) { ArrayList<Avatar> ans = new ArrayList();
    Collections.shuffle(_robots);
    int i = 0;
    while ((i < _robots.size()) && (ans.size() < mAX_EXPLOR2)) {
      Avatar c = (Avatar)_robots.get(i);
      if (_myRobot.distance3D(c) < rad) {
        ans.add(c);
      }
    }
    return ans; }
  
  public Robot_user myAvater() { return _myRobot; }
  public Map getMap() { return _map; }
}
