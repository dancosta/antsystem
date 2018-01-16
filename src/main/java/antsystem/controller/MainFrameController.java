/*
 * MainFrameController.java
 *
 * Created on 15 de Julho de 2006, 12:16
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package antsystem.controller;

import antsystem.gui.MainFrame;
import antsystem.model.Environment;
import antsystem.model.StageListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author danilo
 */
public class MainFrameController implements StageListener, ChangeListener{
    
    private Environment environment;
    private MainFrame view;
    
    //used to disable it when the tabbedPane hides the drawOptionPane panel;
    private DrawOptionPanelController drawOptionPaneController;
    /** Creates a new instance of MainFrameController */
    public MainFrameController(final Environment env, final MainFrame view) {
        this.environment = env;
        this.view = view;
        
        init();
    }
    private void init(){
        view.getTabbedPane().addChangeListener(this);
        view.setEnvironment(environment);
        environment.addStageListener(this);
    }

    

    public DrawOptionPanelController getDrawOptionPaneController() {
        return drawOptionPaneController;
    }

    public void setDrawOptionPaneController(DrawOptionPanelController drawOptionPaneController) {
        this.drawOptionPaneController = drawOptionPaneController;
    }
    // change listener implementation
    // used to know when the tab in mainFrame is changed by the user
    public void stateChanged(ChangeEvent e) {
        if (view.getTabbedPane().getModel().getSelectedIndex() == 1 ){
            getDrawOptionPaneController().install();
        }else{
            getDrawOptionPaneController().uninstall();
        }
            
            
    }
    
    public void currentFPSValue(double fps) {
        view.setFPS("" + fps);
    }

    public void totalNumberOfUpates(long l) {
        view.setUpdates(Long.toString(l));
    }
}
