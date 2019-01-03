package Algorithms;

import Coords.MyCoords;
import GIS.GIS_element;
import GIS.GIS_element_obj;
import GIS.GIS_layer;
import GIS.GIS_layer_obj;
import Game.Obstacle;
import Game.ObstacleCorner;
import Geom.GeomRectangle;
import Geom.Point3D;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

/**
 *
 */
public class PathsAvoidObstacles {

    /**
     * This method will get an arraylist of obstacles and create new gis_element_obj (corner position)
     * of the obstacles, as long as the position is outside of any other obstacles. (no overlapping).
     * it returns the set of these objects.
     */
    public Set createObstacleCorners(ArrayList<GIS_element> obstacles){
        GIS_layer cornersSet = new GIS_layer_obj();
        MyCoords conv = new MyCoords();
        Iterator<GIS_element> obsIt = obstacles.iterator();
        while(obsIt.hasNext()){
            GIS_element obs = obsIt.next();
            GeomRectangle rectObs = (GeomRectangle)obs.getGeom();
            //creates 4 new corners:
            ObstacleCorner topLeft, topRight, botLeft,botRight;
            Point3D topLeftPos = rectObs.getLeftUp();
            Point3D topRightPos = rectObs.getRightUp();
            Point3D botLeftPos = rectObs.getLeftDown();
            Point3D botRightPos = rectObs.getRightDown();
            Point3D vectorToAddEpsilon = new Point3D(1, 1, 0);
            topRightPos = conv.add(topRightPos, vectorToAddEpsilon);
            topLeftPos = conv.add(topLeftPos, vectorToAddEpsilon);
            botLeftPos = conv.add(botLeftPos, vectorToAddEpsilon);
            botRightPos = conv.add(botRightPos, vectorToAddEpsilon);
            ObstacleCorner corner = new ObstacleCorner(topRightPos);
            cornersSet.add(corner);
            corner = new ObstacleCorner(topLeftPos);
            cornersSet.add(corner);
            corner = new ObstacleCorner(botLeftPos);
            cornersSet.add(corner);
            corner = new ObstacleCorner(botRightPos);
            cornersSet.add(corner);
        }

        //run through all corners made, check for each one if it inside one of the obstacles.

        return cornersSet;
    }


    /**
     * This method gets a source point and a given target Point3D position.
     * it calculates the shortest path to the given target while avoiding collision with obstacles.
     */
    public ArrayList<GIS_element> pathToTargetInclObstacles(GIS_element_obj target){
        ArrayList<GIS_element> path = null;
        Point3D targetPos = (Point3D)target.getGeom();


        return path;
    }


}
