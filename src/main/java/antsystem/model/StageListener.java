/*
 * StageListener.java
 *
 * Created on 15 de Julho de 2006, 00:10
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package antsystem.model;

/**
 *
 * @author danilo
 */
public interface StageListener {
    public void currentFPSValue(double fps);
    public void totalNumberOfUpates(long l);
}
