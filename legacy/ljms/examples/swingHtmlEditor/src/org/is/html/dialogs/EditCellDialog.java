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
 * Html table cell properties dialog
 *
 * @since jdk1.2
 */
public class EditCellDialog extends InsertDialog{

  private Element te, ce;
  protected AttributeSet existedAttrs; //attributes cached

  private GHTMLDocument doc;

  public static final int WIDTH_MIN=1;
  public static final int WIDTH_MAX=1024;
  static final int WIDTH_DEFAULT=100;

  public static final int HEIGHT_MIN=1;
  public static final int HEIGHT_MAX=5000;
  static final int HEIGHT_DEFAULT=30;

  private IntTextField wf,hf,rsf,csf;
  private DimCombo wc,hc;
  private VACombo vac;

  private Color bgc;
  private JButton bgb;

  private Rectangle posInTable;

  public EditCellDialog(Element te, Element ce, GHTMLDocument doc){

    this.te=te;
    this.ce=ce;
    this.doc=doc;
    existedAttrs=ce.getAttributes();
   // AbstractDocument.BranchElement he=(AbstractDocument.BranchElement)element;
   // he.dump(System.out,1);
    posInTable=TableUtils.getPositionInTableAsRectangle(ce,te);
    if(posInTable==null){
       System.err.println("EditCellDialog::constructor:position in table not determined");
       return;
    }

    setTitle("Cell properties");
    getContentPane().setLayout(new BorderLayout());

    InternalPanel panel=new InternalPanel();

    getContentPane().add(panel, BorderLayout.CENTER);
    this.pack();
   // this.setResizable(false);
    Utils.setCentalizedLocationRelativeMe(TopManager.getGHTMLEditor().getFrame(),this);
  }

  class InternalPanel extends JPanel{

    InternalPanel(){

    setLayout(new BorderLayout());
    this.setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

//center panel (container for other bordered panels):
    JPanel centerPanel=new JPanel();
    centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

//north panel:

    JPanel rcPanel=new JPanel();
    rcPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"Span cells"));

    GridBagLayout gridbag = new GridBagLayout();
    rcPanel.setLayout(gridbag);

    JLabel crl=new JLabel("Span across columns:");
    GridBagConstraints c = new GridBagConstraints();
    c.gridx=0; c.gridy=0; c.gridwidth=1; c.gridheight=1; c.fill=GridBagConstraints.NONE; c.weightx=0; c.weighty=0; c.insets=new Insets(5,5,5,5); c.anchor=GridBagConstraints.EAST;
    gridbag.setConstraints(crl, c);
    rcPanel.add(crl,c);

    csf=new IntTextField(1, 100, 4);
    c.gridx=1; c.gridy=0; c.gridwidth=1; c.gridheight=1; c.fill=GridBagConstraints.NONE; c.weightx=0; c.weighty=0; c.insets=new Insets(5,5,5,5); c.anchor=GridBagConstraints.WEST;
    gridbag.setConstraints(csf, c);
    rcPanel.add(csf,c);

    JLabel rcl=new JLabel("Span down rows:");
    c.gridx=0; c.gridy=1; c.gridwidth=1; c.gridheight=1; c.fill=GridBagConstraints.NONE; c.weightx=0; c.weighty=0; c.insets=new Insets(5,5,5,5); c.anchor=GridBagConstraints.EAST;
    gridbag.setConstraints(rcl, c);
    rcPanel.add(rcl,c);

    rsf=new IntTextField(1, 100, 4);
    c.gridx=1; c.gridy=1; c.gridwidth=1; c.gridheight=1; c.fill=GridBagConstraints.NONE; c.weightx=0; c.weighty=0; c.insets=new Insets(5,5,5,5); c.anchor=GridBagConstraints.WEST;
    gridbag.setConstraints(rsf, c);
    rcPanel.add(rsf,c);

    centerPanel.add(rcPanel);

//dim panel:
    JPanel dimPanel=new JPanel();

    dimPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(),"Cell Dimensions"));
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
                int defaultPercent=30;
                wf.setMax(100);
                wf.setText(defaultPercent);
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

    hc=new DimCombo(hf,true);
  //  Dimension sizeOfFull=wc.getPreferredSize();
  //  hc.setPreferredSize(sizeOfFull);
  //  hc.setMinimumSize(sizeOfFull);
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

    JLabel val=new JLabel("Vertical Alignment:");
    val.setHorizontalAlignment(SwingConstants.RIGHT);
    c.gridx=0; c.gridy=0; c.gridwidth=1; c.gridheight=1; c.fill=GridBagConstraints.NONE; c.weightx=0; c.weighty=0; c.insets=new Insets(5,5,5,5); c.anchor=GridBagConstraints.EAST;
    generalPanelGB.setConstraints(val, c);
    generalPanel.add(val,c);

    vac=new VACombo();
    c.gridx=1; c.gridy=0; c.gridwidth=1; c.gridheight=1; c.fill=GridBagConstraints.NONE; c.weightx=0; c.weighty=0; c.insets=new Insets(5,5,5,5); c.anchor=GridBagConstraints.WEST;
    generalPanelGB.setConstraints(vac, c);
    generalPanel.add(vac,c);
    vac.addActionListener(new ActionListener(){
          public void actionPerformed(ActionEvent evt){
   
          }
    });


    bgb = new JButton("Cell Background Color", new ColoredBox(Color.white)){
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

      setSpanFromElement(HTML.Attribute.COLSPAN);
      setSpanFromElement(HTML.Attribute.ROWSPAN);

      setDimFromElement(HTML.Attribute.WIDTH);
      setDimFromElement(HTML.Attribute.HEIGHT);

      setValignFromElement();
      setCellBGColorFromElement();

  }//init

  }//class

  public String getBgAsRGBString(){

    if(bgc==null)return null;
    return HTMLUtils.colorToHex(bgc);
  }

  public int getColSpan(){

    try{
      return Integer.parseInt(csf.getText());
    }
    catch(Exception e){
      //System.err.println("TablePropDialog::getNumCols:"+ncf.getText()+" is not integer!");
      return 1;
    }
  }

  public int getRowSpan(){

    try{
      return Integer.parseInt(rsf.getText());
    }
    catch(Exception e){
      //System.err.println("TablePropDialog::getNumRows:"+nrf.getText()+" is not integer!");
      return 1;
    }
  }

  private String getCellWidthAsString(){

     String text=wf.getText();
     if(text==null)return "";
     return text.trim();
  }

  private String getCellHeightAsString(){

     String text=hf.getText();
     if(text==null)return "";
     return text.trim();
  }

  public void modifyCellAttributes(GHTMLDocument doc){

    SimpleAttributeSet attrs = new SimpleAttributeSet(existedAttrs);

    //WIDTH:
    attrs.removeAttribute(HTML.Attribute.WIDTH);

    int widthMode=wc.getMode();
    if(widthMode==DimCombo.AUTO){
       attrs.removeAttribute(HTML.Attribute.WIDTH);
    }
    else if(widthMode==DimCombo.PIXELS){
       attrs.addAttribute(HTML.Attribute.WIDTH, getCellWidthAsString());
    }
    else if(widthMode==DimCombo.PERCENT && !getCellWidthAsString().equals("")){
       attrs.addAttribute(HTML.Attribute.WIDTH, getCellWidthAsString()+"%");
    }

    //HEIGHT:
    int heightMode=hc.getMode();
    attrs.removeAttribute(HTML.Attribute.HEIGHT);
    if(heightMode==DimCombo.AUTO){
       attrs.removeAttribute(HTML.Attribute.HEIGHT);
    }
    else if(heightMode==DimCombo.PIXELS){
       attrs.addAttribute(HTML.Attribute.HEIGHT, getCellHeightAsString());
    }

    //BG:
    if(bgc!=null){
       attrs.addAttribute(HTML.Attribute.BGCOLOR, HTMLUtils.colorToHex(bgc));
    }
    else attrs.removeAttribute(HTML.Attribute.BGCOLOR);

    //SPANNING:
    spanIfNeeded(attrs, HTML.Attribute.COLSPAN);
    spanIfNeeded(attrs, HTML.Attribute.ROWSPAN);

    String newValign=vac.getModeAsAttrString();
    attrs.removeAttribute(HTML.Attribute.VALIGN);
    if(newValign!=null){
      attrs.addAttribute(HTML.Attribute.VALIGN, vac.getModeAsAttrString());
    }

    doc.updateElement(ce, attrs);
    //System.out.println(new Grid(te));
  }

  /**
   * @param attr used here as a flag defining orientation
   */
  private void spanIfNeeded(MutableAttributeSet attrs, HTML.Attribute attr){

    //find GUI control to apply depending on attr:
    IntTextField textFieldApplyTo=null;
    if(attr==HTML.Attribute.ROWSPAN) textFieldApplyTo=rsf;
    else if(attr==HTML.Attribute.COLSPAN) textFieldApplyTo=csf;

    try{
      String text=textFieldApplyTo.getText();
      int num=Integer.parseInt(text);
      if(num<2)return;

      attrs.removeAttribute(attr);
      reorganizeCells(attr, num);
      attrs.addAttribute(attr, text);
    }
    catch(NumberFormatException nfe){
    }
  }


  /**
   * @param attr used here as a flag defining the orientation
   */
  private void reorganizeCells(HTML.Attribute attr, int num){

    try{
      if(attr==HTML.Attribute.ROWSPAN){

        if(!rsf.isEnabled())return; //!

        //collect elements to remove:
        Vector elementsToRemove=new Vector();
        int i=0;
        for(int curRow=posInTable.y+1; curRow<posInTable.y+num; curRow++){ //all rows
          //System.out.println(i+" removed");
          Element rowElem=TableUtils.getRowElement(te, curRow);
          Element colElem=TableUtils.getColumnElement(rowElem, posInTable.x);
          elementsToRemove.addElement(colElem);
        }

        //remove in one locked block
        doc.removeElements(elementsToRemove);
      }
      else if(attr==HTML.Attribute.COLSPAN){

        if(!csf.isEnabled())return; //!

        doc.removeElements(ce.getParentElement(), posInTable.x+1, num-1);
      }
    }
    catch(Exception e){
       System.err.println("EditCellDialog::reorganizeCells: failed:"+e);
       e.printStackTrace(System.err);
    }
  }

  /**
   * @param attr used here as a flag defining the orientation
   *//*
  private void reorganizeCells(HTML.Attribute attr, int num){

    try{
      if(attr==HTML.Attribute.ROWSPAN){

        //collect elements to remove:
        Vector elementsToRemove=new Vector();

        int removed=0;
        for(int i=0; removed<num

        for(int curRow=posInTable.y+1; curRow<posInTable.y+num; curRow++){ //all rows
          //System.out.println(i+" removed");
          Element rowElem=TableUtils.getRowElement(te, curRow);
          Element colElem=TableUtils.getColumnElement(rowElem, posInTable.x, posInTable.width);
          elementsToRemove[i]=colElem;
          i++;
        }

        //remove in one locked block
        doc.removeElements(elementsToRemove);
      }
      else if(attr==HTML.Attribute.COLSPAN){

        Vector elementsToRemove=new Vector();
        Element rowElem=TableUtils.getRowElement(te, posInTable.y);
        int index=0;
        for(int curCol=posInTable.x+1; curCol<posInTable.x+num; curCol++){ //all rows
          Element colElem=TableUtils.getColumnElement(rowElem, posInTable.x, index);
          elementsToRemove.addElement();
        }
        doc.removeElements(ce.getParentElement(), posInTable.x+1, num-1);
      } 
    }
    catch(Exception e){
       System.err.println("EditCellDialog::removeExtraElements: failed:"+e);
    }
  }
*/

  /**
   * @param attr used here as a flag defining the orientation
   */
  private void setDimFromElement(HTML.Attribute attr){

    int mode=getModeFromElement(attr);

    //find GUI control to apply depending on attr:
    DimCombo comboToApply=null;
    IntTextField textFieldToAct=null;

    if(attr==HTML.Attribute.WIDTH){
      comboToApply=wc;
      textFieldToAct=wf;
    }
    else if(attr==HTML.Attribute.HEIGHT){
      comboToApply=hc;
      textFieldToAct=hf;
    }

    comboToApply.setSelectedIndex(mode);
    textFieldToAct.setText(getCellSizeFromElement(attr));
    if(mode==DimCombo.AUTO){
      textFieldToAct.setEnabled2(false);
    }

  }

  /**
   * @param attr used here as a flag defining the orientation
   */
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
          if(attr==HTML.Attribute.HEIGHT){
             System.err.println("EditCellDialog::getModeFromElement:cell height in %??? - been removed");
             return DimCombo.AUTO;
             //here we want to distinguish between parent attribute WIDTH and cell attribute
             //returning AUTO if % been defined in the parent only

          }
          return DimCombo.PERCENT;
       }
       else return DimCombo.PIXELS;
    }
  }

  /**
   * @param attr used here as a flag defining the orientation
   */
  public String getCellSizeFromElement(HTML.Attribute attr){

       String str=null;
       if(existedAttrs.isDefined(attr)){
          str=(String)existedAttrs.getAttribute(attr);
       }
       if(str==null){
          return "";
       }
       else{
          if(str.indexOf("%")!=-1){
            return str.substring(0, str.length()-1);
          }
          else return str;
       }
  }

  /**
   * @param attr used here as a flag defining the orientation
   */
  private void setSpanFromElement(HTML.Attribute attr){ //attr acts as a constant here

       int howMany=getRemaining(attr);

       //find GUI control to apply
       IntTextField textFieldToAct=null;
       if(attr==HTML.Attribute.ROWSPAN) textFieldToAct=rsf;
       else if(attr==HTML.Attribute.COLSPAN) textFieldToAct=csf;

       //make upper limit knowing table's dimensions:
       textFieldToAct.setMax(howMany);
       String existing=null;
       if(existedAttrs.isDefined(attr)){
          existing=(String)existedAttrs.getAttribute(attr);
       }

       //now set from element if needed:
       if(existing==null)textFieldToAct.setText("1");
       else{
          textFieldToAct.setText(existing);
          textFieldToAct.setEnabled2(false);  //!
       }
  }

  /**
   * @param attr used here as a flag defining the orientation
   */
  private int getRemaining(HTML.Attribute attr){    //attr ats here as a constant

       if(attr==HTML.Attribute.ROWSPAN) return posInTable.height-posInTable.y;
       else if(attr==HTML.Attribute.COLSPAN) return posInTable.width-posInTable.x;
       return 0;
  }

  /**
   * Sets bg color taking attribute from Element
   */
  private void setCellBGColorFromElement(){

       String bg=null;
  //     if(existedAttrs.isDefined(HTML.Attribute.BGCOLOR)){
          bg=(String)existedAttrs.getAttribute(HTML.Attribute.BGCOLOR);
   //    }
       if(bg==null){
          bgb.setIcon(new ColoredBox(Color.white));
       }
       else{
          bgc=HTMLUtils.stringToColor(bg);
          bgb.setIcon(new ColoredBox(bgc));
       }
  }

  /**
   * Sets valign taking attribute from Element
   */
  private void setValignFromElement(){

       String va=null;
       if(existedAttrs.isDefined(HTML.Attribute.VALIGN)){
          va=(String)existedAttrs.getAttribute(HTML.Attribute.VALIGN);
       }
       vac.setFromAttribute(va);
  }

}
