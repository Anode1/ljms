package org.is.html.dialogs;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.util.*;
import java.awt.*;

import org.is.gui.IComboBox;
import org.is.html.*;
import org.is.util.Utils;

/**
 * Dialog for image properties modifying
 *
 * @since jdk1.2
 */
public class ImagePropDialog extends InsertDialog{

  protected JTextField wf,hf;
  protected IComboBox url_combo;

  protected static final int DEFAULT_WIDTH=50;
  protected static final int DEFAULT_HEIGHT=50;

  public static final String LEFT_ALIGN="left";
  public static final String RIGHT_ALIGN="right";
  public static final String CENTER_ALIGN="middle";
  protected String alignment=LEFT_ALIGN;


  public ImagePropDialog() {

    super();

    setTitle("Image properties");
    getContentPane().setLayout(new BorderLayout());

    InternalPanel panel=new InternalPanel();

    getContentPane().add(panel, BorderLayout.CENTER);
    this.pack();
    this.setResizable(false);
    Utils.setCentalizedLocationRelativeMe(TopManager.getGHTMLEditor().getFrame(),this);
  }

  class InternalPanel extends JPanel{

    public InternalPanel(){

    setLayout(new BorderLayout());

    this.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

    JPanel north_panel=new JPanel();

    north_panel.setLayout(new BorderLayout());

		JLabel url_label = new JLabel();
    url_label.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));
		url_label.setText("Image URL");
		north_panel.add(url_label,BorderLayout.WEST);

		url_combo = new IComboBox();
    initCombo();
    url_combo.setSelectedIndex(0);
		north_panel.add(url_combo,BorderLayout.CENTER);
    add(north_panel,BorderLayout.NORTH);

    JPanel centerPanel=new JPanel();
    GridBagLayout gridbag = new GridBagLayout();
    GridBagConstraints c = new GridBagConstraints();
        
    centerPanel.setLayout(gridbag);

    /*
    JPanel radioPanel=new JPanel();
    radioPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"Image alignment"));
    radioPanel.setLayout(new BoxLayout(radioPanel, BoxLayout.Y_AXIS));
    Dimension d=new Dimension(150,100);
    radioPanel.setMinimumSize(d);
    radioPanel.setPreferredSize(d);
    ButtonGroup rbg=new ButtonGroup();
    JRadioButton lb=new JRadioButton("left");
    radioPanel.add(lb);
    lb.addActionListener(new ActionListener(){
        		public void actionPerformed(java.awt.event.ActionEvent event){
                 alignment=LEFT_ALIGN;
		        }
    });
    JRadioButton mb=new JRadioButton("middle");
    radioPanel.add(mb);
    mb.addActionListener(new ActionListener(){
        		public void actionPerformed(java.awt.event.ActionEvent event){
                  alignment=CENTER_ALIGN;
  	        }
    });
    JRadioButton rb=new JRadioButton("right");
    rb.addActionListener(new ActionListener(){
        		public void actionPerformed(java.awt.event.ActionEvent event){
                 alignment=RIGHT_ALIGN;
		        }
    });
    radioPanel.add(rb);
    rbg.add(lb);
    if(alignment==LEFT_ALIGN)lb.setSelected(true);
    rbg.add(mb);
    if(alignment==CENTER_ALIGN)mb.setSelected(true);
    rbg.add(rb);
    if(alignment==RIGHT_ALIGN)rb.setSelected(true);

    lb.setEnabled(false);
    rb.setEnabled(false);
    mb.setEnabled(false);

    c.gridx=0; c.gridy=0; c.gridwidth=1; c.gridheight=1; c.fill=GridBagConstraints.HORIZONTAL; c.weightx=0.5; c.weighty=0; c.insets=new Insets(5,5,5,5); c.anchor=GridBagConstraints.CENTER;
    gridbag.setConstraints(radioPanel, c);
    centerPanel.add(radioPanel,c);

    */

    JPanel dimPanel=new JPanel();
    dimPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"Image Size"));
    GridBagLayout gridbag2 = new GridBagLayout();
    dimPanel.setLayout(gridbag2);

    JLabel wl=new JLabel("Width:");
    wl.setHorizontalAlignment(SwingConstants.RIGHT);
    c.gridx=0; c.gridy=0; c.gridwidth=1; c.gridheight=1; c.fill=GridBagConstraints.NONE; c.weightx=0; c.weighty=0; c.insets=new Insets(5,5,5,5); c.anchor=GridBagConstraints.EAST;
    gridbag2.setConstraints(wl, c);
    dimPanel.add(wl,c);

    wf=new JTextField(Integer.toString(DEFAULT_WIDTH), 4);
    c.gridx=1; c.gridy=0; c.gridwidth=1; c.gridheight=1; c.fill=GridBagConstraints.NONE; c.weightx=0; c.weighty=0; c.insets=new Insets(5,5,5,5); c.anchor=GridBagConstraints.WEST;
    gridbag2.setConstraints(wf, c);
    dimPanel.add(wf,c);

    JLabel hl=new JLabel("Height:");
    hl.setHorizontalAlignment(SwingConstants.RIGHT);
    c.gridx=0; c.gridy=1; c.gridwidth=1; c.gridheight=1; c.fill=GridBagConstraints.NONE; c.weightx=0; c.weighty=0; c.insets=new Insets(5,5,5,5); c.anchor=GridBagConstraints.EAST;
    gridbag2.setConstraints(hl, c);
    dimPanel.add(hl,c);

    hf=new JTextField(Integer.toString(DEFAULT_HEIGHT),4);
    c.gridx=1; c.gridy=1; c.gridwidth=1; c.gridheight=1; c.fill=GridBagConstraints.NONE; c.weightx=0; c.weighty=0; c.insets=new Insets(5,5,5,5); c.anchor=GridBagConstraints.WEST;
    gridbag2.setConstraints(hf, c);
    dimPanel.add(hf,c);

    c.gridx=1; c.gridy=0; c.gridwidth=1; c.gridheight=1; c.fill=GridBagConstraints.BOTH; c.weightx=0.5; c.weighty=1; c.insets=new Insets(5,5,5,5); c.anchor=GridBagConstraints.CENTER;
    gridbag.setConstraints(dimPanel, c);

    centerPanel.add(dimPanel,c);

    add(centerPanel,BorderLayout.CENTER);

  	JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,5,5,5));
		JButton okButton = new JButton("OK");
		buttonPanel.add(okButton);
		JButton cancelButton = new JButton("Cancel");
		buttonPanel.add(cancelButton);
		this.add(buttonPanel, "South");

    okButton.addActionListener(new ActionListener(){
        		public void actionPerformed(java.awt.event.ActionEvent event){
               dispose();
		        }
    });

    cancelButton.addActionListener(new ActionListener(){
        		public void actionPerformed(java.awt.event.ActionEvent event){
               dispose();
               setCancelled(true);
		        }
    });

    add(buttonPanel,BorderLayout.SOUTH);

  }

  }

  public String getUrl(){

     String url=url_combo.getData();
     if(url==null)return "";
     return url;
  }

  public int getImageWidth(){

    try{
      return Integer.parseInt(wf.getText());
    }
    catch(Exception e){
      return 30;
    }
  }

  public int getImageHeight(){

    try{
      return Integer.parseInt(hf.getText());
    }
    catch(Exception e){
      return 30;
    }
  }

  public String getImageAlignment(){

    if(alignment==null)return "";
    return alignment;
  }

  public void setImageAlignment(String alignment){

    this.alignment=alignment;
  }

  public void setURLs(String[] urls){

    url_combo.populate(urls);
  }

  private void initCombo(){

    url_combo.sendData("http://localhost/foo.jpg");
  }


}
