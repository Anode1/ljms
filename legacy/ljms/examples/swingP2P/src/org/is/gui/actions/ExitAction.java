package org.is.gui.actions;

import java.awt.event.ActionEvent;
import java.awt.Frame;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;

import org.is.gui.TopManager;

//import org.is.gui.Applet;

/**
 * Container of all application events
 *
 * @since jdk1.2
 */
public class ExitAction extends AbstractAction{

	public ExitAction() {

	   super("exit");
	}

  public void actionPerformed(ActionEvent e) {

     exit();
	}

  public void exit(){

     Frame f=TopManager.getMainFrame();

	   int response=JOptionPane.showConfirmDialog(f, "Do you really want to exit?");

     switch(response){

	     case JOptionPane.YES_OPTION:
            returnResult();
            //f.dispose();
            System.exit(0);
	     case JOptionPane.NO_OPTION:
            return;
       case JOptionPane.CANCEL_OPTION:
            return;
       case JOptionPane.CLOSED_OPTION:
            return;
     }

  }

  private void returnResult(){

     try{
        //String result=TopManager.getGHTMLEditor().getHTMLChunk();//getText();
        //Applet a=TopManager.getApplet();
        //if(a!=null)a.appletCallback(result); //if null - we are in application 
     }
     catch(Exception e){
        System.out.println("ExitAction::returnResult:"+e);
     }
  }

}
