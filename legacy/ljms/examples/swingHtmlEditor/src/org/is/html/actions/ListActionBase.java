package org.is.html.actions;

import java.awt.event.ActionEvent;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.*;

import org.is.html.GHTMLEditor;
import org.is.html.GHTMLDocument;
import org.is.html.GHTMLEditorKit;

/**
 * Base class for list actions (OL/UL)
 *
 * @since jdk1.2
 */
public abstract class ListActionBase extends GHTMLEditorKit.InsertHTMLTextAction{

  private String _html;
  private HTML.Tag tag; //UL or OL

	public ListActionBase(String nm, String htmlWithLi, String _html, HTML.Tag tag){

    super(nm, htmlWithLi, HTML.Tag.TD, tag, HTML.Tag.BODY, tag);
    this._html=_html;
    this.tag=tag;
	}

	public void actionPerformed(ActionEvent e){

    GHTMLEditor editor=GHTMLEditAction.retreiveGEditor();
    if(editor==null)return;

    try{

      GHTMLDocument doc=editor.getGHTMLDocument();

      Position fromPos=doc.createPosition(editor.getSelectionStart());
      Position toPos=doc.createPosition(editor.getSelectionEnd());
      int begin = fromPos.getOffset();
      int end = toPos.getOffset();

      if(begin==end){
//
        Element paragraph = doc.getParagraphElement(begin);
		    if(paragraph.getParentElement() != null) {
		      parentTag = (HTML.Tag)paragraph.getParentElement().getAttributes().getAttribute(StyleConstants.NameAttribute);
		      super.actionPerformed(e);
		    }
//
        begin=fromPos.getOffset();//-1;
        end=toPos.getOffset();
        editor.setSelectionStart(begin);
        editor.setSelectionEnd(end>0?end-1:0);
        editor.reload();
        return;
      }

     // Element newTagElement=doc.wrapSelection(attrs, charElement, begin, end);

      //System.out.println(editor.getSelectionStart()+" "+editor.getSelectionEnd());

      DefaultStyledDocument.ElementSpec es[]=doc.getElementSpecs(begin, end-begin);

      editor.replaceSelection("");

      Element paragraph = doc.getParagraphElement(begin);
      super.html=_html;

      if(paragraph.getParentElement() != null) {
	      parentTag = (HTML.Tag)paragraph.getParentElement().getAttributes().getAttribute(StyleConstants.NameAttribute);
	      super.actionPerformed(e);
	    }

      //editor.getGHTMLKit().insertHTMLSafe(doc, begin, html, 0, 0, tag);

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
    catch(Exception ex){
      System.err.println("ListActionBase::actionPerformed:"+ex);
    }
	}


}

