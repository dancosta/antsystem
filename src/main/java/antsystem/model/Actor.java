/*
 * Actor.java
 *
 * Created on 2 de Julho de 2006, 22:51
 *
 * 
 */

package antsystem.model;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

/**
 * Actor is an abstract base class for all actors to be be put in the {@link antsystem.antsystem.model.Environment}.
 * These actors includes mainly all the agents in the system like {@link Ant3}, and other types of actor
 * such as {@link antsystem.antsystem.model.FoodSource} and {@link antsystem.antsystem.model.Nest}
 * 
 * @author Danilo N. Costa
 */
public abstract class Actor {
    private Point position = new Point(0,0);
    private Dimension dimension;
    private int velX, velY;
    private SpriteCache spriteCache;
   
    
    
    /** Creates a new instance of Actor */
    public Actor(SpriteCache spriteCache) {
        this(spriteCache, null, null);
    }
    
    public Actor(Point center, Dimension dimension){
        this(null, center, dimension);
    }
    
    public Actor(SpriteCache spriteCache, Point center, Dimension dimension/*, ObstacleSet obstacles, ObstacleSet borders*/){
        this.dimension = dimension;
        if(dimension!=null)
            setCenterAt(center);
        
        this.setSpriteCache(spriteCache);
        
    }
    public Actor(){
        this(null);
    }
    

    
    /**
     * The bounding box of the actor
     * @return The bounding box of the actor
     */
    public Rectangle2D getBoundingBox(){
        return new Rectangle2D.Float(position.x, position.y, dimension.width, dimension.height);
    }
    
    /**
     * Returns the current position of the Actor.
     * @return The position is based on the most top left point of the Actor. 
     */
    public Point getPosition() {
        return position;
    }

    /**
     * Sets a new position for the Actor
     * @param position The most top left point of the new position
     */
    public void setPosition(Point position) {
        this.position = position;
    }

    /**
     * Returns the current dimension of the Actor.
     * @return The <code>Dimension</code> of the Actor. 
     */
    public Dimension getDimension() {
        return dimension;
    }

    /**
     * Sets the new dimension of the Actor
     * @param dimension The new <code> Dimension </code> to be set in the Actor.
     */
    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }

    
    /**
     * Returns the current velocity in X axis of the Actor.
     * @return The current X velocity in <code> int </code> precision.
     */
    public int getVelX() {
        return velX;
    }

    /**
     * Sets the new velocity in X axis.
     * @param velX The value in <code> int </code> precision.
     */
    public void setVelX(int velX) {
        this.velX = velX;
    }

     /**
     * Returns the current velocity in Y axis of the Actor.
     * @return The current Y velocity in <code> int </code> precision.
     */
    public int getVelY() {
        return velY;
    }

    /**
     * Sets the new velocity in Y axis.
     * @param velY The value in <code> int </code> precision.
     */
    public void setVelY(int velY) {
        this.velY = velY;
    }
    
    /**
     * The main method of the Actor.
     * It will be called by the {@link Environment} in every update of the World.
     * This method must be overridden to provides the correctly action for the Actor, 
     * for example, if the Actor is moving, it should update its velocity values here.
     * 
     */
    abstract public void act();
    
    /**
     * Its called by Environment every time it needs to update the visual representation of the World
     * This method must be overridden to provides the correctly visual representation. 
     * @param g The Graphics context in which the actor will be painted.
     */
    abstract public void paint(Graphics2D g);
    
    /**
     * Its called by the Environment every time the Actor is collided with some obstacle in the <code>Environment</code>.
     *
     * @param sensorIdx The number corresponding to the sensor that is collided.  
     */
    abstract public void hitAnObstacle(int sensorIdx);
    
    /**
     * Its called by the <code> Environmnet </code> whenever it is collided to another <code> Actor </code> in the <code> Environment </code>.
     * @param a The <code>Actor</code> tho whom it is collided.
     * @param sensor The index of the sensor collided.
     * @param distanceToTheCenter The distance to the center point of the collided <code> Actor</code>.
     */
    abstract public void actorHit(Actor a, int sensor, double distanceToTheCenter);
    
    /**
     * Returns the correspondent bounding box of sensor index. 
     * @param sensor The index of the desired sensor.
     * @return A <code> Rectangle2D</code> correspondent to the desired sensor.
     */
    abstract public Rectangle2D getSensor(int sensor);
    
    /**
     * Returns the number of Sensors in the Actor
     * @return the number of Sensors
     */
    abstract public int getNumOfSensors();

    /**
     * Returns the actual center position of the <code> Actor </code>
     * @return <code>Point</code> instance corresponding to the actual center position.
     */
    public Point getCenter() {
        return new Point(getPosition().x + (int) (getDimension().width/2), getPosition().y + (int) (getDimension().height/2));
    }
    
    /**
     * Sets the new position for the Actor using the desired center.
     * @param p The <code> Point </code> to the new center position.
     */
    public void setCenterAt(final Point p){
        if (dimension == null)
            throw new RuntimeException("Dimension is null, impossible to calculate the real location");
        
        setPosition(new Point(p.x - getDimension().width/2 , p.y - getDimension().width/2));
    }
    
    
    
    /**
     * Tests whether the <code>Rectangle2D </code> hits the <code>Actor</code>
     * @param r The rectangle against which the test will be done. 
     * @return True if the rectangle hits the <code>Actor</code>, false other way. 
     */
    public boolean hit(Rectangle2D r){
        Shape s = new Ellipse2D.Float(getPosition().x,
                getPosition().y,getDimension().width, getDimension().height);
        
        return (!s.contains(r)&&s.intersects(r));
        
    }

    /**
     * Tests whether the <code>Rectangle2D </code> is inside the <code>Actor</code>
     * @param r The rectangle against which the test will be done. 
     * @return True if the rectangle is inside the <code>Actor</code>, false other way. 
     */
    public boolean isInside(Rectangle2D r){
        Shape s = new Ellipse2D.Float(getPosition().x,
                getPosition().y,getDimension().width, getDimension().height);
        return s.contains(r);
    }
    
    
    public SpriteCache getSpriteCache() {
        return spriteCache;
    }

    public void setSpriteCache(SpriteCache spriteCache) {
        this.spriteCache = spriteCache;
    }

}
