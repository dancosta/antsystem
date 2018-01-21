/*
 * ObstacleMatrix.java
 *
 * Created on 7 de Outubro de 2006, 21:31
 *
 */

package antsystem.model;

import java.awt.Point;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author Danilo N Costa
 */
public class ObstacleMatrix {
    
    Set <Point> obstacleParticle = new HashSet<Point>();
    /** Creates a new instance of ObstacleMatrix */
    public ObstacleMatrix() {
    }
    
    public void setObstacleParticle(Point point){
        obstacleParticle.add(point);
    }
    
    public boolean isObstructed(Point point){
        return obstacleParticle.contains(point);
    }
    
    public Point[] toArray(){
        return obstacleParticle.toArray(new Point[0]);
    }
    
}
