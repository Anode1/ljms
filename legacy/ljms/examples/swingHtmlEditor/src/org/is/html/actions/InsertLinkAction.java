package org.is.html.actions;

import java.awt.event.ActionEvent;
import java.awt.Frame;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.text.*;
import javax.swing.text.html.*;

import org.is.html.*;

/**
 * Action making link
 */
public class InsertLinkAction extends GHTMLEditAction{

  public InsertLinkAction(){

    super("_link-action");
  }

	public void action(ActionEvent ae)throws Exception{

    int pos = editor.getSelectionStart();
    Element charEl = doc.getCharacterElement(pos);
   //System.out.println("charEl:"+charEl);

    Element wrappingA=HTMLUtils.getWrappingElementByAttribute(charEl, HTML.Tag.A);
    if(wrappingA==null){ //handle existing link
      //System.out.println("Wrapping A Element:"+Utils.element2String(wrappingA));
      //handle as character attributes:

    }
    else{               //handle newly created link
      //System.out.println("A=null");
     //  AttributeSet a=(SimpleAttributeSet)e.getAttributes().getAttribute(HTML.Tag.A);

     //  return;
    }

    AttributeSet oldAttrSet = charEl.getAttributes();
    //System.out.println("oldAttrSet:"+oldAttrSet);

    String initValue=(String)oldAttrSet.getAttribute(HTML.Attribute.HREF);
    if (initValue == null)initValue="";
    String newLink=(String)JOptionPane.showInputDialog(TopManager.getGHTMLEditor().getFrame(), "Enter URL for link", Resources.getString("Dialogs.link"), JOptionPane.QUESTION_MESSAGE, null, null, initValue);
    if(newLink==null)newLink="";

    SimpleAttributeSet hrefAttr = new SimpleAttributeSet();
    hrefAttr.addAttribute(HTML.Attribute.HREF, newLink);

    SimpleAttributeSet newAttr = new SimpleAttributeSet();
    newAttr.addAttributes(oldAttrSet);


    if (oldAttrSet.getAttribute(HTML.Tag.A) != null) {
       newAttr.removeAttribute(HTML.Tag.A);
    }
    else{
       newAttr.addAttribute(HTML.Tag.A, hrefAttr);
    }

/*
    if (oldAttrSet.getAttribute(HTML.Tag.A) == null) {
       newAttr.addAttribute(HTML.Tag.A, hrefAttr);
    } else {
       newAttr.removeAttribute(HTML.Tag.A);
    }
*/

    reload(editor, doc, pos, newAttr);

	}

  private void reload(GHTMLEditor editor, HTMLDocument doc, int pos, SimpleAttributeSet newAttr)throws Exception{

    String oldText = editor.getSelectedText();

    editor.replaceSelection("");

    doc.insertString(pos, oldText, newAttr);

    if(oldText!=null)editor.select(pos, pos+oldText.length());
		editor.repaint();
		editor.requestFocus();

  }



}

