package Game;

import GIS.GIS_element_obj;
import GIS.Meta_data_element;
import Geom.GeomRectangle;
import Geom.Point3D;

public class Obstacle extends GIS_element_obj {

    public Obstacle(String line) {
        super();
        String[] arg = line.split(",");
        Point3D leftDown = new Point3D(arg[3]+","+arg[2]+","+"0");
        Point3D topRight = new Point3D(arg[6]+","+arg[5]+","+"0");
        GeomRectangle rectangle = new GeomRectangle(leftDown,topRight);
        setGeom(rectangle);
        setMetaData(new Meta_data_element("Obstacle","B"));
        try {
            setID(Integer.parseInt(arg[1]));
        } catch (NumberFormatException e) {
            throw new RuntimeException("Exception creating new Player "+e.getMessage());
        }
    }
}
