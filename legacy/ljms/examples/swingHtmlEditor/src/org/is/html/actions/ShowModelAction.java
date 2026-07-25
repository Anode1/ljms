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
public class ShowModelAction extends GHTMLEditAction{

	public ShowModelAction() {

	  super("show-model");
	}

  public void action(ActionEvent e){
  /*
    SwingUtilities.invokeLater( new Runnable(){
      public void run() {
      */
        SourceDialog se=TopManager.getDialog();
        se.setTitle("Model");
        se.setText(ModelDisplay.showModel(TopManager.getGHTMLEditor().getGHTMLDocument()));
     /* }
    });

    TopManager.getMainFrame().repaint();
    */
    java.awt.Frame f=TopManager.getGHTMLEditor().getFrame();
    f.validate();
    editor.requestFocus();
	}


}
