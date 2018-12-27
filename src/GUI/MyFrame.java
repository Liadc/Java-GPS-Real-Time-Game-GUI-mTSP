package GUI;

//import Algorithms.ShortestPathAlgo;
import Algorithms.Solution;
import File_format.Path2KML;
import Game.Fruit;
import Game.Game;
import Game.Map;
import Game.Ghost;
import Game.Obstacle;
import Game.Player;
import Game.Packman;
import Geom.Path;
import Geom.Point3D;
import Robot.Play;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * some of the code is taken from: https://javatutorial.net/display-text-and-graphics-java-jframe
 */
public class MyFrame extends JPanel implements MouseListener {

    private Image image; //game background image.
    private Game game; //game object to work with.
    private int typeToAdd = 0; //1 for pacman, 2 for fruits.
    private Map map; //map object according to provided image.
    private static MyFrame ourJFrame;
    private Painter paintThread;
    private Play play;


    public MyFrame() {
        this.game = new Game();
        Point3D topLeft = new Point3D(32.101898,35.202369); //TODO: Change this hardCoded to Game Bounded (ourJframe.play.getBoundingBox();)
        Point3D downRight = new Point3D(32.105728,35.212416);
        this.map = new Map(new File("Resources/GameMaps/Ariel1.png"),topLeft,downRight);
        addMouseListener(this);
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
        frame.setSize(900, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setVisible(true);


        MenuBar MainMenu = new MenuBar();
        frame.setMenuBar(MainMenu);
        Menu fileMenu = new Menu("File");
        Menu addMenu = new Menu("Add");
        Menu algoMenu = new Menu("Algorithm");


        MenuItem player = new MenuItem("Add Player");
        MenuItem fruitItemMenu = new MenuItem("Fruit");
        MenuItem reset = new MenuItem("Reset");
        MenuItem LoadboazCSV = new MenuItem("LoadboazCSV");



        player.addActionListener((e -> ourJFrame.typeToAdd = 1));

        //reset clicked
        reset.addActionListener(e -> {
            ourJFrame.resetGame();
            ourJFrame.repaint();
        });
        addMenu.add(player);
        addMenu.add(fruitItemMenu);
        addMenu.add(reset);

        MenuItem loadFromCsvItemMenu = new MenuItem("Load From CSV");
        MenuItem saveToCsvItemMenu = new MenuItem("Save To CSV");
        MenuItem exportToKML = new MenuItem("Export to KML");

        MenuItem run = new MenuItem("Run");

        algoMenu.add(run);





        //load csv of boaz
        fileMenu.add(LoadboazCSV);
        LoadboazCSV.addActionListener(e->{
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
                ourJFrame.play.setIDs(311508220,316602630);
                ourJFrame.game = new Game(ourJFrame.play.getBoard());

                System.out.println(chooser.getSelectedFile());
            }else{
                System.out.println("No file selected.");
            }
            ArrayList<String> board_data = ourJFrame.play.getBoard();
            for(int i=0;i<board_data.size();i++) {
                System.out.println(board_data.get(i));
            }
            ourJFrame.repaint();
        });




        //save file
        fileMenu.add(saveToCsvItemMenu);
        saveToCsvItemMenu.addActionListener(e->{
            if(ourJFrame.paintThread != null){ //if we have a thread painting in the background, we will stop the animation and kill the thread.
                ourJFrame.paintThread.stopAnimKillThread();
            }
            JFileChooser chooser = new JFileChooser("./Resources/dataExamples");
            FileNameExtensionFilter filter =   new FileNameExtensionFilter(
                    "CSV Files", "csv");
            chooser.setFileFilter(filter);
            chooser.setAcceptAllFileFilterUsed(false);  // disable the "All files" option.
            int returnValue = chooser.showSaveDialog(null);
            if(returnValue == JFileChooser.APPROVE_OPTION){
                File file = new File(String.valueOf(chooser.getSelectedFile()));
                if (file.getName().endsWith(".csv")) {
                    ourJFrame.saveFile(file);
                    System.out.println(chooser.getSelectedFile());
                }else{
                    JOptionPane.showMessageDialog(null, "Saved games must end with .csv file extension!! Try again.");
                }
            }else{
                System.out.println("Cancel button pressed.");
            }
        });


        //run algo clicked
        run.addActionListener(l->{ if(ourJFrame.paintThread != null && ourJFrame.paintThread.isKeepGoing()){ /*if we have a thread painting in the background, we will stop the animation and kill the thread.*/ourJFrame.paintThread.stopAnimKillThread();
            }
            try {
                ourJFrame.runAlgo();
            }catch (RuntimeException e){
                JOptionPane.showMessageDialog(null, e.getMessage());

            }
        });
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
//        if(this.game.getPacmen().size() == 0){
//            throw new RuntimeException("No pacmen to calculate solution.");
//        } else if(this.game.getFruits().size() == 0){
//            throw new RuntimeException("No fruits to calculate solution.");
//        }
//        ArrayList<GIS_element> packmen = new ArrayList<>(this.game.getPacmen());
//        Solution bestSolution = null;
//        long bestTime = Long.MAX_VALUE;
//        for(int i=0;i<packmen.size()*game.getFruits().size()*2;i++) { //change to get faster speed -> less optimized solution.
//            Collections.shuffle(packmen);
//            ShortestPathAlgo algo = new ShortestPathAlgo(packmen,game.getFruits());
//            Solution algoSolution = algo.runAlgo();
//            if (bestTime > algoSolution.timeToComplete()) {
//                bestSolution = algoSolution;
//                bestTime = (long)algoSolution.timeToComplete();
//            }
//        }
//        linesSolution = bestSolution;
//        resetTimeAfterAlgoAndSetEatenTimes(linesSolution);
//        System.out.println("Total time to complete all paths: " + linesSolution.timeToComplete()/1000);
//        paintThread = new Painter(bestSolution,ourJFrame);
//        Thread repainter = new Thread(paintThread);
//        repainter.start();

    }

    /**
     * Because we run the algo couple times, we need to set the eating times for each packman, since they are eating from the
     * beginning each run of the algorithm
     * @param solution
     */
    private void resetTimeAfterAlgoAndSetEatenTimes(Solution solution){
        Iterator<Path> paths = solution.getPaths().iterator();
        while (paths.hasNext()) {
            Path pt = paths.next();
            pt.getPacmanInPath().getData().setUTCtime(solution.getTimeStart()); //reset pacman time to the time of best algorithm start time.
            Iterator<Fruit> frIt = pt.getFruitsInPath().iterator();
            while(frIt.hasNext()){
                Fruit frInPath = frIt.next();
                frInPath.getData().setUTCtime(solution.getTimeStart());//reset fruit time to the time of best algorithm start time.
                frInPath.setTimeToEat((long)(pt.getDistance(pt.getFruitsInPath().indexOf(frInPath))/pt.getPacmanInPath().getSpeed()*1000)); //set eaten time for fruit in specific path in best algo solution.
            }
        }
    }

    /**
     * This function will save our Game into csv file.
     * @param file - get File Object to save the game into.
     */
    private void saveFile(File file) {
        this.game.saveGameToCsv(file.getAbsolutePath());
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        if(ourJFrame.paintThread != null && ourJFrame.paintThread.isKeepGoing()){ //if we have a thread painting in the background, we will stop the animation and kill the thread.
            ourJFrame.paintThread.stopAnimKillThread();
            showMessageToScreen("You clicked to add into the map while animation was running.\n" +
                    "We will stop the animation now.");
        }
        if (typeToAdd == 1 && !ourJFrame.game.hasPlayer()) {
            Point3D pointPixel = new Point3D(e.getX(), e.getY(), 0);
            Point3D globalPoint = map.PixelsToCoords(pointPixel, getHeight(), getWidth());
            //TODO: Check if inBound BOX
            ourJFrame.play.setInitLocation(globalPoint.x(),globalPoint.y());
            typeToAdd=2;
        }
        repaint();
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
}