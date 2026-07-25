package org.is.html.dialogs;

import javax.swing.*;
import javax.swing.text.html.HTML;
import javax.swing.text.*;
import javax.swing.text.AbstractDocument;
import javax.swing.border.*;
import javax.swing.event.*;
import java.awt.event.*;
import java.util.*;
import java.awt.*;

import org.is.html.*;
import org.is.gui.IntTextField;
import org.is.gui.ColoredBox;
import org.is.util.*;

/**
 * Html table properties dialog. <p>
 *
 * @since jdk1.2
 */
public class EditTableDialog extends InsertDialog{

  protected AttributeSet existedAttrs; //attributes cached
  protected Element te;

  public static final int ROWS_MIN=1;
  public static final int ROWS_MAX=30;
  static final int ROWS_DEFAULT=3;

  public static final int COLS_MIN=1;
  public static final int COLS_MAX=30;
  static final int COLS_DEFAULT=3;

  public static final int WIDTH_MIN=1;
  public static final int WIDTH_MAX=1024;
  static final int WIDTH_DEFAULT=800;

  public static final int HEIGHT_MIN=1;
  public static final int HEIGHT_MAX=5000;
  static final int HEIGHT_DEFAULT=600;

  public static final int BORDER_MIN=0;
  public static final int BORDER_MAX=100;
  static final int BORDER_DEFAULT=1;
  
  private IntTextField wf,hf,nrf,ncf,bf;
  private DimCombo wc,hc;

  private Color bgc;
  private JButton bgb;

  public EditTableDialog(Element te){

    this.te=te;
    if(te!=null){
      existedAttrs=te.getAttributes();
    }
    else existedAttrs=new SimpleAttributeSet();

   // AbstractDocument.BranchElement he=(AbstractDocument.BranchElement)element;
   // he.dump(System.out,1);

    setTitle("Table settings");
    getContentPane().setLayout(new BorderLayout());

    InternalPanel panel=new InternalPanel();

    getContentPane().add(panel, BorderLayout.CENTER);
    this.pack();
   // this.setResizable(false);
    Utils.setCentalizedLocationRelativeMe(TopManager.getGHTMLEditor().getFrame(),this);
  }

  class InternalPanel extends JPanel{

    public InternalPanel(){

    setLayout(new BorderLayout());
    this.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

//center panel (container for other bordered panels):
    JPanel centerPanel=new JPanel();
    centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

//north panel:

    JPanel rcPanel=new JPanel();
    rcPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"Rows and Columns"));

    GridBagLayout gridbag = new GridBagLayout();
    rcPanel.setLayout(gridbag);

    JLabel nrl=new JLabel("Number of rows:");
    GridBagConstraints c = new GridBagConstraints();
    c.gridx=0; c.gridy=0; c.gridwidth=1; c.gridheight=1; c.fill=GridBagConstraints.NONE; c.weightx=0; c.weighty=0; c.insets=new Insets(5,5,5,5); c.anchor=GridBagConstraints.EAST;
    gridbag.setConstraints(nrl, c);
    rcPanel.add(nrl,c);

    nrf=new IntTextField(ROWS_MIN, ROWS_MAX, 4);
    c.gridx=1; c.gridy=0; c.gridwidth=1; c.gridheight=1; c.fill=GridBagConstraints.NONE; c.weightx=0; c.weighty=0; c.insets=new Insets(5,5,5,5); c.anchor=GridBagConstraints.WEST;
    gridbag.setConstraints(nrf, c);
    rcPanel.add(nrf,c);

    JLabel ncl=new JLabel("Number of columns:");
    c.gridx=0; c.gridy=1; c.gridwidth=1; c.gridheight=1; c.fill=GridBagConstraints.NONE; c.weightx=0; c.weighty=0; c.insets=new Insets(5,5,5,5); c.anchor=GridBagConstraints.EAST;
    gridbag.setConstraints(ncl, c);
    rcPanel.add(ncl,c);

    ncf=new IntTextField(COLS_MIN, COLS_MAX, 4);
    c.gridx=1; c.gridy=1; c.gridwidth=1; c.gridheight=1; c.fill=GridBagConstraints.NONE; c.weightx=0; c.weighty=0; c.insets=new Insets(5,5,5,5); c.anchor=GridBagConstraints.WEST;
    gridbag.setConstraints(ncf, c);
    rcPanel.add(ncf,c);

    centerPanel.add(rcPanel);

//dim panel:
    JPanel dimPanel=new JPanel();

    dimPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"Table Dimensions"));
    GridBagLayout gridbag2 = new GridBagLayout();
    dimPanel.setLayout(gridbag2);

    JLabel wl=new JLabel("Width:");
    wl.setHorizontalAlignment(SwingConstants.RIGHT);
    c.gridx=0; c.gridy=0; c.gridwidth=1; c.gridheight=1; c.fill=GridBagConstraints.NONE; c.weightx=0; c.weighty=0; c.insets=new Insets(5,5,5,5); c.anchor=GridBagConstraints.EAST;
    gridbag2.setConstraints(wl, c);
    dimPanel.add(wl,c);

    wf=new IntTextField(WIDTH_MIN, WIDTH_MAX, 4);
    c.gridx=1; c.gridy=0; c.gridwidth=1; c.gridheight=1; c.fill=GridBagConstraints.NONE; c.weightx=0; c.weighty=0; c.insets=new Insets(5,5,5,5); c.anchor=GridBagConstraints.WEST;
    gridbag2.setConstraints(wf, c);
    dimPanel.add(wf,c);

    wc=new DimCombo(wf);
    c.gridx=2; c.gridy=0; c.gridwidth=1; c.gridheight=1; c.fill=GridBagConstraints.NONE; c.weightx=0; c.weighty=0; c.insets=new Insets(5,5,5,5); c.anchor=GridBagConstraints.WEST;
    gridbag2.setConstraints(wc, c);
    dimPanel.add(wc,c);

    wc.addActionListener(new ActionListener(){
          public void actionPerformed(ActionEvent evt){
             int selected=wc.getSelectedIndex();
             if(selected==0){    //auto
                wf.setEnabled2(false);
                wf.setMax(WIDTH_MAX);
                wf.setText("");
             }
             else if(selected==1){  //pixels
                wf.setEnabled2(true);
                wf.setMax(WIDTH_MAX);
                wf.setText(WIDTH_DEFAULT);
             }
             else if(selected==2){  //percent
                wf.setEnabled2(true);
                wf.setMax(100);
                wf.setText("100");
             }
             else{
                wf.setEnabled2(false);
                wf.setMax(WIDTH_MAX);
                wf.setText("");
             }
          }
    });

    JLabel cl=new JLabel("Height:");
    cl.setHorizontalAlignment(SwingConstants.RIGHT);
    c.gridx=0; c.gridy=1; c.gridwidth=1; c.gridheight=1; c.fill=GridBagConstraints.NONE; c.weightx=0; c.weighty=0; c.insets=new Insets(5,5,5,5); c.anchor=GridBagConstraints.EAST;
    gridbag2.setConstraints(cl, c);
    dimPanel.add(cl,c);

    hf=new IntTextField(HEIGHT_MIN, HEIGHT_MAX, 4);
    c.gridx=1; c.gridy=1; c.gridwidth=1; c.gridheight=1; c.fill=GridBagConstraints.NONE; c.weightx=0; c.weighty=0; c.insets=new Insets(5,5,5,5); c.anchor=GridBagConstraints.WEST;
    gridbag2.setConstraints(hf, c);
    dimPanel.add(hf,c);

    hc=new DimCombo(hf);
    c.gridx=2; c.gridy=1; c.gridwidth=1; c.gridheight=1; c.fill=GridBagConstraints.NONE; c.weightx=0; c.weighty=0; c.insets=new Insets(5,5,5,5); c.anchor=GridBagConstraints.WEST;
    gridbag2.setConstraints(hc, c);
    dimPanel.add(hc,c);

    hc.addActionListener(new ActionListener(){
          public void actionPerformed(ActionEvent evt){
             int selected=hc.getSelectedIndex();
             if(selected==0){    //auto
                hf.setEnabled2(false);
                hf.setMax(HEIGHT_MAX);
                hf.setText("");
             }
             else if(selected==1){  //pixels
                hf.setEnabled2(true);
                hf.setMax(HEIGHT_MAX);
                hf.setText(HEIGHT_DEFAULT);
             }
             else if(selected==2){  //percent
                hf.setEnabled2(true);
                int defaultPercent=100;  //change this: initialize from actual table
                hf.setMax(defaultPercent);
                hf.setText(defaultPercent);
             }
             else{
                hf.setEnabled2(false);
                hf.setMax(HEIGHT_MAX);
                hf.setText("");
             }
          }
    });

    centerPanel.add(dimPanel);

//general panel:
    JPanel generalPanel=new JPanel();

    GridBagLayout generalPanelGB = new GridBagLayout();
    generalPanel.setLayout(generalPanelGB);

    JLabel bl=new JLabel("Border width:");
    bl.setHorizontalAlignment(SwingConstants.RIGHT);
    c.gridx=0; c.gridy=0; c.gridwidth=1; c.gridheight=1; c.fill=GridBagConstraints.NONE; c.weightx=0; c.weighty=0; c.insets=new Insets(5,5,5,5); c.anchor=GridBagConstraints.EAST;
    generalPanelGB.setConstraints(bl, c);
    generalPanel.add(bl,c);

    bf=new IntTextField(BORDER_MIN, BORDER_MAX, 4);
    c.gridx=1; c.gridy=0; c.gridwidth=1; c.gridheight=1; c.fill=GridBagConstraints.NONE; c.weightx=0; c.weighty=0; c.insets=new Insets(5,5,5,5); c.anchor=GridBagConstraints.WEST;
    generalPanelGB.setConstraints(bf, c);
    generalPanel.add(bf,c);

    bgb = new JButton("Background Color", new ColoredBox(Color.white)){
      public float getAlignmentY() { return 0.5f; }
    };

    bgb.setRequestFocusEnabled(true);
    c.gridx=0; c.gridy=1; c.gridwidth=2; c.gridheight=1; c.fill=GridBagConstraints.NONE; c.weightx=0; c.weighty=0; c.insets=new Insets(5,5,5,5); c.anchor=GridBagConstraints.CENTER;
    generalPanelGB.setConstraints(bgb, c);
    generalPanel.add(bgb,c);

    bgb.addActionListener(new ActionListener(){
      public void actionPerformed(ActionEvent ev){
         Color chosen=JColorChooser.showDialog(null, "Color Chooser", (bgc!=null?bgc:Color.white));
         if(chosen!=null){
            bgc=chosen;
            bgb.setIcon(new ColoredBox(chosen));
         }
         else{
            bgb.setIcon(new ColoredBox(Color.white));
         }
      }
    });


//
    centerPanel.add(generalPanel);
//

    add(centerPanel,BorderLayout.CENTER);

//south panel:
  	JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
    buttonPanel.setBorder(BorderFactory.createEmptyBorder(10,5,5,5));

		JButton okButton = new JButton("OK");
		buttonPanel.add(okButton);
    okButton.addActionListener(new ActionListener(){
        		public void actionPerformed(java.awt.event.ActionEvent event){
               dispose();
		        }
    });

		JButton cancelButton = new JButton("Cancel");
		buttonPanel.add(cancelButton);
    cancelButton.addActionListener(new ActionListener(){
        		public void actionPerformed(java.awt.event.ActionEvent event){
               dispose();
               setCancelled(true);
		        }
    });

    add(buttonPanel, BorderLayout.SOUTH);
    init();
  }//constructor

  private void init(){

   if(te==null){
      nrf.setText(ROWS_DEFAULT);
      ncf.setText(COLS_DEFAULT);
      bf.setText(BORDER_DEFAULT);

   }
   else{
      Dimension d=TableUtils.getTableDimensions(te);
      //System.out.println(p);

      nrf.setText(d.height);
      nrf.setEnabled(false);
      ncf.setText(d.width);
      ncf.setEnabled(false);

      int mode=getWidthModeFromElement();
      wc.setSelectedIndex(mode);
      wf.setText(getTableWidthFromElement());
      if(mode==DimCombo.AUTO){
        wf.setEnabled2(false);
      }

      mode=getHeightModeFromElement();
      hc.setSelectedIndex(mode);
      hf.setText(getTableHeightFromElement());
      if(mode==DimCombo.AUTO){
        hf.setEnabled2(false);
      }


      String bStr=(String)existedAttrs.getAttribute(HTML.Attribute.BORDER);
      if(bStr==null)bf.setText(BORDER_DEFAULT);
      else bf.setText(bStr);

      setTableBGColorFromElement();
   }

  }//init

  }//class

  public int getCurrentWidthMode(){

    return wc.getMode();
  }

  public int getCurrentHeightMode(){

    return hc.getMode();
  }

  public int getBorderWidth(){

    try{
      return Integer.parseInt(bf.getText());
    }
    catch(Exception e){
      //System.err.println("TablePropDialog::getBorderWidth:"+bf.getText()+" is not integer!");
      return 1;
    }
  }

  public String getBgAsRGBString(){

    if(bgc==null)return null;
    return HTMLUtils.colorToHex(bgc);
  }

  public int getNumCols(){

    try{
      return Integer.parseInt(ncf.getText());
    }
    catch(Exception e){
      //System.err.println("TablePropDialog::getNumCols:"+ncf.getText()+" is not integer!");
      return 1;
    }
  }

  public int getNumRows(){

    try{
      return Integer.parseInt(nrf.getText());
    }
    catch(Exception e){
      //System.err.println("TablePropDialog::getNumRows:"+nrf.getText()+" is not integer!");
      return 1;
    }
  }

  private String getTableWidthAsString(){

     return wf.getText();
  }

  private String getTableHeightAsString(){

     return hf.getText();
  }



  public String createTableFromGUI(){

    StringBuffer sb=new StringBuffer("<table");
    sb.append(" "+HTML.Attribute.BORDER+"="+getBorderWidth());
    if(!wc.isAuto()){
      sb.append(" "+HTML.Attribute.WIDTH+"="+getTableWidthAsString()+(wc.isPercent()?"%":""));
    }
    if(!hc.isAuto()){
      sb.append(" "+HTML.Attribute.HEIGHT+"="+getTableHeightAsString()+(hc.isPercent()?"%":""));
    }
    String rgbString=getBgAsRGBString();
    if(rgbString!=null){
      sb.append(" "+HTML.Attribute.BGCOLOR+"="+rgbString);
    }

    sb.append(">");
    for(int i=0; i<getNumRows(); i++){
       sb.append("<tr>");
       for(int j=0; j<getNumCols(); j++){
          sb.append("<td>&nbsp;</td>");
          //sb.append("<td><p></p></td>");
       }
       sb.append("</tr>");
    }
    sb.append("</table>");
    return sb.toString();
  }

  public void modifyTableAttributes(GHTMLDocument doc){

    SimpleAttributeSet attrs = new SimpleAttributeSet(existedAttrs);

    //WIDTH:
    attrs.removeAttribute(HTML.Attribute.WIDTH);

    int widthMode=wc.getMode();
    if(widthMode==DimCombo.AUTO){
      attrs.removeAttribute(HTML.Attribute.WIDTH);
    }
    else if(widthMode==DimCombo.PIXELS){
      attrs.addAttribute(HTML.Attribute.WIDTH, getTableWidthAsString());
    }
    else if(widthMode==DimCombo.PERCENT){
      attrs.addAttribute(HTML.Attribute.WIDTH, getTableWidthAsString()+"%");
    }

    //HEIGHT:
    int heightMode=hc.getMode();
    attrs.removeAttribute(HTML.Attribute.HEIGHT);
    if(heightMode==DimCombo.AUTO){
      attrs.removeAttribute(HTML.Attribute.HEIGHT);
    }
    else if(heightMode==DimCombo.PIXELS){
      attrs.addAttribute(HTML.Attribute.HEIGHT, getTableHeightAsString());
    }
    else if(heightMode==DimCombo.PERCENT){
      attrs.addAttribute(HTML.Attribute.HEIGHT, getTableHeightAsString()+"%");
    }

    //BG:
    if(bgc!=null){
      attrs.addAttribute(HTML.Attribute.BGCOLOR, HTMLUtils.colorToHex(bgc));
    }
    else attrs.removeAttribute(HTML.Attribute.BGCOLOR);

    attrs.removeAttribute(HTML.Attribute.BORDER);
    attrs.addAttribute(HTML.Attribute.BORDER, Integer.toString(getBorderWidth()));

    //System.out.println(HTMLUtils.element2String(element));

    doc.updateElement(te, attrs);
  }

  public int getWidthModeFromElement(){

    return getModeFromElement(HTML.Attribute.WIDTH);
  }

  public int getHeightModeFromElement(){

    return getModeFromElement(HTML.Attribute.HEIGHT);
  }

  private int getModeFromElement(HTML.Attribute attr){

    String str=null;
    if(existedAttrs.isDefined(attr)){
      str=(String)existedAttrs.getAttribute(attr);
    }
    if(str==null ){
      return DimCombo.AUTO;
    }
    else{
      if(str.indexOf("%")!=-1){
         return DimCombo.PERCENT;
      }
      else return DimCombo.PIXELS;
    }
  }

  private String getTableWidthFromElement(){

      String widthStr=null;
      if(existedAttrs.isDefined(HTML.Attribute.WIDTH)){
         widthStr=(String)existedAttrs.getAttribute(HTML.Attribute.WIDTH);
      }
      if(widthStr==null)return "";
      else{
         if(widthStr.indexOf("%")!=-1){
           return widthStr.substring(0, widthStr.length()-1);
         }
         else return widthStr;
      }
  }

  private String getTableHeightFromElement(){

      String hStr=null;
      if(existedAttrs.isDefined(HTML.Attribute.HEIGHT)){
         hStr=(String)existedAttrs.getAttribute(HTML.Attribute.HEIGHT);
      }
      if(hStr==null)return "";
      else{
         if(hStr.indexOf("%")!=-1){
           return hStr.substring(0, hStr.length()-1);
         }
         else return hStr;
      }
  }

  public void setTableBGColorFromElement(){

      String bg=null;
      if(existedAttrs.isDefined(HTML.Attribute.BGCOLOR)){
         bg=(String)existedAttrs.getAttribute(HTML.Attribute.BGCOLOR);
      }
      if(bg==null){
         bgb.setIcon(new ColoredBox(Color.white));
      }
      else{
         bgc=HTMLUtils.stringToColor(bg);
         bgb.setIcon(new ColoredBox(bgc));
      }
  }

}
