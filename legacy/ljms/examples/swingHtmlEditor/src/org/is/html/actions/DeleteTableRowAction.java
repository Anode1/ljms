package org.is.html.actions;

import javax.swing.text.Element;
import java.awt.Point;

import org.is.html.*;

/**
 * Action deleting row in the table.
 *
 * @since jdk1.2
 */
public class DeleteTableRowAction extends TableRowActionBase{

  public DeleteTableRowAction(){

    super("delete-table-row");
  }

  public void doWithRow(GHTMLDocument doc, Element te, Element currentRowElement, int index)throws Exception{

    Element parent=currentRowElement.getParentElement();
    if(parent.getElementCount()<2)return;
        
    doc.removeElement(parent, index);
    //doc.reload();    
  }


}

