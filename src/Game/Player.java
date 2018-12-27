package Game;

import GIS.GIS_element_obj;
import GIS.Meta_data;
import GIS.Meta_data_element;
import Geom.Geom_element;
import Geom.Point3D;

import java.awt.*;

public class Player extends GIS_element_obj {

    private double speed;
    private double eatRadius;
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
    }

    /**
     * Constructor for the GIS_element object. gets a String.
     * the string will be splitted and each argument will be sent to the right construction.
     *
     * @param String line, The all data about this element.
     *
     */

    public Player(String line){
        super();
        String[] arg = line.split(",");
        Point3D LatLonAlt = new Point3D(arg[2]+","+arg[3]+","+"0");
        setGeom(LatLonAlt);
        setMetaData(new Meta_data_element("Player","M"));
        try {
            setID(Integer.parseInt(arg[1]));
            this.speed = Double.parseDouble(arg[5]);
            this.eatRadius = Double.parseDouble(arg[6]);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Exception creating new Player "+e.getMessage());
        }
    }


}
