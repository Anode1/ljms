package org.is.html.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.text.html.HTML;
import javax.swing.text.Element;

/**
 * HTML BR element insertion element
 *
 * @since jdk1.2
 */
public class BreakAction extends GHTMLEditAction{

	public BreakAction(){

		super("break-action"); 
	}

	public void action(ActionEvent e)throws Exception{

    int pos = editor.getSelectionStart();

    //System.out.println("pos="+pos);

    editor.getGHTMLKit().insertHTMLSafe(editor, doc, pos, "<BR>", 0, 0, HTML.Tag.BR);

    /*
    Element charEl=doc.getCharacterElement(pos);
    doc.insertHTML2(charEl.getParentElement(), pos, "<BR>");
    */

    editor.setCaretPosition(pos+1);
    
		editor.repaint();
		editor.requestFocus();
	}




}

