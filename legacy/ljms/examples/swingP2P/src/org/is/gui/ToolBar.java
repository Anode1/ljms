package org.is.gui;

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

    add(str2button("import"));
    add(str2button("export"));
    add(Box.createHorizontalStrut(5));

    add(str2button("undo"));

    add(Box.createHorizontalStrut(5));

    add(str2button("help"));

	  add(Box.createHorizontalGlue());

    add(Box.createHorizontalStrut(5));

    /*
    StatusIcon si=new StatusIcon();
    add(si);
    */

  }

}
