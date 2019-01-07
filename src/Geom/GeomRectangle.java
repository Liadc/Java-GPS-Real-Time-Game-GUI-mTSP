package Geom;

import Coords.MyCoords;
import Game.Map;

import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;

/**
 * This class represents a 2D rectangle in real-life dimensions.
 * It will use 4 points just for ease of use for the programmer, although we can represent a rectangle with 2 points
 * (or 1 point with width and height, etc..)
 */
public class GeomRectangle implements Geom_element{
    private Point3D leftDown,leftUp,rightDown,rightUp;
    private MyCoords coords = new MyCoords();

    /**
     * This constructor will create a rectangle object using only 2 points (the upperRight and bottomLeft points)
     * @param leftDown the bottomLeft point of the rectangle.
     * @param rightUp the upperRight point of the rectangle.
     */

    public GeomRectangle(Point3D leftDown , Point3D rightUp){
        this.leftDown = leftDown;
        this.rightUp = rightUp;
        this.rightDown = new Point3D(rightUp.x(), leftDown.y(), 0);
        this.leftUp = new Point3D(leftDown.x(), rightUp.y(),0);
    }

    /**
     * This method will return a double represents the width of the rectangle.
     * @return width of the rectangle in Double.
     */
    public double getWidth(){
//        leftUp.transformXY();
//        rightUp.transformXY();
//        double dist = coords.distance3d(leftUp, rightUp);
//        leftUp.transformXY();
//        rightUp.transformXY();
//        System.out.println("Width: "+dist); //todo: delete this

        return Math.abs(rightUp.x()-leftUp.x());
    }

    /**
     * This method will return a double represents the height of the rectangle.
     * @return height of the rectangle in Double.
     */
    public double getHeight(){

//        double dist = coords.distance3d(leftUp, leftDown);
        System.out.println(leftUp.y()-rightDown.y());
        return  Math.abs(leftUp.y()-rightDown.y());
    }

    //TODO: create tests for this.
    public boolean isPointInside(Point3D ptPos){
        return (getRightUp().x() >= ptPos.x() && getRightUp().y() >= ptPos.y() &&
                getLeftDown().x() <= ptPos.x() && getLeftDown().y() <= ptPos.y());
    }


    //TODO: create test for this function: gets a line (2 points) and returns boolean if it intersects with the rectangle.
    public boolean segmentIntersects(Point3D p1, Point3D p2){
        Rectangle2D.Double rct2D = new Rectangle2D.Double( getLeftUp().y() , getLeftUp().x() , getWidth() , getHeight() );
        Line2D.Double line2d = new Line2D.Double(p1.y(), p1.x(), p2.y(), p2.x());
        return rct2D.intersectsLine(line2d);
    }



    //no needed to implement.
    @Override
    public double distance3D(Point3D p) {
        return 0;
    }

    //no needed to implement.
    @Override
    public double distance2D(Point3D p) {
        return 0;
    }

    public Point3D getLeftDown() {
        return leftDown;
    }

    public void setLeftDown(Point3D leftDown) {
        this.leftDown = leftDown;
    }

    public Point3D getLeftUp() {
        return leftUp;
    }

    public void setLeftUp(Point3D leftUp) {
        this.leftUp = leftUp;
    }

    public Point3D getRightDown() {
        return rightDown;
    }

    public void setRightDown(Point3D rightDown) {
        this.rightDown = rightDown;
    }

    public Point3D getRightUp() {
        return rightUp;
    }

    public void setRightUp(Point3D rightUp) {
        this.rightUp = rightUp;
    }
}
