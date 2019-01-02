package GIS;

import Geom.Geom_element;
import Geom.Point3D;

/**
 * This interface represents a GIS element with geometric representation and meta data such as:
 * Orientation, color, string, timing...
 *
 * @author Liad and Timoe
 */
public interface GIS_element {
    public Geom_element getGeom();

    public Meta_data getData();

    public void translate(Point3D vec);

    public int getID();
    boolean isNecessary();
    void setNecessary(boolean necessary);
}
