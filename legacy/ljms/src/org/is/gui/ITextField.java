package org.is.gui;

import javax.swing.*;
import java.awt.event.*;
import java.awt.Dimension;

/**
 * JTextField having IInterface (see IInterface documentation).
 *
 * @since jdk1.2
 */
public class ITextField extends JTextField implements IInterface{

    public ITextField(){

       this(16);
    }

    public ITextField(int cols){

       super(cols);
       /*
       setPreferredSize(new Dimension(250,24));
       setMaximumSize(new Dimension(800,34));
       setMinimumSize(new Dimension(20,14));
       */
       //addFocusListener(this);
    }


    public boolean userInputIsValid(){

        return true;
    }


    public void sendData(String string){
        setText(string);
    }


    public String getData(){

       if(getText()==null)return null;
       return getText().trim();
    }



}
