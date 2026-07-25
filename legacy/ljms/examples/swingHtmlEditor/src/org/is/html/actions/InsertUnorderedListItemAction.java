package org.is.html.actions;

import java.awt.event.ActionEvent;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.JEditorPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

import org.is.html.GHTMLEditorKit;

/**
 *
 * @since jdk1.2
 */
public class InsertUnorderedListItemAction extends GHTMLEditorKit.InsertHTMLTextAction{

  final static String INSERT_UL_HTML="<ul><li><p></p></li></ul>";

	public InsertUnorderedListItemAction(){

	  super("InsertUnorderedListItem", INSERT_UL_HTML, HTML.Tag.UL, HTML.Tag.LI);
	}

	public void actionPerformed(ActionEvent e){

	  JEditorPane editor = getEditor(e);
    if(editor==null)return;

    super.actionPerformed(e);

    editor.repaint();
	  editor.requestFocus();
	}

}

