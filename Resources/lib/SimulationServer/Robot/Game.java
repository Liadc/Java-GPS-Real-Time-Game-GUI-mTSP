package Robot;

import Coords.GeoBox;
import Coords.LatLonAlt;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;



public class Game
  implements Serializable
{
  private Packman _player;
  private ArrayList<Packman> _robots;
  private ArrayList<Packman> _ghosts;
  private ArrayList<Fruit> _targets;
  private ArrayList<GeoBox> _boxes;
  
  public Game()
  {
    _robots = new ArrayList();
    _ghosts = new ArrayList();
    _targets = new ArrayList();
    _boxes = new ArrayList();
    _player = new Packman(new LatLonAlt(0.0D, 0.0D, 0.0D), 2.0D);
  }
  
  public Game(String f) { this();
    try {
      BufferedReader br = new BufferedReader(new FileReader(f));
      String header = br.readLine();
      


      String line = br.readLine();
      while (line != null) {
        if (line.startsWith("M")) {
          _player = new Packman(line);
        }
        if (line.startsWith("P")) {
          Packman p = new Packman(line);
          _robots.add(p);
        }
        if (line.startsWith("G")) {
          Packman p = new Packman(line);
          _ghosts.add(p);
        }
        if (line.startsWith("F")) {
          Fruit ff = new Fruit(line);
          _targets.add(ff);
        }
        if (line.startsWith("B")) {
          GeoBox ff = new GeoBox(line);
          _boxes.add(ff);
        }
        
        line = br.readLine();
      }
      br.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
      System.out.println("Exception ");
    }
  }
  
  public Game(Game g) { this();
    for (int i = 0; i < _robots.size(); i++)
      _robots.add(new Packman((Packman)_robots.get(i)));
    for (int i = 0; i < _ghosts.size(); i++)
      _ghosts.add(new Packman((Packman)_ghosts.get(i)));
    for (int i = 0; i < _targets.size(); i++)
      _targets.add(new Fruit((Fruit)_targets.get(i)));
    for (int i = 0; i < _boxes.size(); i++)
      _boxes.add(new GeoBox((GeoBox)_boxes.get(i))); }
  
  public ArrayList<Packman> getRobots() { return _robots; }
  public ArrayList<Packman> getGhosts() { return _ghosts; }
  public Packman getGhosts(int i) { return (Packman)_ghosts.get(i); }
  public ArrayList<Fruit> getTargets() { return _targets; }
  public void add(Packman r) { _robots.add(r); }
  public void add(Fruit f) { _targets.add(f); }
  public void add(GeoBox b) { _boxes.add(b); }
  public int sizeR() { return _robots.size(); }
  public int sizeG() { return _ghosts.size(); }
  public int sizeT() { return _targets.size(); }
  public int sizeB() { return _boxes.size(); }
  public Packman getPackman(int i) { return (Packman)_robots.get(i); }
  public Packman getPlayer() { return _player; }
  public Fruit getTarget(int i) { return (Fruit)_targets.get(i); }
  public void removeTarget(int i) { _targets.remove(i); }
  public void removeRobot(int i) { _robots.remove(i); }
  public GeoBox getBox(int i) { return (GeoBox)_boxes.get(i); }
  
  public void clear() { _robots.clear();
    _targets.clear();
    _boxes.clear();
    _ghosts.clear();
    _player = null;
  }
  
  public ArrayList<String> getGame() { ArrayList<String> ans = new ArrayList();
    if (_player != null) ans.add(_player.toString1());
    for (int i = 0; i < _robots.size(); i++) {
      ans.add(((Packman)_robots.get(i)).toString());
    }
    for (int i = 0; i < _ghosts.size(); i++) {
      ans.add(((Packman)_ghosts.get(i)).toString2());
    }
    for (int i = 0; i < _targets.size(); i++) {
      ans.add(((Fruit)_targets.get(i)).toString());
    }
    for (int i = 0; i < _boxes.size(); i++) {
      ans.add(((GeoBox)_boxes.get(i)).toString());
    }
    return ans;
  }
  
  public void setPlayer(Packman p) { _player = p; }
  
  public void write2File(String name) {
    try {
      BufferedWriter out = new BufferedWriter(new FileWriter(name));
      out.write("Type,ID,Lat,Lon,Alt,Speed/Weight,Radius," + _robots.size() + "," + _targets.size() + "," + _boxes.size() + "\n");
      for (int i = 0; i < _robots.size(); i++) {
        String line = ((Packman)_robots.get(i)).toString();
        out.write(line + "\n");
      }
      for (int i = 0; i < _ghosts.size(); i++) {
        String line = ((Packman)_ghosts.get(i)).toString2();
        out.write(line + "\n");
      }
      for (int i = 0; i < _targets.size(); i++) {
        String line = ((Fruit)_targets.get(i)).toString();
        out.write(line + "\n");
      }
      for (int i = 0; i < _boxes.size(); i++) {
        String line = ((GeoBox)_boxes.get(i)).toString();
        out.write(line + "\n");
      }
      out.close();
    }
    catch (IOException e)
    {
      e.printStackTrace();
      System.out.println("Exception ");
    }
  }
  
  public void addGhost(Packman r) { _ghosts.add(r); }
}
