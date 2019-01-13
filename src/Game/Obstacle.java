package Game;

import GIS.GIS_element_obj;
import GIS.Meta_data_element;
import Geom.GeomRectangle;
import Geom.Point3D;

/**
 * This class representes the Obstacles in our game.
 * it will extend the GIS_element_obj and its geom will be a GeomRectangle object.
 */
public class Obstacle extends GIS_element_obj {

    /**
     * The constructor for the Obstacle object, gets a String representing data about this element separated by commas ,
     * @param line String, the data about this object separated by commas.
     */
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

    /**
     * This method will get a Point3D point and checks and returns if this point is inside the obstacle.
     * @param pt Point3D, the point to check whether its inside the obstacle or not.
     * @return true iff the point3D is inside the obstacle.
     */
    public boolean isPointInside(Point3D pt){
        return ((GeomRectangle) getGeom()).isPointInside(pt);
    }
}
