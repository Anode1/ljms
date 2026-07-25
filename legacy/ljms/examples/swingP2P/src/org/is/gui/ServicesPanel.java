package org.is.gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import java.awt.event.*;
import java.util.*;
import java.awt.*;

/**
 */
public class ServicesPanel extends JPanel{

  private SJList sl;
  private IntTextField pf;
  private Services services=new Services();
  private ServicesTable st;

  public ServicesPanel(){

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

    st = new ServicesTable();
    //wrap into ScrollPane to see the header
    JScrollPane sp=new JScrollPane();
    sp.getViewport().add(st);
    sp.setMinimumSize(new Dimension(200,50));
    sp.setPreferredSize(new Dimension(400,50));
    sp.setMaximumSize(new Dimension(800,50));

    c.gridx=0; c.gridy=0; c.gridwidth=3; c.gridheight=1; c.fill=GridBagConstraints.BOTH; c.weightx=1; c.weighty=1; c.insets=new Insets(5,5,5,5); c.anchor=GridBagConstraints.CENTER;
    gridbag.setConstraints(sp, c);
    rcPanel.add(sp,c);

    JButton disAllButton=new JButton("Stop All");
    c.gridx=0; c.gridy=1; c.gridwidth=1; c.gridheight=1; c.fill=GridBagConstraints.NONE; c.weightx=0; c.weighty=0; c.insets=new Insets(5,5,5,5); c.anchor=GridBagConstraints.WEST;
    gridbag.setConstraints(disAllButton, c);
    rcPanel.add(disAllButton,c);

    JButton removeSButton=new JButton("Remove");
    c.gridx=1; c.gridy=1; c.gridwidth=1; c.gridheight=1; c.fill=GridBagConstraints.NONE; c.weightx=0; c.weighty=0; c.insets=new Insets(5,5,5,5); c.anchor=GridBagConstraints.WEST;
    gridbag.setConstraints(removeSButton, c);
    rcPanel.add(removeSButton,c);
    removeSButton.addActionListener(new ActionListener(){
       public void actionPerformed(ActionEvent evt){
          removeService();
       }
    });

    //yPanel.add(rcPanel);

//connectWith panel:
    JPanel connectWithPanel=new JPanel();

    connectWithPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"Make new connection"));
    GridBagLayout gridbag2 = new GridBagLayout();
    connectWithPanel.setLayout(gridbag2);

    JLabel pl=new JLabel("Port");
    pl.setHorizontalAlignment(SwingConstants.RIGHT);
    c.gridx=0; c.gridy=0; c.gridwidth=1; c.gridheight=1; c.fill=GridBagConstraints.NONE; c.weightx=0; c.weighty=0; c.insets=new Insets(5,5,5,5); c.anchor=GridBagConstraints.EAST;
    gridbag2.setConstraints(pl, c);
    connectWithPanel.add(pl,c);

    pf=new IntTextField(1, 65536, 5);
    c.gridx=1; c.gridy=0; c.gridwidth=1; c.gridheight=1; c.fill=GridBagConstraints.NONE; c.weightx=0; c.weighty=0; c.insets=new Insets(5,5,5,5); c.anchor=GridBagConstraints.WEST;
    gridbag2.setConstraints(pf, c);
    connectWithPanel.add(pf,c);

    JLabel wl=new JLabel("Service");
    wl.setHorizontalAlignment(SwingConstants.RIGHT);
    c.gridx=2; c.gridy=0; c.gridwidth=1; c.gridheight=1; c.fill=GridBagConstraints.NONE; c.weightx=0; c.weighty=0; c.insets=new Insets(5,5,5,5); c.anchor=GridBagConstraints.EAST;
    gridbag2.setConstraints(wl, c);
    connectWithPanel.add(wl,c);

    sl=new SJList();
    JScrollPane scrPane=new JScrollPane(sl);
    //scrPane.setMinimumSize(new Dimension(150,20));
    //scrPane.setPreferredSize(new Dimension(150,40));
    c.gridx=3; c.gridy=0; c.gridwidth=1; c.gridheight=1; c.fill=GridBagConstraints.NONE; c.weightx=0; c.weighty=0; c.insets=new Insets(5,5,5,5); c.anchor=GridBagConstraints.WEST;
    gridbag2.setConstraints(scrPane, c);
    connectWithPanel.add(scrPane,c);

    JButton cb=new JButton("Add New");
    c.gridx=4; c.gridy=0; c.gridwidth=1; c.gridheight=1; c.fill=GridBagConstraints.NONE; c.weightx=0; c.weighty=0; c.insets=new Insets(5,5,5,5); c.anchor=GridBagConstraints.EAST;
    gridbag2.setConstraints(cb, c);
    connectWithPanel.add(cb,c);

    cb.addActionListener(new ActionListener(){
       public void actionPerformed(ActionEvent evt){
          addService();
       }
    });

    //yPanel.add(connectWithPanel);

    JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, rcPanel, connectWithPanel);
    //splitPane.setContinuousLayout(true);
	  //splitPane.setOneTouchExpandable(true);

    add(splitPane,BorderLayout.CENTER);

    init();
  }//constructor

  private void init(){

    sl.setListData(services.getServices());

    sl.addListSelectionListener(new ListSelectionListener(){
        public void valueChanged(ListSelectionEvent evt){
           JList src=(JList)evt.getSource();
           if(evt.getValueIsAdjusting())return;
           
           Service s=(Service)sl.getSelectedValue();
           if(s==null)return;
           pf.setText(Integer.toString(s.getPort()));
        }
    });

    //double-click on List is the same as Start button:
    /*
    sl.addMouseListener(new MouseAdapter(){
      public void mouseClicked(MouseEvent evt){
         if(evt.getClickCount()==2){
            JList src=(JList)evt.getSource();
            int i=src.locationToIndex(evt.getPoint());
            if(i>=0){
               Service s=(Service)src.getModel().getElementAt(i);
               pf.setText(Integer.toString(s.getPort()));
               startService();
            }
         }
      }
    });
     */
  }//init


  private void addService(){

    Service s=(Service)sl.getSelectedValue();
    if(s==null){
      JOptionPane.showMessageDialog(null, "Select a service to start", "Warning", JOptionPane.WARNING_MESSAGE, null);
      return;
    }

    //System.out.println(s.toString());

    int port=0;
    String portString=pf.getText();
    try{
      port=Integer.parseInt(portString);
    }
    catch(NumberFormatException nfe){
      JOptionPane.showMessageDialog(null, "Port should be a number in the range: 0..65536", "Warning", JOptionPane.WARNING_MESSAGE, null);
      return;
    }

    if(!st.portIsAvailable(portString)){
      JOptionPane.showMessageDialog(null, "Port "+port+" has been used already by one of the services - specify another port or remove service using this port number", "Warning", JOptionPane.WARNING_MESSAGE, null);
      return;
    }

    s.setPort(port);
    st.addService(s);

    

  }

  private void removeService(){
    int[] rowsSelected=st.getSelectedRows();
    
  }
}
