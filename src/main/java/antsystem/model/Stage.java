/*
 * Stage.java
 *
 * Created on 2 de Julho de 2006, 20:30
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package antsystem.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JPanel;

/**
 *
 * @author danilo
 */
public abstract class Stage extends JPanel implements Runnable{
    protected int width;
    protected int height;
    private Color bgColor;
    protected SpriteCache spriteCache;
    volatile protected boolean running = false;
    
    private int numOfUpdatesBeforePainting=1;
    private long updates = 0;
    //para o desenho off-screen
    protected Image secondBufferImage;
    public static Graphics doubleBufferdGraphics;
    
    protected Thread animator;
    
    private List<StageListener> stageListeners = new ArrayList<StageListener>();
    
    long  afterTime, diffTime;
    long  beforeTime = System.nanoTime();
    /** Creates a new instance of Stage */
    public Stage(int width, int height, Color color) {
        setBackground(color);
        bgColor = color;
        setOpaque(false);
        //setWidth(width);
        //setHeight(height);
        setPreferredSize(new Dimension(width, height));
        
        spriteCache = new SpriteCache("/resources");
        
    }
    
//    public int getWidth() {
//        return width;
//    }
    
    public void setWidth(int width) {
        this.width = width;
    }
    
//    public int getHeight() {
//        return height;
//    }
    
    public void setHeight(int height) {
        this.height = height;
    }
    
    public Color getBgColor() {
        return bgColor;
    }
    
    public void setBgColor(Color bgColor) {
        this.bgColor = bgColor;
    }
    
    public void play(){
        running = true;
        start();
    }
    public void stop(){
        running=false;
        animator=null;
    }
    
    public void run(){
        
        
        while (running && isVisible()){
            step();
        }
        
    }
    
    public void step(){
        setUpdates(getUpdates() + 1);
        //System.out.println("calling updateWorld: " + updates);
        updateWorld();
        if(running){
            //apenas pinta quando der o numero de updates configurado
            if(getUpdates() % getNumOfUpdatesBeforePainting()==0){
                paintWorld();
                paintScreen();
            }
        }else{ /*!running, � entao um step individual e deve sempre ser pintado*/
            paintWorld();
            paintScreen();
        }
        diffTime = System.nanoTime() - beforeTime;
        fireStageListeners(diffTime, getUpdates());
            try {

               Thread.sleep(1);
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        beforeTime = System.nanoTime();
    }
    protected void start(){
        
        animator = new Thread(this, "Stage");
        animator.start( );
        
        
        
    }
    
    private void fireStageListeners(long spentTime, long updates){
        //System.out.println("" + spentTime);
        //spentTime = spentTime/1000000;
        double fps = 1000;
        if (spentTime > 0)
            fps = (double)(1000*1000000/(spentTime+10));
        
        for (StageListener listener : stageListeners){
            listener.currentFPSValue(fps);
            listener.totalNumberOfUpates(updates);
        }
    }
    
    public void addStageListener(StageListener fpsListener){
        stageListeners.add(fpsListener);
    }
    public void removeStageListener(StageListener fpsListener){
        stageListeners.remove(fpsListener);
    }
    abstract protected void updateWorld();
    
    abstract protected void paintWorld();
    
    abstract protected void paintScreen();
    
    
    
    public Image getSecondBufferImage() {
        return secondBufferImage;
    }
    
    public void setSecondBufferImage(Image secondBufferImage) {
        this.secondBufferImage = secondBufferImage;
    }
    
    public Graphics getDoubleBufferdGraphics() {
        return doubleBufferdGraphics;
    }
    
    public void setDoubleBufferdGraphics(Graphics doubleBufferdGraphics) {
        this.doubleBufferdGraphics = doubleBufferdGraphics;
    }
    /*
     * Called when this component is added to some frame and therefore can
     * create DoubleBufferGraphics
     */
    public void addNotify() {
        super.addNotify();
        createBufferStrategy();
        paintWorld();
        paintScreen();
        repaint();
    }
    
    public void createBufferStrategy() {
        System.out.println("antes do createBuffer");
        setSecondBufferImage(createImage( getPreferredSize().width,getPreferredSize().height ));
        setDoubleBufferdGraphics(getSecondBufferImage().getGraphics());
        System.out.println("depois do createBuffer");
    }
    
    public int getNumOfUpdatesBeforePainting() {
        return numOfUpdatesBeforePainting;
    }
    
    public void setNumOfUpdatesBeforePainting(int numOfUpdatesBeforePainting) {
        this.numOfUpdatesBeforePainting = numOfUpdatesBeforePainting;
    }

    public long getUpdates() {
        return updates;
    }

    public void setUpdates(long updates) {
        this.updates = updates;
    }
    
}
