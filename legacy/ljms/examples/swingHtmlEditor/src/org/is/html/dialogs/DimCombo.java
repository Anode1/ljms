package org.is.html.dialogs;

import javax.swing.JComboBox;

import org.is.gui.IntTextField;

/**
 * Combo choosing a dimension. It is connected with corresponded JTextField but
 * which is created outside (to make it more flexible for layouting)
 *
 * @since jdk1.2
 */
public class DimCombo extends JComboBox{

  private IntTextField tf;

  public static final int AUTO=0;   //if width or height is not specified at all
  public static final int PIXELS=1;
  public static final int PERCENT=2;

  //language independant labels
  private String auto="Auto";
  private String pixels="Pixels";
  private String percent="Percent";

  public DimCombo(IntTextField tfPassed){

     this(tfPassed, false);
  }

  public DimCombo(IntTextField tfPassed, boolean skipPercent){

     super();
     this.tf=tfPassed;

     //order is significant!
     addItem(auto);
     addItem(pixels);
     if(!skipPercent)addItem(percent);   //used for vertical size (cells)

     setSelectedItem(auto);
     tf.setEnabled2(false);
     tf.setText("");

  }//constructor

  public int getMode(){

     int selectedIndex=getSelectedIndex();
     if(selectedIndex==0){
        return AUTO;
     }
     if(selectedIndex==1){
        return PIXELS;
     }
     if(selectedIndex==2){
        return PERCENT;
     }
     //System.err("TablePropDialog::getMode:Uknown index: default (AUTO) is returned");
     return AUTO;
  }

  public boolean isAuto(){

     return getMode()==AUTO;
  }

  public boolean isPercent(){

     return getMode()==PERCENT;
  }

  public boolean isPixels(){

     return getMode()==PIXELS;
  }

}
