/*
 * Environment.java
 *
 * Created on 26 de Junho de 2006, 21:54
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package antsystem.model;

import antsystem.model.Nest;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.Toolkit;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author danilo
 */
public class Environment extends Stage {
    
    private Nest nest;
    private List<FoodSource> foodSources = new ArrayList<FoodSource>();
    private List<Actor> actors = new ArrayList<Actor>();
    
    private PheromoneMatrix pherormoneMatrix;
    //private ObstacleMatrix obstacleMatrix;
    private ObstacleSet obstacles;
    
    private ObstacleSet borders;
    private Shape drawingShape = null;
    
    private int evaporationInterval;
    private int evaporationRate;
    /** Creates a new instance of Environment */
    public Environment(int width, int height, Color color) {
        super(width,height,color);
        
        
        setPherormoneMatrix(new PheromoneMatrix(width, height));
        //pherormoneMatrix = new PheromoneMatrix();
        //obstacleMatrix = new ObstacleMatrix();
        obstacles = new ObstacleSet();
        borders = new ObstacleSet();
        constructBorders(width, height);
        setOpaque(false);
        
    }
    
    /*
     * Create obstacles in the border of the environment canvas.
     * Must be called after each resize of the environment.
     */
    public void constructBorders(int width, int height){
        
        borders.clear();
        //top line
        borders.addObstacle(new Line2D.Float(1f,1f,width-2,1f));
        //left line
        borders.addObstacle(new Line2D.Float(1f,1f,1f,height-2));
        //bottom line
        borders.addObstacle(new Line2D.Float(1f,height-2,width-2,height-2));
        //right line
        borders.addObstacle(new Line2D.Float(width-2,1f,width-2,height-2));
        
    }
    
    
    /**
     * Gets the current location of the nest
     * @return the nest position
     */
    public Point getNestLocation() {
        return getNest().getPosition();
    }
    
    /**
     * Places the nest in the new location.
     * @param nestLocation new location of the nest
     */
    public void setNestLocation(Point nestLocation) {
        this.getNest().setPosition(nestLocation);
    }
    
    
    
    /**
     * Verify if the Actor a has hit an obstacle.
     * @param a The Actor to be tested against all the existing obstacles.
     */
    protected void hitObstacleTest(Actor a){
        for(int i=0; i<a.getNumOfSensors(); i++){
            if(borders.hit(a.getSensor(i)) || obstacles.hit(a.getSensor(i)))
                a.hitAnObstacle(i);
        }
        
    }
    
    /**
     * Verify if the Actor has hit an FoodSource.
     * @param a The Actor to be tested.
     */
    protected void foundFoodTest(Actor a){
        for(FoodSource fs : foodSources){
            //avoid unecessary test
            double distance = a.getCenter().distance(fs.getCenter());
           // System.out.println("Distancia at� a fonte de comida: " + distance);
            if(distance < (fs.getDimension().getWidth()/2)+15){
             //   System.out.println("vai testar colis�o");
                for(int i=0; i<a.getNumOfSensors(); i++){
                    if(fs.hit(a.getSensor(i))){
                        a.actorHit(fs,i,distance);
                    }
                }
                //One food was found, so, to this actor, it is not necessary to test with another fs
                break;
            }
            
            
        }
    }
    
    /**
     * Verify if the Actor has hit an Nest.
     * @param a The Actor to be tested.
     */
    protected void foundNestTest(Actor a){
        double distance = a.getCenter().distance(getNest().getCenter());
        
        if(distance < getNest().getDimension().getWidth()/2 + 15 ){
            for(int i=0; i<a.getNumOfSensors(); i++){
                if(getNest().hit(a.getSensor(i))){
                    a.actorHit(getNest(),i,distance);
                }
            }
        }
    }
    
    int countUptatesToKownWhenToEvaporate=-1;
    protected void updateWorld() {
        synchronized(this){
            for(Actor ant: actors){
                ant.act();
                hitObstacleTest(ant);
                foundFoodTest(ant);
                
                //Tenho q modificar isso aqui para fazer com que a mimiguinha
                //perceba o ninho pela antena, e entao possa virar para o ladinho certo
                //e continuar andando at� quase o centro do ninho e entao
                //deposidar o feromonio
                //the same for the food! Arh
                
                foundNestTest(ant);
//                if (nest.isInside(ant.getBoundingBox()))
//                    System.out.println("is inside nest");
//                if(nest.hit(ant.getBoundingBox())){
//                    ant.nestFound();
//                    System.out.println("hit nest");
//                }
            }
            ++countUptatesToKownWhenToEvaporate;
            if( (countUptatesToKownWhenToEvaporate % getEvaporationInterval())==0){
                //System.out.println("Evaporando 33: " + countUptatesToKownWhenToEvaporate);
                countUptatesToKownWhenToEvaporate=0;
                pherormoneMatrix.evaporate(getEvaporationRate());
            }
            
            if(foodSources.size()>0){
                FoodSource[] arrayFS =  foodSources.toArray(new FoodSource[1]);
                for(FoodSource fs: arrayFS){
                    if(fs.getAmountOfFood()<=6){
                        foodSources.remove(fs);
                    }
                }
            }
        }
        
        
    }
    
    /**
     * Creates a World's representation in the Buffered Graphics
     * 
     */
    protected void paintWorld() {
        Graphics2D g2d = (Graphics2D)getDoubleBufferdGraphics();
        
        //painting the backGround
        g2d.setColor(this.getBgColor());
        g2d.fillRect(0,0, getPreferredSize().width, getPreferredSize().height);
        
        
        //painting the obstacles
        Stroke originalStroke = g2d.getStroke();
        g2d.setStroke(new BasicStroke(5.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
        borders.drawObstacles(g2d);
        g2d.setStroke(new BasicStroke(5.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL));
        obstacles.drawObstacles(g2d);
        g2d.setStroke(originalStroke);
        
        //painting the pheromoneMatirx
        getPherormoneMatrix().pait(g2d);
        //painting the nest
        if(nest!=null)
            nest.paint(g2d);
        //painting the food sources
        for(FoodSource fs : foodSources){
            
            fs.paint(g2d);
        }
        
        //painting actors
        synchronized(this){
            for (Actor ant : actors){
                ant.paint(g2d);
            }
        }
        
        
    }
    
    /**
     * Paints the Buffered Graphics to the Screen
     */
    protected void paintScreen(){
        Graphics g=null;
        
        g = this.getGraphics();
        Image dbimage = getSecondBufferImage();
        if (g!=null && dbimage!=null){
            g.drawImage(dbimage,0,0,null);
            Toolkit.getDefaultToolkit().sync();
        }
        //during drawing obstacles process
        if (drawingShape != null){
            Graphics2D g2d = (Graphics2D) g;
            Color originalColor = g2d.getColor();
            g2d.setColor(Color.RED);
            g2d.draw(drawingShape);
            g2d.setColor(originalColor);
        }
    }
    
    /**
     * This method should be used to instantly show an model's Environment change.
     * It calls the <code> paintWorld </code> and <code> paintScreen </code>
     */
    public void activeRepaint(){
        paintWorld();
        paintScreen();
    }
    
    /**
     * Adds a new Source of food in the Environment
     * @param fs the new {@link FoodSource}
     */
    public void addFoodSource(FoodSource fs){
        foodSources.add(fs);
        paintWorld();
        paintScreen();
    }
    
    /**
     * Gets the {@link Nest} in the {@link Environment}
     * @return the nest or <code> null </code>
     */
    public Nest getNest() {
        return nest;
    }
    
    /**
     * Sets the nest
     * @param nest {@link Nest} to be set
     */
    public void setNest(Nest nest) {
        this.nest = nest;
        paintWorld();
        paintScreen();
        //repaint();
    }
    
    
    /**
     * Creates a new {@link Nest} and sets it as the nest
     * @param location of the nest
     */
    public void setNest(Point location){
        setNest(new Nest(location, spriteCache));
    }
    
    /**
     * Paints this {@link Environment}
     * @param g the Graphics context to be painted
     */
    public void paint(Graphics g) {
        super.paint(g);
        if(getSecondBufferImage()!=null)
            g.drawImage(getSecondBufferImage(),0,0,null);
        
    }
    
    
    /**
     * Creates new actors
     *
     * @param numOfAnts number of actors to be created
     */
    @Deprecated
    public void creatAnts(int numOfAnts) {
        for (int i=0; i<numOfAnts; i++)
            creatAnt();
        
        paintWorld();
        paintScreen();
        //repaint();
    }
    
    private void creatAnt() {
        Ant3 ant = new Ant3();
        if(nest!=null)
            ant.setPosition(nest.getCenter());
        //ant.initRadomDirection();
        //ant.setDirection(Direction.NORTH);
        ant.setPheromoneMatrix(getPherormoneMatrix());
        
        //a velocidade tem que ser parametriz�vel
        ant.setVelocity(2);
        
        synchronized (this){
            actors.add(ant);
        }
        
    }
    
    /**
     * Adds a new Actor in the Environment
     * @param a the actor to be added
     */
    public void addActor(Actor a){
        if(nest!=null)
            a.setPosition(nest.getCenter());
        
        synchronized (this){
            actors.add(a);
        }
    }
    
    /**
     * Gets the set of obstacles (shapes) currently existent in the Environment.
     * @return The ObstacleSet.
     */
    public ObstacleSet getObstacles() {
        return obstacles;
    }
    
    /**
     * Gets the currently set drawing shape.
     * @return The shape.
     */
    public Shape getDrawingShape() {
        return drawingShape;
    }
    
    /**
     * Sets the shape which is being drawing in the moment. This way it will be rendered in a different color.
     * @param drawingShape The shape being drawing.
     */
    public void setDrawingShape(Shape drawingShape) {
        this.drawingShape = drawingShape;
    }
    
    /**
     * Gets the currently set PheromoneMatrix
     * @return The instance currently used.
     */
    public PheromoneMatrix getPherormoneMatrix() {
        return pherormoneMatrix;
    }
    
    /**
     * Sets the pheromone matrix to be used in the Environment.
     * @param pherormoneMatrix The instance to be set.
     */
    public void setPherormoneMatrix(PheromoneMatrix pherormoneMatrix) {
        this.pherormoneMatrix = pherormoneMatrix;
    }
    
    /**
     * The evaporation interval currently used.
     * @return The number of updates before one evaporation.
     */
    public int getEvaporationInterval() {
        return evaporationInterval;
    }
    
    /**
     * Sets the interval between two evaporation events.
     * @param evaporationInterval The number of updates before one evaporation.
     */
    public void setEvaporationInterval(int evaporationInterval) {
        this.evaporationInterval = evaporationInterval;
    }
    
    /**
     * The evaporation rate currently used
     * @return How much of pheromone is evaporated per turn.
     */
    public int getEvaporationRate() {
        return evaporationRate;
    }
    
    /**
     * Sets the evaporation rate
     * @param evaporationRate How much of pheromone will be subtracted in each location which has pheromone.
     */
    public void setEvaporationRate(int evaporationRate) {
        this.evaporationRate = evaporationRate;
    }
    
    /**
     * Removes all actors in the Environment
     */
    public void removeAllActors(){
        actors.clear();
    }
    
    /**
     * Removes all obstacles, except the borders, in the Environment
     */
    public void removeAllObstacles(){
        getObstacles().clear();
    }
    
    /**
     * Clear all the pheromone existent.
     */
    public void clearPheromoneMatrix(){
        getPherormoneMatrix().clear();
    }
}
