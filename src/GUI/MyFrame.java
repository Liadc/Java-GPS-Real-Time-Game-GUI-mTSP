package GUI;

import Algorithms.PathsAvoidObstacles;
import Coords.MyCoords;
import GIS.GIS_element;
import GIS.GIS_layer;
import GIS.GIS_layer_obj;
import Game.*;
import Geom.GeomRectangle;
import Geom.Point3D;
import Robot.Play;
import graph.Graph;
import graph.Node;
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
import java.util.Collections;
import java.util.Iterator;

import static Algorithms.PathsAvoidObstacles.initGraph;
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


    private MyFrame() {
        game = new Game();
        Point3D topLeft = new Point3D(35.20236,32.10572);
        Point3D downRight = new Point3D(35.21235,32.10194);
        this.map = new Map(new File("Resources/GameMaps/Ariel1.png"),topLeft,downRight);
        addMouseListener(this);
        this.setFocusable(true);
        this.requestFocus();
        addKeyListener(this);
    }

    /**
     * paint function.
     * will rewrite each time the objects inside the Game in their current location,
     * Run on Packman array and Fruits array, ghosts, obstacles, corners, player etc. and paint them one by one.
     *
     * LineSolution will be created and painted too.
     * @param g Graphics, java utility.
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
            g.setColor(Color.decode(obstacle.getData().getColor()));
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
            System.out.println("Now calling DatabaseConnectionQueries function");//shows the calling for the database query function. optional.
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
                    e.printStackTrace();
                }
            }else{
                Runtime runtime = Runtime.getRuntime();
                try {
                    runtime.exec("xdg-open " + url);
                } catch (IOException e) {
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

        MenuItem runRandomized = new MenuItem("Run Randomized Algorithm");
        MenuItem runGreedy = new MenuItem("Run Greedy Algorithm");
        MenuItem corners = new MenuItem("Show Corners");

        algoMenu.add(runRandomized);
        algoMenu.add(runGreedy);
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

        //run algo randomized clicked
        runRandomized.addActionListener(l->{ if(ourJFrame.paintThread != null && ourJFrame.paintThread.isKeepGoing()){ /*if we have a thread painting in the background, we will stop the animation and kill the thread.*/
            ourJFrame.paintThread.stopAnimKillThread();
        }
            try {
                ourJFrame.runRandomizedAlgo();
            }catch (RuntimeException e){
                JOptionPane.showMessageDialog(null, e.getMessage());
            }
        });

        //run algo greedy clicked.
        runGreedy.addActionListener(e->{
            if(ourJFrame.paintThread != null && ourJFrame.paintThread.isKeepGoing()){ /*if we have a thread painting in the background, we will stop the animation and kill the thread.*/
            ourJFrame.paintThread.stopAnimKillThread();
        }
            try {
            ourJFrame.runGreedyAlgo();
            }catch (RuntimeException p){
                JOptionPane.showMessageDialog(null, p.getMessage());
            }
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
     * This method will be used to initialize a game with all required data to perform any algorithm logic.
     * The method will return an ArrayList of targets (GIS_Elements), which at this moment is only fruits as we find this is the most efficiently
     * way to score the highest in a game.
     * It places the Player on a random first target, but all further algorithmic logic will be performed according to Algorithm selected (Greedy/Randomized).
     * @return ArrayList of GIS_elements, the targets to be used for the player to eat.
     */
    private ArrayList<GIS_element> algoInit() {
        if (play == null || ourJFrame.game == null || ourJFrame.game.getFruits() == null) {
            showMessageToScreen("Game is not initiated or fruits are not initiated. \nLoad a new game and press again.");
            return null;
        }
        if(play.isRuning()){
            showMessageToScreen("Already running manual play. \nReset your game and Run Algorithm without placing the player.");
            return null;
        }
        if(ourJFrame.game.getFruits().size() == 0){
            throw new RuntimeException("No fruits to calculate solution.");
        }
        ArrayList<GIS_element> targets = new ArrayList<>(ourJFrame.game.getFruits()); //random each run. we init from a SET. order not guaranteed.
        Collections.shuffle(targets); //shuffling targets order to get randomized solution.
        Player player = new Player();
        Point3D playerPos = (Point3D)targets.get(0).getGeom();
        ourJFrame.game.initCorners();
        player.setGeom(playerPos); //place player on the first target. (fruit).
        ourJFrame.game.addPlayer(player);
        ourJFrame.repaint();

        //server simulations: targets order are randomized.
        play.setInitLocation(playerPos.y(),playerPos.x());
//        System.out.println();
        ourJFrame.game.updateGame(play.getBoard());
        ourJFrame.paintImmediately(0, 0, getWidth(), getHeight());
        System.out.println("*********** !! Game Started !! ***********");
        play.start();


        return targets;
    }

    private void runGreedyAlgo() {
        ArrayList<GIS_element> targets = algoInit();
        play.rotate(0); //get rid of first element, will set the GIS_element to EATEN, no performance loss.
        ourJFrame.game.updateGame(play.getBoard()); //updates the game according to our first element eaten.
        if(targets == null){ //this means initialization is wrong, message is already on screen, we should not move onto the algorithm if this happens-> just return.
            return;
        }
        Player player = (Player)game.getPlayer();
        //player is already set on the first target (randomized) from the ArrayList of targets.
        //from now on, the player will search for the next shortest path to the next target from all available targets at this time,
        //and move to the target, as long as it has not been eaten.
        GIS_layer tr = new GIS_layer_obj();
        tr.addAll(targets);
        while(tr.size() > 0 && play.isRuning()){ //still have targets to kill.
            Graph G = initGraph(tr, game, map, getHeight(), getWidth());
            String targetNode = "closeTarget";
            Node trNode = new Node(targetNode);
            G.add(trNode);
            for(GIS_element targetsLeft : tr){
                G.addEdge("" + targetsLeft.getData().getType() + targetsLeft.getID(), targetNode, 0); //we add zero-weight edge from all targets to newly-created "target"
                //which will help super-position of the real closest target. this way, Dijkstra's algorithm will find shortest route to our "closeTarget" node, between all other targets.
            }
            String sourceNode = "fromCurrentPos";
            Node fromNode = new Node(sourceNode);
            G.add(fromNode);
            PathsAvoidObstacles.addSourceToGraph(G, (Point3D) player.getGeom(), sourceNode, game, map, getHeight(), getWidth());
            ArrayList<String> pathStringToTarget = pathToTargetInclObstacles(G, sourceNode, targetNode, true);
            ArrayList<GIS_element> realPath = parseNameList(pathStringToTarget); //the gis_element order to move on, in order to get to the target.
            System.out.println("next targets for the player in greedy algo: " + realPath); //prints next target. optional.
//            realPath.remove(0); //first element is the fromTarget, we already hold it since it was the toTarget last iteration. for iteration zero-> player is placed on first target. no need to include.
            player.setTargetsOrder(realPath);
            player.moveToAllTargets(game.getGhosts()); //will move to all targets set.

            tr.removeIf(GIS_element::isEaten); //all eaten elements are not a viable GIS_element target. Already killed.
        }

    }



    /**
     * this method execute by the Menu,
     * check if there are packmans or fruits in the game.
     * we consider that everytime the HashSet will be ordered differently each time, and we take this HashSet into an Array.
     * and then we run the algorithm on this shuffled array.
     * we save the time to complete the solution and the solution
     */
    private void runRandomizedAlgo() {
        ArrayList<GIS_element> targets  = algoInit();
        if(targets == null){ //this means initialization is wrong, message is already on screen, we should not move onto the algorithm if this happens-> just return.
            return;
        }
        Player player = (Player)game.getPlayer();
        player.addTargetsList(targets); //add all fruits as targets for the player. we can add Packmen also, or any object which implements GIS_Element .
        updatePlayerPathToTargets(player,false);
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
     * This method will look for the player targets and checks if the path is legit
     * (i.e - the player can move to each target one by one without collision with obstacles).
     * If it finds that the next target requires maneuvering an obstacle,
     * it will calculate shortest path to the target while avoiding obstacles using Dijkstra's algorithm in Graph.
     * it then updates the target list of the player, so the player can move to targets in the order provided without collisions with obstacles.
     */
    private void updatePlayerPathToTargets(Player player, boolean greedy) {
        ArrayList<GIS_element> currentTargetsOrder = player.getTargetsOrder();
        ArrayList<GIS_element> newTargetsOrder = new ArrayList<>();
        for(int i = 0 ;i<currentTargetsOrder.size()-1; i++){
            GIS_element fromTarget = currentTargetsOrder.get(i);
            GIS_element toTarget = currentTargetsOrder.get(i+1);
            String fromTrName = "" + fromTarget.getData().getType() + fromTarget.getID();
            String toTrName = "" + toTarget.getData().getType() + toTarget.getID();
            //calc shortest path with obstacles avoiding using Dijkstra's:
//            System.out.println("Best route from (" +fromTrName+ ") to target named: " + toTrName+" is: "); //will print the best route from a node to the target. optional.

            //calc shortest path to next target, obstacles avoided:
            Graph g = PathsAvoidObstacles.initGraph(ourJFrame.game.getFruits(), ourJFrame.game,map,getHeight(),getWidth());
            ArrayList<String> pathStringToTarget = pathToTargetInclObstacles(g, fromTrName, toTrName, greedy);
            ArrayList<GIS_element> realPath = parseNameList(pathStringToTarget); //the gis_element order to move on, in order to get to the target.
            realPath.remove(0); //first element is the fromTarget, we already hold it since it was the toTarget last iteration. for iteration zero-> player is placed on first target. no need to include.
            newTargetsOrder.addAll(realPath);
        }
//        System.out.println("The order of movement for player will be: " + newTargetsOrder); //prints the order of movement for next target. optional.
        player.setTargetsOrder(newTargetsOrder);
    }

    /**
     * This method will print the game board and statistics incl. game score to the console.
     */
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

            //100% Guaranteed to be inside bounding box, since this is a mouse click,
            // which can only be clicked inside the frame,
            // the frame bounds are already defined.
            ourJFrame.game.addPlayer(globalPoint);

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
            System.out.println("Last move was rotate on Angle from player to click pixel. Angle: "+angle);
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

    //Keyboard supported.
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