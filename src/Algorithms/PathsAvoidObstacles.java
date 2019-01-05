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
import graph.Graph;
import graph.Node;

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
    public Set createObstacleCorners(Set<GIS_element> obstacles){
        GIS_layer cornersSet = new GIS_layer_obj();
        MyCoords conv = new MyCoords();
        int cornerID = 0;
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
            ObstacleCorner corner = new ObstacleCorner(topRightPos,cornerID++);
            cornersSet.add(corner);
            corner = new ObstacleCorner(topLeftPos,cornerID++);
            cornersSet.add(corner);
            corner = new ObstacleCorner(botLeftPos,cornerID++);
            cornersSet.add(corner);
            corner = new ObstacleCorner(botRightPos,cornerID++);
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
     * This method will get a Player element, GIS_Layer of targets, and GIS_Layer of obstacles.
     * The method will create a new Graph, source vertex is player object. targets are all vertexes available in the graph.
     * it will calculate all corners of obstacles and add them to the graph to make the graph connected.
     * the method will check between each pair of vertexes if there is LOS (line of sight, if they can 'see' each other with no
     * obstacles between). if there is LOS, it will calculate edge weight between these vertex and add it as an edge to the graph.
     * The method returns the Graph as and UNDIRECTED CONNECTED GRAPH. note: Dijkstra's algorithm has not run on this graph yet.
     * @param player GIS_element, the player object, will be used as the Source vertex in the graph.
     * @param targets GIS_layer, all targets/vertex to be used in the graph. fruits+packmen inside ONE layer is SUPPORTED!
     * @param obstacles GIS_layer, all obstacle objects in one layer.
     * @return Graph, G, an undirected-connected-graph. dijkstra's algorithms hasn't run on this graph yet.
     */
    public Graph initGraph(GIS_element player, GIS_layer targets, GIS_layer obstacles){
        Graph G = new Graph();
        String source = "Player Node(Source)";
        G.add(new Node(source));

        //adding vertex nodes to our graph.
        Iterator<GIS_element> targetIt = targets.iterator();
        addVertex(G, targetIt);

        //adding corners of obstacles as vertexes.
        GIS_layer obsCorners = (GIS_layer)createObstacleCorners(obstacles);
        Iterator<GIS_element> cornersIt = obsCorners.iterator();
        addVertex(G, cornersIt);

        //add edges while considering LOS (line of sight) between all added vertexes.
        //hold all node/vertex objects in one data structure:
        ArrayList<GIS_element> allVertexes = new ArrayList<>(targets);
        allVertexes.addAll(obsCorners);
        MyCoords coords = new MyCoords(); //used to calc weight on edges.
        //graph is directed/undirected, we will include both edges to make sure.
        for(int i=0; i<allVertexes.size(); i++){
            for(int j=0; j<allVertexes.size(); j++){
                if(i!=j) {
                    Point3D posV1 = (Point3D) allVertexes.get(i).getGeom();
                    Point3D posV2 = (Point3D) allVertexes.get(j).getGeom();
                    //check LOS between 2 Point3D positions.
                    Iterator<GIS_element> obsIt = obstacles.iterator();
                    while(obsIt.hasNext()){
                        GIS_element obs = obsIt.next();
                        GeomRectangle obsGeom = (GeomRectangle)obs.getGeom();
                        if (!obsGeom.segmentIntersects(posV1, posV2)) {
                            String firstNode = ""+allVertexes.get(i).getData().getType()+allVertexes.get(i).getID();
                            String secondNode = ""+allVertexes.get(j).getData().getType()+allVertexes.get(j).getID();
                            Double edgeWeight = coords.distance3d(posV1, posV2);
                            G.addEdge(firstNode,secondNode,edgeWeight);
                        }
                    }
                }
            }
        }
        return G;
    }

    private void addVertex(Graph g, Iterator<GIS_element> targetIt) {
        while(targetIt.hasNext()){
            GIS_element elem = targetIt.next();
            String nodeName = "" + elem.getData().getType() + elem.getID(); //i.e: name for node of Fruit object with ID 6 =>  F6.
            //another example: name for node of Packmen object with ID 13 =>  P13.   name for node of ObstacleCorner with ID 13 => OC13.
            Node vertex = new Node(nodeName);
            g.add(vertex);
        }
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
