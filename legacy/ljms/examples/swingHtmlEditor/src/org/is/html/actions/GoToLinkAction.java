package org.is.html.actions;

import java.awt.event.ActionEvent;
import java.awt.Frame;
import java.io.IOException;
import javax.swing.JOptionPane;
import javax.swing.Action;
import javax.swing.text.*;
import javax.swing.text.html.*;

import org.is.html.*;

/**
 * Action setting new Page in the editor
 *
 * @since jdk1.2 
 */
public class GoToLinkAction extends GHTMLEditAction{

  public GoToLinkAction(){

     super("go-to-link");
  }

	public void action(ActionEvent ae){

    String newLink=(String)JOptionPane.showInputDialog(TopManager.getGHTMLEditor().getFrame(), "Enter URL for link", Resources.getString("Dialogs.link"), JOptionPane.QUESTION_MESSAGE, null, null, "http://");
    if(newLink==null || newLink.trim().equals("")){
       return; //no link - do nothing
    }

    try{
      editor.setPage(newLink);
    }
    catch(IOException ie){
      System.err.println("GoToLinkAction::"+ie);
    }
  }




}

