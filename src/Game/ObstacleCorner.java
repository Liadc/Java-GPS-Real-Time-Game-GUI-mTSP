package Game;

import GIS.GIS_element_obj;
import GIS.Meta_data_element;
import Geom.GeomRectangle;
import Geom.Point3D;

/**
 * This class represents an Obstacle Corner in our game. will be used for Dijkstra's algorithm in the graph's
 * in order to avoid obstacles while moving.
 */
public class ObstacleCorner extends GIS_element_obj {

    /**
     * Constructor for the ObstacleCorner, will get a positioin Point3D, and an ID.
     * @param pos Point3D, the position to place the corner on.
     * @param ID the ID for this corner.
     */
    public ObstacleCorner(Point3D pos,int ID) {
        super();
        setMetaData(new Meta_data_element("Obstacle Corner","OC"));
        setGeom(pos);
        setID(ID);
    }
}
