package org.is.html.dialogs;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.util.*;
import java.awt.Frame;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import org.is.html.*;
import org.is.gui.IComboBox;

/**
 * Panel for selecting languages and Look&Feels. <p>
 * Old class - should be redesigned (written for another project)
 *
 * @since jdk1.2
 */
public class LangLafPanel extends JPanel{

  protected JLabel language_label, lookAndFeel_label;
  protected JPanel north_panel;
  protected IComboBox language_combo;
  protected Vector locales=new Vector();
  protected static IComboBox lookAndFeel_combo;
  protected UIManager.LookAndFeelInfo[] lfInfo;
  protected JButtonsBar buttonsBar;
  protected int dirtyLangSelection=0;
  protected int dirtyLAFSelection=0;

  public LangLafPanel() {

    GridBagLayout gridbag = new GridBagLayout();  //for north_panel
    GridBagLayout main_gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();

    setLayout(main_gridbag);

//north panel:
    north_panel=new JPanel();
    north_panel.setLayout(gridbag);
    c.gridx=0; c.gridy=0; c.fill=GridBagConstraints.BOTH; c.weightx=1; c.weighty=1; c.insets=new Insets(10,5,5,5); c.anchor=GridBagConstraints.CENTER;
    main_gridbag.setConstraints(north_panel, c);
    add(north_panel,c);

		language_label = new JLabel();
		language_label.setText(Resources.getString("Preferences.labels.Language_label"));

    c.gridx=0; c.gridy=0; c.fill = GridBagConstraints.NONE; c.weighty=0; c.weightx=0; c.insets=new Insets(5,5,5,5);
    gridbag.setConstraints(language_label, c);
		north_panel.add(language_label,c);

		language_combo = new IComboBox();
		language_combo.setMinimumSize(new Dimension(180,25));
		language_combo.setMaximumSize(new Dimension(180,25));
		language_combo.setPreferredSize(new Dimension(180,25));
    c.gridx=1; c.gridy=0; c.fill = GridBagConstraints.NONE; c.weighty=0; c.weightx=0; c.insets=new Insets(5,5,5,5);
    gridbag.setConstraints(language_combo, c);
		north_panel.add(language_combo,c);


    for(int i=0;i<TopManager.supportedLocales.length;i++){
        language_combo.sendData(TopManager.supportedLocales[i].getDisplayName());
    }
    language_combo.setSelectedIndex(0);

//try to select current user language:
    selectUserLanguage();

		lookAndFeel_label = new JLabel();
		lookAndFeel_label.setText(Resources.getString("Preferences.labels.Look_and_feel"));
		lookAndFeel_label.setBounds(150,100,200,25);
    c.gridx=0; c.gridy=1; c.fill = GridBagConstraints.NONE; c.weighty=0; c.weightx=0; c.insets=new Insets(5,5,5,5);
    gridbag.setConstraints(lookAndFeel_label, c);
		north_panel.add(lookAndFeel_label,c);

		lookAndFeel_combo = new IComboBox();
		lookAndFeel_combo.setMinimumSize(new Dimension(180,25));
		lookAndFeel_combo.setMaximumSize(new Dimension(180,25));
		lookAndFeel_combo.setPreferredSize(new Dimension(180,25));
    c.gridx=1; c.gridy=1; c.fill = GridBagConstraints.NONE; c.weighty=0; c.weightx=0; c.insets=new Insets(5,5,5,5);
    gridbag.setConstraints(lookAndFeel_combo, c);
		north_panel.add(lookAndFeel_combo,c);

    //populate look and feel combo taking installed lafs:
    lfInfo = UIManager.getInstalledLookAndFeels();
    for (int i = 0; i < lfInfo.length; i++){
			try {
				  String className = lfInfo[i].getClassName();
				  Class cl = Class.forName(className);
				  LookAndFeel lf = (LookAndFeel)cl.newInstance();
					if (!lf.isSupportedLookAndFeel()) {
					  className = null;
					}

				  if (className != null) {
					  lookAndFeel_combo.addItem(lfInfo[i].getName());
				  }
			}catch (Throwable t) {
				//System.out.println("Failed loading " + lfInfo[i].getClassName() + ":\n" + t);
			}
    }//for
    //lookAndFeel_combo.setSelectedIndex(0);

    setOpaque(true);

    buttonsBar=new JButtonsBar();
    c.gridx=0; c.gridy=1; c.gridwidth=2; c.fill=GridBagConstraints.HORIZONTAL; c.weightx=1; c.weighty=0.0; c.insets=new Insets(0,5,5,5); c.anchor=GridBagConstraints.NORTHWEST;
    main_gridbag.setConstraints(buttonsBar, c);
    add(buttonsBar,c);

		buttonsBar.apply_button.addActionListener(new java.awt.event.ActionListener(){
        		public void actionPerformed(java.awt.event.ActionEvent event){
               save();
		        }
    });

		buttonsBar.undo_button.addActionListener(new java.awt.event.ActionListener(){
        		public void actionPerformed(java.awt.event.ActionEvent event){

		        }
    });

		buttonsBar.redo_button.addActionListener(new java.awt.event.ActionListener(){
        		public void actionPerformed(java.awt.event.ActionEvent event){

		        }
    });

  }//constructor


/**
 * Select current user's default Language
 */
  public void selectUserLanguage(){
   /*
    String currentUserLanguage=TopManager.defaultLocale.getDisplayName();
    for(int i=0;i<language_combo.getItemCount();i++){   //try to find the same
         if(((String)language_combo.getItemAt(i)).equals(currentUserLanguage)){
            language_combo.setSelectedIndex(i);
            break;
         }
    }
    */
  }

/**
 * Saves current settings into DB
 */
  public void save(){

          //apply L&F:
     			LookAndFeel oldLF = UIManager.getLookAndFeel();

          int i=lookAndFeel_combo.getSelectedIndex();
          if(i==-1 || i>lfInfo.length)return;
          UIManager.LookAndFeelInfo lfi=lfInfo[i];
			    try {

				    UIManager.setLookAndFeel(lfi.getClassName());

				    // Switch all component UI's
            Frame f=TopManager.getGHTMLEditorFrame();
				    SwingUtilities.updateComponentTreeUI(f);
				    f.invalidate();
				    f.validate();
           /*
            Resources.props.put("look_and_feel",Integer.toString(i)); //save
            Resources.props.put("look_and_feel_class",lfi.getClassName());
            */
            //TopManager.getMainFrameMenuBar().updateUI();
				    f.repaint();
			    }catch (Throwable t) {
				    // Ignore all exceptions
				      System.err.println("Failed to install " + lfi.getName() + " L&F\n" + t);
				      try {
					        UIManager.setLookAndFeel(oldLF);
				      }catch (Throwable e) {
					        System.err.println("Failed to restore old L&F");
				      }
			    }


    }

}