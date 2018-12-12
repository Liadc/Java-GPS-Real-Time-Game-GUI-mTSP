package Game;

import GIS.GIS_element;
import GIS.GIS_element_obj;
import GIS.Meta_data_element;
import Geom.Geom_element;

public class Fruit extends GIS_element_obj implements GIS_element {
    /*
    has the following fields from GIS element:
    Geom_element, Meta_data (name, color, type, utcTime).
     */
    private double weight; //fruit weight.
    private boolean isEaten;

    /**
     * Constructor for the GIS_element object. gets a Geom_element and Meta_data.
     *
     * @param geometryOfElement Geom_element, the geometry object of the element.
     * @param dataOfElement     Meta_data, the data of the element.
     */
    public Fruit(Geom_element geometryOfElement, Meta_data_element dataOfElement) {
        super(geometryOfElement, dataOfElement);
        this.weight = 1;
        this.isEaten = false;
    }

    public Fruit(Geom_element geometryOfElement, Meta_data_element dataOfElement, double weight) {
        super(geometryOfElement, dataOfElement);
        this.weight = weight;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public boolean isEaten() {
        return isEaten;
    }

    public void setEaten(boolean eaten) {
        isEaten = eaten;
    }
}