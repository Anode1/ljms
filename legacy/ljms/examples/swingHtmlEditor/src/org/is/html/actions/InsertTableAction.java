package org.is.html.actions;

import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import javax.swing.text.html.*;
import javax.swing.text.*;
 
import org.is.html.*;
import org.is.html.actions.GHTMLEditAction;
import org.is.html.dialogs.EditTableDialog;

/**
 * Action inserting a table.
 *
 * @since jdk1.2 
 */
public class InsertTableAction extends GHTMLEditorKit.InsertHTMLTextAction{//InsertHTMLAction{

  public InsertTableAction(){

    super("insert-table", null, HTML.Tag.TD, HTML.Tag.TABLE, HTML.Tag.BODY, HTML.Tag.TABLE);
  }

	public void actionPerformed(ActionEvent e){

    GHTMLEditor editor=GHTMLEditAction.retreiveGEditor();
    if(editor==null)return;

    try{

      GHTMLDocument doc=editor.getGHTMLDocument();
      if(doc==null)return;

      int pos=editor.getCaretPosition();
      if(pos>doc.getLength())pos=doc.getLength();
      if(pos<1)pos=1;
      Position oldDotPos=doc.createPosition(pos-1);

      EditTableDialog dialog=new EditTableDialog(null);
      dialog.show();
      dialog.dispose(); //! reuse it! - do not dispose
      if(dialog.isCancelled()){
	  	  editor.requestFocus();
        return;
      }

      super.html=dialog.createTableFromGUI();

      Element paragraph = doc.getParagraphElement(pos);
		  if(paragraph.getParentElement() != null) {
		     parentTag = (HTML.Tag)paragraph.getParentElement().getAttributes().getAttribute(StyleConstants.NameAttribute);
		     super.actionPerformed(e);
		  }

    }
    catch(Exception ex){
      System.err.println("InsertTableAction::actionPerformed:"+ex);
    }
    //editor.requestFocus();
    editor.reload();
  }


}

