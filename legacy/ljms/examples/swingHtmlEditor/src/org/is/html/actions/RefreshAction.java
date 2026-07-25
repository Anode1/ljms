package org.is.html.actions;

import java.awt.event.ActionEvent;

import org.is.html.GHTMLEditor;

/**
 * Action forcing reload of the content in the pane (used mostly for debugging)
 */
public class RefreshAction extends GHTMLEditAction{

	public RefreshAction() {

	   super("refresh-action");
	}

  public void action(ActionEvent e) {

    editor.reload();  //do not put this into InvokeLater (it is already been wrapped in thread in editor)
	}


}
