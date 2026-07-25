package org.is.html.actions;

import java.io.*;
import javax.swing.AbstractAction;
import java.awt.event.ActionEvent;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.Element;
import javax.swing.text.AbstractDocument;

import org.is.io.FileUtils;
import org.is.html.dialogs.SourceDialog;
import org.is.html.*;

/**
 * Save into file action.
 * Used in application version of the editor
 *
 * @since jdk1.2  
 */
public class DebugAction extends GHTMLEditAction{

	public DebugAction() {

	  super("debug-action");
	}

  public void action(ActionEvent evt){

    int pos = editor.getSelectionStart();

    Element charEl = doc.getCharacterElement(pos);

    SourceDialog se=TopManager.getDialog();
    se.setTitle("Parsing Tree");

    se.setText(GComponent.cloneTree(doc.getDefaultRootElement()).printPreorder());
   /*
    Element e=doc.getParagraphElement(pos);
    for(int i=0; i<2 && e!=null; i++){
      e=e.getParentElement();
    }

    AbstractDocument.AbstractElement parel=(AbstractDocument.AbstractElement)doc.getDefaultRootElement();

    ByteArrayOutputStream mem=new ByteArrayOutputStream();
    PrintStream pw=new PrintStream(mem, true);
    parel.dump(pw, 1);
    se.setText(mem.toString());
     */
    editor.repaint();
  }


}
