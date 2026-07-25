package org.is.html;

import javax.swing.JButton;
import javax.swing.Box;

import org.is.html.actions.Actions;

/**
 * ToolBar bar for MainFrame
 *
 * @since jdk1.2
 */
public class ToolBar2 extends ToolBarBase{

  public ToolBar2(){

    JButton b;

    add(new FontsComboBox());
    add(new FontSizeComboBox());    

    addSeparator();

    add(str2button("bold"));
    add(str2button("italic"));
    b=(JButton)add(str2button("underline"));
    add(Box.createHorizontalStrut(5));
    add(str2button("fg"));
    add(Box.createHorizontalStrut(5));
    add(str2button("left"));
    add(str2button("center"));
    add(str2button("right"));

    addSeparator();

    add(str2button("ul"));
    add(str2button("ol"));

    addSeparator();

    add(str2button("help"));

	  add(Box.createHorizontalGlue());
  }

}