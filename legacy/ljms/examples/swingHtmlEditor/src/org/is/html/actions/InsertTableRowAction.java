package org.is.html.actions;

import javax.swing.text.Element;
import java.awt.Dimension;

import org.is.html.*;
import org.is.util.*;

/**
 * Action inserting a row into table.
 *
 * @since jdk1.2
 */
public class InsertTableRowAction extends TableRowActionBase{

  public InsertTableRowAction(){

    super("insert-table-row");
  }

  public void doWithRow(GHTMLDocument doc, Element te, Element currentRowElement, int index)throws Exception{

    Dimension tableDim=TableUtils.getTableDimensions(te);

    StringBuffer htmlb=new StringBuffer("<tr>");
    for(int j=0; j<tableDim.width; j++){
      htmlb.append("<td>&nbsp;</td>");
    }
    htmlb.append("</td>");
    String html=htmlb.toString();

    //System.out.println(html);

    doc.insertAfterEnd(currentRowElement, html);
  }


}

