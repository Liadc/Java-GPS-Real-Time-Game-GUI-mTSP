package Game;

import Coords.MyCoords;
import GIS.*;
import GUI.MyFrame;
import Geom.Geom_element;
import Geom.Point3D;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

public class Player extends GIS_element_obj {

    private double speed;
    private double eatRadius;
    private ArrayList<GIS_element> targetsOrder;

    private static MyCoords coords = new MyCoords();


    /**
     * Constructor for the GIS_element object. gets a Geom_element, Meta_data and ID.
     * @param geometryOfElement Geom_element, the geometry object of the element.
     * @param dataOfElement     Meta_data, the data of the element.
     * @param ID                Integer, the ID for the element.
     */

    public Player(Geom_element geometryOfElement, Meta_data dataOfElement, int ID) {
        super(geometryOfElement, dataOfElement, ID);
    }

    /**
     * empty constructor for the Player.
     * Will set its speed to 20.0 , eat radius to 1, and an empty arraylist of targets.
     */
    public Player() {
        super();
        this.speed = 20.0;
        this.eatRadius = 1;
        targetsOrder = new ArrayList<>();
    }

    /**
     * Constructor for the GIS_element object. gets a String.
     * the string will be splitted and each argument will be sent to the right construction.
     *
     * @param line, The all data about this element.
     *
     */

    public Player(String line){
        super();
        String[] arg = line.split(",");
        Point3D LatLonAlt = new Point3D(arg[3]+","+arg[2]+","+"0");
        setGeom(LatLonAlt);
        setMetaData(new Meta_data_element("Player","M"));
        try {
            setID(Integer.parseInt(arg[1]));
            this.speed = Double.parseDouble(arg[5]);
            this.eatRadius = Double.parseDouble(arg[6]);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Exception creating new Player "+e.getMessage());
        }
        targetsOrder = new ArrayList<>();
    }



    /**
     * Given a gis_element_obj as target, this method will return the angle from the player position to this target.
     * @param target gis_element_obj, such as fruit,packmen, etc.
     * @return the angle, in degrees (Double), to the target.
     */
    public double getAngleToTarget(GIS_element target){
        Point3D trPoint = (Point3D)target.getGeom();
        trPoint.transformXY();
        Point3D currentPos = (Point3D)this.getGeom();
        currentPos.transformXY();
        double[] azm = coords.azimuth_elevation_dist(currentPos,trPoint);
        double angle = azm[0];
        trPoint.transformXY();
        currentPos.transformXY();
        return angle;
    }

    /**
     * This function will get a target and moves to target location until target is eaten, while avoiding ghosts on the way.
     * @param target gis_element_obj, such as a fruit or packmen, etc.
     * @param ghosts GIS_layer, a set containing all ghosts (enemies) to avoid while moving to a target.
     */
    public void moveToEatTarget(GIS_element target, GIS_layer ghosts) {
        Point3D targetPos = (Point3D) target.getGeom();
        //check if distance is less than eating radius, if so, return. if target is no longer available in map (already eaten), return.
        if(distancePointFromEatRadius((Point3D) target.getGeom()) == 0 || target.isEaten()) return;
        //otherwise
        while (MyFrame.play.isRuning() && distancePointFromEatRadius(targetPos) > 0.5 && !target.isEaten()){
            //player about to rotate towards target. logic for checking for ghosts in the way will be here.
//            avoidGhosts(getAngleToTarget(target), ghosts);
            MyFrame.play.rotate(getAngleToTarget(target));  //actually moving.
            MyFrame.ourJFrame.game.updateGame(MyFrame.play.getBoard());
            MyFrame.ourJFrame.paintImmediately(0,0,MyFrame.ourJFrame.getWidth(),MyFrame.ourJFrame.getHeight());
            System.out.println(MyFrame.play.getStatistics());//show the score
            try {
                Thread.sleep(33); //33=>30 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if(!(target instanceof ObstacleCorner)){ //an Obstacle Corner cannot be eaten.
            target.setEaten(true);
        }
    }

    /**
     * This method will get the angleToMove the player is about to perform, and a Set of ghosts (or any set representing an "enemy", its actually a Set of GIS_elements.
     * for each ghost, it will check if it is in our way to the target we are about to rotate to. (normalized angle vectors is the danger zone).
     * if it is, AND it's close to us, we will perform 3 rotates with angle normalized to the ghost, to maneuver avoid the ghost.
     * note: multiple close ghosts coming from all sides will not succeed to maneuver. also, multiple obstacles near ghosts coming towards us will sometimes not proceed.
     * according to Boaz, there will be no "overlapping" or "many obstacles" with close range to move(short paths) so this function is will satisfy question 4 for our AI algorithm.
     * @param angleToMove double, the angle that the player is about to rotate to.
     * @param ghosts GIS_layer, (set of gis_elements) , our enemies we will avoid before rotation.
     */
    private void avoidGhosts(double angleToMove, GIS_layer ghosts) {
        Point3D currentPos = (Point3D)getGeom();
        //for each ghost, we will check if it is in our way to the target we are about to rotate to.
        //if it is, AND it's close to us, we will perform 3 rotates with angle normalized to the ghost, to maneuver avoid the ghost.
        double minDangerZone = angleToMove-90;
        double maxDangerZone = angleToMove+90;
        while (minDangerZone<0){
            minDangerZone+=360;
        }
        while (maxDangerZone>=360){
            maxDangerZone-=360;
        }
        if(minDangerZone>maxDangerZone){ //if 360 degrees reached we can swap min-max to later use correct logic.
            double temp = minDangerZone;
            minDangerZone = maxDangerZone;
            maxDangerZone = temp;
        }
        System.out.println("Min danger zone degree: "+ minDangerZone); //todo: delete
        System.out.println("Max danger zone degree: "+ maxDangerZone);//todo: delete
        for(GIS_element ghost : ghosts){
            double angleToGhost = getAngleToTarget(ghost);
            if (angleToGhost > minDangerZone && angleToGhost < maxDangerZone && distancePointFromEatRadius((Point3D) ghost.getGeom()) < 6) {
                double angleNormalized = angleToGhost;
                if(angleToGhost >minDangerZone && angleToGhost<angleToMove)  angleNormalized+=60; else angleNormalized -=60;
                for(int i =0; i<3; i++) {
                    System.out.println("Angle to Ghost: " + angleToGhost); //todo: delete
                    System.out.println("Maneuvering to avoid ghost! slow-motion movement!");
                    System.out.println("Rotating towards angle: " + angleNormalized);
                    MyFrame.play.rotate(angleNormalized);  //actually moving. towards normalized-vector angle to the ghost.
                    MyFrame.ourJFrame.game.updateGame(MyFrame.play.getBoard());
                    MyFrame.ourJFrame.paintImmediately(0, 0, MyFrame.ourJFrame.getWidth(), MyFrame.ourJFrame.getHeight());
                    try {
                        Thread.sleep(500); //slow motion movement.
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }

    /**
     * This method will get a set of Ghosts, and moves the player to all current available targets, one by one.
     * if, for any reason, a target becomes unavailable(such as a fruit is eaten by another packmen)
     * while the player is on the way to the fruits, it will move to the next target in it's arraylist of targets.
     * All this while avoiding ghosts on its way.
     * @param ghosts GIS_layer, the Set of ghosts to avoid.
     */
    public void moveToAllTargets(GIS_layer ghosts){
        Iterator<GIS_element> targetIt = targetsOrder.iterator();
        while(MyFrame.play.isRuning() && targetIt.hasNext()){
            GIS_element target = targetIt.next();
            if(!target.isEaten())
                moveToEatTarget(target, ghosts);             //moves to next target
        }
    }

    /**
     * This method calculate distance point from radius of packman
     * @param p - point to check distance to
     * @return the distance between the Radius of packman to Point p.
     */
    public double distancePointFromEatRadius(Point3D p){
        MyCoords coords = new MyCoords();
        Point3D currnetPos = (Point3D)this.getGeom();
        double d = coords.distance3d(p,currnetPos);
        double dr = d - this.getEatRadius();
        double ans = Math.max(0, dr);
        return ans;
    }

    /**
     * This method will get a String representing all data about the new Object, but will only update the Geom of the
     * Player, i.e its  Point3D position.
     * @param firstBoardLine String, the data about this packman needed to be parsed in order to update the Geom.
     */
    public void updateGeom(String firstBoardLine) {
        String[] arg = firstBoardLine.split(",");
        Point3D LatLonAlt = new Point3D(arg[3]+","+arg[2]+","+"0");
        setGeom(LatLonAlt);
    }

    /*** Targets for player ***/

    /**
     * This method will get a GIS_element target and add it to the player's arrayList of targets.
     * @param target GIS_element, an object to add into the ArrayList of targets for this player.
     */
    public void addTarget(GIS_element target){
        this.targetsOrder.add(target);
    }

    /**
     * This method will get an ArrayList of GIS_elements (targets) and sets the ArrayList of targets for the player
     * to this given ArrayList.
     * @param targets ArrayList of GIS_elements, to set as targets for this player.
     */
    public void setTargetsOrder(ArrayList<GIS_element> targets){
        this.targetsOrder = targets;
    }

    /**
     * This method will get an ArrayList of GIS_elements (targets) and adds all elements into the current ArrayList
     * of targets for this player.
     * @param targets ArrayList of GIS_elements, to add to the current ArrayList of targets.
     */
    public void addTargetsList(ArrayList<GIS_element> targets){
        this.targetsOrder.addAll(targets);
    }

    /**
     * This method will get an GIS_element (target) and removes it from the ArrayList of targets for this player.
     * @param target
     */
    public void removeTarget(GIS_element target){
        this.targetsOrder.remove(target);
    }

    /**** getters and setters ****/

    public ArrayList<GIS_element> getTargetsOrder(){
        return this.targetsOrder;}

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getEatRadius(){
        return this.eatRadius;
    }

    public void setEatRadius(double eatRadius) {
        this.eatRadius = eatRadius;
    }


}
