/*
 * Direction.java
 *
 * Created on 25 de Setembro de 2005, 20:00
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package antsystem.model;

import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

/**
 *
 * @author Danilo  Costa
 */
public class Direction {
    public static final int NORTH = 1;
    public static final int NORTHEAST = 2;
    public static final int EAST = 3;
    public static final int SOUTHEAST = 4;
    public static final int SOUTH = 5;
    public static final int SOUTHWEST = 6;
    public static final int WEST = 7;
    public static final int NORTHWEST = 8;
    
    public static final int NO_TURN = 0;
    public static final int CLOCKWISE_45 = 1;
    public static final int CLOCKWISE_90 = 2;
    public static final int CLOCKWISE_135 = 3;
    public static final int CLOCKWISE_180 = 4;
    public static final int COUNTERCLOCKWISE_45 = -1;
    public static final int COUNTERCLOCKWISE_90 = -2;
    public static final int COUNTERCLOCKWISE_135 = -3;
    
    private int direction;
    
    public Direction(){}
    public Direction(int dir){
        setDirection(dir);
    }
    public int getDirection(){
        return direction;
    }
    
    public void setDirection(int direction){
        this.direction = direction;
    }
    
    
    public AffineTransform turn(int levelToTurn, Point2D turnBase){
        AffineTransform af = null;
        turn(levelToTurn);
        switch(levelToTurn){
            case CLOCKWISE_45:
                af = AffineTransform.getRotateInstance(Math.toRadians(45),
                        turnBase.getX(), turnBase.getY()); break;
            case CLOCKWISE_90:
                af = AffineTransform.getRotateInstance(Math.toRadians(90),
                        turnBase.getX(), turnBase.getY()); break;
            case CLOCKWISE_135:
                af = AffineTransform.getRotateInstance(Math.toRadians(135),
                        turnBase.getX(), turnBase.getY()); break;
            case COUNTERCLOCKWISE_45:
                af = AffineTransform.getRotateInstance(Math.toRadians(-45),
                        turnBase.getX(), turnBase.getY()); break;
            case COUNTERCLOCKWISE_90:
                af = AffineTransform.getRotateInstance(Math.toRadians(-90),
                        turnBase.getX(), turnBase.getY()); break;
            case COUNTERCLOCKWISE_135:
                af = AffineTransform.getRotateInstance(Math.toRadians(-135),
                        turnBase.getX(), turnBase.getY()); break;
            case CLOCKWISE_180:
                af = AffineTransform.getRotateInstance(Math.toRadians(180),
                        turnBase.getX(), turnBase.getY()); break;
        }
        return af;
    }
    
    
    public void turn(int levelToTurn) {
        
        switch (direction){
            case NORTH: switch(levelToTurn){
                case CLOCKWISE_45: setDirection(NORTHEAST); break;
                case CLOCKWISE_90: setDirection(EAST); break;
                case CLOCKWISE_135: setDirection(SOUTHEAST); break;
                case COUNTERCLOCKWISE_45: setDirection(NORTHWEST); break;
                case COUNTERCLOCKWISE_90: setDirection(WEST);break;
                case COUNTERCLOCKWISE_135:setDirection(SOUTHWEST); break;
                case CLOCKWISE_180: setDirection(SOUTH);break;
            } ;
            break;
            case NORTHEAST: switch(levelToTurn){
                case CLOCKWISE_45: setDirection(EAST); break;
                case CLOCKWISE_90: setDirection(SOUTHEAST); break;
                case CLOCKWISE_135: setDirection(SOUTH); break;
                case COUNTERCLOCKWISE_45: setDirection(NORTH); break;
                case COUNTERCLOCKWISE_90: setDirection(NORTHWEST);break;
                case COUNTERCLOCKWISE_135:setDirection(WEST); break;
                case CLOCKWISE_180: setDirection(SOUTHWEST);break;
            } ;
            break;
            case EAST:switch(levelToTurn){
                case CLOCKWISE_45: setDirection(SOUTHEAST); break;
                case CLOCKWISE_90: setDirection(SOUTH); break;
                case CLOCKWISE_135: setDirection(SOUTHWEST); break;
                case COUNTERCLOCKWISE_45: setDirection(NORTHEAST); break;
                case COUNTERCLOCKWISE_90: setDirection(NORTH);break;
                case COUNTERCLOCKWISE_135:setDirection(NORTHWEST); break;
                case CLOCKWISE_180: setDirection(WEST);break;
            } ;
            break;
            case SOUTHEAST:switch(levelToTurn){
                case CLOCKWISE_45: setDirection(SOUTH); break;
                case CLOCKWISE_90: setDirection(SOUTHWEST); break;
                case CLOCKWISE_135: setDirection(WEST); break;
                case COUNTERCLOCKWISE_45: setDirection(EAST); break;
                case COUNTERCLOCKWISE_90: setDirection(NORTHEAST);break;
                case COUNTERCLOCKWISE_135:setDirection(NORTH); break;
                case CLOCKWISE_180: setDirection(NORTHWEST);break;
            } ;
            break;
            case SOUTH:switch(levelToTurn){
                case CLOCKWISE_45: setDirection(SOUTHWEST); break;
                case CLOCKWISE_90: setDirection(WEST); break;
                case CLOCKWISE_135: setDirection(NORTHWEST); break;
                case COUNTERCLOCKWISE_45: setDirection(SOUTHEAST); break;
                case COUNTERCLOCKWISE_90: setDirection(EAST);break;
                case COUNTERCLOCKWISE_135:setDirection(NORTHEAST); break;
                case CLOCKWISE_180: setDirection(NORTH);break;
            } ;
            break;
            case SOUTHWEST:switch(levelToTurn){
                case CLOCKWISE_45: setDirection(WEST); break;
                case CLOCKWISE_90: setDirection(NORTHWEST); break;
                case CLOCKWISE_135: setDirection(NORTH); break;
                case COUNTERCLOCKWISE_45: setDirection(SOUTH); break;
                case COUNTERCLOCKWISE_90: setDirection(SOUTHEAST);break;
                case COUNTERCLOCKWISE_135:setDirection(EAST); break;
                case CLOCKWISE_180: setDirection(NORTHEAST);break;
            } ;
            break;
            case WEST:switch(levelToTurn){
                case CLOCKWISE_45: setDirection(NORTHWEST); break;
                case CLOCKWISE_90: setDirection(NORTH); break;
                case CLOCKWISE_135: setDirection(NORTHEAST); break;
                case COUNTERCLOCKWISE_45: setDirection(SOUTHWEST); break;
                case COUNTERCLOCKWISE_90: setDirection(SOUTH);break;
                case COUNTERCLOCKWISE_135:setDirection(SOUTHEAST); break;
                case CLOCKWISE_180: setDirection(EAST);break;
            } ;
            break;
            case NORTHWEST:switch(levelToTurn){
                case CLOCKWISE_45: setDirection(NORTH); break;
                case CLOCKWISE_90: setDirection(NORTHEAST); break;
                case CLOCKWISE_135: setDirection(EAST); break;
                case COUNTERCLOCKWISE_45: setDirection(WEST); break;
                case COUNTERCLOCKWISE_90: setDirection(SOUTHWEST);break;
                case COUNTERCLOCKWISE_135:setDirection(SOUTH); break;
                case CLOCKWISE_180: setDirection(SOUTHEAST);break;
            }  ;
            break;
        }
        
    }
    
}
