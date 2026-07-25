package org.is.gui;

import javax.swing.*;
import java.awt.event.*;
import javax.swing.text.PlainDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.AttributeSet;
import java.lang.NumberFormatException;

/**
 * TextField validating user's input when it has to be an integer.
 *
 * @since jdk1.0
 */
public class IntTextField extends JTextField{

    protected IntDoc doc;

    public IntTextField(int minValue, int maxValue, int numCols){

      super(numCols);
      doc=new IntDoc(minValue, maxValue);
      setDocument(doc);
    }

    public void setText(String str){

      try{
        doc.remove(0, doc.getLength());
        doc.insertString(0,str,null);
      }
      catch(Exception ble){
        System.out.println("IntTextFileld::"+ble);
      }
    }

    public void setText(int n){

      setText(Integer.toString(n));
    }

    public void setEnabled2(boolean enabled){

      setEnabled(enabled);
      setEditable(enabled);
    }

    public void setMin(int newMin){

      doc.setMin(newMin);
    }

    public void setMax(int newMax){

      doc.setMax(newMax);
    }

class IntDoc extends PlainDocument {

    int min, max;

    public IntDoc(){

      this(Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public IntDoc(int min, int max){

      this.min = min;
      this.max = max;
    }

    public void insertString(int offs, String str, AttributeSet a)throws BadLocationException{

      int i = 0;
      String s = getText(0, getLength()) + str;
      try{
        i = Integer.parseInt(s);
      }catch(NumberFormatException nfe){
        return;
      }
      if((i < min)||(i > max)){
        return;
      }
      super.insertString(offs, str, a);
    }

    public int getMin(){

      return min;
    }

    public void setMin(int newMin){

      min=newMin;
    }

    public int getMax(){

      return max;
    }

    public void setMax(int newMax){

      max=newMax;
    }
}


}