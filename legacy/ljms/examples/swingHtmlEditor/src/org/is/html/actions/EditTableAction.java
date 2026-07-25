package org.is.html.actions;

import java.util.Enumeration;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import javax.swing.text.html.*;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.DefaultStyledDocument$ElementSpec;

import org.is.html.*;
import org.is.html.dialogs.EditTableDialog;

/**
 * Action editing a table
 *
 * @since jdk1.2 
 */
public class EditTableAction extends GHTMLEditAction{

  private Element element;

  public EditTableAction(Element element){

    super("edit-table-action"); //name been set just for consistency
    this.element=element;
  }

	public void action(ActionEvent e){

    EditTableDialog dialog=new EditTableDialog(element);
    dialog.show();
    dialog.dispose(); //! reuse it! - do not dispose
    if(dialog.isCancelled()){
  		editor.repaint();
	  	editor.requestFocus();
      return;
    }

    dialog.modifyTableAttributes(doc);   //!

  	editor.repaint();
		editor.requestFocus();
  }




}

