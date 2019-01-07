package Game;

import GIS.GIS_element_obj;
import GIS.Meta_data_element;
import Geom.GeomRectangle;
import Geom.Point3D;

public class ObstacleCorner extends GIS_element_obj {

    public ObstacleCorner(Point3D pos,int ID) {
        super();
        setMetaData(new Meta_data_element("Obstacle Corner","OC"));
        setGeom(pos);
        setID(ID);
    }
}
