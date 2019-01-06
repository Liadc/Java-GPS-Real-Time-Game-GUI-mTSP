package Robot;

import java.util.ArrayList;
import java.util.Date;

public class UserIDs implements java.io.Serializable
{
  private ArrayList<String> _id;
  private Date _created;
  private int _level;
  private int _games_played;
  private int _games_won;
  private double _grade;
  private ArrayList<String> _log;
  
  public UserIDs()
  {
    int id1 = new java.util.Random().nextInt();
    String ans = "EX4_OOP_Ariel_Default_" + id1;
    ArrayList<String> id = new ArrayList();
    id.add(ans);
    init(id);
  }
  

  public UserIDs(ArrayList<String> ids) { init(ids); }
  
  private void init(ArrayList<String> ids) {
    _id = new ArrayList();
    for (int i = 0; i < ids.size(); i++) {
      _id.add((String)ids.get(i));
    }
    _created = new Date();
    _level = 0;
    _games_played = 0;
    _games_won = 0;
    _grade = 0.0D;
    _log = new ArrayList();
    String name = (String)_id.get(0);
    addLog(new Date() + ", a new user named: " + name + "  was created");
  }
  
  public ArrayList<String> get_id() { return _id; }
  
  private void set_id(ArrayList<String> _id)
  {
    this._id = _id;
  }
  
  public Date get_created() { return _created; }
  
  private void set_created(Date _created) {
    this._created = _created;
  }
  
  public int get_level() { return _level; }
  
  public void set_level(int _level) {
    this._level = _level;
  }
  
  public int get_games_played() { return _games_played; }
  
  public void set_games_played(int _games_played) {
    this._games_played = _games_played;
  }
  
  public int get_games_won() { return _games_won; }
  
  public void set_games_won(int _games_won) {
    this._games_won = _games_won;
  }
  
  public String get_log(int i) { return (String)_log.get(i); }
  
  public int getLogLength() { return _log.size(); }
  
  public void addLog(String line) { _log.add(line); }
  
  public double get_grade() {
    return _grade;
  }
  
  private void set_grade(double _grade) { this._grade = _grade; }
}
