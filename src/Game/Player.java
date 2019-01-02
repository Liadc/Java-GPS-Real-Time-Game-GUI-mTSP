package Game;

import Coords.MyCoords;
import GIS.GIS_element_obj;
import GIS.Meta_data;
import GIS.Meta_data_element;
import GUI.MyFrame;
import Geom.Geom_element;
import Geom.Point3D;

import java.util.ArrayList;

public class Player extends GIS_element_obj {

    private double speed;
    private double eatRadius;
    private ArrayList<GIS_element_obj> targetsOrder;

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
        this.speed = 0;
        this.eatRadius = 0;
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

    public void addTarget(GIS_element_obj target){
        this.targetsOrder.add(target);
    }

    public void removeTarget(GIS_element_obj target){
        this.targetsOrder.remove(target);
    }

    public double getAngleToTarget(GIS_element_obj target){
        Point3D trPoint = (Point3D)target.getGeom();
        trPoint.transformXY();
        Point3D currentPos = (Point3D)this.getGeom();
        currentPos.transformXY();
        double[] azm = coords.azimuth_elevation_dist(currentPos,trPoint);
        double angle = azm[0];
        return angle;
    }

    public void moveToEatTarget(GIS_element_obj target) {
        Point3D currentPos = (Point3D) this.getGeom();
        Point3D targetPos = (Point3D) target.getGeom();
        //check if distance is less than eating radius, return.
        if(distancePointFromEatRadius((Point3D) target.getGeom()) == 0) return;
        //otherwise
        while (distancePointFromEatRadius((Point3D) target.getGeom()) != 0 && MyFrame.play.isRuning()){
            MyFrame.play.rotate(getAngleToTarget(target));
        }
        targetsOrder.remove(target);
    }

    public void moveToAllTargets(){
//        while()
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
