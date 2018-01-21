/*
 * Ant2.java
 *
 * Created on 2 de Julho de 2006, 23:01
 *
 */

package antsystem.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.Random;

/**
 *
 * @author Danilo N Costa
 */
public class Ant2 extends Actor{
    private boolean carringFood;
    private int vx, vy, velocity;
    private Direction direction= new Direction();
    
    private Point oldPosition;
    
    private PheromoneMatrix pheromoneMatrix;
    
    
    /** Creates a new instance of Ant2 */
    public Ant2() {
        this.setDimension(new Dimension(10,10));
     
    }
    
    public Ant2(Point center, Dimension dimension){
        super(center,dimension);
    }
    public Ant2(int x, int y, int width, int height, int direction) {
        
        setPosition(new Point(x,y));
        setDimension(new Dimension(width, height));
        setDirection(direction);
        
    }
   
    public boolean isCarringFood() {
        return carringFood;
    }
    
    public void setCarringFood(boolean carringFood) {
        this.carringFood = carringFood;
    }
    
    public void initRadomDirection(){
        Random rdm = new Random();
        int d = 1 + rdm.nextInt(8) ;
        switch(d){
            case 1: setDirection(Direction.NORTH);
            break;
            case 2: setDirection(Direction.NORTHEAST);
            break;
            case 3: setDirection(Direction.EAST);
            break;
            case 4: setDirection(Direction.SOUTHEAST);
            break;
            case 5: setDirection(Direction.SOUTH);
            break;
            case 6: setDirection(Direction.SOUTHWEST);
            break;
            case 7: setDirection(Direction.WEST);
            break;
            case 8: setDirection(Direction.NORTHWEST);
            break;
        }
    }
    
    protected void randomlyTurn(){
        Random rdm = new Random();
        int randomNumber = rdm.nextInt(100) ;
        //probabilidade Hard codded!! Preciso deixar isso gen�rico!
        
        if (randomNumber < 5 ){
            //5%
            direction.turn(Direction.CLOCKWISE_45);
            
        }else if(randomNumber <7){
            //2%
            direction.turn(Direction.CLOCKWISE_90);
        }else if(randomNumber <12){
            //5%
            direction.turn(Direction.COUNTERCLOCKWISE_45);
        }else if(randomNumber < 14){
            //2%
            direction.turn(Direction.COUNTERCLOCKWISE_90);
        }
        adustVelocity();
    }
    boolean hasHit = false;
    @Override public void act() {
        if (!hasHit){
            depositPheromone();
            randomlyTurn();
            oldPosition = getPosition();
            setPosition(new Point(getPosition().x+vx, getPosition().y+vy));
            
            
        }else{
            setPosition(oldPosition);
            hasHit = false;
        }
    }
    
    @Override
    public void hitAnObstacle(int sensorIdx) {
        hasHit = true;
    }
    
    
    @Override public void paint(Graphics2D g2d) {
        
        Color oldColor = g2d.getColor();
        
        g2d.setPaint(new Color(255,255,0,100));
        
        if (isCarringFood())
            g2d.setPaint(Color.BLUE);
        
        int x = getPosition().x;
        int y = getPosition().y;
        int width = getDimension().width;
        int height = getDimension().height;
        
        float centerX = x+(getDimension().width)/2;
        float centerY = y+(getDimension().height)/2;
        
        g2d.fill( new Ellipse2D.Float(x,y,width, height));
        g2d.setPaint(Color.BLACK);
        g2d.draw(new Ellipse2D.Float(x,y,width, height));
        switch (getDirection()){
            case Direction.NORTH: g2d.draw(new Line2D.Float(centerX, centerY, centerX, centerY-(height/2)-2));
            break;
            case Direction.NORTHEAST: g2d.draw(new Line2D.Float(centerX, centerY, x+width+1, y-1));
            break;
            case Direction.EAST: g2d.draw(new Line2D.Float(centerX, centerY, x+width+2, centerY));
            break;
            case Direction.SOUTHEAST: g2d.draw(new Line2D.Float(centerX, centerY, x+width+1, y+height+1));
            break;
            case Direction.SOUTH: g2d.draw(new Line2D.Float(centerX, centerY, centerX, y + height + 2));
            break;
            case Direction.SOUTHWEST: g2d.draw(new Line2D.Float(centerX, centerY, x-1, y+height+1));
            break;
            case Direction.WEST: g2d.draw(new Line2D.Float(centerX, centerY, x-2, centerY));
            break;
            case Direction.NORTHWEST: g2d.draw(new Line2D.Float(centerX, centerY, x-1, y-1));
            break;
        }
        g2d.setPaint(oldColor);
        
    }
    
    private void depositPheromone(){
        int x = getCenter().x;
        int y= getCenter().y;
        //int value = pheromoneMatrix.getValue(x,y) + 1;
        //pheromoneMatrix.setValue(x,y,value);
        pheromoneMatrix.depositPheromone(x,y,15);
        
    }
    private void adustVelocity(){
        //para que o caminhar na horizontal seja constante em pixels
        //vide tri�ngulo de pit�goras, com os catetos de mesmo comprimento
        
        AffineTransform af;
        double normalizer = Math.sqrt(0.5d);
        switch (getDirection()){
            case Direction.NORTH: vx =0; vy=-getVelocity();
            break;
            case Direction.NORTHEAST: vx=(int)(getVelocity()*normalizer); vy=-vx;
            break;
            case Direction.EAST: vx=getVelocity(); vy=0;
            break;
            case Direction.SOUTHEAST: vx=(int)(getVelocity()*normalizer); vy=vx;
            break;
            case Direction.SOUTH: vy=+getVelocity(); vx=0;
            break;
            case Direction.SOUTHWEST: vx=-1*(int)(getVelocity()*normalizer); vy=-vx;
            break;
            case Direction.WEST: vx=-getVelocity(); vy=0;
            break;
            case Direction.NORTHWEST: vx=-1*(int)(getVelocity()*normalizer); vy = vx;
            break;
        }
    }
    public void setDirection(Direction direction) {
        this.direction = direction;
        adustVelocity();
    }
    
    public void setDirection(int direction) {
        this.direction.setDirection(direction);
        adustVelocity();
    }
    
    public int getDirection() {
        return direction.getDirection();
    }
    
    public int getVelocity() {
        return velocity;
    }
    
    @Override
    public void setPosition(Point position) {
        super.setPosition(position);
        
    }
    

    
    /**
     * Set constant for velocity
     * @param velocity in pixels
     */
    public void setVelocity(int velocity) {
        this.velocity = velocity;
    }
    
    public PheromoneMatrix getPheromoneMatrix() {
        return pheromoneMatrix;
    }
    
    public void setPheromoneMatrix(PheromoneMatrix pheromoneMatrix) {
        this.pheromoneMatrix = pheromoneMatrix;
    }

    public Rectangle2D getSensor(int i) {
        return null;
    }

    public int getNumOfSensors() {
        return 0;
    }

    public void nestFound() {
    }

    public void foodFound() {
    }

    public void actorHit(Actor a, int sensor, double distanceToTheCenter) {
    }
}
