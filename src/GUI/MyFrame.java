package GUI;

//import Algorithms.ShortestPathAlgo;
import Algorithms.Solution;
import Coords.Cords;
import Coords.MyCoords;
import File_format.Path2KML;
import Game.Fruit;
import Game.Game;
import Game.Map;
import Game.Ghost;
import Game.Obstacle;
import Game.Player;
import Game.Packman;
import Geom.Path;
import Geom.GeomRectangle;
import Geom.Point3D;
import Robot.Play;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * some of the code is taken from: https://javatutorial.net/display-text-and-graphics-java-jframe
 */
public class MyFrame extends JPanel implements MouseListener, KeyListener {

    private Image image; //game background image.
    private Game game; //game object to work with.
    private int typeToAdd = 1; //1 for player
    private Map map; //map object according to provided image.
    private static MyFrame ourJFrame;
    private Painter paintThread;
    private Play play;


    public MyFrame() {
        this.game = new Game();
        Point3D topLeft = new Point3D(35.20236,32.10572); //TODO: Change this hardCoded to Game Bounded (ourJframe.play.getBoundingBox();)
        Point3D downRight = new Point3D(35.21235,32.10194);
        this.map = new Map(new File("Resources/GameMaps/Ariel1.png"),topLeft,downRight);
        addMouseListener(this);
        this.setFocusable(true);
        this.requestFocus();
        addKeyListener(this);
    }

    /**
     * paint function.
     * will rewrite each time the packmans in they curret location, (if we changed them)
     * Run on Packman array and Fruits array, and paint them one by one.
     *
     * LineSolution will be created and painted too.
     * @param g
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
//        System.out.println("Started paint");
        image = Toolkit.getDefaultToolkit().getImage(map.getImagePath());
        int w = this.getWidth();
        int h = this.getHeight();
        g.drawImage(image, 0, 0, w, h, this);


        Iterator PacIterator = game.getPacmen().iterator();
        Iterator FruitIterator = game.getFruits().iterator();
        Iterator GhostIterator = game.getGhosts().iterator();
        Iterator ObstacleIterator = game.getObstacles().iterator();


        while (PacIterator.hasNext()) {
            Packman pacman = (Packman)PacIterator.next();
            Point3D pixel = null;
            try { //pixel might be out of map bounds.
                pixel = map.CoordsToPixels((Point3D)pacman.getGeom(), getHeight(), getWidth());
            } catch (Exception e) {
                resetGame();
                showMessageToScreen(e.getMessage());
                break;
            }
            g.setColor(Color.decode(pacman.getData().getColor()));
            g.fillArc((int) pixel.x()-8, (int) pixel.y()-8, 16, 16,30,300);
            g.drawString("ID: "+pacman.getID(),(int)pixel.x()-5,(int)pixel.y()-10);
            g.drawString("Speed: "+pacman.getSpeed(),(int)pixel.x()-5,(int)pixel.y()-25);
        }


        while (FruitIterator.hasNext()) {
            Fruit fruit = (Fruit)FruitIterator.next();
            if (!fruit.isEaten()) {
                Point3D pixel;
                try { //pixel might be out of map bounds.
                    pixel = map.CoordsToPixels((Point3D)fruit.getGeom(), getHeight(), getWidth());
                } catch (Exception e) {
                    showMessageToScreen(e.getMessage());
                    resetGame();
                    break;
                }
                g.setColor(Color.decode(fruit.getData().getColor()));
                g.fillOval((int) pixel.x()-5, (int) pixel.y()-5, 10, 10);
                g.drawString("ID:"+fruit.getID(),(int)pixel.x()-5,(int)pixel.y()-5);
            }
        }

        while (GhostIterator.hasNext()) {
            Ghost ghost = (Ghost) GhostIterator.next();
                Point3D pixel;
                try { //pixel might be out of map bounds.
                    pixel = map.CoordsToPixels((Point3D)ghost.getGeom(), getHeight(), getWidth());
                } catch (Exception e) {
                    showMessageToScreen(e.getMessage());
                    resetGame();
                    break;
                }
                g.setColor(Color.decode(ghost.getData().getColor()));
                g.fillOval((int) pixel.x()-5, (int) pixel.y()-5, 20, 20);
                g.drawString("IDGhost:"+ghost.getID(),(int)pixel.x()-5,(int)pixel.y()-5);
        }

        while (ObstacleIterator.hasNext()) {
            Obstacle obstacle = (Obstacle) ObstacleIterator.next();
            Point3D upperLeft,upperRight,bottomLeft;
            GeomRectangle rect = (GeomRectangle)obstacle.getGeom();
            upperLeft = rect.getLeftUp();
            upperRight = rect.getRightUp();
            bottomLeft = rect.getLeftDown();
            try { //pixel might be out of map bounds.
                upperLeft = map.CoordsToPixels(upperLeft, getHeight(), getWidth());
                upperRight = map.CoordsToPixels(upperRight, getHeight(), getWidth());
                bottomLeft = map.CoordsToPixels(bottomLeft, getHeight(), getWidth());
            } catch (Exception e) {
                showMessageToScreen(e.getMessage());
                resetGame();
                break;
            }
            g.setColor(Color.BLACK); //todo: change to Color.decode(obstacle.getData().getColor())
            g.fillRect((int)upperLeft.x(),(int)upperLeft.y(),(int)(upperRight.x()-upperLeft.x()),(int)(bottomLeft.y()-upperLeft.y()));
        }

        //print player if there is one
        if(ourJFrame.game.hasPlayer()) {
            Point3D pixelPlayer = (Point3D) ourJFrame.game.getPlayer().getGeom();
            pixelPlayer = map.CoordsToPixels(pixelPlayer, getHeight(), getWidth());
            g.setColor(Color.white);
            g.fillOval((int) pixelPlayer.x()-6, (int) pixelPlayer.y()-6, 12, 12);
        }


//        System.out.println("Finished paint");
    }
    private static void showMessageToScreen(String msg){
        JOptionPane.showMessageDialog(null, msg);
    }

    private void resetGame() {
        this.game = new Game();
        if(ourJFrame.paintThread != null){
            ourJFrame.paintThread.stopAnimKillThread();
        }
        ourJFrame.repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Pacman and Fruits");
        ourJFrame = new MyFrame();
        frame.getContentPane().add(ourJFrame);
        frame.setSize(1200, 800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setVisible(true);


        MenuBar MainMenu = new MenuBar();
        frame.setMenuBar(MainMenu);
        Menu fileMenu = new Menu("File");
        Menu addMenu = new Menu("Add");
        Menu algoMenu = new Menu("Algorithm");


        MenuItem player = new MenuItem("Add Player");
        MenuItem reset = new MenuItem("Reset");
        MenuItem loadCSVex4 = new MenuItem("Load Game");



        player.addActionListener((e -> ourJFrame.typeToAdd = 1));

        //reset clicked
        reset.addActionListener(e -> {
            ourJFrame.resetGame();
            ourJFrame.repaint();
        });
        addMenu.add(player);


        MenuItem saveToCsvItemMenu = new MenuItem("Save To CSV");

        MenuItem run = new MenuItem("Run");

        algoMenu.add(run);


        //load CSV file for Ex4.
        fileMenu.add(loadCSVex4);
        loadCSVex4.addActionListener(e->{
            ourJFrame.resetGame();
            if(ourJFrame.paintThread != null){ //if we have a thread painting in the background, we will stop the animation and kill the thread.
                ourJFrame.paintThread.stopAnimKillThread();
            }
            JFileChooser chooser = new JFileChooser("./Resources/Data");
            FileNameExtensionFilter filter =   new FileNameExtensionFilter(
                    "CSV Files", "csv");
            chooser.setFileFilter(filter);
            chooser.setAcceptAllFileFilterUsed(false);  // disable the "All files" option.
            int returnValue = chooser.showOpenDialog(null);
            if(returnValue == JFileChooser.APPROVE_OPTION){
                File file = new File(String.valueOf(chooser.getSelectedFile()));
                String file_name = file.getAbsolutePath();
                ourJFrame.play = new Play(file_name);
                ourJFrame.play.setIDs(316602630,311508220);
                ourJFrame.game = new Game(ourJFrame.play.getBoard());

                System.out.println(chooser.getSelectedFile());
            }else{
                System.out.println("No file selected.");
            }
            if(ourJFrame.play!=null) {
                System.out.println(ourJFrame.play.getStatistics());
                ArrayList<String> board_data = ourJFrame.play.getBoard();
                for (int i = 0; i < board_data.size(); i++) {
                    System.out.println(board_data.get(i));
                }
            }
            ourJFrame.repaint();
        });

        //run algo clicked
        run.addActionListener(l->{ if(ourJFrame.paintThread != null && ourJFrame.paintThread.isKeepGoing()){ /*if we have a thread painting in the background, we will stop the animation and kill the thread.*/
            ourJFrame.paintThread.stopAnimKillThread();
        }
            try {
                ourJFrame.runAlgo();
            }catch (RuntimeException e){
                JOptionPane.showMessageDialog(null, e.getMessage());

            }
        });
        fileMenu.add(reset);
        MainMenu.add(fileMenu);
        MainMenu.add(addMenu);
        MainMenu.add(algoMenu);

    }

    /**
     * this method execute by the Menu,
     * check if there are packmans or fruits in the game.
     * we consider that everytime the HashSet will be ordred different each time, and we take this HashSet into an Array.
     * and then we run the algorithm on this shuffled array.
     * we save the time to complete the solution and the solution,
     * if the timeToComplete is lower then the lowest time we got untill now, we will save the new Solution and the new BestTime.
     */
    private void runAlgo() {
        if(this.game.getPacmen().size() == 0){
            throw new RuntimeException("No pacmen to calculate solution.");
        } else if(this.game.getFruits().size() == 0){
            throw new RuntimeException("No fruits to calculate solution.");
        }
        //TODO: implement method for algorithm to run. Question 4.
        throw new RuntimeException("Not yet implemented.");
    }

    private void printBoardAndStats(){
        ArrayList<String> board_data = ourJFrame.play.getBoard();
        for(int i=0;i<board_data.size();i++) {
            System.out.println(board_data.get(i));
        }
        System.out.println(ourJFrame.play.getStatistics());
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(ourJFrame.paintThread != null && ourJFrame.paintThread.isKeepGoing()){ //if we have a thread painting in the background, we will stop the animation and kill the thread.
            ourJFrame.paintThread.stopAnimKillThread();
            showMessageToScreen("You clicked to add into the map while animation was running.\n" +
                    "We will stop the animation now.");
        }
        if (typeToAdd == 1 && ourJFrame.play != null && !ourJFrame.game.hasPlayer()) {
            System.out.println("Adding player");
            Point3D pointPixel = new Point3D(e.getX(), e.getY(), 0);
            Point3D globalPoint = map.PixelsToCoords(pointPixel, getHeight(), getWidth());
            //TODO: Check if inBound BOX
            ourJFrame.game.addPlayer(globalPoint);
            //todo: if there is no game selected but pacman is clicked to add, throw exception with message to screen.
            ourJFrame.play.setInitLocation(globalPoint.y(),globalPoint.x());
            repaint();
            ourJFrame.play.start();
            typeToAdd=2;
        }
        else if(typeToAdd == 2 && ourJFrame.game.hasPlayer()){
            Point3D pos = (Point3D)ourJFrame.game.getPlayer().getGeom();
            pos.transformXY();
            MyCoords coords = new MyCoords();
            Point3D clickPoint = new Point3D(e.getX(), e.getY(), 0); //in pixels.
            clickPoint = map.PixelsToCoords(clickPoint,getHeight(),getWidth());
            clickPoint.transformXY();
            double[] azm = coords.azimuth_elevation_dist(pos,clickPoint );
            double angle = azm[0];
            ourJFrame.play.rotate(angle);
            ourJFrame.game = new Game(play.getBoard());
            printBoardAndStats();
            System.out.println("Last move was rotate on Angle from player to click pixel: "+angle);
            ourJFrame.repaint();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {

    }

    @Override
    public void mouseReleased(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {

    }

    @Override
    public void mouseExited(MouseEvent e) {

    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch( keyCode ) {
            case KeyEvent.VK_UP:
                ourJFrame.play.rotate(0);
                ourJFrame.game = new Game(play.getBoard());
                printBoardAndStats();
                ourJFrame.repaint();
                break;
            case KeyEvent.VK_DOWN:
                ourJFrame.play.rotate(180);
                ourJFrame.game = new Game(play.getBoard());
                printBoardAndStats();
                ourJFrame.repaint();
                break;
            case KeyEvent.VK_LEFT:
                ourJFrame.play.rotate(270);
                ourJFrame.game = new Game(play.getBoard());
                printBoardAndStats();
                ourJFrame.repaint();
                break;
            case KeyEvent.VK_RIGHT :
                ourJFrame.play.rotate(90);
                ourJFrame.game = new Game(play.getBoard());
                printBoardAndStats();
                ourJFrame.repaint();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}