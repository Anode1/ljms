package org.is.html.actions;

import javax.swing.AbstractAction;
import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import java.awt.event.ActionEvent;

import org.is.html.HTMLUtils;

/**
 * Table removing action
 */
public class TableDeleteAction extends GHTMLEditAction{

	public TableDeleteAction() {

	  super("table-delete");
	}

	public final void action(ActionEvent e)throws Exception{

    Element te=HTMLUtils.getWrappingElementByTag(editor, HTML.Tag.TABLE);
    if(te==null)return;

    doc.removeElement(te);

    editor.reload();
  	//editor.repaint();
    //editor.requestFocus();
  }
}
