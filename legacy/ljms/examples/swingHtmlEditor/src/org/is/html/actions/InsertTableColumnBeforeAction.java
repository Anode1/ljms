package org.is.html.actions;

import javax.swing.text.Element;

import org.is.html.*;

/**
 * Action inserting a column into table.
 *
 * @since jdk1.2
 */
public class InsertTableColumnBeforeAction extends TableColumnActionBase{

  private boolean before;

  public InsertTableColumnBeforeAction(){

    super("insert-table-column-before");
  }

  public void doWithCell(GHTMLDocument doc, Element currentColElement, int index)throws Exception{

    String html="<td>&nbsp;</td>";

    doc.insertBeforeStart(currentColElement, html);
  }



}

