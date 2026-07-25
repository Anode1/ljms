package org.is.html.actions;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.html.HTML;
import javax.swing.text.Element;
import javax.swing.text.Caret;

import org.is.html.*;

/**
 * Enter key pressed action.,p>
 * This is not used now: look into the code - why.
 * If the solution for proper <p></p> insertion will be found, it's better to
 * remove old-fashioned jdk1.1 keyEvent handlers from GHTMLEditor not to have
 * it as a KeyEventListener (all other Key events are handled in Actions been
 * created through KeyMap)
 *
 * @since jdk1.2
 */
public class EnterKeyAction extends GHTMLEditAction{

	public EnterKeyAction(){

		super("EnterKeyAction");
	}

	public void action(ActionEvent e) throws Exception{

    Caret caret = editor.getCaret();
    int dot = caret.getDot();
    Element charE=doc.getCharacterElement(dot);

    if(HTMLUtils.firstInElement(charE, dot, HTML.Tag.UL)){
       Element el=HTMLUtils.getWrappingElementByTag(charE, HTML.Tag.UL);
       doc.insertBeforeStart(el,"<p></p>");
       editor.reload();
       //System.out.println("first in UL");
       return;
    }
    else if(HTMLUtils.firstInElement(charE, dot, HTML.Tag.OL)){
       Element el=HTMLUtils.getWrappingElementByTag(charE, HTML.Tag.OL);
       doc.insertBeforeStart(el,"<p></p>");
       //System.out.println("first in OL");
       editor.reload();
       return;
    }
    else if(HTMLUtils.firstInElement(charE, dot, HTML.Tag.TABLE)){
       Element el=HTMLUtils.getWrappingElementByTag(charE, HTML.Tag.TABLE);
       doc.insertBeforeStart(el,"<p></p>");
       Actions.fireAction(this,"break-action");
       editor.reload();
       //System.out.println("first in Table");
       return;
    }

    if(HTMLUtils.isWrappedInTag(editor, HTML.Tag.UL)){
       Actions.fireAction(this,"InsertUnorderedListItem");
    }
    else if(HTMLUtils.isWrappedInTag(editor, HTML.Tag.OL)){
       Actions.fireAction(this,"InsertOrderedListItem");
    }
    else if(HTMLUtils.isWrappedInTag(editor, HTML.Tag.TD)){
       Actions.fireAction(this,"break-action");
    }
    else{  //default behaviour
           //because of the problem here we don't use this class but
           //rather use old-fashioned jdk1.1 key event handlers
           //in Editor which is KeyEventListener
       editor.replaceSelection("\n");

    }


	}




}

