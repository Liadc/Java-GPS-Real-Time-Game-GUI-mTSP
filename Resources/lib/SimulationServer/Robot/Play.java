package Robot;

import Coords.GeoBox;
import Coords.LatLonAlt;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;















public class Play
{
  private Game _game;
  private ArrayList<Long> _ids;
  private Date _start;
  private int _status;
  private int _steps;
  private int _gameID;
  private String _res;
  private int _wrongLocation;
  private int _ghostKills;
  private double _lastGhostKill;
  private double _score;
  private double _time;
  private double _maxTime;
  private GeoBox _BB;
  private String _gameFile = "unknown.123";
  public static final double DT = 100.0D;
  public static final double MAX_TIME = 100000.0D;
  public static final int INIT = 0;
  public static final int RUN = 1;
  public static final int PUASE = 2;
  public static final int DONE = 3; private static final LatLonAlt MIN = new LatLonAlt(32.101898D, 35.202369D, 0.0D);
  private static final LatLonAlt MAX = new LatLonAlt(32.105728D, 35.212416D, 0.0D);
  


  public Play(String g)
  {
    _game = new Game(g);
    _status = 0;
    _gameFile = g;
    init();
  }
  
  public Play(Game g) { _game = g;
    _status = 0;
    init();
  }
  
  public Play() { _game = new Game();
    _status = 0;
    init();
  }
  


  public boolean isRuning()
  {
    return _status == 1;
  }
  


  public String getStatistics()
  {
    Date end = new Date();
    double time_left = _maxTime - _time;
    String ans = "Play Report:" + end + " ,total time:" + _time + " ,score:" + _score + ", Time left:" + time_left + ", kill by ghosts:" + _ghostKills + ", out of box:" + _wrongLocation;
    return ans;
  }
  


  public String getBoundingBox()
  {
    return _BB.toString();
  }
  


  public void stop()
  {
    if (_status == 1)
    {

      _time = _maxTime;
    }
  }
  








  public boolean setInitLocation(double lat, double lon)
  {
    boolean ans = false;
    if (_status != 1) {
      LatLonAlt ll = new LatLonAlt(lat, lon, 0.0D);
      if (isValid(ll)) {
        _game.getPlayer().setLocation(ll);
        ans = true;
      }
    }
    return ans;
  }
  


  public void setIDs(long id1)
  {
    _ids = new ArrayList();
    _ids.add(Long.valueOf(id1));
  }
  


  public void setIDs(long id1, long id2)
  {
    _ids = new ArrayList();
    _ids.add(Long.valueOf(id1));_ids.add(Long.valueOf(id2));
  }
  


  public void setIDs(long id1, long id2, long id3)
  {
    _ids = new ArrayList();
    _ids.add(Long.valueOf(id1));_ids.add(Long.valueOf(id2));_ids.add(Long.valueOf(id3));
  }
  
  private void init() { _score = 0.0D;
    _steps = 0;
    _time = 0.0D;
    _wrongLocation = 0;
    _lastGhostKill = 0.0D;
    _ghostKills = 0;
    _res = "";
    _BB = new GeoBox(MIN, MAX);
    if ((_ids == null) || (_ids.size() == 0)) {
      _ids = new ArrayList();
      _ids.add(Long.valueOf(123456789L));
    }
    
    int hash = getHash1();
  }
  
  public int getHash1()
  {
    int ans = 0;
    ArrayList<String> game = _game.getGame();
    for (int i = 1; i < game.size(); i++) {
      int a1 = ((String)game.get(i)).hashCode();
      ans ^= a1;
    }
    _gameID = ans;
    return ans;
  }
  


  public void start() { start(100000.0D); }
  
  private void start(double time_out_ms) {
    _maxTime = time_out_ms;
    _start = new Date();
    _status = 1;
    init();
    _game.getPlayer().set_speed(20.0D);
  }
  




  public ArrayList<String> getBoard()
  {
    ArrayList<String> ans = _game.getGame();
    
    return ans;
  }
  





  public boolean rotate(double ang)
  {
    boolean ans = false;
    boolean done = checkDone();
    if (_status == 1) {
      Packman p = _game.getPlayer();
      p.setOrientation(ang);
      ans = move();
    }
    return ans;
  }
  
  private boolean isValid(LatLonAlt p)
  {
    boolean ans = true;
    if (!_BB.isIn2D(p)) { ans = false;
    } else {
      for (int i = 0; (ans) && (i < _game.sizeB()); i++) {
        GeoBox b = _game.getBox(i);
        if (b.isIn2D(p)) ans = false;
      }
    }
    return ans;
  }
  

  private void run()
  {
    Packman p = _game.getPlayer();
    for (int i = 0; i < _game.sizeG(); i++) {
      Packman g = _game.getGhosts(i);
      g.setOrientation(p.getLocation());
      g.move(100.0D);
    }
    
    boolean cont = true;
    for (int i = 0; (cont) && (i < _game.sizeR()); i++) {
      Packman pk = _game.getPackman(i);
      int find = getClosestFruit(pk);
      Fruit cl = _game.getTarget(find);
      pk.setOrientation(cl.getLocation());
      if (pk.distance3D(cl) < pk.get_radius()) {
        _game.removeTarget(find);
        if (_game.sizeT() == 0) cont = false;
      }
      pk.move(100.0D);
    }
  }
  
  private void test() {
    Packman p = _game.getPlayer();
    double dist = p.get_radius() + p.get_speed() * 100.0D / 1000.0D;
    for (int i = 0; i < _game.sizeT(); i++) {
      Fruit f = _game.getTarget(i);
      if (p.distance3D(f) < dist) {
        _score += f.getWeight();
        _game.removeTarget(i);
      }
    }
  }
  
  private void test1() { Packman p = _game.getPlayer();
    
    double dist = p.get_radius() + p.get_speed() * 100.0D / 1000.0D;
    for (int i = 0; i < _game.sizeR(); i++) {
      Packman pk = _game.getPackman(i);
      if (p.distance3D(pk) < dist) {
        _score += pk.get_radius();
        _game.removeRobot(i);
      }
    }
  }
  
  private void test2() { Packman p = _game.getPlayer();
    
    for (int i = 0; (_time - _lastGhostKill > 3000.0D) && (i < _game.sizeG()); i++) {
      Packman pk = _game.getGhosts(i);
      double dist = pk.get_radius() + pk.get_speed() * 100.0D / 1000.0D;
      if (p.distance3D(pk) < dist) {
        _score -= 20.0D;
        _ghostKills += 1;
        _lastGhostKill = _time;
      }
    }
  }
  
  private int getClosestFruit(Packman p) {
    int ans = -1;
    ArrayList<Fruit> ff = _game.getTargets();
    if (ff.size() > 0) {
      ans = 0;
      double min_d = p.distance3D((Fruit)ff.get(ans));
      for (int i = 1; i < ff.size(); i++) {
        double d = p.distance3D((Fruit)ff.get(i));
        if (d < min_d) {
          min_d = d;
          ans = i;
        }
      }
    }
    return ans;
  }
  
  private boolean move()
  {
    boolean ans = false;
    if (_status == 1) {
      Packman p = _game.getPlayer();
      LatLonAlt pv = new LatLonAlt(p.getLocation());
      p.move(100.0D);test();test1();test2();ans = true;
      if (!isValid(p.getLocation())) {
        _score -= 1.0D;
        _wrongLocation += 1;
        p.setLocation(pv);
      }
      _steps += 1;
      _time += 100.0D;
      if ((_status == 1) && (!checkDone())) { run();
      }
    }
    return ans;
  }
  
  private boolean checkDone() {
    boolean ans = false;
    if ((_status == 1) && ((_game.sizeT() == 0) || (_time >= _maxTime))) {
      _status = 3;
      double remain_time = _maxTime - _time;
      _score += remain_time / 1000.0D;
//      sendReport();
      ans = true;
      
      _res = toString();
    }
    return ans;
  }
  
//  private void sendReport() { String jdbcUrl = "jdbc:mysql://ariel-oop.xyz:3306/oop?useUnicode=yes&characterEncoding=UTF-8&useSSL=false";
//    String jdbcUser = "boaz";
//    String jdbcPassword = "9125";
//    try {
//      Class.forName("com.mysql.jdbc.Driver");
//      Connection connection =
//        DriverManager.getConnection(jdbcUrl, jdbcUser, jdbcPassword);
//      Statement statement = connection.createStatement();
//
//      String id = _ids.get(0) + ",0,0";
//      if (_ids.size() == 2) {
//        id = _ids.get(0) + "," + _ids.get(1) + ",0";
//      }
//      if (_ids.size() == 3) {
//        id = _ids.get(0) + "," + _ids.get(1) + "," + _ids.get(2);
//      }
//      String insertSQL = "INSERT INTO logs (FirstID, SecondID, ThirdID, LogTime,Point, SomeDouble)\r\nVALUES (" +
//        id + ", CURRENT_TIMESTAMP," + _score + "," + _gameID + ");";
//      statement.executeUpdate(insertSQL);
//      statement.close();
//      connection.close();
//    }
//    catch (SQLException sqle) {
//      System.out.println("SQLException: " + sqle.getMessage());
//      System.out.println("Vendor Error: " + sqle.getErrorCode());
//    }
//    catch (ClassNotFoundException e) {
//      e.printStackTrace();
//    }
//  }
}
