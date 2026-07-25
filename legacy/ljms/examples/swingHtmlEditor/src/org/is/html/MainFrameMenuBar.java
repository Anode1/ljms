package org.is.html;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.*;
import java.awt.event.*;

import javax.swing.text.html.HTMLEditorKit;

import org.is.html.actions.Actions;
import org.is.util.Utils;
import org.is.html.dialogs.PreferencesDialog;

/**
 * Main Menu for this application.
 *
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
         (new PreferencesDialog()).setVisible(true);
	    }
    });

    menu.addSeparator();

	  menuItem = menu.add(Resources.getString("menu.exit"));
	  menuItem.addActionListener(Actions.getAction("exit"));

    ///////////////
	  menu=add(new JMenu("Edit"));
      menuItem = menu.add(new JMenuItem(Resources.getString("menu.undo")));
      menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_MASK, false));
      menuItem.setEnabled(false);
	    menuItem.addActionListener(new ActionListener(){
	      public void actionPerformed(ActionEvent e) {

	      }
      });
      menuItem = menu.add(new JMenuItem(Resources.getString("menu.redo")));
      menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, KeyEvent.CTRL_MASK | KeyEvent.SHIFT_MASK, false));
      menuItem.setEnabled(false);
	    menuItem.addActionListener(new ActionListener(){
	      public void actionPerformed(ActionEvent e) {

	      }
      });

    menu.addSeparator();

	  menuItem = menu.add(Resources.getString("menu.cut"));
    menuItem.addActionListener(Actions.getAction("cut-to-clipboard"));
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, KeyEvent.CTRL_MASK, false));

	  menuItem = menu.add(Resources.getString("menu.copy"));
    menuItem.addActionListener(Actions.getAction("copy-to-clipboard"));
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_MASK, false));

	  menuItem = menu.add(Resources.getString("menu.paste"));
    menuItem.addActionListener(Actions.getAction("paste-from-clipboard"));
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_MASK, false));

	  menuItem = menu.add(Resources.getString("menu.delete"));
    menuItem.setEnabled(false);

	  menuItem = menu.add(Resources.getString("menu.selectAll"));
    menuItem.addActionListener(Actions.getAction("select-all"));
    menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_MASK, false));

    menu.addSeparator();

	  menuItem = menu.add(Resources.getString("menu.linkTo"));
    menuItem.addActionListener(Actions.getAction("link-action"));


	  menuItem = menu.add(Resources.getString("menu.removeLink"));
    menuItem.setEnabled(false);

    menu.addSeparator();

	  menuItem = menu.add(Resources.getString("menu.find"));
    menuItem.setEnabled(false);

	  menuItem = menu.add(Resources.getString("menu.fn"));
    menuItem.setEnabled(false);

	  menuItem = menu.add(Resources.getString("menu.fp"));
    menuItem.setEnabled(false);
	  //menuItem.addActionListener(new CutAction(frame));

	  menuItem = menu.add(Resources.getString("menu.rfn"));
    menuItem.setEnabled(false);
	  //menuItem.addActionListener(new CutAction(frame));

    menu.addSeparator();

    JMenu menu1=new JMenu(Resources.getString("menu.properties"));
	  menuItem = menu.add(menu1);
	    menuItem = menu1.add(Resources.getString("menu.frame"));
      menuItem.setEnabled(false);

	    menuItem = menu1.add(Resources.getString("menu.page"));
      menuItem.setEnabled(false);

    ///////////////
    menu = add(new JMenu(Resources.getString("menu.view")));
	  menuItem = menu.add(Resources.getString("menu.toolbars"));
    menuItem.setEnabled(false);
    menu.addSeparator();
	  menuItem = menu.add(Resources.getString("menu.src"));
    menuItem.addActionListener(Actions.getAction("show-source"));
	  menuItem = menu.add("Document Model");
    menuItem.addActionListener(Actions.getAction("show-model"));


    ///////////////
    menu = add(new JMenu(Resources.getString("menu.insert")));

	    menuItem = menu.add(Resources.getString("menu.spacer"));
      menuItem.setEnabled(false);

	//    menuItem = menu.add(Resources.getString("menu.pre"));
  //    menuItem.addActionListener(Actions.getAction("InsertPre"));

	    menuItem = menu.add(Resources.getString("menu.break"));
      menuItem.addActionListener(Actions.getAction("break-action"));

	    menuItem = menu.add(Resources.getString("menu.hr"));
      menuItem.addActionListener(Actions.getAction("InsertHR"));

      menu1 = add(new JMenu("List"));
 	    menuItem = menu.add(menu1);  //add to Insert
  	    menuItem = menu1.add(Resources.getString("menu.ul"));
        menuItem.addActionListener(Actions.getAction("ul"));

	      menuItem = menu1.add(Resources.getString("menu.ol"));
        menuItem.addActionListener(Actions.getAction("ol"));

      menu.addSeparator();

      menu1 = add(new JMenu("Form Item"));
      menu1.setEnabled(false);
	    menuItem = menu.add(menu1);
	      menuItem = menu1.add("Text Box");
	      menuItem = menu1.add("Text Area");
	      menuItem = menu1.add("Check Box");
	      menuItem = menu1.add("Radio Button");
	      menuItem = menu1.add("Drop Down List");
	      menuItem = menu1.add("List Box");
        menu.addSeparator();

	      menuItem = menu.add("Submit Button");
        menuItem.setEnabled(false);

	      menuItem = menu.add("Image Submit Button");
        menuItem.setEnabled(false);

      menu.addSeparator();

	    menuItem = menu.add("Image");
      menuItem.addActionListener(Actions.getAction("insert-image"));

	    menuItem = menu.add("Table");
      menuItem.addActionListener(Actions.getAction("insert-table"));

	    menuItem = menu.add("Anchor");
      menuItem.setEnabled(false);

	    menuItem = menu.add("HTML Tag");
      menuItem.setEnabled(false);
    ///////////////
    menu = add(new JMenu("Style"));

	    menuItem = menu.add("Plain");
      menuItem.setEnabled(false);
      menu.addSeparator();

	    menuItem = menu.add("Bold");
      menuItem.addActionListener (Actions.getAction("font-bold"));

	    menuItem = menu.add("Italic");
      menuItem.addActionListener (Actions.getAction("font-italic"));

	    menuItem = menu.add(Resources.getString("menu.underline"));
      menuItem.addActionListener (Actions.getAction("font-underline"));

	    menuItem = menu.add("Superscript");
      menuItem.setEnabled(false);

	    menuItem = menu.add("Subscript");
      menuItem.setEnabled(false);

    menu.addSeparator();

    menu1=new JMenu("Font");
      menu.add(menu1);
	    menuItem = menu.add(menu1);

	    menuItem = menu1.add("SanSerif");
      menuItem.addActionListener (Actions.getAction("font-family-SansSerif"));

	    menuItem = menu1.add("Monospaced");
      menuItem.addActionListener (Actions.getAction("font-family-Monospaced"));

	    menuItem = menu1.add("Serif");
      menuItem.addActionListener (Actions.getAction("font-family-Serif"));

	    menuItem = menu1.add("Other");
      //add here runtime determination of all fonts available
      menuItem.setEnabled(false);

    menu1=new JMenu("Size");
	    menuItem = menu.add(menu1);
	    menuItem = menu1.add("8");
      menuItem.addActionListener (Actions.getAction("font-size-8"));
      menuItem = menu1.add("10");
      menuItem.addActionListener (Actions.getAction("font-size-10"));
      menuItem = menu1.add("12");
      menuItem.addActionListener (Actions.getAction("font-size-12"));
      menuItem = menu1.add("14");
      menuItem.addActionListener (Actions.getAction("font-size-14"));
      menuItem = menu1.add("16");
      menuItem.addActionListener (Actions.getAction("font-size-16"));
      menuItem = menu1.add("18");
      menuItem.addActionListener (Actions.getAction("font-size-18"));
      menuItem = menu1.add("24");
      menuItem.addActionListener (Actions.getAction("font-size-24"));
      menuItem = menu1.add("36");
      menuItem.addActionListener (Actions.getAction("font-size-36"));
      menuItem = menu1.add("48");
      menuItem.addActionListener (Actions.getAction("font-size-48"));
      menu1.addSeparator();
      menuItem = menu1.add("Increase"); menuItem.setEnabled(false);
      menuItem = menu1.add("Descrease"); menuItem.setEnabled(false);

    menu1=new JMenu("Color");
	    menuItem = menu.add(menu1);
	    menuItem = menu1.add("Text color");
      menuItem.addActionListener(Actions.getAction("fg-color"));

    ///////////////
    menu = add(new JMenu("Format"));
    menu.setEnabled(false);
    ///////////////
    menu = add(new JMenu("Frame"));
    menu.setEnabled(false);
    ///////////////

    menu = add(new JMenu("Help"));

    menuItem = menu.add(new JMenuItem("Help Topics"));
	  menuItem.addActionListener(Actions.getAction("help"));

    menu.addSeparator();

    menuItem = menu.add(new JMenuItem("About"));
	  menuItem.addActionListener(new ActionListener(){
	    public void actionPerformed(ActionEvent e) {
           javax.swing.JOptionPane.showMessageDialog(null,"<html><body><CENTER>HTML Editor<p>2000<br>Written by Vasili Gavrilov</CENTER></body></html>", "About", javax.swing.JOptionPane.PLAIN_MESSAGE, Images.getIcon("logo"));
	    }
    });

  }//constructMenuBar




}
