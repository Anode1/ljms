package org.is.gui;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.*;
import java.awt.event.*;

import org.is.gui.actions.Actions;
import org.is.gui.actions.ExitAction;
import org.is.util.Utils;

/**
 * Main application menu
 *
 * @version 1.0
 * @since jdk1.2
 */
public class MainFrameMenuBar extends JMenuBar{

  final private JFrame frame;

  public MainFrameMenuBar(JFrame f){

    super();

    this.frame=f;

    JMenu menu=add(new JMenu(Resources.getString("menu.file")));

    JMenuItem menuItem = menu.add(new JMenuItem(Resources.getString("menu.preferences")));
	  menuItem.addActionListener(new ActionListener(){
	    public void actionPerformed(ActionEvent e) {
         //(new PreferencesDialog()).setVisible(true);
	    }
    });

    menu.addSeparator();

	  menuItem = menu.add(Resources.getString("menu.exit"));     
	  menuItem.addActionListener(new ExitAction());

    menu.addSeparator();

    menu = add(new JMenu("Help"));

    menuItem = menu.add(new JMenuItem("Help Topics"));
	  menuItem.addActionListener(new ActionListener(){
	    public void actionPerformed(ActionEvent e) {
         javax.swing.JOptionPane.showMessageDialog(null, "Help is not implemented yet");
	    }
    });

    menu.addSeparator();

    menuItem = menu.add(new JMenuItem("About"));
	  menuItem.addActionListener(new ActionListener(){
	    public void actionPerformed(ActionEvent e) {
         JOptionPane.showMessageDialog(null, "<html><body><center>P2P Portal<br>2001<br>SrcPortal Inc.</center></body></html>", "About", JOptionPane.PLAIN_MESSAGE, Images.getIcon("/images/amof15.jpg"));
         //(new org.is.gui.dialogs.About(frame)).show();
	    }
    });

  }




}
