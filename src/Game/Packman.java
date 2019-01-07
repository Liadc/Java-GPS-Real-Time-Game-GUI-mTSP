package Game;

import Coords.MyCoords;
import GIS.GIS_element;
import GIS.GIS_element_obj;
import GIS.Meta_data_element;
import Geom.Geom_element;
import Geom.Point3D;

import java.net.IDN;

/**
 * This class represents a Pacman object in our game. it will have Geom_element, Meta_data element, speed, eating radius to eat fruits, timeTraveled (updated for the last solution
 * provided by algorithm run) and an orientation.
 */
public class Packman extends GIS_element_obj{

    private double speed;
    private double eatRadius;

    /**
     * Constructor for the Packman object. gets a String and creates a new Packman object.
     * The string is exactly in the form given in Ex4 string so we can parse with related indexes.
     * @param line the string represents all data required to create a packman.
     */
    public Packman(String line) {
        super();
        String[] lineArr = line.split(",");
        try {
            this.setID(Integer.parseInt(lineArr[1]));
            Point3D geom = new Point3D(Double.parseDouble(lineArr[3]), Double.parseDouble(lineArr[2]), Double.parseDouble(lineArr[4]));
            this.setGeom(geom);
            this.setMetaData(new Meta_data_element("Packman", "P"));
            this.speed = Double.parseDouble(lineArr[5]);
            this.eatRadius = Double.parseDouble(lineArr[6]);
        }catch (Exception e){
            throw new RuntimeException("Exception creating new Packman, " + e.getMessage());
        }
    }

    /**
     * Constructor for the Packman object. gets a Geom_element and Meta_data_element, and ID.
     * @param geometryOfElement Geom_element, the geometry object of the element.
     * @param dataOfElement     Meta_data, the data of the element.
     * @param ID integer, ID to construct the packman with.
     */
    public Packman(Geom_element geometryOfElement, Meta_data_element dataOfElement, int ID) { //Meta_data_element is here ON PURPOSE, we must build new pacman object with this type of meta.
        super(geometryOfElement, dataOfElement, ID);
        this.speed = 1;
        this.eatRadius = 1;
    }

    /**
     * Constructor for the Packman object. gets a Geom_element and Meta_data_element, ID, speed and eatRadius.
     * @param geometryOfElement Geom_element, the geometry object of the element.
     * @param dataOfElement     Meta_data, the data of the element.
     * @param ID integer, ID to construct the packman with.
     * @param speed double, speed of packman as meters/seconds.
     * @param eatRadius double, eating radius for the packman.
     */
    public Packman(Geom_element geometryOfElement, Meta_data_element dataOfElement,int ID, double speed, double eatRadius) {
        super(geometryOfElement, dataOfElement, ID);
        this.speed = speed;
        this.eatRadius = eatRadius;

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

    /**** Getters and Setters ****/

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getEatRadius() {
        return eatRadius;
    }

    public void setEatRadius(double eatRadius) {
        this.eatRadius = eatRadius;
    }

    public void updateGeom(String firstBoardLine) {
        String[] arg = firstBoardLine.split(",");
        Point3D LatLonAlt = new Point3D(arg[3]+","+arg[2]+","+"0");
        setGeom(LatLonAlt);
    }

}
