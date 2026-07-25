package org.is.html.dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.is.html.*;
import org.is.util.Utils;

/**
 * Help Frame
 */
public class HelpDialog extends JDialog{

  public HelpDialog() {

    super(TopManager.getGHTMLEditor().getFrame(), false);
    setTitle("Help window");

//get Dimensions of our frame from properties
    try{
       int f_width=500;//Integer.parseInt(Resources.getString("Help.size.x"));
       int f_height=300;//Integer.parseInt(Resources.getString("Help.size.y"));
	     this.setSize(new Dimension(f_width,f_height));
    }
    catch(NumberFormatException nfe){this.setSize(new Dimension(600,400));}

    //setResizable(false);

    Utils.setCentalizedLocation(this);
    getContentPane().setLayout(new BorderLayout());

  }


}
