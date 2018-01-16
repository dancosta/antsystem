/*
 * Nest.java
 *
 * Created on 4 de Julho de 2006, 16:08
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package antsystem.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author danilo
 */
public class Nest extends Actor{
    
    private int amountOfCollectFood=0;
    /** Creates a new instance of Nest */
    
    public Nest(int x, int y) {
        this(new Point(x,y), null);
    }
    
    public Nest(Point location){
        this(location,null);
    }
    
    public Nest(Point center, SpriteCache spriteCache){
        this(center,new Dimension(50,50), null);
    }
    
    public Nest(Point center, Dimension dimension, SpriteCache spriteCache){
        super(spriteCache,center,dimension);
        
    }
    

    public void paint(Graphics2D g) {
        
        g.setColor(Color.MAGENTA);
        g.fillOval(getPosition().x,getPosition().y,
                getDimension().width, getDimension().height);
        
        g.setColor(Color.WHITE);
        Font f = new Font("Serif", Font.BOLD, 12);
        g.setFont(f);
        String s = Integer.toString(getAmountOfCollectedFood());
        
        Rectangle2D bounds = f.getStringBounds(s,g.getFontRenderContext());
        float x = (float)(getCenter().getX()-bounds.getWidth()/2f);
        float y = (float)(getCenter().getY()-(bounds.getY())/2f);
        g.drawString(s,x,y);
        
    }

    public void act() {
    }

    public void hitAnObstacle(int sensorIdx) {
    }

  
    public Rectangle2D getSensor(int i) {
        return null;
    }

    public int getNumOfSensors() {
        return 0;
    }

    public void actorHit(Actor a, int sensor, double distanceToTheCenter) {
    }

    /**
     * Returns the total amount of collected food
     * @return The total amount of collected food
     */
    public int getAmountOfCollectedFood() {
        return amountOfCollectFood;
    }

    /**
     * Adds the <code> amountOfCollectedFood </code> to total amount.
     * @param deposit The amount to be added.
     */
    public void depositCollectedFood(int deposit) {
        this.amountOfCollectFood += deposit;
    }

    
}
