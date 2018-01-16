/*
 * FoodSource.java
 *
 * Created on 18 de Janeiro de 2007, 22:47
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
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author danilo
 */
public class FoodSource extends Actor{
    private int amountOfFood;
    /**
     * Creates a new instance of FoodSource
     */
    public FoodSource() {
    }
    public FoodSource(int x, int y) {
        this(new Point(x,y), null);
    }
    
    public FoodSource(Point location){
        this(location,null);
    }
    
    public FoodSource(Point center, SpriteCache spriteCache){
        this(center,0,spriteCache);
    }
    
    public FoodSource(Point center, int amountOfFood){
        this(center,amountOfFood, null);
    }
    public FoodSource(Point center, int amountOfFood, SpriteCache spriteCache){
        //super(spriteCache,center,new Dimension(0,0));
        setAmountOfFood(amountOfFood);
        setCenterAt(center);
        setSpriteCache(spriteCache);
    }

    /**
     * Returns the amount of food currently existent.
     * @return The amount of food.
     */
    public int getAmountOfFood() {
        return amountOfFood;
    }

    /**
     * Sets the total of food.
     * @param amountOfFood Amount of food to be set as the total.
     */
    public void setAmountOfFood(int amountOfFood) {
        this.amountOfFood = amountOfFood;
        int d =0;
        if(amountOfFood<=50)
            d =50;
        else if (amountOfFood > 1000)
            d = 95 + (int)(amountOfFood * 0.005); 
        else if (amountOfFood > 50 )
            d = 50 + (int)(amountOfFood * 0.05);
        
        else
            d = amountOfFood;
        
        System.out.println("d: " + d);
        setDimension(new Dimension(d,d));
        setCenterAt(getCenter());
    }
    
    /**
     * Withdraw the specified amount of food. 
     * @param amount The amount to be subtract from the total.
     * @return 
     */
    public int withDraw(int amount){
        int ret = Math.max(getAmountOfFood(), amount);
        setAmountOfFood(Math.max(0,getAmountOfFood()-amount));
        return ret;
    }

    @Override
    public void paint(Graphics2D g) {
       
        g.setColor(Color.GREEN);
        g.fillOval(getPosition().x,getPosition().y,
                getDimension().width, getDimension().height);
        
        g.setColor(Color.WHITE);
        Font f = new Font("Serif", Font.BOLD, 12);
        g.setFont(f);
        String s = Integer.toString(getAmountOfFood());
        
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
    

}
