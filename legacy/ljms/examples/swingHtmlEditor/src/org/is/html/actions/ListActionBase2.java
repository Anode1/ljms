package org.is.html.actions;

import java.awt.event.ActionEvent;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.*;

import org.is.html.GHTMLEditor;
import org.is.html.GHTMLDocument;

/**
 *
 * @since jdk1.2
 */
public abstract class ListActionBase2 extends GHTMLEditAction{

	public ListActionBase2(String nm){

	  super(nm);
	}

  protected abstract String getHTML();

  protected abstract String getHTMLWithLI();

  protected abstract HTML.Tag getTag();

	public void action(ActionEvent e)throws Exception{

      Position fromPos=doc.createPosition(editor.getSelectionStart());
      Position toPos=doc.createPosition(editor.getSelectionEnd());
      int begin = fromPos.getOffset();
      int end = toPos.getOffset();

      if(begin==end){
        editor.getGHTMLKit().insertHTMLSafe(editor, doc, begin, getHTMLWithLI(), 0, 0, getTag());
        begin=fromPos.getOffset();//-1;
        editor.setSelectionStart(begin);
        editor.setSelectionEnd(begin);
        editor.reload();
        return;
      }

     // Element newTagElement=doc.wrapSelection(attrs, charElement, begin, end);

      //System.out.println(editor.getSelectionStart()+" "+editor.getSelectionEnd());

      DefaultStyledDocument.ElementSpec es[]=doc.getElementSpecs(begin, end-begin);

      editor.replaceSelection("");

      editor.getGHTMLKit().insertHTML(doc, begin, getHTML(), 0, 0, getTag());

      //editor.setCaretPosition(begin+1);

      begin = fromPos.getOffset();

      doc.insert(begin, es);

      end = fromPos.getOffset();

      editor.setSelectionStart(begin);
      editor.setSelectionEnd(end);

      SimpleAttributeSet set = new SimpleAttributeSet();
      set.addAttribute(StyleConstants.NameAttribute, HTML.Tag.LI);
      doc.setParagraphAttributes(begin, end - begin - 1, set, false);

      editor.reload();

	}


}

