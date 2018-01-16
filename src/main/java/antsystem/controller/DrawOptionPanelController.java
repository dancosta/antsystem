/*
 * DrawOptionPanelController.java
 *
 * Created on 9 de Outubro de 2006, 15:41
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package antsystem.controller;

import antsystem.gui.DrawOptionPanel;
import antsystem.model.Environment;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author danilo
 */
public class DrawOptionPanelController {
    private DrawOptionPanel view;
    private Environment model;
    private MouseHandler mouseHandler;
    private KeyHandler keyHandler;
    private Graphics2D g2d;
    /** Creates a new instance of DrawOptionPanelController */
    public DrawOptionPanelController(DrawOptionPanel view, Environment model) {
        this.view = view;
        this.model = model;
        mouseHandler = new MouseHandler();
        keyHandler = new KeyHandler();
        //this.model.setLayout(new BorderLayout());
        //this.model.add(new DrawingPanel(), BorderLayout.CENTER);
        //this.model.add(new JLabel("dadadadadadad"), BorderLayout.SOUTH);
        //this.model.add(new JButton("bobobob"), BorderLayout.NORTH);
    
    }
    
    
    
    
    private void addObstacle(Shape obstacle){
        model.getObstacles().addObstacle(obstacle);
    }
    
    private void draw(Shape shape){
        model.setDrawingShape(shape);
        model.activeRepaint();
    }
    
    /**
     * Remove the Handler to mouse events on the Environment Canva's
     */
    void uninstall() {
        model.removeMouseListener(mouseHandler);
        model.removeMouseMotionListener(mouseHandler);
        model.removeKeyListener(keyHandler);
        
    }
    
    /**
     * Add the Handler to mouse events on the Environment Canva's
     */
    public void install() {
        this.model.addMouseListener(mouseHandler);
        this.model.addMouseMotionListener(mouseHandler);
        this.model.addKeyListener(keyHandler);
        model.requestFocus();
        
    }
    
    public Graphics2D getG2d() {
        return g2d;
    }
    
    public Environment getModel() {
        return model;
    }
    
    public DrawOptionPanelController.MouseHandler getMouseHandler() {
        return mouseHandler;
    }
    
    private class MouseHandler extends MouseAdapter implements MouseMotionListener{
        boolean firstClick = true;
        int initialX, initialY;
        boolean drawingLine = false;
        boolean drawingPolyLine = false;
        List<Point2D> polyLinePoints = new ArrayList<Point2D>();
        public void mouseReleased(MouseEvent e) {
            
        }
        
        public void mousePressed(MouseEvent e) {
            model.requestFocus();
            if(view.getSelectedOptionType()==DrawOptionPanel.SELECT)
                handleSelection(e);
            
        }
        
        public void mouseClicked(MouseEvent e) {
            if (view.getSelectedOptionType()==DrawOptionPanel.LINE){
                
                
                if(firstClick){
                    firstClick = false;
                    drawingLine = true;
                    initialX = e.getX();
                    initialY = e.getY();
                    
                }else{
                    drawingLine = false;
                    addObstacle(new Line2D.Float(initialX, initialY, e.getX(), e.getY()));
                    draw(null);
                    firstClick = true;
                }
                
                
            }else if(view.getSelectedOptionType()==DrawOptionPanel.POLLY_LINE){
                
                
                polyLinePoints.add(new Point2D.Float(e.getX(), e.getY()));
                if(e.getClickCount()==1){
                    
                    
                    if(polyLinePoints.size()<2){
                        //ainda estamos desenhando a primeira linha
                        drawingLine = true;
                        initialX = e.getX();
                        initialY = e.getY();
                    }else{
                        drawingLine = false;
                        drawingPolyLine=true;
                    }
                    
                }else{
                    
                    drawingLine = false;
                    drawingPolyLine=false;
                    addObstacle(getPolyLine());
                    draw(null);
                    polyLinePoints.clear();
                }
                
                
            }else if(view.getSelectedOptionType()==DrawOptionPanel.PHEROMONE_LEVEL)
                handlePheromoneLevel(e.getX(), e.getY());
            
            
        }
        
        public void mouseDragged(MouseEvent e) {
            if(view.getSelectedOptionType()==DrawOptionPanel.SELECT)
                handleDragSelection(e);
        }
        
        public void mouseMoved(MouseEvent e) {
            if (drawingLine){
                draw(new Line2D.Float(initialX, initialY, e.getX(), e.getY()));
            }else if (drawingPolyLine){
                //tem um bug aqui, quando se � o primeiro ponto, fica faltando o moveTo inicial do generalPath
                GeneralPath pline = getPolyLine();
                pline.lineTo(e.getX(), e.getY());
                draw(pline);
            }
        }
        
        private GeneralPath getPolyLine(){
            GeneralPath polyLine = new GeneralPath();
            
            Point2D firstPoint = polyLinePoints.get(0);
            polyLine.moveTo((float)firstPoint.getX(),(float)firstPoint.getY());
            
            for (int i=1; i<polyLinePoints.size(); i++ ){
                Point2D p = polyLinePoints.get(i);
                polyLine.lineTo((float)p.getX(),(float)p.getY());
            }
            polyLine.setWindingRule(GeneralPath.WIND_NON_ZERO);
            //polyLine.closePath();
            return polyLine;
        }
        
        private void handleSelection(MouseEvent me) {
            initialX = me.getX();
            initialY = me.getY();
            Shape shp = model.getObstacles().whoIsHit(new Rectangle2D.Float(me.getX()-1.5f,me.getY()-1.5f,3, 3));
            
            model.getObstacles().setSelectedShape(shp);
            
            draw(null);
        }
        
        private void handleDragSelection(MouseEvent me) {
            int deltaX =  me.getX()-initialX;
            int deltaY =  me.getY() - initialY;
            initialX = me.getX();
            initialY = me.getY();
            Shape shp =  model.getObstacles().getSelectedShape();
            if (shp==null) return;
            
            AffineTransform aft = AffineTransform.getTranslateInstance(deltaX,deltaY);
            Shape transformedShape = aft.createTransformedShape(shp);
            model.getObstacles().removeObstacle(shp);
            
            model.getObstacles().addObstacle(transformedShape);
            model.getObstacles().setSelectedShape(transformedShape);
//
//            Rectangle rect = (Rectangle)shp;
//            rect.translate(deltaX, deltaY);
            draw(null);
//            if(shp instanceof Line2D){
//                Line2D line = (Line2D)shp;
//                line.setLine(line.getX1()+deltaX, line.getY1()+deltaY, line.getX2()+deltaX, line.getY2()+deltaY);
//            }else if( shp instanceof GeneralPath){
//
//            }
        }

        private void handlePheromoneLevel(int x, int y) {
            int level = model.getPherormoneMatrix().getPheromoneLevel(x,y);
            view.setPheromoneLevel(x,y,level);
        }
        
    }
    
    private class KeyHandler extends KeyAdapter{
        public void keyPressed(KeyEvent e) {
            if(e.getKeyCode()==KeyEvent.VK_DELETE){
                Shape shp = model.getObstacles().getSelectedShape();
                if (shp!=null){
                    model.getObstacles().removeObstacle(shp);
                    draw(null);
                }
            }
        }
        
    }
    
}
