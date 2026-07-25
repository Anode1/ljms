package org.is.html.actions;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import javax.swing.text.StyleConstants;
import javax.swing.text.*;
import javax.swing.text.html.HTML;

import org.is.html.*;
import org.is.util.*;

/**
 * The base for table column actions 
 *
 * @since jdk1.2
 */
public abstract class TableColumnActionBase extends GHTMLEditAction{

  public TableColumnActionBase(String name){

    super(name);
  }

  /**
   * Method defined in subclasses which actually defined what to do with cell elements
   */
  public abstract void doWithCell(GHTMLDocument doc, Element currentColElement, int index)throws Exception;


	public final void action(ActionEvent e)throws Exception{

      Element te=HTMLUtils.getWrappingElementByTag(editor, HTML.Tag.TABLE);
      if(te==null){
        //System.err.println("TableColumnActionBase::actionPerformed: we are already not in Table!");
        return;
      }

      int pos=editor.getSelectionStart();

      Point p=TableUtils.getPositionInTable(doc.getCharacterElement(pos), te);
      if(p==null)return;

      Dimension tableDim=TableUtils.getTableDimensions(te);

      // Element parent=currentColElement.getParentElement();

      AbstractDocument.BranchElement bte=(AbstractDocument.BranchElement)te;
      Enumeration brothers=bte.children();

      for(Enumeration enum = bte.children() ; enum.hasMoreElements() ;) {

          Element currentRowElement=(Element)enum.nextElement();

          if(currentRowElement==null){
            System.err.println("TableColumnActionBase::currentRowElement is null?");
            continue;
          }
          if(currentRowElement.getAttributes().getAttribute(StyleConstants.NameAttribute)!=HTML.Tag.TR){
            System.err.println("TableColumnActionBase::currentRowElement is not TR?");
            continue;
          }

          Element currentColElement=currentRowElement.getElement(p.x);
          if(currentColElement==null){
            System.err.println("TableColumnActionBase::currentColElement is null?");
            continue;
          }
          HTML.Tag cellTag=(HTML.Tag)currentColElement.getAttributes().getAttribute(StyleConstants.NameAttribute);
          if(cellTag!=HTML.Tag.TD && cellTag!=HTML.Tag.TH){
            System.err.println("TableColumnActionBase::currentColElement is not TD?");
            continue;
          }

          doWithCell(doc, currentColElement, p.x); //do something with it (defined in subclasses)

      }//for (rows)

      //editor.reload();
  		editor.repaint();
	  	editor.requestFocus();

  }//actionPerformed


}

