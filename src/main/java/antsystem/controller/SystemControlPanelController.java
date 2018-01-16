/*
 * SystemControlPanelController.java
 *
 * Created on 3 de Julho de 2006, 21:45
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package antsystem.controller;

import antsystem.model.Ant3;
import antsystem.gui.AntConfigurationPanel;
import antsystem.gui.MainFrame;
import antsystem.gui.SystemControlPanel;
import antsystem.model.Environment;
import antsystem.model.FoodSource;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JOptionPane;

/**
 *
 * @author danilo
 */
public class SystemControlPanelController implements ActionListener{
    
    private Environment environment;
    private SystemControlPanel view;
    private MainFrame mainFrame;
    
    private static int SETTING_NEST=1;
    private static int SETTING_FOODSOURCE=2;
    private static int NOTHING=0;
    private int whatIsBeingDone;
    private int totalAntsNumber = 0;
    
    private int amountOfFood = 0;
    /** Creates a new instance of SystemControlPanelController */
    public SystemControlPanelController(MainFrame mainFrame, SystemControlPanel view) {
        this.environment = mainFrame.getEnvironment();
        this.view = view;
        this.view.addActionListener(this);
        this.mainFrame = mainFrame;
        environment.addMouseListener(new MouseHandler());
    }
    
    public void actionPerformed(ActionEvent e) {
      
        if(e.getActionCommand().equals("nest")){
            handleSettingNest();
        } else if(e.getActionCommand().equals("food"))
            handleSettingSourceFood();
        else if(e.getActionCommand().equals("start")){
            handleStart();
        }else if(e.getActionCommand().equals("stop")){
            environment.stop();
        }else if(e.getActionCommand().equals("addAnts")){
            handleAddAnts();
        }else if(e.getActionCommand().equals("resize")){
            handleResize();
        }else if(e.getActionCommand().equals("step")){
            environment.step();
        }else if(e.getActionCommand().equals("reset")){
            handleReset();
        }
        
    }
    
    private void handleSettingNest(){
        whatIsBeingDone = (whatIsBeingDone==SETTING_NEST)?NOTHING:SETTING_NEST;
    }
    
    private void handleSettingSourceFood(){
        whatIsBeingDone = (whatIsBeingDone==SETTING_FOODSOURCE)?NOTHING:SETTING_FOODSOURCE;
        if(!(whatIsBeingDone==SETTING_FOODSOURCE))
            return;
        String s = (String)JOptionPane.showInputDialog("Food concentration?");
        if (s==null || s.trim().length()==0){
            cancelSourceFood();
        }
        
        try {
            int val = Integer.parseInt(s);
            if(val <=0)
                cancelSourceFood();
            else
                amountOfFood = val;
        } catch (Exception e){
            cancelSourceFood();
        }
        
        
    }
    private void cancelSourceFood(){
        whatIsBeingDone = NOTHING;
        view.setFoodButtonState(false);
        amountOfFood=0;
    }
    private void handleAddAnts(){
        int numOfAnts = 0;
        if( (null!=view.getNumOfAntsText()) && (!"".equals(view.getNumOfAntsText().trim()) )){
            
            numOfAnts = Integer.parseInt(view.getNumOfAntsText());
        }
        if (numOfAnts != 0){
            AntConfigurationPanel antConfig = mainFrame.getAntConfiguration();
            //environment.creatAnts(numOfAnts);
            for(int i=0; i<numOfAnts; i++){
                
                Ant3 ant = new Ant3(environment.getPherormoneMatrix(),2);
                
                ant.setNoTurnProbability(antConfig.getNoTurnProbability());
                ant.setClockwise_45_Probability(antConfig.getCW45Propability());
                ant.setClockwise_90_Probability(antConfig.getCW90Propability());
                ant.setClockwise_135_Probability(antConfig.getCW135Propability());
                ant.setClockwise_180_Probability(antConfig.getCW180Propability());
                ant.setCounterclockwise_45_Probability(antConfig.getCCW45Propability());
                ant.setCounterclockwise_90_Probability(antConfig.getCCW90Propability());
                ant.setCounterclockwise_135_Probability(antConfig.getCCW135Propability());
                ant.setNormalColor(antConfig.getAntNormalColor());
                ant.setCarryingFoodPheromoneDepositAmount(antConfig.getCarryingFoodDepositValue());
                ant.setCarryingFoodPheromoneThreshold(antConfig.getCarryingFoodThresholdValue());
                ant.setCarryingFoodPheromoneWeight(antConfig.getCarryingFoodWeightValue());
                ant.setPheromoneDepositAmount(antConfig.getNoFoodDepositValue());
                ant.setPheromoneWeight(antConfig.getNoFoodWeightValue());
                ant.setPheromoneThreshold(antConfig.getNoFoodThresholdValue());
                ant.setNormalColor(antConfig.getAntNormalColor());
                ant.setCarryingFoodColor(antConfig.getCarryingFoodColor());
                ant.setVelocity(antConfig.getVelocity());
                environment.addActor(ant);
            }
            environment.activeRepaint();
        }
        totalAntsNumber += numOfAnts;
        mainFrame.setNumOfAnts(Integer.toString(totalAntsNumber));
    }
    
    private void handleStart() {
        environment.setNumOfUpdatesBeforePainting(view.getNumOfUpdatesBeforePainting());
        environment.setEvaporationInterval(view.getPheromoneEvaporationInterval());
        environment.setEvaporationRate(view.getPheromoneEvaporationRate());
        environment.play();
    }
    
    private void handleResize() {
        int w = Integer.parseInt(view.getWidthText());
        int h = Integer.parseInt(view.getHeightText());
        environment.setPreferredSize(new Dimension(w,h));
        environment.constructBorders(w,h);
        //Its important to re-create the image used as a bufferimage
        environment.createBufferStrategy();
        environment.revalidate();
        environment.activeRepaint();
        environment.repaint();
    }
    
    private void handleReset(){
        environment.stop();
        
        environment.removeAllActors();
        environment.clearPheromoneMatrix();
        environment.setUpdates(0);
        totalAntsNumber=0;
        mainFrame.setNumOfAnts("0");
        mainFrame.setUpdates("0");
        environment.activeRepaint();
        
    }
    private class MouseHandler implements MouseListener{
        public void mouseClicked(MouseEvent e) {
            
            if(whatIsBeingDone==SETTING_NEST){
                
                environment.setNest(e.getPoint());
                view.setNestButtonState(false);
                view.allowAddingAnts();
                whatIsBeingDone=NOTHING;
            }else if(whatIsBeingDone==SETTING_FOODSOURCE){
                environment.addFoodSource(new FoodSource(e.getPoint(), amountOfFood));
                view.setFoodButtonState(false);
                whatIsBeingDone=NOTHING;
            }
        }
        
        public void mousePressed(MouseEvent e) {
            
        }
        
        public void mouseReleased(MouseEvent e) {
        }
        
        public void mouseEntered(MouseEvent e) {
            ((Component)e.getSource()).setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
        }
        
        public void mouseExited(MouseEvent e) {
        }
        
    }
}
