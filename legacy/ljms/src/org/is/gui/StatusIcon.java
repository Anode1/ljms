package org.is.gui;

import javax.swing.*;
import java.awt.Dimension;
import java.awt.*;

/**
 * Status icon.
 * Not used now
 *
 * @since jdk1.2
 */
public class StatusIcon extends JButton{

  protected ColoredOval l1=new ColoredOval(Color.red);
  protected ColoredOval l2=new ColoredOval(Color.green);

  public StatusIcon() {

    //setBackground(Color.black);
    //this.setBorderPainted(false);
    this.setFocusPainted(false);
    this.setSelected(true);
    setEnabled(false);
    
    Dimension dim=new Dimension(24,24);
    setPreferredSize(dim);
    setMinimumSize(dim);
    setMaximumSize(dim);

    setStatus(false);
    instance=this;
  }

  public float getAlignmentY() { return 0.5f; }  

  public void setStatus(boolean started){

    if(started){
       setIcon(l2);
    }
    else{
       setIcon(l1);
    }
    repaint();
  }

  //convenience instance:
  private static StatusIcon instance;

  public static void connected(boolean s){
    if(instance!=null)instance.setStatus(s);
  }

  public static void trig(){
    if(instance==null)return;
    if(instance.getIcon()==instance.l1)instance.setIcon(instance.l2);
    else instance.setIcon(instance.l1);

  }


}