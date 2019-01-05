package Game;

import GIS.GIS_element_obj;
import GIS.Meta_data_element;
import Geom.Geom_element;
import Geom.Point3D;

/**
 * This class represents a Fruit object inside our game (target) it extends the GIS_element_obj, and will also hold these extra fields:
 * Weight of this fruit, a boolean isEaten to indicate whether the fruit has been eaten by a pacman, an ID integer, and a long timeToEat which will indicate
 * how long it took to eat this fruit in the last Algorithm Solution run.
 */
public class Fruit extends GIS_element_obj{
    /*
    has the following fields from GIS element:
    Geom_element, Meta_data (name, color, type, utcTime).
     */
    private double weight; //fruit weight.
    private int ID;
    private long timeToEat = 0;

    /**
     * Constructor for the Fruit object. gets a Geom_element, Meta_data_element and ID.
     * default value for weight will be 1. isEaten is set to False.
     * @param geometryOfElement Geom_element, the geometry object of the element.
     * @param dataOfElement     Meta_data, the data of the element.
     * @param ID Integer, ID for this fruit.
     */
    public Fruit(Geom_element geometryOfElement, Meta_data_element dataOfElement,int ID) {
        super(geometryOfElement, dataOfElement, ID);
        this.weight = 1;
        this.ID = ID;
    }
    /**
     * Constructor for the Fruit object. gets a Geom_element, Meta_data_element, ID and weight.
     * default value for isEaten is set to False.
     * @param geometryOfElement Geom_element, the geometry object of the element.
     * @param dataOfElement     Meta_data, the data of the element.
     * @param ID Integer, ID for this fruit.
     * @param weight double, the weight of this fruit.
     */
    public Fruit(Geom_element geometryOfElement, Meta_data_element dataOfElement, int ID, double weight) {
        super(geometryOfElement, dataOfElement,ID);
        this.weight = weight;
        this.ID = ID;

    }
    public Fruit(String line) {
        super();
        String[] arr = line.split(",");
        String p = arr[3] + "," + arr[2] + "," + arr[4];
        Point3D point = new Point3D(p);
        setGeom(point);

        try {
            setID(Integer.parseInt(arr[1]));
            this.weight = Double.parseDouble(arr[5]);
            this.setMetaData(new Meta_data_element("Fruit", "F"));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Exception creating new Fruit, " + e.getMessage());
        }
    }
    /*** Getters and Setters ***/
    public double getWeight() {
        return this.weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setTimeToEat(long timeToEat) {
        this.timeToEat = timeToEat;
    }

    public long getTimeToEat() {
        return timeToEat;
    }

    /**
     * ToString for Fruit, to represent just the ID.
     * @return String, the ID for this fruit.
     */
    @Override
    public String toString() {
        return "Fruit{"+this.ID+"}";
    }
    public void updateGeom(String firstBoardLine) {
        String[] arg = firstBoardLine.split(",");
        Point3D LatLonAlt = new Point3D(arg[3]+","+arg[2]+","+"0");
        setGeom(LatLonAlt);
    }



}
