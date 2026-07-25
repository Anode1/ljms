package org.is.html.actions;

import java.awt.event.ActionEvent;
import java.awt.Frame;
import javax.swing.AbstractAction;
import javax.swing.JEditorPane;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.StyledEditorKit;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.MutableAttributeSet;

import org.is.html.Resources;

/**
 * Generic Insertion Action
 * Usage:
 * <pre>
 * String string = new String("<img src = \""+"buttonImage2.gif\""+" align = \"middle\">");
 * InsertActionBase action = new InsertActionBase("insert-foo", string, HTML.Tag.HEAD, new SimpleAttributeSet());
 * </pre>
 *
 */
public class InsertActionBase extends StyledEditorKit.StyledTextAction{

  private HTML.Tag tag;
  private SimpleAttributeSet attrSet;
  private String text;

  public InsertActionBase(String name, HTML.Tag tag, SimpleAttributeSet attrSet){

    super(name);
    this.tag = tag;
    this.attrSet = attrSet;
  }

  public InsertActionBase(String name, String text, HTML.Tag tag, SimpleAttributeSet attrSet){

    this(name, tag, attrSet);
    this.text=text;
  }

	public void actionPerformed(ActionEvent e){

	  JEditorPane editor = getEditor(e);
	  if (editor == null)return;

    if (text == null){
      SimpleAttributeSet set = new SimpleAttributeSet();
      set.addAttribute(tag, attrSet);
      setCharacterAttributes(editor, set, false);
    }
    else{
      HTMLDocument doc = (HTMLDocument)editor.getDocument();

      StyledEditorKit k = getStyledEditorKit(editor);
      MutableAttributeSet inputAttributes = k.getInputAttributes();
      SimpleAttributeSet set = new SimpleAttributeSet();
      set.addAttribute(tag, attrSet);

      inputAttributes.addAttributes(set);

      ActionEvent evt = new ActionEvent(editor, 0, text);
      (new DefaultEditorKit.InsertContentAction()).actionPerformed(evt);

      int pos=editor.getSelectionStart();
      String oldText = editor.getSelectedText();

      try {
        if(oldText!=null)doc.remove(pos, oldText.length());
      }
      catch (Exception ex) {
        System.out.println("InsertActionBase::reloadInsert:" + e + ":pos " + pos);
      }
      try {
        doc.insertString(pos, oldText, inputAttributes  );
      }
      catch (Exception ex) {
        System.out.println("InsertActionBase::reloadInsert:" + e + ":pos " + pos);
      }

      try{
        editor.select(pos, pos+oldText.length());
		    editor.repaint();
		    editor.requestFocus();
      }
      catch(Exception ex){
        System.err.println("InsertActionBase::reloadInsert:error during selection:"+e);
      }
    }

	}


}

