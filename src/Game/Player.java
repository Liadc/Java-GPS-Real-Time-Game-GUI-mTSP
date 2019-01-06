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
     *
     * @param geometryOfElement Geom_element, the geometry object of the element.
     * @param dataOfElement     Meta_data, the data of the element.
     * @param ID
     */

    public Player(Geom_element geometryOfElement, Meta_data dataOfElement, int ID) {
        super(geometryOfElement, dataOfElement, ID);
    }

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
        return angle;
    }

    /**
     * This function will get a target and moves to target location until target is eaten
     * @param target gis_element_obj, such as a fruit or packmen, etc.
     */
    public void moveToEatTarget(GIS_element target) {
        Point3D currentPos = (Point3D) this.getGeom();
        Point3D targetPos = (Point3D) target.getGeom();
        //check if distance is less than eating radius, if so, return. if target is no longer available in map (already eaten), return.
        if(distancePointFromEatRadius((Point3D) target.getGeom()) == 0 ) return;
        //otherwise
        while (MyFrame.play.isRuning() && distancePointFromEatRadius((Point3D) target.getGeom()) != 0 && !target.isEaten()){
            MyFrame.play.rotate(getAngleToTarget(target));
            MyFrame.ourJFrame.game.updateGame(MyFrame.play.getBoard());
            MyFrame.ourJFrame.paintImmediately(0,0,MyFrame.ourJFrame.getWidth(),MyFrame.ourJFrame.getHeight());
            try {
                Thread.sleep(33); //30 FPS
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        target.setEaten(true);
    }

    /**
     * This method will move to all current available targets, one by one. if, for any reason, a target becomes unavailable(such as
     * a fruit is eaten by another packmen) while the player is on the way to the fruits, it will move to the next target in it's arraylist of targets.
     */
    public void moveToAllTargets(){
        Iterator<GIS_element> targetIt = targetsOrder.iterator();
        while(MyFrame.play.isRuning() && targetIt.hasNext()){
            GIS_element target = targetIt.next();
            if(!target.isEaten())
                moveToEatTarget(target);             //move to next target
        }
    }

    /**
     * This method calculate distance point from radius of packman
     * @param p - point to distance to
     * @return the distance between the Radius of packman to Point p.
     */
    public double distancePointFromEatRadius(Point3D p){
        MyCoords coords = new MyCoords();
        double d = coords.distance3d(p,(Point3D)this.getGeom());
        double dr = d - this.getEatRadius();
        double ans = Math.max(0, dr);
        return ans;
    }


    public void updateGeom(String firstBoardLine) {
        String[] arg = firstBoardLine.split(",");
        Point3D LatLonAlt = new Point3D(arg[3]+","+arg[2]+","+"0");
        setGeom(LatLonAlt);
    }

    /*** Targets for player ***/

    public void addTarget(GIS_element target){
        this.targetsOrder.add(target);
    }

    public void addTargetsList(ArrayList<GIS_element> targets){
        this.targetsOrder.addAll(targets);
    }

    public void removeTarget(GIS_element target){
        this.targetsOrder.remove(target);
    }

    /**** getters and setters ****/

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
