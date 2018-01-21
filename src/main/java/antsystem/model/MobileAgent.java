/*
 * MobileAgent.java
 *
 * Created on 11 de Junho de 2006, 13:19
 *
 */

package antsystem.model;

/**
 *
 * @author Danilo N Costa
 */
public abstract class MobileAgent extends Thread{
    
    private long timeToSleep = 40;
    private boolean continuousWalking = false;
    /** Creates a new instance of MobileAgent */
    public MobileAgent() {
    }
    
    /**
     * Configura a taxa de atualiza�ao do Agente
     */
    public void setFramesPerSecond(int amount){
        timeToSleep = 1000/amount;
    }
    
    public void startWalking(){
        continuousWalking = true;
        super.start();
    }
    
    public void stopWalking(){
        continuousWalking = false;
        this.interrupt();
    }
    protected abstract void randomlyTurn();
    protected abstract void walk();
    
    public void run() {
        while(continuousWalking){
            randomlyTurn();
            walk();
            try {
                this.sleep(timeToSleep);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
    
    
}
