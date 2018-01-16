/*
 * ActionSupport.java
 *
 * Created on 5 de Maio de 2006, 23:56
 *
 */

package antsystem.helper;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Danilo  Costa
 */
public class ActionSupport implements  ActionListener{
    private Component who;
    private List<ActionListener> listeners = new ArrayList<ActionListener>(); 
    /** Creates a new instance of ActionSupport */
    public ActionSupport(Component componet) {
        who = componet;
    }
    
    public void addActionListener(ActionListener listener){
        listeners.add(listener);
    }
    public void removeActionListener(ActionListener listener){
        listeners.remove(listener);
    }
    
    public void actionPerformed(ActionEvent e) {
        for (ActionListener listener : listeners){
            listener.actionPerformed(new ActionEvent(e.getSource(), ActionEvent.ACTION_PERFORMED, e.getActionCommand()));
        }
    }
    
    
}
