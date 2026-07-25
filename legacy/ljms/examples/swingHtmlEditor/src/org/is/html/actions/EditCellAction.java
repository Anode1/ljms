package org.is.html.actions;

import java.util.Enumeration;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import javax.swing.text.html.*;
import javax.swing.text.Element;
import javax.swing.text.Caret;
import javax.swing.text.DefaultStyledDocument$ElementSpec;

import org.is.html.*;
import org.is.html.dialogs.EditCellDialog;

/**
 * Action editing a cell in a table
 *
 * @since jdk1.2 
 */
public class EditCellAction extends GHTMLEditAction{

  private Element te;

  public EditCellAction(Element te){

    super("edit-cell-action"); //name been set just for consistency
    this.te=te;
  }

	public void action(ActionEvent e){

    Caret caret=editor.getCaret();
    int pos=caret.getDot();

    Element ce=TableUtils.getWrappingTD(doc.getCharacterElement(pos));
    if(ce==null)return; //just to be sure

    EditCellDialog dialog=new EditCellDialog(te, ce, doc);
    dialog.show();
    dialog.dispose(); //! reuse it! - do not dispose
    if(!dialog.isCancelled()){
      dialog.modifyCellAttributes(doc);
    }

  	editor.repaint();
		editor.requestFocus();
  }




}

