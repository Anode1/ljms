package org.is.html.actions;

import java.awt.event.ActionEvent;

/**
 */
public class NewAction extends GHTMLEditAction{

	public NewAction() {

	  super("new-document");
	}

  public void action(ActionEvent e){

    editor.clearAllText();
    editor.setCaretPosition(0);
    editor.requestFocus();
	}


}
