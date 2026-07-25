package org.is.html.actions;

import java.awt.event.ActionEvent;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTML;
import javax.swing.JEditorPane;

import org.is.html.GHTMLEditor;
import org.is.html.GHTMLDocument;
import org.is.html.GHTMLEditorKit;

/**
 */
public class InsertOrderedListItemAction extends GHTMLEditorKit.InsertHTMLTextAction{

  final static String INSERT_OL_HTML="<ol><li><p></p></li></ol>";

	public InsertOrderedListItemAction(){

	  super("InsertOrderedListItem", INSERT_OL_HTML, HTML.Tag.OL, HTML.Tag.LI);
	}

	public void actionPerformed(ActionEvent e){

	  JEditorPane editor = getEditor(e);
    if(editor==null)return;

    super.actionPerformed(e);

		editor.repaint();
		editor.requestFocus();

	}




}

