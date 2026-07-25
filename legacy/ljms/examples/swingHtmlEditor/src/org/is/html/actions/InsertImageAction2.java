package org.is.html.actions;

import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.text.html.*;
import javax.swing.AbstractAction;
import javax.swing.text.*;

import org.is.html.Resources;
import org.is.html.GHTMLEditor;
import org.is.html.GHTMLDocument;
import org.is.html.GHTMLEditorKit;
import org.is.html.dialogs.ImagePropDialog;

/**
 * Action inserting an image.
 * This class is more consistent than currently working
 * but due to Sun bug throws EmptyStackException
 *
 */
public class InsertImageAction2 extends GHTMLEditorKit.InsertHTMLTextAction{

  public InsertImageAction2(){

    super("insert-image", null, HTML.Tag.TD, HTML.Tag.IMG, HTML.Tag.BODY, HTML.Tag.IMG);
  }

	public void actionPerformed(ActionEvent ae){

    GHTMLEditor editor=GHTMLEditAction.retreiveGEditor();
    if(editor==null)return;

    try{

      GHTMLDocument doc=editor.getGHTMLDocument();
      if(doc==null)return;

      ImagePropDialog dialog=new ImagePropDialog();
      dialog.show();
      dialog.dispose(); //! reuse it! - do not dispose
      if(dialog.isCancelled())return;

      //String newLink=(String)JOptionPane.showInputDialog(GAbstractAction.getMainFrame(), "Enter URL for image", "src", JOptionPane.QUESTION_MESSAGE, null, null, "");
      //if(newLink==null)newLink="";
          //  align = \""+dialog.getAlignment()+"\"
      String INSERT_HTML="<img src = \""+dialog.getUrl()+"\" width=\""+dialog.getImageWidth()+"\" height=\""+dialog.getImageHeight()+"\">";

      super.html=INSERT_HTML;

      editor.replaceSelection("");
      int pos=editor.getCaretPosition();

      Element paragraph = doc.getParagraphElement(pos);
      if(paragraph.getParentElement() != null) {

	      parentTag = (HTML.Tag)paragraph.getParentElement().getAttributes().getAttribute(StyleConstants.NameAttribute);
	      super.actionPerformed(ae);
	    }


      editor.reload();

    }
    catch (Exception e) {
        System.out.println("InsertImageAction:"+e);
    }

	}


}

