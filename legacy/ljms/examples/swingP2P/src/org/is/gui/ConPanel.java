package org.is.gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import java.awt.event.*;
import java.util.*;
import java.awt.*;

/**
 *
 * @version 1.0
 * @since jdk1.2
 */
public class ConPanel extends JPanel{

  public ConPanel(){

    setLayout(new BorderLayout());
    setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

    //JPanel yPanel=new JPanel();
    //yPanel.setLayout(new BoxLayout(yPanel, BoxLayout.Y_AXIS));

//north (table) panel:

    JPanel rcPanel=new JPanel();
    rcPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"Active Connections"));
    rcPanel.setMinimumSize(new Dimension(100,150));

    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
    rcPanel.setLayout(gridbag);

    ConTable conTable = new ConTable();
    c.gridx=0; c.gridy=0; c.gridwidth=3; c.gridheight=1; c.fill=GridBagConstraints.BOTH; c.weightx=1; c.weighty=1; c.insets=new Insets(5,5,5,5); c.anchor=GridBagConstraints.CENTER;
    gridbag.setConstraints(conTable, c);
    rcPanel.add(conTable,c);

    JButton disAllButton=new JButton("Disconnect All");
    c.gridx=0; c.gridy=1; c.gridwidth=1; c.gridheight=1; c.fill=GridBagConstraints.NONE; c.weightx=0; c.weighty=0; c.insets=new Insets(5,5,5,5); c.anchor=GridBagConstraints.WEST;
    gridbag.setConstraints(disAllButton, c);
    rcPanel.add(disAllButton,c);

    JButton disButton=new JButton("Disconnect");
    c.gridx=1; c.gridy=1; c.gridwidth=1; c.gridheight=1; c.fill=GridBagConstraints.NONE; c.weightx=0; c.weighty=0; c.insets=new Insets(5,5,5,5); c.anchor=GridBagConstraints.WEST;
    gridbag.setConstraints(disButton, c);
    rcPanel.add(disButton,c);

    //yPanel.add(rcPanel);

//connectWith panel:
    JPanel connectWithPanel=new JPanel();

    connectWithPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"Make new connection"));
    GridBagLayout gridbag2 = new GridBagLayout();
    connectWithPanel.setLayout(gridbag2);

    JLabel wl=new JLabel("Host");
    wl.setHorizontalAlignment(SwingConstants.RIGHT);
    c.gridx=0; c.gridy=0; c.gridwidth=1; c.gridheight=1; c.fill=GridBagConstraints.NONE; c.weightx=0; c.weighty=0; c.insets=new Insets(5,5,5,5); c.anchor=GridBagConstraints.EAST;
    gridbag2.setConstraints(wl, c);
    connectWithPanel.add(wl,c);

    JTextField hf=new JTextField();
    c.gridx=1; c.gridy=0; c.gridwidth=1; c.gridheight=1; c.fill=GridBagConstraints.HORIZONTAL; c.weightx=1; c.weighty=0; c.insets=new Insets(5,5,5,5); c.anchor=GridBagConstraints.WEST;
    gridbag2.setConstraints(hf, c);
    connectWithPanel.add(hf,c);

    JLabel pl=new JLabel("Port");
    pl.setHorizontalAlignment(SwingConstants.RIGHT);
    c.gridx=2; c.gridy=0; c.gridwidth=1; c.gridheight=1; c.fill=GridBagConstraints.NONE; c.weightx=0; c.weighty=0; c.insets=new Insets(5,5,5,5); c.anchor=GridBagConstraints.EAST;
    gridbag2.setConstraints(pl, c);
    connectWithPanel.add(pl,c);

    IntTextField pf=new IntTextField(1, 65536, 5);
    c.gridx=3; c.gridy=0; c.gridwidth=1; c.gridheight=1; c.fill=GridBagConstraints.NONE; c.weightx=0; c.weighty=0; c.insets=new Insets(5,5,5,5); c.anchor=GridBagConstraints.WEST;
    gridbag2.setConstraints(pf, c);
    connectWithPanel.add(pf,c);

    JButton cb=new JButton("Connect");
    c.gridx=3; c.gridy=1; c.gridwidth=1; c.gridheight=1; c.fill=GridBagConstraints.NONE; c.weightx=0; c.weighty=0; c.insets=new Insets(5,5,5,5); c.anchor=GridBagConstraints.EAST;
    gridbag2.setConstraints(cb, c);
    connectWithPanel.add(cb,c);

    cb.addActionListener(new ActionListener(){
          public void actionPerformed(ActionEvent evt){
             TopManager.getMainFrame().setServiceStarted(green);
             green=!green;
          }
    });

    //yPanel.add(connectWithPanel);

    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, rcPanel, connectWithPanel);
    //splitPane.setContinuousLayout(true);
	  //splitPane.setOneTouchExpandable(true);

    add(splitPane,BorderLayout.CENTER);

    init();
  }//constructor

  boolean green;

  private void init(){


  }//init



}
