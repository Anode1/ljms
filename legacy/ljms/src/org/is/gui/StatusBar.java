package org.is.gui;

import javax.swing.*;
import java.awt.Dimension;
import java.awt.*;

/**
 * Status bar for Main Frame
 *
 * @since jdk1.2
 */
public class StatusBar extends JPanel {

  protected String string;
  protected JLabel label;
  protected int height=18;

  public StatusBar() {

    label=new JLabel();
    label.setForeground(Color.black);
    label.setMinimumSize(new Dimension(20,height));
    label.setPreferredSize(new Dimension(500,height));
    label.setMaximumSize(new Dimension(1000,height));
    label.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
    setLayout(new BorderLayout(3,3));
    add("Center",label);

    setMinimumSize(new Dimension(30,30));
 		setBorder(BorderFactory.createLoweredBevelBorder());
    label.setText(" For Help, press F1");
  }


  public StatusBar(String newString){

      this();
      showStatus(newString);
  }

  public void showStatus(String newString){

      string=new String(newString);
      label.setText(string);
      repaint();
  }


}