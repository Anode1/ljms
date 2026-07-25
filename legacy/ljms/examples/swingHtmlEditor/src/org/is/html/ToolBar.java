package org.is.html;

import javax.swing.JButton;
import javax.swing.Box;

/**
 * ToolBar bar for MainFrame
 *
 * @since jdk1.2
 */
public class ToolBar extends ToolBarBase{

  public ToolBar(){

    JButton b;

    add(str2button("new"));    
    add(str2button("import"));
    add(str2button("export"));
    add(Box.createHorizontalStrut(5));
    add(str2button("copy"));
    add(str2button("cut"));
    add(str2button("paste"));
    add(Box.createHorizontalStrut(5));
     /*
    (add(str2button("undo"))).setEnabled(false);
    (add(str2button("redo"))).setEnabled(false);
    addSeparator();
     */
    add(str2button("objects"));
    add(str2button("source"));

    addSeparator();

    add(str2button("linkto"));
 //   add(createButton());

    add(Box.createHorizontalStrut(5));
    add(str2button("hr"));
    add(str2button("br"));
    add(str2button("image"));
    //b=(JButton)add(str2button("photo")); b.setEnabled(false);
    add(Box.createHorizontalStrut(5));
    add(str2button("table"));
    add(str2button("row_insert_before"));
    add(str2button("row_insert"));
    add(str2button("column_insert_before"));    
    add(str2button("column_insert"));
    add(str2button("row_delete"));
    add(str2button("column_delete"));
    add(str2button("table_delete"));

	  add(Box.createHorizontalGlue());
  }

}
