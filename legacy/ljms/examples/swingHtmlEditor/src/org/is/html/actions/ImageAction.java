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

import org.is.html.*;

/**
 * Action modifying properties of an image
 *
 * @since jdk1.2
 */
public class ImageAction extends GHTMLEditAction{

  public ImageAction(){

    super("image-action");
  }

	public void action(ActionEvent ae){

    int pos = editor.getSelectionStart();
    String oldText = editor.getSelectedText();

    String newLink=(String)JOptionPane.showInputDialog(TopManager.getGHTMLEditor().getFrame(), "Enter URL for image", "src", JOptionPane.QUESTION_MESSAGE, null, null, "");
    if(newLink==null)newLink="";

    SimpleAttributeSet hrefAttr = new SimpleAttributeSet();
    hrefAttr.addAttribute(HTML.Attribute.SRC, newLink);

    SimpleAttributeSet newAttr = new SimpleAttributeSet();
    newAttr.addAttribute(HTML.Tag.IMG, hrefAttr);

    Element elem = doc.getCharacterElement(pos);


  //NOT FINISHED!

	}


}

