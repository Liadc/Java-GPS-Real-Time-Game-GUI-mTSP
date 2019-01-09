package GUI;

import Algorithms.PathsAvoidObstacles;
import Coords.MyCoords;
import GIS.GIS_element;
import Game.Fruit;
import Game.Game;
import Game.Map;
import Game.Ghost;
import Game.Obstacle;
import Game.ObstacleCorner;
import Game.Player;
import Game.Packman;
import Geom.GeomRectangle;
import Geom.Point3D;
import Robot.Play;
import graph.Graph;
import showDB.DatabaseConnectionQueries;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;

import static Algorithms.PathsAvoidObstacles.pathToTargetInclObstacles;

/**
 * some of the code is taken from: https://javatutorial.net/display-text-and-graphics-java-jframe
 */
public class MyFrame extends JPanel implements MouseListener, KeyListener {

    private Image image; //game background image.
    public Game game; //game object to work with.
    private int typeToAdd = 1; //1 for player
    private Map map; //map object according to provided image.
    public static MyFrame ourJFrame;
    private Painter paintThread;
    public static Play play;


    public MyFrame() {
        game = new Game();
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


        Iterator PacIterator = ourJFrame.game.getPacmen().iterator();
        Iterator FruitIterator = ourJFrame.game.getFruits().iterator();
        Iterator GhostIterator = ourJFrame.game.getGhosts().iterator();
        Iterator ObstacleIterator = ourJFrame.game.getObstacles().iterator();
        Iterator cornersIterator = null;
        if(ourJFrame.game.getObstacleCorners() != null)
            cornersIterator = ourJFrame.game.getObstacleCorners().iterator();


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
            g.setColor(Color.decode(obstacle.getData().getColor())); //todo: change to Color.decode(obstacle.getData().getColor())
            g.fillRect((int)upperLeft.x(),(int)upperLeft.y(),(int)(upperRight.x()-upperLeft.x()),(int)(bottomLeft.y()-upperLeft.y()));
        }

        while (cornersIterator!=null && cornersIterator.hasNext()) {
            ObstacleCorner corner = (ObstacleCorner)cornersIterator.next();
            Point3D pixel;
            try { //pixel might be out of map bounds.
                pixel = map.CoordsToPixels((Point3D)corner.getGeom(), getHeight(), getWidth());
            } catch (Exception e) {
                showMessageToScreen(e.getMessage());
                resetGame();
                break;
            }
            g.setColor(Color.decode(corner.getData().getColor()));
            g.fillOval((int) pixel.x()-5, (int) pixel.y()-5, 10, 10);
            g.drawString("ID:"+corner.getID(),(int)pixel.x()-5,(int)pixel.y()-5);
        }


        //print player if there is one
        if(ourJFrame.game.hasPlayer()) {
            Point3D pixelPlayer = (Point3D) ourJFrame.game.getPlayer().getGeom();
            pixelPlayer = map.CoordsToPixels(pixelPlayer, getHeight(), getWidth());
            g.setColor(Color.white);
            g.drawString("Player",(int)pixelPlayer.x()-15,(int)pixelPlayer.y()-10);
            g.fillOval((int) pixelPlayer.x()-6, (int) pixelPlayer.y()-6, 12, 12);
        }


//        System.out.println("Finished paint");
    }
    private static void showMessageToScreen(String msg){
        JOptionPane.showMessageDialog(null, msg);
    }

    private void resetGame() {
        typeToAdd = 1;
        game = new Game();
        if(ourJFrame.paintThread != null){
            ourJFrame.paintThread.stopAnimKillThread();
        }
        ourJFrame.repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Pacman and Fruits");
        ourJFrame = new MyFrame();
        frame.getContentPane().add(ourJFrame);
        frame.setSize(1200, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setVisible(true);


        MenuBar MainMenu = new MenuBar();
        frame.setMenuBar(MainMenu);
        Menu fileMenu = new Menu("File");
        Menu addMenu = new Menu("Add");
        Menu algoMenu = new Menu("Algorithm");
        Menu dbMenu = new Menu("Database");

        MenuItem compareDb = new MenuItem("Compare Results");
        MenuItem showWebsite = new MenuItem("Compare, Sort & Search FULL WEB-SITE");
        dbMenu.add(compareDb);
        dbMenu.add(showWebsite);
        compareDb.addActionListener(e->{
            String[] choices = { "Ex4_OOP_example1.csv", "Ex4_OOP_example2.csv", "Ex4_OOP_example3.csv",
                    "Ex4_OOP_example4.csv", "Ex4_OOP_example5.csv", "Ex4_OOP_example6.csv" ,
                    "Ex4_OOP_example7.csv" , "Ex4_OOP_example8.csv" , "Ex4_OOP_example9.csv"};
            String input = (String) JOptionPane.showInputDialog(null, "Choose Map to compare results: ",
                    "Compare results from Database ", JOptionPane.QUESTION_MESSAGE, null,
                    choices, // Array of choices
                    choices[0]); // Initial choice
            System.out.println(input);
            System.out.println("Now calling DatabaseConnectionQueries function");//todo: delete
            Double[] avgs;
            avgs = DatabaseConnectionQueries.getAvgFromDB(input);
            showMessageToScreen("For the Map: " + input+"\n" +
                    "Average Points for everyone (excluding us): " + avgs[1]+"\n"+
                    "Average Points for Liad & Timor: "+ avgs[0] + "\n"+
                    "Best Score for this map (excluding us): " + avgs[3]+"\n"+
                    "Best Score for Liad & Timor for this map: "+avgs[2]);
        });

        showWebsite.addActionListener(f->{
            String url = "https://liad.cloud/";

            if(Desktop.isDesktopSupported()){
                Desktop desktop = Desktop.getDesktop();
                try {
                    desktop.browse(new URI(url));
                } catch (IOException | URISyntaxException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }else{
                Runtime runtime = Runtime.getRuntime();
                try {
                    runtime.exec("xdg-open " + url);
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        });

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
        MenuItem corners = new MenuItem("Add Corners");

        algoMenu.add(run);
        algoMenu.add(corners);


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
                play = new Play(file_name);
                play.setIDs(316602630,311508220);
                ourJFrame.game = new Game(play.getBoard());

                System.out.println(chooser.getSelectedFile());
            }else{
                System.out.println("No file selected.");
            }
            ourJFrame.repaint();
        });

        //run algo clicked
        run.addActionListener(l->{ if(ourJFrame.paintThread != null && ourJFrame.paintThread.isKeepGoing()){ /*if we have a thread painting in the background, we will stop the animation and kill the thread.*/
            ourJFrame.paintThread.stopAnimKillThread();
        }
//            try { //todo: uncomment this.
                ourJFrame.runAlgo();
//            }catch (RuntimeException e){
//                JOptionPane.showMessageDialog(null, e.getMessage());
//
//            }
        });

        corners.addActionListener(e->{
            ourJFrame.game.initCorners();
            ourJFrame.repaint();
        });

        fileMenu.add(reset);
        MainMenu.add(fileMenu);
        MainMenu.add(addMenu);
        MainMenu.add(algoMenu);
        MainMenu.add(dbMenu);


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
        if (play == null || ourJFrame.game == null || ourJFrame.game.getFruits() == null) {
            showMessageToScreen("Game is not initiated or fruits are not initiated. \nLoad a new game and press again.");
            return;
        }
        if(play.isRuning()){
            showMessageToScreen("Already running manual play. \nReset your game and Run Algorithm without placing the player.");
            return;
        }
        if(ourJFrame.game.getFruits().size() == 0){
            throw new RuntimeException("No fruits to calculate solution.");
        }
        //TODO: implement method for algorithm to run. Question 4.
        //todo: work these lines ->>
        ArrayList<GIS_element> targets = new ArrayList<>(ourJFrame.game.getFruits()); //random each run. we init from a SET. order not guaranteed.
        Player player = new Player();
        Point3D playerPos = (Point3D)targets.get(0).getGeom();
        ourJFrame.game.initCorners();
        player.setGeom(playerPos); //place player on the first target. (fruit).
        ourJFrame.game.addPlayer(player);
        ourJFrame.repaint();

        //server simulations: targets order are randomized and are simulated for many times. we hold the best score.
        play.setInitLocation(playerPos.y(),playerPos.x());
//        System.out.println();
        ourJFrame.game.updateGame(play.getBoard());
        System.out.println("*********** !! Game Started !! ***********");
        play.start();
        player.addTargetsList(targets); //add all fruits as targets for the player. we can add Packmen also, or any object which implements GIS_Element .
        updatePlayerPathToTargets(player); //todo: update.
        player.moveToAllTargets(ourJFrame.game.getGhosts());
    }

    private ArrayList<GIS_element> parseNameList(ArrayList<String> pathToTarget) {
        ArrayList<GIS_element> realTargets = new ArrayList<>();
        for(int i=0;i<pathToTarget.size();i++){
            String elemStr = pathToTarget.get(i);
            if (elemStr.contains("F")) { //fruit.
                int ID = Integer.parseInt(elemStr.substring(1));
                for(GIS_element realFruit : ourJFrame.game.getFruits()){
                    if (ID == realFruit.getID()) {
                        realTargets.add(realFruit);
                    }
                }
            } else if (elemStr.contains("P")) { //pacman.
                int ID = Integer.parseInt(elemStr.substring(1));
                for(GIS_element realPac : ourJFrame.game.getPacmen()){
                    if (ID == realPac.getID()) {
                        realTargets.add(realPac);
                    }
                }
            } else if (elemStr.contains("OC")) { //Obstacle Corner.
                int ID = Integer.parseInt(elemStr.substring(2));
                for(GIS_element realCorner : ourJFrame.game.getObstacleCorners()){
                    if (ID == realCorner.getID()) {
                        realTargets.add(realCorner);
                    }
                }
            }
        }
        return realTargets;
    }

    /**
     * This method will look for the player targets and checks if the path is legit (i.e - the player can move to each target one by one without collision with obstacles).
     * If it finds that the next target requires maneuvering an obstacle, it will calculate shortest path to the target while avoiding obstacles using Dijkstra's algorithm in Graph.
     * it then updates the target list of the player, so the player can move to targets in the order provided without collisions with obstacles.
     */
    private void updatePlayerPathToTargets(Player player) {
        ArrayList<GIS_element> currentTargetsOrder = player.getTargetsOrder();
        ArrayList<GIS_element> newTargetsOrder = new ArrayList<>();
        for(int i = 0 ;i<currentTargetsOrder.size()-1; i++){
            GIS_element fromTarget = currentTargetsOrder.get(i);
            GIS_element toTarget = currentTargetsOrder.get(i+1);
            String fromTrName = "" + fromTarget.getData().getType() + fromTarget.getID();
            String toTrName = "" + toTarget.getData().getType() + toTarget.getID();
            //calc shortest path with obstacles avoiding using Dijkstra's:
            System.out.println("Best route from (" +fromTrName+ ") to target named: " + toTrName+" is: "); //todo: delete

            //calc shortest path to next target, obstacles avoided:
            Graph g = PathsAvoidObstacles.initGraph(ourJFrame.game.getFruits(), ourJFrame.game.getObstacles(), ourJFrame.game,map,getHeight(),getWidth());
            ArrayList<String> pathStringToTarget = pathToTargetInclObstacles(g, fromTrName, toTrName);
            ArrayList<GIS_element> realPath = parseNameList(pathStringToTarget); //the gis_element order to move on, in order to get to the target.
            realPath.remove(0); //first element is the fromTarget, we already hold it since it was the toTarget last iteration. for iteration zero-> player is placed on first target. no need to include.
            newTargetsOrder.addAll(realPath);
        }
        System.out.println("The order of movement for player will be: " + newTargetsOrder);
        player.setTargetsOrder(newTargetsOrder);
    }

    private void printBoardAndStats(){
        ArrayList<String> board_data = play.getBoard();
        for(int i=0;i<board_data.size();i++) {
            System.out.println(board_data.get(i));
        }
        System.out.println(play.getStatistics());
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if(ourJFrame.paintThread != null && ourJFrame.paintThread.isKeepGoing()){ //if we have a thread painting in the background, we will stop the animation and kill the thread.
            ourJFrame.paintThread.stopAnimKillThread();
            showMessageToScreen("You clicked to add into the map while animation was running.\n" +
                    "We will stop the animation now.");
        }
        if (typeToAdd == 1 && play != null && !ourJFrame.game.hasPlayer()) {
            Point3D pointPixel = new Point3D(e.getX(), e.getY(), 0);
            Point3D globalPoint = map.PixelsToCoords(pointPixel, getHeight(), getWidth());
            System.out.println("Adding player at: " + globalPoint);
            //TODO: Check if inBound BOX
            ourJFrame.game.addPlayer(globalPoint);
            //todo: if there is no game selected but pacman is clicked to add, throw exception with message to screen.
            play.setInitLocation(globalPoint.y(),globalPoint.x());
            repaint();
            play.start();
            typeToAdd=2;
        }
        else if(ourJFrame.game.hasPlayer()){
            Point3D pos = (Point3D) ourJFrame.game.getPlayer().getGeom();
            pos.transformXY();
            MyCoords coords = new MyCoords();
            Point3D clickPoint = new Point3D(e.getX(), e.getY(), 0); //in pixels.
            clickPoint = map.PixelsToCoords(clickPoint,getHeight(),getWidth());
            clickPoint.transformXY();
            double[] azm = coords.azimuth_elevation_dist(pos,clickPoint );
            double angle = azm[0];
            pos.transformXY();
            play.rotate(angle);
            ourJFrame.game.updateGame(play.getBoard());
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
                play.rotate(0);
                ourJFrame.game.updateGame(play.getBoard());
                printBoardAndStats();
                ourJFrame.repaint();
                break;
            case KeyEvent.VK_DOWN:
                play.rotate(180);
                ourJFrame.game.updateGame(play.getBoard());
                printBoardAndStats();
                ourJFrame.repaint();
                break;
            case KeyEvent.VK_LEFT:
                play.rotate(270);
                ourJFrame.game.updateGame(play.getBoard());
                printBoardAndStats();
                ourJFrame.repaint();
                break;
            case KeyEvent.VK_RIGHT :
                play.rotate(90);
                ourJFrame.game.updateGame(play.getBoard());
                printBoardAndStats();
                ourJFrame.repaint();
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {

    }
}