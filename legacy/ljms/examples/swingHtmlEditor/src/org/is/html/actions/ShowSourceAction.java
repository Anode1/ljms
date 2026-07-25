package org.is.html.actions;

import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import java.awt.event.ActionEvent;

import org.is.io.FileUtils;
import org.is.html.dialogs.SourceDialog;
import org.is.html.*;

/**
 * Save into file action.
 * Used in application version of the editor
 *
 * @since jdk1.2
 */
public class ShowSourceAction extends GHTMLEditAction{

	public ShowSourceAction() {

	  super("show-source");
	}

  public void action(ActionEvent e){
    /*
   Runnable worker = new Runnable(){
      public void run(){
        try{                                     */
          SourceDialog se=TopManager.getDialog();
          se.setTitle(Resources.getString("Dialogs.src"));
          se.setText(TopManager.getGHTMLEditor().getText());
          /*
        }
        catch(Exception e){
          System.err.println("ShowSourceAction::"+e);
        }
      }
    };
    SwingUtilities.invokeLater(worker);  */
    java.awt.Frame f=TopManager.getGHTMLEditor().getFrame();
    f.validate();
    editor.requestFocus();
	}

}
