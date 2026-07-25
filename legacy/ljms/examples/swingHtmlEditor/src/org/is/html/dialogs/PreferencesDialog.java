package org.is.html.dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.is.util.Utils;
import org.is.html.Resources;
import org.is.html.TopManager;

/**
 * Preferences Frame
 */
public class PreferencesDialog extends JDialog{

  public PreferencesDialog() {

    super(TopManager.getGHTMLEditorFrame() ,true);

    setTitle(Resources.getString("Preferences.Frame_Title"));

//get Dimensions of our frame from properties
    try{
       int f_width=Integer.parseInt(Resources.getString("PreferencesFrame.size.x"));
       int f_height=Integer.parseInt(Resources.getString("PreferencesFrame.size.y"));
	     this.setSize(new Dimension(f_width,f_height));
    }
    catch(NumberFormatException nfe){
      this.setSize(new Dimension(600,400));
    }

    //setResizable(false);

    Utils.setCentalizedLocation(this);
    getContentPane().setLayout(new BorderLayout());
    PreferencesPanelForFrame panel=new PreferencesPanelForFrame();
    getContentPane().add(panel,"Center");

  }

class PreferencesPanelForFrame extends JPanel{

  protected JTabbedPane tab_panel;
  
  public PreferencesPanelForFrame() {

    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
   	setLayout(gridbag);

		JLabel title = new JLabel();
		title.setText(Resources.getString("Preferences.Title"));
		title.setFont(new Font("SansSerif", Font.BOLD, 15));
    c.gridx=0; c.gridy=0; c.fill=GridBagConstraints.NONE;
    c.insets=new Insets(5,5,0,5); c.anchor=GridBagConstraints.NORTHEAST;
	  add(title,c);

    tab_panel=new JTabbedPane();

    c.gridx=0; c.gridy=1;
    c.insets=new Insets(0,5,5,5); c.anchor=GridBagConstraints.NORTHWEST;
	  c.fill = GridBagConstraints.BOTH;
    c.weighty=100; c.weightx=100;
    gridbag.setConstraints(tab_panel, c);
	  add(tab_panel,c);

    JPanel internalPanel=new JPanel();
    internalPanel.setLayout(new BorderLayout());
    LangLafPanel langLafPanel=new LangLafPanel();
    internalPanel.add(langLafPanel,"Center");
    tab_panel.addTab(Resources.getString("Preferences.Tab.Profile"),internalPanel);
    tab_panel.addTab("Settings",new JPanel());
    tab_panel.setEnabledAt(1,false);

    tab_panel.setSelectedIndex(0);    
  }
  
}

}
