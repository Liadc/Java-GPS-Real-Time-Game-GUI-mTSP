package Geom;

import Coords.MyCoords;
import Game.Fruit;
import Game.Packman;

import java.util.ArrayList;
import java.util.Iterator;

public class Path {

    private Packman pacmanInPath;
    private ArrayList<Fruit> fruitsInPath;
    private Point3D pacmanStartPosition;
    //add gps data as needed

    //constructor:
    public Path(Packman pacForPath, ArrayList<Fruit> fruitsInPath){
        this.pacmanInPath = pacForPath;
        this.fruitsInPath = fruitsInPath;
        this.pacmanStartPosition = (Point3D)pacmanInPath.getGeom();
    }


    //Gets the distance of the path traveled from pacman position to fruit index in path.
    //to get the total distance traveled in the path, call this function with fruit index as Arraylist.size()-1.
    public double getDistance(int fruitIndex) {
        if(fruitIndex>=this.fruitsInPath.size())
            throw new RuntimeException("You tried to calculate distance in path for more fruits than in the path");
        else if(fruitIndex<0){
            throw new RuntimeException("You entered invalid fruit index in path.");
        }
        MyCoords coordsConv = new MyCoords(); //we use MyCoords object to calculate distance between two Point3D points.
        double pathDistance = 0;
        if(fruitsInPath.size()==0){
            return 0;
        }
        else {
            pathDistance += coordsConv.distance3d(pacmanStartPosition, getFruitLoc(0)); //distance from pacman first Pos to first Fruit.
        }
        // Loop through our fruits locations till the last one, including the path from the (last-1) to (last) .
        for (int fruitLocIndex = 0; fruitLocIndex < fruitIndex; fruitLocIndex++) {
            Point3D fromFruit = getFruitLoc(fruitLocIndex);  // Get fruit location we're travelling from
            Point3D destFruitLoc = fromFruit;  // fruit we're travelling to
            if (fruitLocIndex + 1 <= fruitIndex) {
                destFruitLoc = getFruitLoc(fruitLocIndex + 1);
            }
            // add the distance between the two fruits
            pathDistance += coordsConv.distance3d(fromFruit, destFruitLoc);
        }
        return pathDistance;
    }

   public double getTravelTimeForPacmanWholePath(){
        return getDistance(this.fruitsInPath.size()-1)/pacmanInPath.getSpeed();
   }

    /**
     * Gets time (in MS) and returns how many fruits the pacman ate in the path.
     * @param timeInSeconds time in seconds.
     */
   public int getHowManyEatenAfterXtime(double timeInSeconds){
       int counterFruits = this.fruitsInPath.size();
       int fruitEaten = 0;
       while(counterFruits>0 && fruitEaten<this.fruitsInPath.size()){
           if(getDistance(fruitEaten++)/this.pacmanInPath.getSpeed() > timeInSeconds){
               return this.fruitsInPath.size()-counterFruits;
           }
           counterFruits--;
       }
       return fruitEaten;
   }

   public Point3D getPacPositionAfterXtime(double timeInSeconds){
       int alreadyEatenDuringThisTime = getHowManyEatenAfterXtime(timeInSeconds);
       MyCoords coordsConv = new MyCoords(); //we use MyCoords object to calculate vector between two Point3D points.
       Point3D fromFruitLoc;
       Point3D toFruitLoc;
       double timeFromPacToEatenFruit;
       if(alreadyEatenDuringThisTime==0) {
           fromFruitLoc = pacmanStartPosition;
           timeFromPacToEatenFruit = 0;
           toFruitLoc = getFruitLoc(0);
       }
       else {
           fromFruitLoc = getFruitLoc(alreadyEatenDuringThisTime - 1);
           timeFromPacToEatenFruit = getDistance(alreadyEatenDuringThisTime - 1) / this.pacmanInPath.getSpeed();
           if(alreadyEatenDuringThisTime==this.fruitsInPath.size()) //ate all
               return getFruitLoc(alreadyEatenDuringThisTime-1);
           else
               toFruitLoc = getFruitLoc(alreadyEatenDuringThisTime);
       }

       Point3D vectorBetween = coordsConv.vector3D(fromFruitLoc,toFruitLoc);
       double timeLeftToMove = timeInSeconds - timeFromPacToEatenFruit;
       double timeForNextRoute = coordsConv.distance3d(fromFruitLoc, toFruitLoc) / this.pacmanInPath.getSpeed();
       double percentagePathToMove = timeLeftToMove/timeForNextRoute;
       Point3D newPosMov = new Point3D(percentagePathToMove * vectorBetween.x(), percentagePathToMove * vectorBetween.y(), percentagePathToMove * vectorBetween.z());
       Point3D newPosition = coordsConv.add(fromFruitLoc,newPosMov);
       return newPosition;
   }

   public double getTimeBetweenTwoFruits(int fruitIndex1, int fruitIndex2){
       MyCoords coordsConv = new MyCoords(); //we use MyCoords object to calculate distance between two Point3D points.
       return coordsConv.distance3d(getFruitLoc(fruitIndex1), getFruitLoc(fruitIndex2))/this.pacmanInPath.getSpeed();
   }


    public Point3D getFruitLoc(int pathPosition) { //returns fruit GPS location in Point3D object.
        return (Point3D)fruitsInPath.get(pathPosition).getGeom();
    }

    public void addFruitToPath(Fruit fruitToAdd){
        this.fruitsInPath.add(fruitToAdd);
    }

    public int getPacmanIDInPath() {
        return this.pacmanInPath.getID();
    }

    public Packman getPacmanInPath() {
        return pacmanInPath;
    }

    @Override
    public String toString() {
        return "Path{" +
                "pacman=" + pacmanInPath.getID() +
                ", fruitsInPath=" + fruitsInPath +
                '}';
    }
}
