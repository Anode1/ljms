package org.is.html.dialogs;

import javax.swing.JComboBox;

/**
 * Combo for choosing of a valign
 *
 * @since jdk1.2
 */
public class VACombo extends JComboBox{

  public static final int DEFAULT=0;   //if width or height is not specified at all
  public static final int MIDDLE=1;
  public static final int TOP=2;
  public static final int BOTTOM=3;

  //language independant labels
  private String DEFAULT_STRING="Default";
  private String MIDDLE_STRING="Middle";
  private String TOP_STRING="Top";
  private String BOTTOM_STRING="Bottom";

  public VACombo(){

     super();

     //order is significant!
     addItem(DEFAULT_STRING);
     addItem(MIDDLE_STRING);
     addItem(TOP_STRING);
     addItem(BOTTOM_STRING);

     setSelectedIndex(0);
  }

  /**
   * Returns currently selected mode as HTML attribute
   */
  public String getModeAsAttrString(){

     int selectedIndex=getSelectedIndex();
     if(selectedIndex==0){
        return null;
     }
     if(selectedIndex==1){
        return "MIDDLE";
     }
     if(selectedIndex==2){
        return "TOP";
     }
     if(selectedIndex==3){
        return "BOTTOM";
     }
     return null;
  }

  public void setFromAttribute(String string){

     if(string==null){
        this.setSelectedItem(DEFAULT_STRING);
        return;
     }

     String trimmed=string.trim();
     if(trimmed.equals("")){
        this.setSelectedItem(DEFAULT_STRING);
        return;
     }

     String lowerCased=trimmed.toLowerCase();

     if(lowerCased.equals("middle")){
        this.setSelectedItem(MIDDLE_STRING);
     }
     else if(lowerCased.equals("top")){
        this.setSelectedItem(TOP_STRING);
     }
     else if(lowerCased.equals("bottom")){
        this.setSelectedItem(BOTTOM_STRING);
     }
     else{
        this.setSelectedItem(DEFAULT_STRING);
     }
  }

}
