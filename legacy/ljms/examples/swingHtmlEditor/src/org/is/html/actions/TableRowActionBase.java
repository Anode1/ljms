package org.is.html.actions;

import java.awt.event.ActionEvent;
import javax.swing.text.Element;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;
import java.awt.Point;

import org.is.html.*;
import org.is.util.*;

/**
 * The base for table row actions 
 *
 * @since jdk1.2
 */
public abstract class TableRowActionBase extends GHTMLEditAction{

  public TableRowActionBase(String name){

    super(name);
  }

  /**
   * Method defined in subclasses which actually defined what to do with the element
   */
  public abstract void doWithRow(GHTMLDocument doc, Element te, Element currentRowElement, int index)throws Exception;

	public final void action(ActionEvent e){

    try{
      int pos=editor.getSelectionStart();

      Element te=HTMLUtils.getWrappingElementByTag(editor, HTML.Tag.TABLE);
      if(te==null){
        //System.err.println("TableRowActionBase::actionPerformed: we are already not in Table!");
        return;
      }
      
      Point p=TableUtils.getPositionInTable(doc.getCharacterElement(pos), te);
      if(p==null)return;

      Element currentRowElement=TableUtils.getRowElement(te, p.y);
      if(currentRowElement==null)return;

      doWithRow(doc, te, currentRowElement, p.y);  //if we have some intermediate children then we are not safe!

      //editor.reload();
      editor.requestFocus();
    }
    catch(Exception ex){
       System.err.println("TableRowActionBase:"+ex);
    }

  }



}

