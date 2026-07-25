package org.is.html.actions;

import java.awt.event.ActionEvent;
import java.awt.Frame;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.Action;
import javax.swing.text.*;
import javax.swing.text.html.*;

import org.is.html.*;

/**
 * Action making link
 *
 * @since jdk1.2 
 */
public class LinkAction extends GHTMLEditAction{

  public LinkAction(){

    super("link-action");
    //putValue(Action.SHORT_DESCRIPTION, "Link aaaaaaaaaaaaaaa");
  }

	public void action(ActionEvent ae){

    int begin = editor.getSelectionStart();
    int end=editor.getSelectionEnd();
    Element charEl = doc.getCharacterElement(begin);

    Element wrappingA=HTMLUtils.getWrappingElementByAttribute(charEl, HTML.Tag.A);
    if(wrappingA==null){ //handle existing link
      insertLink(editor, doc, charEl, begin, end);
    }
    else{               //handle newly created link
      editLink(editor, doc, wrappingA/*, charEl*/, begin);
    }
    editor.select(begin,end);
	}

  private void insertLink(GHTMLEditor editor, HTMLDocument doc, Element charEl, int pos, int end){

  try{
    AttributeSet oldAttrSet = charEl.getAttributes();
    //System.out.println("oldAttrSet:"+oldAttrSet);

    Element endElem=doc.getCharacterElement(end-1);
    if(endElem!=charEl && pos!=end){
       Object[] options = {"OK","CANCEL"};
       int response=JOptionPane.showOptionDialog(null, "You have selected more than one style: if you will click OK, subsequent editing of the link will require editing of links of all paragraphs", "Warning", JOptionPane.DEFAULT_OPTION,
                                    JOptionPane.WARNING_MESSAGE, null, options, options[0]);
	     switch(response){
          case JOptionPane.YES_OPTION:
            break;
	        case JOptionPane.NO_OPTION:
            return;
       }
    }

    String initValue=(String)oldAttrSet.getAttribute(HTML.Attribute.HREF);
    if (initValue == null)initValue="";
    String newLink=(String)JOptionPane.showInputDialog(TopManager.getGHTMLEditor().getFrame(), "Enter URL for link", Resources.getString("Dialogs.link"), JOptionPane.QUESTION_MESSAGE, null, null, initValue);
    if(newLink==null || newLink.trim().equals("")){
       return; //no link - do nothing
    }

    SimpleAttributeSet hrefAttr = new SimpleAttributeSet();
    hrefAttr.addAttribute(HTML.Attribute.HREF, newLink);

    SimpleAttributeSet newAttr = new SimpleAttributeSet();
 //    newAttr.addAttributes(oldAttrSet); //remove this if we want to preserve paragraphs //!

    if (oldAttrSet.getAttribute(HTML.Tag.A) != null) {
       newAttr.removeAttribute(HTML.Tag.A);
    }
    else{
       newAttr.addAttribute(HTML.Tag.A, hrefAttr);
    }

    String oldText = editor.getSelectedText();
    if(oldText==null)oldText=newLink;

    if(end>pos){
      doc.setCharacterAttributes(pos, end-pos, newAttr, false);
    }
    else{
      newAttr.addAttributes(oldAttrSet);
      doc.insertString(pos, oldText, newAttr);
    }

    int textLength=oldText.length();
    if(textLength>0)editor.select(pos, textLength);
  }
  catch(Exception ex){
    System.err.println("InsertLinkAction:"+ex);
  }
  finally{
	  editor.repaint();
	  editor.requestFocus();
  }

  }

  private void editLink(GHTMLEditor editor, HTMLDocument doc, Element wrappingA, /*Element charEl,*/ int pos){

    AttributeSet oldAttrSet = (AttributeSet)wrappingA.getAttributes();
    AttributeSet a=(AttributeSet)oldAttrSet.getAttribute(HTML.Tag.A);

    SimpleAttributeSet hrefAttr = new SimpleAttributeSet();

    SimpleAttributeSet newAttr = new SimpleAttributeSet();
    newAttr.addAttributes(oldAttrSet);

    String initValue=(String)a.getAttribute(HTML.Attribute.HREF);
    if (initValue == null)initValue="";
    String newLink=(String)JOptionPane.showInputDialog(TopManager.getGHTMLEditor().getFrame(), "Enter URL for link", Resources.getString("Dialogs.link"), JOptionPane.QUESTION_MESSAGE, null, null, initValue);

    if(newLink==null || newLink.trim().equals("")){
       //no link - remove the old one
       newAttr.removeAttribute(HTML.Tag.A);
    }
    else{
       hrefAttr.addAttribute(HTML.Attribute.HREF, newLink);
       newAttr.addAttribute(HTML.Tag.A, hrefAttr);
    }

    int begin=wrappingA.getStartOffset();
    int end=wrappingA.getEndOffset();

    doc.setCharacterAttributes(begin, end-begin, newAttr, true);
/*

    try {
       doc.insertString(pos, oldText, newAttr);
    }
    catch (BadLocationException ble) {
       System.out.println("GAbstractAction::reloadInsert:" + ble + ":pos " + pos);
    }

    try{
       if(oldText!=null)editor.select(pos, pos+oldText.length());
		   editor.repaint();
		   editor.requestFocus();
    }
    catch(Exception ex){
       System.err.println("InsertLinkAction::error during selection:"+ex);
    }
  */
  }
     /*
  private void reload(GHTMLEditor editor, HTMLDocument doc, int pos, SimpleAttributeSet newAttr){

    String oldText = editor.getSelectedText();


    editor.replaceSelection("");

    try {
       doc.insertString(pos, oldText, newAttr);
    }
    catch (BadLocationException ble) {
       System.out.println("GAbstractAction::reloadInsert:" + ble + ":pos " + pos);
    }

    try{
       if(oldText!=null)editor.select(pos, pos+oldText.length());
		   editor.repaint();
		   editor.requestFocus();
    }
    catch(Exception ex){
       System.err.println("InsertLinkAction::error during selection:"+ex);
    }

  }
 */


}

