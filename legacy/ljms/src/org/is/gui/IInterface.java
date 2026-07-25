package org.is.gui;
import javax.swing.JFrame;

/**
 * Interface for icomponents
 *
 * @since jdk1.0
 */
public interface IInterface{

/**
 * Checks if user's input is valid
 */
    public boolean userInputIsValid();

/**
 * Sends data to the component (not used for now)
 */
    public void sendData(String data);   //not used for now

/**
 * Returns object from component
 */
    public String getData();



}