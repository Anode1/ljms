package org.is.html.actions;

import java.awt.event.ActionEvent;

/**
 *
 */
public class HelpAction extends GHTMLEditAction{

	public HelpAction() {

	  super("help");
	}

  public void action(ActionEvent e){

    javax.swing.JOptionPane.showMessageDialog(null, "Help is not implemented yet");
    editor.requestFocus();
	}


}
