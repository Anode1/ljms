package org.is.html.actions;

import javax.swing.text.Element;

import org.is.html.*;

/**
 * Action inserting a column into table.
 *
 * @since jdk1.2
 */
public class InsertTableColumnAction extends TableColumnActionBase{

  public InsertTableColumnAction(){

    super("insert-table-column");
  }

  public void doWithCell(GHTMLDocument doc, Element currentColElement, int index)throws Exception{

    String html="<td>&nbsp;</td>";

    doc.insertAfterEnd(currentColElement, html);
  }



}

