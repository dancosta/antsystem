/*
 * ObstacleSet.java
 *
 * Created on 7 de Outubro de 2006, 23:23
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package antsystem.model;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author danilo
 */
public class ObstacleSet {
    Set<Shape> obstacles = new HashSet<Shape>();
    private Graphics2D g2d;
    private Shape selectedShape;
    
    /** Creates a new instance of ObstacleSet */
    public ObstacleSet() {
    }
    
    public synchronized void addObstacle(Shape shape){
        obstacles.add(shape);
    }
    
    public synchronized void removeObstacle(Shape shape){
        obstacles.remove(shape);
    }
    
    public void drawObstacles(Graphics2D g2d){
        Color originalColor = g2d.getColor();
        
        g2d.setColor(Color.BLACK);
        synchronized (this){
            for (Shape shape : obstacles){
                if(shape==selectedShape){
                    g2d.setColor(Color.BLUE);
                    g2d.draw(shape);
                    g2d.setColor(Color.BLACK);
                }else
                    g2d.draw(shape);
                
            }
        }
        g2d.setColor(originalColor);
    }
    
    public boolean hit(Point2D point){
        Graphics2D g2d = getGraphics2D();
        
        synchronized (this){
            for (Shape shape : obstacles){
                
                if (g2d.hit(new Rectangle((int)point.getX(), (int)point.getY(),1,1), shape, false)){
                    return true;
                }
            }
        }
        return false;
    }
    
    public boolean hit(Rectangle2D rect){
        Graphics2D g2d = getGraphics2D();
        synchronized (this){
            for (Shape shape : obstacles){
                
                if(g2d.hit(new Rectangle((int)rect.getX(), (int)rect.getY(),
                        (int)rect.getWidth(), (int)rect.getHeight()), shape, true)){
                    
                    
                    return true;
                }
            }
        }
        return false;
    }
    
    private Graphics2D getGraphics2D() {
        if (g2d==null)
            g2d = (Graphics2D) Stage.doubleBufferdGraphics;
        return g2d;
    }
    
    public Shape whoIsHit(Rectangle2D rect){
        Graphics2D g2d = (Graphics2D) Stage.doubleBufferdGraphics;
        synchronized (this){
            for (Shape shape : obstacles){
                if(g2d.hit(new Rectangle((int)rect.getX(), (int)rect.getY(),
                        (int)rect.getWidth(), (int)rect.getHeight()), shape, true)){
                    return shape;
                }
            }
        }
        return null;
    }
    
    synchronized void clear() {
        obstacles.clear();
    }
    
    public Shape getSelectedShape() {
        return selectedShape;
    }
    
    public void setSelectedShape(Shape selectedShape) {
        this.selectedShape = selectedShape;
    }
}
