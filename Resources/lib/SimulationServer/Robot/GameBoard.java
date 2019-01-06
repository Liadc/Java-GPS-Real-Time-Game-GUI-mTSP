package Robot;

import Coords.GeoBox;
import Coords.LatLonAlt;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

public class GameBoard implements Serializable
{
  private UserIDs _group;
  private Date _startTime;
  private int _status;
  private ArrayList<Game> _games;
  private int _curr_game;
  private GeoBox _BB;
  public static final LatLonAlt MIN = new LatLonAlt(32.101898D, 35.202369D, 0.0D);
  public static final LatLonAlt MAX = new LatLonAlt(32.105728D, 35.212416D, 0.0D);
  public static final int RUN = 1;
  
  public GameBoard() {
    File ff = new File("Ex4_Log_File0_ID.bin");
    if (ff.exists()) {
      try {
        ObjectInputStream ois = new ObjectInputStream(new FileInputStream("Ex4_Log_File0_ID.bin"));
        GameBoard cd1 = (GameBoard)ois.readObject();
        init(cd1);
      }
      catch (Exception e) {
        e.printStackTrace();
        System.err.println("ERR: unable to load file Ex4_Log_File0_ID.bin try using file: Ex4_Log_File1_ID.bin (replace its name to Ex4_Log_File0_ID.bin");
        init();
      }
      
    } else
      init(); }
  
  public static final int STOP = 2;
  
  public void add(Game g) { Game n = new Game(g);
    _games.add(n); }
  
  public static final int PUASE = 3;
  
  public void save() { try { FileOutputStream out = new FileOutputStream("Ex4_Log_File0_ID.bin");
      ObjectOutputStream oout = new ObjectOutputStream(out);
      oout.writeObject(this);
      oout.flush();
      oout.close();
      out.close();
      out = new FileOutputStream("Ex4_Log_File1_ID.bin");
      oout = new ObjectOutputStream(out);
      oout.writeObject(this);
      oout.flush();
      oout.close();
      out.close();
    }
    catch (Exception e) {
      e.printStackTrace(); } }
  
  public static final int ERROR = -1;
  private void init(GameBoard c) { _games = _games;
    _group = _group;
  }
  
  private void init() { _games = init_games();
    _group = init_group();
    
    _BB = new GeoBox(MIN, MAX); }
  
  public static final String _file0 = "Ex4_Log_File0_ID.bin";
  public static final String _file1 = "Ex4_Log_File1_ID.bin";
  private ArrayList<Game> init_games() { ArrayList<Game> ans = new ArrayList();
    
    return ans;
  }
  
  private UserIDs init_group() { return new UserIDs(); }
  
  public int size() {
    return _games.size();
  }
  
  public Game getGame(int index) { return (Game)_games.get(index); }
}
