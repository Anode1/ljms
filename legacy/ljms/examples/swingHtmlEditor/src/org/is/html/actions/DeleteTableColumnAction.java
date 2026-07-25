package org.is.html.actions;

import javax.swing.text.Element;

import org.is.html.*;

/**
 * Action deleting column from table.
 *
 * @since jdk1.2
 */
public class DeleteTableColumnAction extends TableColumnActionBase{

  public DeleteTableColumnAction(){

    super("delete-table-column");
  }

  public void doWithCell(GHTMLDocument doc, Element currentColElement, int index)throws Exception{

    Element parent=currentColElement.getParentElement();
    if(parent.getElementCount()<2)return;

    doc.removeElement(parent, index);
    //doc.reload();
  }



}
