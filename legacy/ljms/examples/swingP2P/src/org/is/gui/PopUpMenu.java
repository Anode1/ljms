package org.is.gui;

import java.awt.Component;
import javax.swing.JPopupMenu;
import javax.swing.JMenuItem;
import javax.swing.JMenu;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

import org.is.gui.actions.Actions;

/**
 * Popup menu for MainPanel
 *
 * @version 1.0
 * @since jdk1.2
 */
public class PopUpMenu extends JPopupMenu{


  protected Component origin;

  public PopUpMenu(Component origin){

    super();

    this.origin=origin;

    JMenuItem menuItem;
/*
	  menuItem = add(new JMenuItem(Resources.getString("menu.cut")));
	  menuItem.addActionListener(Actions.getAction("cut-to-clipboard"));

	  menuItem = add(new JMenuItem(Resources.getString("menu.copy")));
	  menuItem.addActionListener(Actions.getAction("copy-to-clipboard"));

	  menuItem = add(new JMenuItem(Resources.getString("menu.paste")));
	  menuItem.addActionListener(Actions.getAction("paste-from-clipboard"));
*/
    addSeparator();




  }



}