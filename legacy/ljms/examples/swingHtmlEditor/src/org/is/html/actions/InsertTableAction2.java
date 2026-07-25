package org.is.html.actions;

import java.util.Enumeration;
import java.awt.event.ActionEvent;
import javax.swing.ImageIcon;
import javax.swing.text.html.*;
import javax.swing.text.*;
import javax.swing.text.DefaultStyledDocument$ElementSpec;

import org.is.html.*;
import org.is.html.dialogs.EditTableDialog;

/**
 * Action inserting a table
 * <p>
 * This class is not used now - InsertTableAction has been used instead
 *
 * @since jdk1.2
 */
public class InsertTableAction2 extends GHTMLEditAction{

  public InsertTableAction2(){

    super("_insert-table");
  }

	public void action(ActionEvent e){

    EditTableDialog dialog=new EditTableDialog(null);
    dialog.show();
    dialog.dispose(); //! reuse it! - do not dispose
    if(dialog.isCancelled()){
  		editor.repaint();
	  	editor.requestFocus();
      return;
    }

    int pos = editor.getSelectionStart();
    int length=editor.getSelectionEnd()-pos;

    String html=dialog.createTableFromGUI();

    String oldText = editor.getSelectedText();


//
    //v
    //        Element elementNl = rootNl;//.getElement(i);
    //        AttributeSet attrNl =  elementNl.getAttributes();
    //        Style s = docNl.getLogicalStyle(elementNl.getStartOffset());
            //System.out.println("style at "+elementNl.getStartOffset()+": "+s/*.getAttribute(StyleConstants.FontConstants.FontSize)*/);
    //        StyleConstants.setLineSpacing(s, ((float) /*StyleConstants.getLineSpacing(s)*/lineHeight)/nlfLineHeight);
            //StyleConstants.setSpaceAbove(s, lineHeight - nlf.getSize());
            //System.out.println("\t\t\t => "+s/*.getAttribute(StyleConstants.FontConstants.FontSize)*/);
   //         docNl.setLogicalStyle(elementNl.getStartOffset(), s);

   //    System.out.println(Utils.attr2String(s));

/*
StyleContext context = StyleContext.getDefaultStyleContext();
MutableAttributeSet attr = new SimpleAttributeSet( );
Style def = context.getStyle( StyleContext.DEFAULT_STYLE );
attr = context.addStyle( "<table>", def );
attr.addAttribute( AbstractDocument.ParagraphElementName, "<table>" );
attr.addAttribute( AbstractDocument.ElementNameAttribute, "<table>" );

java.util.Vector parseBuffer = new java.util.Vector();
ElementSpec es;
es = new ElementSpec(attr,ElementSpec.StartTagType);
//es.setDirection(ElementSpec.OriginateDirection);
//parseBuffer.addElement(es);

String foo="FOO";
es = new ElementSpec(attr, ElementSpec.ContentType, foo.toCharArray(), pos, foo.length() );
//es.setDirection(ElementSpec.JoinPreviousDirection);
parseBuffer.addElement(es);

es = new ElementSpec( attr, ElementSpec.EndTagType);
//es.setDirection(ElementSpec.JoinPreviousDirection);
//parseBuffer.addElement(es);

ElementSpec [] buffer = new ElementSpec[parseBuffer.size()];
parseBuffer.copyInto(buffer);

doc.insertElSpec(pos, buffer);
 */
///

    editor.replaceSelection("");

    try {

      //System.out.println(Utils.attr2String(editor.getParagraphAttributes()));
    //SimpleAttributeSet hrefAttr = new SimpleAttributeSet();
    //hrefAttr.addAttribute(HTML.Attribute.HREF, newLink);

      Element ce=doc.getCharacterElement(pos);


      editor.getGHTMLKit().insertHTMLSafe(editor, doc, pos, html, 0, 0, HTML.Tag.TABLE);

    //  (new GHTMLEditorKit.InsertHTMLAction("_insert-table", html, HTML.Tag.BODY, HTML.Tag.TABLE)).actionPerformed(e); //new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "_insert-table")

      //  editor.getGHTMLKit().adjustSelection(editor,doc,pos,length);
      //  AbstractDocument.DefaultDocumentEvent de=new AbstractDocument.DefaultDocumentEvent(startPos.getOffset(), );


      //editor.setCaretPosition(startPos.getOffset());
    }
    catch (Exception ex) {
      System.out.println("InsertTableAction2:"+ex+"at pos:"+pos);
    }

    editor.reload();
//		editor.repaint();
//		editor.requestFocus();

  }




}

