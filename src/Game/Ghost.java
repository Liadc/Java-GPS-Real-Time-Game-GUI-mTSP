package Game;

import GIS.GIS_element_obj;
import GIS.Meta_data;
import GIS.Meta_data_element;
import Geom.Geom_element;
import Geom.Point3D;

public class Ghost extends GIS_element_obj {

    private double speed;
    private double eatRadius;

    /**
     * Constructor for the Ghost object. gets a Geom_element, Meta_data and ID.
     *
     * @param geometryOfElement Geom_element, the geometry object of the element.
     * @param dataOfElement     Meta_data, the data of the element.
     * @param ID
     */
    public Ghost(Geom_element geometryOfElement, Meta_data dataOfElement, int ID) {
        super(geometryOfElement, dataOfElement, ID);
    }


    /**
     * Constructor for the Ghost object. gets a String.
     * the string will be splitted and each argument will be sent to the right construction.
     *
     * @param String line, The all data about this element.
     *
     */
    public Ghost(String line){
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
