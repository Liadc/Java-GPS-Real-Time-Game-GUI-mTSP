package Game;

import Algorithms.PathsAvoidObstacles;
import GIS.GIS_element;
import GIS.GIS_layer;
import GIS.GIS_layer_obj;
import GIS.Meta_data_layerAndProject;
import Geom.Point3D;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * This class represents our Game object. it will hold two GIS_Layers (hashSet): one for fruits, one for pacmen, each
 * will hold our objects inside the game.
 */
public class Game {

    private GIS_layer fruits;
    private GIS_layer pacmen;
    private GIS_layer ghosts;
    private GIS_layer obstacles;
    private GIS_layer obstacleCorners;
    private Player player;

    /**
     * Default constructor for the Game. will initiate empty layer for fruits, and an empty layer for pacmen.
     */
    public Game() {
        pacmen = new GIS_layer_obj();
        pacmen.setMeta(new Meta_data_layerAndProject("Pacmen Layer"));
        fruits = new GIS_layer_obj();
        fruits.setMeta(new Meta_data_layerAndProject("Fruits Layer"));
        ghosts = new GIS_layer_obj();
        ghosts.setMeta(new Meta_data_layerAndProject("Ghosts Layer"));
        obstacles = new GIS_layer_obj();
        obstacles.setMeta(new Meta_data_layerAndProject("Obstacles Layer"));
        player = null;
    }

    public Game(ArrayList<String> board) {
        pacmen = new GIS_layer_obj();
        pacmen.setMeta(new Meta_data_layerAndProject("Pacmen Layer"));
        fruits = new GIS_layer_obj();
        fruits.setMeta(new Meta_data_layerAndProject("Fruits Layer"));
        ghosts = new GIS_layer_obj();
        ghosts.setMeta(new Meta_data_layerAndProject("Ghosts Layer"));
        obstacles = new GIS_layer_obj();
        obstacles.setMeta(new Meta_data_layerAndProject("Obstacles Layer"));
        Iterator<String> lines = board.iterator();
        while (lines.hasNext()) {
            String line = lines.next();
            if (line.charAt(0) == 'M') {
                player = new Player(line);
            } else if (line.charAt(0) == 'P') {
                Packman pacman = new Packman(line);
                pacmen.add(pacman);
            } else if (line.charAt(0) == 'F') {
                Fruit fruit = new Fruit(line);
                fruits.add(fruit);
            } else if (line.charAt(0) == 'G') {
                Ghost ghost = new Ghost(line);
                ghosts.add(ghost);
            } else if (line.charAt(0) == 'B') {
                Obstacle obstacle = new Obstacle(line);
                obstacles.add(obstacle);

            }
        }
    }

    public void updatePlayer(String firstBoardLine) {
        player.updateGeom(firstBoardLine);
    }

    public void updateFruit(String firstBoardLine) {
        String[] parsed = firstBoardLine.split(",");
        int updateID = 0;
        try {
            updateID = Integer.parseInt(parsed[1]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        Iterator fruitIterator = fruits.iterator();
        while (fruitIterator.hasNext()) {
            Fruit fruit = (Fruit) fruitIterator.next();
            if (fruit.getID() == updateID) {
                fruit.updateGeom(firstBoardLine);
                fruit.setNecessary(true);
                break;
            }
        }
    }

    public void updatePackman(String firstBoardLine) {
        String[] parsed = firstBoardLine.split(",");
        int updateID = 0;
        try {
            updateID = Integer.parseInt(parsed[1]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        Iterator pacmenIterator = pacmen.iterator();
        while (pacmenIterator.hasNext()) {
            Packman packman = (Packman) pacmenIterator.next();
            if (packman.getID() == updateID) {
                packman.updateGeom(firstBoardLine);
                packman.setNecessary(true);
                break;
            }
        }
    }

    public void updateGhost(String firstBoardLine) {
        String[] parsed = firstBoardLine.split(",");
        int updateID = 0;
        try {
            updateID = Integer.parseInt(parsed[1]);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        Iterator ghostIterator = ghosts.iterator();
        while (ghostIterator.hasNext()) {
            Ghost ghost = (Ghost) ghostIterator.next();
            if (ghost.getID() == updateID) {
                ghost.updateGeom(firstBoardLine);
                ghost.setNecessary(true);
                break;
            }
        }
    }

    public void updateGame(ArrayList<String> gameBoard) {

        Iterator<String> lines = gameBoard.iterator();
        while (lines.hasNext()) {
            String line = lines.next();
            if (line.charAt(0) == 'M') {
                updatePlayer(line);
            } else if (line.charAt(0) == 'P') {
                updatePackman(line);
            } else if (line.charAt(0) == 'F') {
                updateFruit(line);
            } else if (line.charAt(0) == 'G') {
                updateGhost(line);
            }
        }
        clearUnNecessary();
        resetUnNecessary();
    }

    public void initCorners(){
        if(getObstacleCorners() == null)
            setObstacleCorners((GIS_layer)(PathsAvoidObstacles.createObstacleCorners(this.getObstacles(),this)));
        else {
            System.out.println("Already initiated corners.");
        }
    }


    private void resetUnNecessary() {
        for (GIS_element p : pacmen) {
            p.setNecessary(false);
        }
        for (GIS_element f : fruits) {
            f.setNecessary(false);
        }
    }


    private void clearUnNecessary() {
        ArrayList<GIS_element> pacFruits = new ArrayList<>(pacmen);
        pacFruits.addAll(fruits);
        for(GIS_element elem : pacFruits){ //if objects are about to remove from the game, we set their 'eaten' field to TRUE.
            if (!elem.isNecessary()) {
                elem.setEaten(true);
            }
        }
        pacmen.removeIf(pacman -> !pacman.isNecessary());
        fruits.removeIf(fruit -> !fruit.isNecessary());
    }


    /**
     * Saves current game state into CSV file. returns CSV file path.
     *
     * @return file path for CSV file.
     */
    public void saveGameToCsv(String fullPath) {
        final String COMMA = ",";
        final String NEW_LINE = "\n";
        String CSVheader = "Type,id,Lat,Lon,Alt,Speed/Weight,Radius";
        FileWriter fileWriter = null;

        try {
            fileWriter = new FileWriter(fullPath);
            fileWriter.append(CSVheader); //add header
            fileWriter.append(NEW_LINE); //new line separator after the header

            //Write all pacmen objects to the CSV file
            Iterator itpackman = this.pacmen.iterator();
            while (itpackman.hasNext()) {
                Packman pacman = (Packman) itpackman.next();
                fileWriter.append(pacman.getData().getType()); //type
                fileWriter.append(COMMA);
                fileWriter.append(String.valueOf(pacman.getID())); //ID
                fileWriter.append(COMMA);
                fileWriter.append(String.valueOf(((Point3D) pacman.getGeom()).y())); //lat (same as boaz wrong files)
                fileWriter.append(COMMA);
                fileWriter.append(String.valueOf(((Point3D) pacman.getGeom()).x())); //lon (same as boaz wrong files)
                fileWriter.append(COMMA);
                fileWriter.append(String.valueOf(((Point3D) pacman.getGeom()).z())); //alt
                fileWriter.append(COMMA);
                fileWriter.append(String.valueOf(pacman.getSpeed()));
                fileWriter.append(COMMA);
                fileWriter.append(String.valueOf(pacman.getEatRadius()));
                fileWriter.append(NEW_LINE);
            }
            //Write all fruits objects to the CSV file
            Iterator itFruit = this.fruits.iterator();
            while (itFruit.hasNext()) {
                Fruit fruit = (Fruit) itFruit.next();
                fileWriter.append(fruit.getData().getType()); //type
                fileWriter.append(COMMA);
                fileWriter.append(String.valueOf(fruit.getID())); //ID
                fileWriter.append(COMMA);
                fileWriter.append(String.valueOf(((Point3D) fruit.getGeom()).y())); //lat (same as boaz wrong files)
                fileWriter.append(COMMA);
                fileWriter.append(String.valueOf(((Point3D) fruit.getGeom()).x())); //lon (same as boaz wrong files)
                fileWriter.append(COMMA);
                fileWriter.append(String.valueOf(((Point3D) fruit.getGeom()).z())); //alt
                fileWriter.append(COMMA);
                fileWriter.append(String.valueOf(fruit.getWeight()));
                fileWriter.append(NEW_LINE);
            }
            System.out.println("CSV file created successfully");
        } catch (Exception e) {
            System.out.println("Error in CsvFileWriter");
            e.printStackTrace();
        } finally {
            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                System.out.println("Error while flushing/closing fileWriter !!!");
                e.printStackTrace();
            }
        }
    }

    public void addPlayer(Player player) {
        this.player = player;
    }

    public void addPlayer(Point3D pos) {
        player = new Player();
        player.setID(0);
        player.setGeom(pos);
        player.setSpeed(20);
        player.setEatRadius(1);
    }

    /*** Getters  and Setters ***/
    public GIS_layer getFruits() {
        return fruits;
    }

    public GIS_layer getPacmen() {
        return pacmen;
    }


    public boolean hasPlayer() {
        if (player != null) {
            Point3D pos = (Point3D) (player.getGeom());
            return !(pos.x() == 0 && pos.y() == 0);
        }
        return false;
    }

    public GIS_layer getGhosts() {
        return ghosts;
    }

    public GIS_layer getObstacles() {
        return obstacles;
    }

    public GIS_element getPlayer() {
        return player;
    }

    public GIS_layer getObstacleCorners() {
        return obstacleCorners;
    }

    public void setObstacleCorners(GIS_layer obstacleCorners) {
        this.obstacleCorners = obstacleCorners;
    }

}
