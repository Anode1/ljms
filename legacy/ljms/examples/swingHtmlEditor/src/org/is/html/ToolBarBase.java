package org.is.html;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.Box;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.Icon;
import java.awt.Dimension;
import java.awt.*;
import java.net.URL;
import java.util.MissingResourceException;
import java.awt.event.ActionListener;

import org.is.html.actions.Actions;

/**
 * Base for ToolBars
 *
 * @since jdk1.2
 */
public class ToolBarBase extends JToolBar{

  public ToolBarBase(){

    this.setFloatable(false);
  }


  public JButton str2button(String internalButtonStr) {

    JButton b = new JButton(Images.getIcon(internalButtonStr)){
       public float getAlignmentY() { return 0.5f; }
    };

    b.setRequestFocusEnabled(true);
    Dimension dim=new Dimension(24,24);
    b.setPreferredSize(dim);
    b.setMinimumSize(dim);
    b.setMaximumSize(dim);
   // b.setMargin(new Insets(1,1,1,1));

    //set action:
    try{
      String actionName=Resources.getString("action."+internalButtonStr);

	    Action a = Actions.getAction(actionName);
	    if (a != null) {
	      b.setActionCommand(actionName);
	      b.addActionListener(a);
	    }else{
	      b.setEnabled(false);
  	  }
    }
    catch(MissingResourceException e){
      b.setEnabled(false);
      //System.err.println("ToolBar::str2buttonBase: action name is not specified for button:"+internalButtonStr);
    }

    try{
      String tip = Resources.getString("tip."+internalButtonStr);
	    if (tip != null) {
	      b.setToolTipText(tip);
	    }
    }
    catch(Exception e){
      b.setToolTipText(" ");
      //System.err.println("ToolBar::str2buttonBase:tip string not found in Resources:"+e);
    }

    return b;
  }

  public static class ToggleListener implements ActionListener{

    JButton b;

    public ToggleListener(JButton button){
      this.b=b;
    }

    public void actionPerformed (ActionEvent e){

      boolean enabled = b.isEnabled();
      b.setEnabled(!enabled);
    }
  }

}