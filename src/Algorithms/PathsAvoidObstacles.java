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
import java.util.HashSet;
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
    public Set createObstacleCorners(Set<GIS_element> obstacles){
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
            Point3D vectorToAddEpsilonTR = new Point3D(1, 1, 0); //and all variants.
            Point3D vectorToAddEpsilonTL = new Point3D(-1, 1, 0); //and all variants.
            Point3D vectorToAddEpsilonBR = new Point3D(1, -1, 0); //and all variants.
            Point3D vectorToAddEpsilonBL = new Point3D(-1, -1, 0); //and all variants.
            topRightPos = conv.add(topRightPos, vectorToAddEpsilonTR);
            topLeftPos = conv.add(topLeftPos, vectorToAddEpsilonTL);
            botRightPos = conv.add(botRightPos, vectorToAddEpsilonBR);
            botLeftPos = conv.add(botLeftPos, vectorToAddEpsilonBL);
            ObstacleCorner corner = new ObstacleCorner(topRightPos);
            cornersSet.add(corner);
            corner = new ObstacleCorner(topLeftPos);
            cornersSet.add(corner);
            corner = new ObstacleCorner(botLeftPos);
            cornersSet.add(corner);
            corner = new ObstacleCorner(botRightPos);
            cornersSet.add(corner);
        }
        //run through all corners made, check for each one if it's inside one of the obstacles.
        Iterator<GIS_element> cornerIt = cornersSet.iterator();
        while(cornerIt.hasNext()){
            GIS_element obj = cornerIt.next();
            Point3D cornerPt = (Point3D)obj.getGeom();
            //for this corner point, we will check if it inside any obstacle:
            Iterator<GIS_element> obsIterator = obstacles.iterator();
            while(obsIterator.hasNext()){
                Obstacle obs = (Obstacle)obsIterator.next();
                if(obs.isPointInside(cornerPt)){
                    cornerIt.remove();
                    cornersSet.remove(obj);
                    break; //we are done with this corner, no need to check with more obstacles.
                }
            }
        }
        return cornersSet;
    }


    /**
     * This method gets a source point and a given target Point3D position.
     * it calculates and returns the shortest path to the given target while avoiding collision with obstacles.
     */
    public ArrayList<GIS_element> pathToTargetInclObstacles(GIS_element_obj target){
        ArrayList<GIS_element> path = null;
        Point3D targetPos = (Point3D)target.getGeom();

        //TODO: implement this function

        return path;
    }


}
