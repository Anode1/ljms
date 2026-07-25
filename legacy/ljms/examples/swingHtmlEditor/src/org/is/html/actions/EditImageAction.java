package org.is.html.actions;

import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import javax.swing.text.AttributeSet;
import javax.swing.text.html.HTML;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTMLDocument;
import javax.swing.AbstractAction;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.html.HTMLEditorKit;

import org.is.html.Resources;
import org.is.html.GHTMLEditor;
import org.is.html.dialogs.ImagePropDialog;

/**
 * Action inserting an image
 *
 * @since jdk1.2 
 */
public class EditImageAction extends GHTMLEditAction{

  public EditImageAction(){

    super("edit-image");
  }

	public void action(ActionEvent ae){

    ImagePropDialog dialog=new ImagePropDialog();
    dialog.show();
    dialog.dispose(); //! reuse it! - do not dispose
    if(dialog.isCancelled())return;

    String INSERT_HTML="<img src = \""+dialog.getUrl()+"\" width=\""+dialog.getImageWidth()+"\" height=\""+dialog.getImageHeight()+"\">";

    int pos = editor.getSelectionStart();

    editor.replaceSelection("");

    try {
        editor.getGHTMLKit().insertHTMLSafe(editor, doc, pos, INSERT_HTML, 0, 0, HTML.Tag.IMG);

      //  editor.setCaretPosition(pos+1);
    }
    catch (Exception e) {
        System.out.println("InsertImageAction:"+e+"at pos:"+pos);
    }

		editor.repaint();
		editor.requestFocus();

 	}


}

