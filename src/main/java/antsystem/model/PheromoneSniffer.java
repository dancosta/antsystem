/*
 * PheromoneSniffer.java
 *
 * Created on 23 de Janeiro de 2007, 13:38
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package antsystem.model;

import java.awt.Point;
import java.awt.geom.Point2D;

/**
 *
 * @author danilo
 */
public class PheromoneSniffer {
    
    public static final int FRONT = 0;
    public static final int RIGHT_45 = 1;
    public static final int RIGHT_90 = 2;
    public static final int RIGHT_135 = 3;
    public static final int BACK = 4;
    public static final int LEFT_45 = -1;
    public static final int LEFT_90 = -2;
    public static final int LEFT_135 = -3;
    
    private PheromoneMatrix pheromoneMatrix;
    private int distance;
    /** Creates a new instance of PheromoneSniffer */
    public PheromoneSniffer() {
        this(1, null);
    }
    
    public PheromoneSniffer(PheromoneMatrix pheromoneMatrix){
        this(1,pheromoneMatrix);
    }
    
    public PheromoneSniffer(int distance,PheromoneMatrix pheromoneMatrix){
        if(distance<=0)
            throw new IllegalArgumentException("distance must be greater then 0");
        setDistance(distance);
        this.pheromoneMatrix = pheromoneMatrix;
    }
    
    public int getDistance() {
        return distance;
    }
    
    public void setDistance(int distance) {
        this.distance = distance;
    }
    
    public int sniff(Point2D from, Direction antDirection, int sniffTo){
        int res=0;
        
        switch (antDirection.getDirection()){
            case Direction.NORTH :
                switch (sniffTo){
                    case FRONT:
                        res = sniffNorth(from);
                        break;
                    case RIGHT_45:
                        res = sniffNorthEast(from);
                        break;
                    case RIGHT_90:
                        res = sniffEast(from);
                        break;
                    case RIGHT_135:
                        res = sniffSouthEast(from);
                        break;
                    case BACK:
                        res = sniffSouth(from);
                        break;
                    case LEFT_45:
                        res = sniffNorthWest(from);
                        break;
                    case LEFT_90:
                        res = sniffWest(from);
                        break;
                    case LEFT_135:
                        res = sniffSouthWest(from);
                        break;
                }
                
                break;
            case Direction.NORTHEAST :
                switch (sniffTo){
                    case FRONT:
                        res = sniffNorthEast(from);
                        break;
                    case RIGHT_45:
                        res = sniffEast(from);
                        break;
                    case RIGHT_90:
                        res = sniffSouthEast(from);
                        break;
                    case RIGHT_135:
                        res = sniffSouth(from);
                        break;
                    case BACK:
                        res = sniffSouthWest(from);
                        break;
                    case LEFT_45:
                        res = sniffNorth(from);
                        break;
                    case LEFT_90:
                        res = sniffNorthWest(from);
                        break;
                    case LEFT_135:
                        res = sniffWest(from);
                        break;
                        
                }
                break;
            case Direction.EAST :
                switch (sniffTo){
                    case FRONT:
                        res = sniffEast(from);
                        break;
                    case RIGHT_45:
                        res = sniffSouthEast(from);
                        break;
                    case RIGHT_90:
                        res = sniffSouth(from);
                        break;
                    case RIGHT_135:
                        res = sniffSouthWest(from);
                        break;
                    case BACK:
                        res = sniffWest(from);
                        break;
                    case LEFT_45:
                        res = sniffNorthEast(from);
                        break;
                    case LEFT_90:
                        res = sniffNorth(from);
                        break;
                    case LEFT_135:
                        res = sniffNorthWest(from);
                        break;
                }
                break;
            case Direction.SOUTHEAST :
                switch (sniffTo){
                    case FRONT:
                        res = sniffSouthEast(from);
                        break;
                    case RIGHT_45:
                        res = sniffSouth(from);
                        break;
                    case RIGHT_90:
                        res = sniffSouthWest(from);
                        break;
                    case RIGHT_135:
                        res = sniffWest(from);
                        break;
                    case BACK:
                        res = sniffNorthWest(from);
                        break;
                    case LEFT_45:
                        res = sniffEast(from);
                        break;
                    case LEFT_90:
                        res = sniffNorthEast(from);
                        break;
                    case LEFT_135:
                        res = sniffNorth(from);
                        break;
                }
                break;
            case Direction.SOUTH :
                switch (sniffTo){
                    case FRONT:
                        res = sniffSouth(from);
                        break;
                    case RIGHT_45:
                        res = sniffSouthWest(from);
                        break;
                    case RIGHT_90:
                        res = sniffWest(from);
                        break;
                    case RIGHT_135:
                        res = sniffNorthWest(from);
                        break;
                    case BACK:
                        res = sniffNorth(from);
                        break;
                    case LEFT_45:
                        res = sniffSouthEast(from);
                        break;
                    case LEFT_90:
                        res = sniffEast(from);
                        break;
                    case LEFT_135:
                        res = sniffNorthEast(from);
                        break;
                }
                break;
            case Direction.SOUTHWEST :
                switch (sniffTo){
                    case FRONT:
                        res = sniffSouthWest(from);
                        break;
                    case RIGHT_45:
                        res = sniffWest(from);
                        break;
                    case RIGHT_90:
                        res = sniffNorthWest(from);
                        break;
                    case RIGHT_135:
                        res = sniffNorth(from);
                        break;
                    case BACK:
                        res = sniffNorthEast(from);
                        break;
                    case LEFT_45:
                        res = sniffSouth(from);
                        break;
                    case LEFT_90:
                        res = sniffSouthEast(from);
                        break;
                    case LEFT_135:
                        res = sniffEast(from);
                        break;
                }
                break;
            case Direction.WEST :
                switch (sniffTo){
                    case FRONT:
                        res = sniffWest(from);
                        break;
                    case RIGHT_45:
                        res = sniffNorthWest(from);
                        break;
                    case RIGHT_90:
                        res = sniffNorth(from);
                        break;
                    case RIGHT_135:
                        res = sniffNorthEast(from);
                        break;
                    case BACK:
                        res = sniffEast(from);
                        break;
                    case LEFT_45:
                        res = sniffSouthWest(from);
                        break;
                    case LEFT_90:
                        res = sniffSouth(from);
                        break;
                    case LEFT_135:
                        res = sniffSouthEast(from);
                        break;
                }
                break;
            case Direction.NORTHWEST :
                switch (sniffTo){
                    case FRONT:
                        res = sniffNorthWest(from);
                        break;
                    case RIGHT_45:
                        res = sniffNorth(from);
                        break;
                    case RIGHT_90:
                        res = sniffNorthEast(from);
                        break;
                    case RIGHT_135:
                        res = sniffEast(from);
                        break;
                    case BACK:
                        res = sniffSouthEast(from);
                        break;
                    case LEFT_45:
                        res = sniffWest(from);
                        break;
                    case LEFT_90:
                        res = sniffSouthWest(from);
                        break;
                    case LEFT_135:
                        res = sniffSouth(from);
                        break;
                }
                break;
        }
        return res;
    }
    
//    public int sniff(Point2D from, Direction direction){
//        int res=0;
//        switch (direction.getDirection()){
//            case Direction.NORTH :
//                res = sniffNorth(from);
//                break;
//            case Direction.NORTHEAST :
//                res = sniffNorthEast(from);
//                break;
//            case Direction.EAST :
//                res = sniffEast(from);
//                break;
//            case Direction.SOUTHEAST :
//                res = sniffSouthEast(from);
//                break;
//            case Direction.SOUTH :
//                res = sniffSouth(from);
//                break;
//            case Direction.SOUTHWEST :
//                res = sniffSouthWest(from);
//                break;
//            case Direction.WEST :
//                res = sniffWest(from);
//                break;
//            case Direction.NORTHWEST :
//                res = sniffNorthWest(from);
//                break;
//        }
//        return res;
//    }
    
    private int sniffNorth(Point2D from) {
        int res = 0;
        for(int i=1; i <= getDistance(); i++){
            res=+pheromoneMatrix.getPheromoneLevel(new Point((int)from.getX(),
                    (int)from.getY()-i));
        }
        return res;
    }
    
    private int sniffNorthEast(Point2D from) {
        int res = 0;
        for(int i=1; i <= getDistance(); i++){
            res=+pheromoneMatrix.getPheromoneLevel(new Point((int)from.getX()+i,
                    (int)from.getY()-i));
        }
        return res;
    }
    
    private int sniffEast(Point2D from) {
        int res = 0;
        for(int i=1; i <= getDistance(); i++){
            res=+pheromoneMatrix.getPheromoneLevel(new Point((int)from.getX()+i,
                    (int)from.getY()));
        }
        return res;
    }
    
    private int sniffSouthEast(Point2D from) {
        int res = 0;
        for(int i=1; i <= getDistance(); i++){
            res=+pheromoneMatrix.getPheromoneLevel(new Point((int)from.getX()+i,
                    (int)from.getY()+i));
        }
        return res;
    }
    
    private int sniffSouth(Point2D from) {
        int res = 0;
        for(int i=1; i <= getDistance(); i++){
            res=+pheromoneMatrix.getPheromoneLevel(new Point((int)from.getX(),
                    (int)from.getY()+i));
        }
        return res;
    }
    
    private int sniffSouthWest(Point2D from) {
        int res = 0;
        for(int i=1; i <= getDistance(); i++){
            res=+pheromoneMatrix.getPheromoneLevel(new Point((int)from.getX()-i,
                    (int)from.getY()+i));
        }
        return res;
    }
    
    private int sniffWest(Point2D from) {
        int res = 0;
        for(int i=1; i <= getDistance(); i++){
            res=+pheromoneMatrix.getPheromoneLevel(new Point((int)from.getX()-i,
                    (int)from.getY()));
        }
        return res;
    }
    
    private int sniffNorthWest(Point2D from) {
        int res = 0;
        for(int i=1; i <= getDistance(); i++){
            res=+pheromoneMatrix.getPheromoneLevel(new Point((int)from.getX()-i,
                    (int)from.getY()-i));
        }
        return res;
    }
}
