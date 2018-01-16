/*
 * EnvironmentMatrix.java
 *
 * Created on 25 de Setembro de 2005, 17:34
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package antsystem.model;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Danilo  Costa
 */
public class PheromoneMatrix {
    
    public static Object lock = new Object();
    private int matrix [][];
    private Map<Point, Integer> fakeMatrix;
    
    private int width, height;
    private BufferedImage imageRepresentation;
    
    /** Creates a new instance of EnvironmentMatrix */
    public PheromoneMatrix(int width, int height) {
//        matrix = new int[width][height];
        setWidth(width);
        setHeight(height);
        init(width, height);
    }
    
    public PheromoneMatrix(){
        
    }
    //Retorna o valor de uma posi�ao
    @Deprecated
    public int getValue(int x, int y){
        if ((x>=matrix.length || x<0)||(y>=matrix[x].length || y<0))
            return 0;
        
        return matrix[x][y];
    }
    
    @Deprecated
    public void setValue(int x, int y, int val){
        if ((x>=matrix.length || x<0)||(y>=matrix[x].length || y<0))
            return;
        
        matrix[x][y] = val;
    }
    
    public int getPheromoneLevel(int x, int y){
        return getPheromoneLevel(new Point(x,y));
    }
    
    public int getPheromoneLevel(final Point p){
        Integer i = fakeMatrix.get(p);
        return (i!=null)?i.intValue():0;
    }
    
    public void depositPheromone(int x, int y, int val){
        Point p = new Point(x,y);
        addPheromoneLevel(p, val);
        spreadPheromone(val, p);
    }
    
    public void addPheromoneLevel(final Point p, int val){
        if(!whithinEnvBoundary(p))
            return;
        synchronized (lock){
            Integer i = fakeMatrix.get(p);
            Integer result = null;
            if (i==null)
                result = new Integer(val);
            else
                result = i+val;
            
            fakeMatrix.put(p,result);
            if(result<=0){
                fakeMatrix.remove(p);
                imageRepresentation.setRGB(p.x,p.y,0);
            }
            else{
                
                imageRepresentation.setRGB(p.x,p.y, getRGB(255,232,Math.max(0,255-(int)(result*1.3)),0));
                //imageRepresentation.setRGB(p.x,p.y, getRGB(255,0,Math.max(0,255-(int)(result*1.3)),232));
            }
        }
    }
    
    
    /**
     * Used to spread pheromone near where it was deposited.
     * @param val Value deposited in p.
     * @param p Point where pheromone was deposited.
     */
    private void spreadPheromone(int val, Point p){
        int borderVal = (val>0)?val/2:0;
        for(int i=1;borderVal>0;i++){
            border(i,p,borderVal);
            borderVal=borderVal/2;
        }
    }
    
    
    /**
     * This method is used to spread pheromone along a specific <I>level of border</I> in the environment using the following patern:
     * <PRE><B>
     * 3333333
     * 3222223
     * 3211123
     * 321p123
     * 3211123
     * 3222223
     * 3333333</PRE></B>
     * The <I>p</I> stands for the central point where the pheromone was initially deposited,
     * the numbers shows the respective levels of border.
     * @param blevel The level of the border.
     * @param centralPoint The original point where pheromone was deposited.
     * @param val the value to be added in each position of the border.
     */
    private void border(int bLevel, Point centralPoint, int val ){
        for(int i=-bLevel; i<+bLevel; i++){
            addPheromoneLevel(new Point(centralPoint.x+i, centralPoint.y-bLevel),val); //borda norte
            addPheromoneLevel(new Point(centralPoint.x-bLevel, centralPoint.y+i),val); //borda oeste
            addPheromoneLevel(new Point(centralPoint.x+i, centralPoint.y+bLevel),val); //borda sul
            addPheromoneLevel(new Point(centralPoint.x+bLevel, centralPoint.y+i),val); //borda leste
            
        }
    }
    public Set<Point> existentPoints(){
        return Collections.unmodifiableSet(fakeMatrix.keySet());
    }
    
    private void init(int width, int height) {
        fakeMatrix = new HashMap<Point, Integer>();
        imageRepresentation = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        
        int alpha = 0;
        int red = 255;
        int green = 0;
        int blue = 0;
        
        // Manipulate the r, g, b, and a values.
        
        int rgb = getRGB(alpha,red, green, blue);
        for(int i = 0; i<width; i++)
            for(int j=0; j<height; j++)
                imageRepresentation.setRGB(i,j, rgb);
        
    }
    
    public void evaporate(int level){
        Set<Point> existentPoints = new HashSet<Point>();
        Point[] pArray = new Point[existentPoints.size()];
        synchronized  (lock) {
           existentPoints = existentPoints();
           pArray = existentPoints.toArray(pArray);
        }
        for (Point p: pArray){
            
            addPheromoneLevel(p, -level);
        }
        
    }
    
    private int getRGB(int alpha, int red, int green, int blue){
        return (alpha << 24) | (red << 16) | (green << 8) | blue;
    }
    
    public void pait(Graphics g){
        
        g.drawImage(imageRepresentation, 0,0,null);
    }
    
    private boolean whithinEnvBoundary(Point p) {
        if (p.x<0||p.x>getWidth()||p.y<0||p.y>getHeight())
            return false;
        return true;
    }
    
    public int getWidth() {
        return width;
    }
    
    public void setWidth(int width) {
        this.width = width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public void setHeight(int height) {
        this.height = height;
    }
    
    /**
     * Clears all the pheromone
     */
    public void clear(){
        init(getWidth(),getHeight());
    }
}
