package org.is.html.actions;

import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import javax.swing.text.Element;
import javax.swing.JOptionPane;
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
 */
public class InsertImageAction extends GHTMLEditAction{

  public InsertImageAction(){

    super("insert-image");
  }

	public void action(ActionEvent ae){

    ImagePropDialog dialog=new ImagePropDialog();
    dialog.show();
    dialog.dispose(); //! reuse it! - do not dispose
    if(dialog.isCancelled()){
  		editor.requestFocus();    
      return;
    }

    //String newLink=(String)JOptionPane.showInputDialog(GAbstractAction.getMainFrame(), "Enter URL for image", "src", JOptionPane.QUESTION_MESSAGE, null, null, "");
    //if(newLink==null)newLink="";
          //  align = \""+dialog.getAlignment()+"\"
    String INSERT_HTML="<img src = \""+dialog.getUrl()+"\" width=\""+dialog.getImageWidth()+"\" height=\""+dialog.getImageHeight()+"\">";

    editor.replaceSelection("");
    int pos = editor.getSelectionStart();

    try {
        editor.getGHTMLKit().insertHTMLSafe(editor, doc, pos, INSERT_HTML, 0, 0, HTML.Tag.IMG);

        //
      //  editor.setCaretPosition(pos+1);
    }
    catch (Exception e) {
        System.out.println("InsertImageAction:"+e+"at pos:"+pos);
    }

		editor.requestFocus();

       /*
    String newLink=(String)JOptionPane.showInputDialog(GAbstractAction.getMainFrame(), "Enter URL for image", "src", JOptionPane.QUESTION_MESSAGE, null, null, "");
    if(newLink==null)newLink="";

    SimpleAttributeSet hrefAttr = new SimpleAttributeSet();
    hrefAttr.addAttribute(HTML.Attribute.SRC, newLink);

    SimpleAttributeSet newAttr = new SimpleAttributeSet();
    newAttr.addAttribute(HTML.Tag.IMG, hrefAttr);

    Element elem = doc.getCharacterElement(pos);

    try {
       doc.remove(pos, oldText.length());
    }
    catch (Exception e) {
       System.out.println("InsertImageAction::reloadInsert:" + e + ":pos " + pos);
    }
    try {
       //doc.insertString(pos, "<table border=1><tr><td></td></tr></table>", newAttr);
       doc.insertBeforeStart(elem,"<table border=1 bgcolor=\"#CCFFFF\"><tr><td></td></tr></table>");
       doc.insertAfterStart(elem,"<table border=1 bgcolor=\"#CCAAFF\"><tr><td></td></tr></table>");
       doc.insertBeforeEnd(elem,"<table border=1 bgcolor=\"#AAFFFF\"><tr><td></td></tr></table>");
       doc.insertAfterStart(elem,"<table border=1 bgcolor=\"#CCFFAA\"><tr><td></td></tr></table>");
    }
    catch (Exception e) {
       System.out.println("InsertImageAction::reloadInsert:" + e + ":pos " + pos);
    }

    try{
       editor.select(pos, pos+1);
		   editor.repaint();
		   editor.requestFocus();
    }
    catch(Exception e){
       System.err.println("InsertImageAction::error during selection:"+e);
    }
     */

	}


}

