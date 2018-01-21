/*
 * Main.java
 *
 * Created on 25 de Setembro de 2005, 16:51
 *
 */

package antsystem;

import antsystem.controller.DrawOptionPanelController;
import antsystem.controller.MainFrameController;
import antsystem.controller.SystemControlPanelController;
import antsystem.gui.MainFrame;
import antsystem.model.Environment;

import java.awt.Color;
import javax.swing.UIManager;

/**
 *
 * @author Danilo N. Costa
 */
public class Main {
    
    /** Creates a new instance of Main */
    public Main() {
        
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        try {
            //UIManager.setLookAndFeel(new com.jgoodies.looks.plastic.PlasticXPLookAndFeel());
            //UIManager.setLookAndFeel(new NapkinLookAndFeel());
            //UIManager.setLookAndFeel(new SyntheticaStandardLookAndFeel());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        MainFrame mainFrame = new MainFrame("DNC AntSystem");
        Environment environment = new Environment(800,600,Color.WHITE);
        MainFrameController mfc = new MainFrameController(environment,mainFrame);
        SystemControlPanelController controlPanelController = new SystemControlPanelController(mainFrame, mainFrame.getSystemControlPanel());
        
        DrawOptionPanelController dopController = new DrawOptionPanelController(mainFrame.getDrawOptionPanel(), environment);
        mfc.setDrawOptionPaneController(dopController);
        
        
        //mainFrame.setEnvironment(environment);
        mainFrame.pack();
        mainFrame.setVisible(true);
//        JButton testButton = new JButton("teste");
//        testButton.addMouseListener(new MouseListener() {
//            public void mouseClicked(MouseEvent e) {
//            }
//            public void mouseEntered(MouseEvent e) {
//                System.out.println("estrou");
//            }
//            public void mouseExited(MouseEvent e) {
//            }
//            public void mousePressed(MouseEvent e) {
//            }
//            public void mouseReleased(MouseEvent e) {
//            }
//        });
//        environment.add(testButton, BorderLayout.WEST);
//        //environment.doLayout();
//        environment.validate();
//        //environment.revalidate();
//        //environment.repaint();
//        System.out.println("testButton: " + testButton.isVisible());
//        System.out.println("testButton: " + testButton.getSize());
        
        
        
        
        
    }
    
}

