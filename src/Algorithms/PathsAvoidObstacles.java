package Algorithms;

import Coords.MyCoords;
import GIS.GIS_element;
import GIS.GIS_layer;
import GIS.GIS_layer_obj;
import Game.Game;
import Game.Map;
import Game.Obstacle;
import Game.ObstacleCorner;
import Geom.GeomRectangle;
import Geom.Point3D;
import graph.Graph;
import graph.Graph_Algo;
import graph.Node;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
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
    public static Set createObstacleCorners(Set<GIS_element> obstacles, Game game){
        if (game.getObstacleCorners() != null) {
            return game.getObstacleCorners();
        }
        GIS_layer cornersSet = new GIS_layer_obj();
        MyCoords conv = new MyCoords();
        int cornerID = 0;
        Iterator<GIS_element> obsIt = obstacles.iterator();
        while(obsIt.hasNext()){
            GIS_element obs = obsIt.next();
            GeomRectangle rectObs = (GeomRectangle)obs.getGeom();
            //creates 4 new corners:
            Point3D topLeftPos = rectObs.getLeftUp();
            Point3D topRightPos = rectObs.getRightUp();
            Point3D botLeftPos = rectObs.getLeftDown();
            Point3D botRightPos = rectObs.getRightDown();
            Point3D vectorToAddEpsilonTR = new Point3D(3, 3, 0); //and all variants.
            Point3D vectorToAddEpsilonTL = new Point3D(-3, 3, 0); //and all variants.
            Point3D vectorToAddEpsilonBR = new Point3D(3, -3, 0); //and all variants.
            Point3D vectorToAddEpsilonBL = new Point3D(-3, -3, 0); //and all variants.
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
        game.setObstacleCorners(cornersSet);
        return cornersSet;
    }

    /**
     * This method will get a Player element, GIS_Layer of targets, and GIS_Layer of obstacles.
     * The method will create a new Graph, source vertex is player object. targets are all vertexes available in the graph.
     * it will calculate all corners of obstacles and add them to the graph to make the graph connected.
     * the method will check between each pair of vertexes if there is LOS (line of sight, if they can 'see' each other with no
     * obstacles between). if there is LOS, it will calculate edge weight between these vertex and add it as an edge to the graph.
     * The method returns the Graph as and UNDIRECTED CONNECTED GRAPH. note: Dijkstra's algorithm has not run on this graph yet.
     * @param targets GIS_layer, all targets/vertex to be used in the graph. fruits+packmen inside ONE layer is SUPPORTED!
     * @param game the Game we will take Obstacles from, and initialize obstacle corner into.
     * @param map map to be used in conversions and to calculate the line-of-sight between edges.
     * @param ht height of the frame to be used to calculate the line-of-sight between edges.
     * @param wd width of the frame to be used to calculate the line-of-sight between edges.
     * @return Graph, G, an undirected-connected-graph. dijkstra's algorithms hasn't run on this graph yet.
     */
    public static Graph initGraph(GIS_layer targets, Game game, Map map,double ht,double wd){
        Graph G = new Graph();
//        String source = "Player Node(Source) First Pos"; //todo: delete this
//        G.add(new Node(source));

        //adding vertex nodes to our graph.
        Iterator<GIS_element> targetIt = targets.iterator();
        addVertex(G, targetIt);

        //adding corners of obstacles as vertexes.
        GIS_layer obsCorners = (GIS_layer)createObstacleCorners(game.getObstacles(),game);
        Iterator<GIS_element> cornersIt = obsCorners.iterator();
        addVertex(G, cornersIt);

        //add edges while considering LOS (line of sight) between all added vertexes.
        //hold all node/vertex objects in one data structure:
        ArrayList<GIS_element> allVertexes = new ArrayList<>(targets);
        allVertexes.addAll(obsCorners);
        ArrayList<GIS_element> obstaclesAL = new ArrayList<>(game.getObstacles());
        MyCoords coords = new MyCoords(); //used to calc weight on edges.
        //graph is directed/undirected, we will include both edges to make sure.
        for(int i=0; i<allVertexes.size(); i++){
            Point3D posV1 = (Point3D) allVertexes.get(i).getGeom();
            Point3D v1Pixelated = map.CoordsToPixels(posV1,ht,wd);
            for(int j=0; j<allVertexes.size(); j++){
                if(i!=j) {
                    Point3D posV2 = (Point3D) allVertexes.get(j).getGeom();
                    Point3D v2Pixelated = map.CoordsToPixels(posV2,ht,wd);
                    Line2D.Double lineRep = new Line2D.Double(v1Pixelated.x(), v1Pixelated.y(), v2Pixelated.x(), v2Pixelated.y());
                    boolean intersects = false;
                    for(GIS_element obs : obstaclesAL){
                        GeomRectangle obsGeom = (GeomRectangle)obs.getGeom();
                        Point3D topLeftObs = obsGeom.getLeftUp();
                        Point3D downRightObs = obsGeom.getRightDown();
                        Point3D tlPixelated = map.CoordsToPixels(topLeftObs,ht,wd);
                        Point3D drPixelated = map.CoordsToPixels(downRightObs,ht,wd);
                        Rectangle2D.Double rect = new Rectangle2D.Double(tlPixelated.x(), tlPixelated.y(), Math.abs(drPixelated.x()-tlPixelated.x()), Math.abs(drPixelated.y()-tlPixelated.y()));
                        if (lineRep.intersects(rect)) {
                            intersects = true;
                            break;
                        }
                    }
                    if(!intersects) {
                        String firstNode = "" + allVertexes.get(i).getData().getType() + allVertexes.get(i).getID();
                        String secondNode = "" + allVertexes.get(j).getData().getType() + allVertexes.get(j).getID();
                        Double edgeWeight = coords.distance3d(posV1, posV2);
//                        System.out.println("Added edge between " + firstNode + " to " + secondNode + " with weight: " + edgeWeight); //todo: delete this. fix function.
                        G.addEdge(firstNode, secondNode, edgeWeight);
                    }
                }
            }
        }
        return G;
    }

    private static void addVertex(Graph g, Iterator<GIS_element> targetIt) {
        while(targetIt.hasNext()){
            GIS_element elem = targetIt.next();
            String nodeName = "" + elem.getData().getType() + elem.getID(); //i.e: name for node of Fruit object with ID 6 =>  F6.
            //another example: name for node of Packmen object with ID 13 =>  P13.   name for node of ObstacleCorner with ID 13 => OC13.
            Node vertex = new Node(nodeName);
            g.add(vertex);
        }
    }



    /**
     * This method gets a Graph, source GIS_element and a given target GIS_element object.
     * it calculates and returns the shortest path to the given target while avoiding collision with obstacles.
     */
    public static ArrayList<String> pathToTargetInclObstacles(Graph g,String source, String target, boolean greedy){
        Graph_Algo.dijkstra(g, source);
        Node b = g.getNodeByName(target);
        ArrayList<String> shortestPath = b.getPath();
        if(!greedy) {
            shortestPath.add(target);
        }
        else {
            shortestPath.remove(0);
        }
        System.out.println("shortest path: "+shortestPath);//todo: delete
        return shortestPath;
    }


    /**
     * This method will get a graph G, a Point3D representing the source position to add to the graph, and a name to be used for this vertex in the graph.
     * it will also add all relevant edges for this vertex.
     * @param g the Graph we want to add the vertex to.
     * @param sourcePos the Position in Point3D of the source vertex we want to add.
     * @param nameSource the name preferred to be used for the added vertex.
     * @param game the Game we will take Obstacles from, and initialize obstacle corner into.
     * @param map map to be used in conversions and to calculate the line-of-sight between edges.
     * @param ht height of the frame to be used to calculate the line-of-sight between edges.
     * @param wd width of the frame to be used to calculate the line-of-sight between edges.
     */
    public static void addSourceToGraph(Graph g, Point3D sourcePos, String nameSource,Game game, Map map,double ht,double wd) {
        Point3D sourcePosPixelated = map.CoordsToPixels(sourcePos, ht, wd);

        //now adding edges:
        ArrayList<GIS_element> obstaclesAL = new ArrayList<>(game.getObstacles());
        MyCoords coords = new MyCoords(); //used to calc weight on edges.
        for (int i = 0; i < g.size(); i++) {
            Node edge = g.getNodeByIndex(i);
            GIS_element vertexInGraph = null;
            try{
            vertexInGraph = parseNameElementToObject(edge.get_name(), game);
            }catch (Exception e) { //could not be parse if eaten at the same time of moving towards. will ensure 100% greedy perfection.
//                if (vertexInGraph == null) {
//                    System.out.println("Could not find object with type and ID : " + edge.get_name());
                    continue;
//                }
            }
            if (vertexInGraph == null) {
//                System.out.println("Could not find object with type and ID : " + edge.get_name());
                continue;
            }
            Point3D posV2 = (Point3D) vertexInGraph.getGeom();
            Point3D v2Pixelated = map.CoordsToPixels(posV2, ht, wd);
            Line2D.Double lineRep = new Line2D.Double(sourcePosPixelated.x(), sourcePosPixelated.y(), v2Pixelated.x(), v2Pixelated.y());
            boolean intersects = false;
            for (GIS_element obs : obstaclesAL) {
                GeomRectangle obsGeom = (GeomRectangle) obs.getGeom();
                Point3D topLeftObs = obsGeom.getLeftUp();
                Point3D downRightObs = obsGeom.getRightDown();
                Point3D tlPixelated = map.CoordsToPixels(topLeftObs, ht, wd);
                Point3D drPixelated = map.CoordsToPixels(downRightObs, ht, wd);
                Rectangle2D.Double rect = new Rectangle2D.Double(tlPixelated.x(), tlPixelated.y(), Math.abs(drPixelated.x() - tlPixelated.x()), Math.abs(drPixelated.y() - tlPixelated.y()));
                if (lineRep.intersects(rect)) {
                    intersects = true;
                    break;
                }
            }
            if (!intersects) {
                Double edgeWeight = coords.distance3d(sourcePos, posV2);
                        System.out.println("Added edge between " + nameSource + " to " + edge.get_name() + " with weight: " + edgeWeight); //todo: delete this. fix function.
                g.addEdge(edge.get_name(), nameSource, edgeWeight);
            }
        }
    }

    /**
     * This method will get a vertexName (in a graph) and a Game, and will search an object with specific name in the Game given.
     * i.e - for String F6, it will search an object Fruit with ID 6 , in the Game given to the method.
     * will return NULL if no gis_element found with the given name inside the given game.
     * @param vertexName the name to search an object for.
     * @param game the game to search the object inside.
     * @return the GIS_element to return based on the string given.
     */
    private static GIS_element parseNameElementToObject(String vertexName, Game game){
        if (vertexName.contains("F")) { //fruit.
            int ID = Integer.parseInt(vertexName.substring(1));
            for(GIS_element realFruit : game.getFruits()){
                if (ID == realFruit.getID()) {
                    return realFruit;
                }
            }
        } else if (vertexName.contains("P")) { //pacman.
            int ID = Integer.parseInt(vertexName.substring(1));
            for(GIS_element realPac : game.getPacmen()){
                if (ID == realPac.getID()) {
                    return realPac;
                }
            }
        } else if (vertexName.contains("OC")) { //Obstacle Corner.
            int ID = Integer.parseInt(vertexName.substring(2));
            for(GIS_element realCorner : game.getObstacleCorners()){
                if (ID == realCorner.getID()) {
                    return realCorner;
                }
            }
        }

        return null; //could not parse name or could not find an object in the game with that name.
    }

}
