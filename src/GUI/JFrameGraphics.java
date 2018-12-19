package GUI;

import Algorithms.ShortestPathAlgo;
import Algorithms.Solution;
import File_format.Path2KML;
import GIS.GIS_element;
import GIS.Meta_data_element;
import Game.Fruit;
import Game.Game;
import Game.Map;
import Game.Packman;
import Geom.Path;
import Geom.Point3D;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * some of the code is taken from: https://javatutorial.net/display-text-and-graphics-java-jframe
 */
public class JFrameGraphics extends JPanel implements MouseListener {

    private Image image; //game background image.
    private Game game; //game object to work with.
    private int typeToAdd = 0; //1 for pacman, 2 for fruits.
    private Map map; //map object according to provided image.
    private static Solution linesSolution;
    private int IDfruits = 0;
    private int IDpacs = 0;
    private static JFrameGraphics ourJFrame;
    private Painter paintThread;
    private static boolean latLonSwitched = false;


    public JFrameGraphics() {
        this.game = new Game();
        Point3D topLeft = new Point3D(35.20236,32.10572);
        Point3D downRight = new Point3D(35.21235,32.10194);
        this.map = new Map(new File("Resources/GameMaps/Ariel1.png"),topLeft,downRight);
        addMouseListener(this);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
//        System.out.println("Started paint");
        image = Toolkit.getDefaultToolkit().getImage(map.getImagePath());
        int w = this.getWidth();
        int h = this.getHeight();
        g.drawImage(image, 0, 0, w, h, this);
        Iterator PacIterator = game.getPacmen().iterator();
        Iterator FruitIterator = game.getFruits().iterator();

        while (PacIterator.hasNext()) {
            Packman pacman = (Packman)PacIterator.next();
            Point3D pixel = null;
            try { //pixel might be out of map bounds.
                pixel = map.CoordsToPixels((Point3D)pacman.getGeom(), getHeight(), getWidth(),false);
                if(pixel.z()==1){ //z pixel is updated to 1 if the switched lat-lon works for our map!
                    latLonSwitched = true;
                }
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
                    pixel = map.CoordsToPixels((Point3D)fruit.getGeom(), getHeight(), getWidth(),false);
                    if(pixel.z()==1){ //z pixel is updated to 1 if the switched lat-lon works for our map!
                        latLonSwitched = true;
                    }
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
        if(this.linesSolution!=null) {
            Iterator<Path> pathIterator = this.linesSolution.getPaths().iterator();
            while (pathIterator.hasNext()) {
                Path path = pathIterator.next();
                g.setColor(path.getColor());
                Point3D pixel1, pixel2;
                try {
                    pixel1 = map.CoordsToPixels(path.getPacmanStartPosition(), getHeight(), getWidth(), false);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(null, e.getMessage());
                    this.game = new Game();
                    g.drawImage(image, 0, 0, w, h, this);
                    break;
                }
                Iterator<Fruit> fruitItPath = path.getFruitsInPath().iterator();
                while (fruitItPath.hasNext()) {
                    Fruit fruit = fruitItPath.next();
                    try { //pixel might be out of map bounds.
                        pixel2 = map.CoordsToPixels((Point3D) fruit.getGeom(), getHeight(), getWidth(), false);
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(null, e.getMessage());
                        this.game = new Game();
                        g.drawImage(image, 0, 0, w, h, this);
                        break;
                    }
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setStroke(new BasicStroke(3));
                    g2.drawLine((int) pixel1.x(), (int) pixel1.y(), (int) pixel2.x(), (int) pixel2.y());
                    pixel1 = pixel2;
                }
            }
        }

//        System.out.println("Finished paint");
    }
    private static void showMessageToScreen(String msg){
        JOptionPane.showMessageDialog(null, msg);
    }

    private void resetGame() {
        this.game = new Game();
        if(linesSolution!=null) //if we have a solution after running algorithm, we will have to erase its paths .
            linesSolution.getPaths().clear();
        if(paintThread!=null) //if we have a repainter thread which is showing animation, we will have to kill it.
            ourJFrame.paintThread.setKeepGoing(false); //kills repainter thread
        latLonSwitched = false;
        ourJFrame.repaint();
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Pacman and Fruits");
        ourJFrame = new JFrameGraphics();
        frame.getContentPane().add(ourJFrame);
        frame.setSize(900, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(true);
        frame.setVisible(true);


        MenuBar MainMenu = new MenuBar();
        frame.setMenuBar(MainMenu);
        Menu fileMenu = new Menu("File");
        Menu addMenu = new Menu("Add");
        Menu algoMenu = new Menu("Algo");


        MenuItem pacmenItemMenu = new MenuItem("Pacman");
        MenuItem fruitItemMenu = new MenuItem("Fruit");
        MenuItem reset = new MenuItem("Reset");



        fruitItemMenu.addActionListener(e -> ourJFrame.typeToAdd = 2);
        pacmenItemMenu.addActionListener((e -> ourJFrame.typeToAdd = 1));

        //reset clicked
        reset.addActionListener(e -> {
            ourJFrame.resetGame();
            ourJFrame.repaint();
        });
        addMenu.add(reset);
        addMenu.add(pacmenItemMenu);
        addMenu.add(fruitItemMenu);

        MenuItem loadFromCsvItemMenu = new MenuItem("Load From CSV");
        MenuItem saveToCsvItemMenu = new MenuItem("Save To CSV");
        MenuItem exportToKML = new MenuItem("Export to KML");

        MenuItem run = new MenuItem("run");

        algoMenu.add(run);


        //load file
        fileMenu.add(loadFromCsvItemMenu);
        loadFromCsvItemMenu.addActionListener(e->{
            JFileChooser chooser = new JFileChooser("./Resources/dataExamples");
            FileNameExtensionFilter filter =   new FileNameExtensionFilter(
                    "CSV Files", "csv");
            chooser.setFileFilter(filter);
            chooser.setAcceptAllFileFilterUsed(false);  // disable the "All files" option.
            int returnValue = chooser.showOpenDialog(null);
            if(returnValue == JFileChooser.APPROVE_OPTION){
                File file = new File(String.valueOf(chooser.getSelectedFile()));
                ourJFrame.loadFile(file);
                System.out.println(chooser.getSelectedFile());
            }else{
                System.out.println("No file selected.");
            }
        });

        //save file
        fileMenu.add(saveToCsvItemMenu);
        saveToCsvItemMenu.addActionListener(e->{
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

        //export to kml clicked
        fileMenu.add(exportToKML);
        exportToKML.addActionListener(e->{
            if(linesSolution==null){
                showMessageToScreen("You have to run the algorithm first to find the paths solution.\n" +
                        "After that, you can try to export again.");
            }
            else if(latLonSwitched){
                showMessageToScreen("You have switched LAT-LON coordinates while loading a CSV file. \n" +
                        "You can only view where the points would have been if you provided a CORRECT file, \n" +
                        "But you cannot export this game into KML.");
            }else {
                String fileName = JOptionPane.showInputDialog("Enter name for your kml file: ");
                Path2KML toKml = new Path2KML();
                toKml.constructKML(fileName, linesSolution, ourJFrame.game);
            }
        });

        //run algo clicked
        run.addActionListener(l->{
            try {
                ourJFrame.runAlgo();
            }catch (RuntimeException e){
                JOptionPane.showMessageDialog(null, e.getMessage());

            }
        });
        MainMenu.add(algoMenu);
        MainMenu.add(fileMenu);
        MainMenu.add(addMenu);

    }

    private void runAlgo() {
        if(this.game.getPacmen().size() == 0){
            throw new RuntimeException("No pacmen to calculate solution.");
        } else if(this.game.getFruits().size() == 0){
            throw new RuntimeException("No fruits to calculate solution.");
        }
        ArrayList<GIS_element> packmen = new ArrayList<>(this.game.getPacmen());
        Solution bestSolution = null;
        long bestTime = Long.MAX_VALUE;
        for(int i=0;i<packmen.size()*game.getFruits().size()*2;i++) { //TODO: change to get faster speed -> less optimized solution.
            Collections.shuffle(packmen);
            ShortestPathAlgo algo = new ShortestPathAlgo(packmen,game.getFruits());
            Solution algoSolution = algo.runAlgo();
            if (bestTime > algoSolution.timeToComplete()) {
                bestSolution = algoSolution;
                bestTime = (long)algoSolution.timeToComplete();
            }
        }
        linesSolution = bestSolution;
        resetTimeAfterAlgoAndSetEatenTimes(linesSolution);
        System.out.println(linesSolution); //TODO: delete this.
        System.out.println("Total time to complete all paths: " + linesSolution.timeToComplete()/1000);
        paintThread = new Painter(bestSolution,ourJFrame);
        Thread repainter = new Thread(paintThread);
        repainter.start();

    }
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

    private void saveFile(File file) {
        this.game.saveGameToCsv(file.getAbsolutePath());
    }

    private void loadFile(File file) {
        resetGame();
        try {
            this.game = new Game(file);
            repaint(); //repaint might update latLonSwitched for loaded file. we will give message to the user.
        }catch (Exception e){
            JOptionPane.showMessageDialog(null, "This CSV file is not compatible with our game.");
        }

    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (typeToAdd == 1) {
            Point3D point = new Point3D(e.getX(), e.getY(), 0);
            Point3D globalpoint = map.PixelsToCoords(point, getHeight(), getWidth());
            Meta_data_element pacman_meta = new Meta_data_element("Packman name", "P"); //color is white as default.
            Packman pac = new Packman(globalpoint, pacman_meta,IDpacs++, 1, 1); //orientation is (1,1,1) as default.
            game.getPacmen().add(pac);
        }
        if (typeToAdd == 2) {
            Point3D point = new Point3D(e.getX(), e.getY(), 0);
            Point3D globalpoint2 = map.PixelsToCoords(point, getHeight(), getWidth());
            Meta_data_element fruit_meta = new Meta_data_element("Fruit name", "F"); //color is red as default.
            Fruit fruit = new Fruit(globalpoint2,fruit_meta,IDfruits++,1);
            game.getFruits().add(fruit);
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