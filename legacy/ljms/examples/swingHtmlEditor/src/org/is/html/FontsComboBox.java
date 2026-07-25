package org.is.html;

import javax.swing.JComboBox;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.Dimension;

import org.is.html.actions.Actions;

public class FontsComboBox extends JComboBox implements ActionListener{

  public FontsComboBox(){

    populate();

    Dimension dim=new Dimension(150,24);
    setPreferredSize(dim);
    setMinimumSize(dim);
    setMaximumSize(dim);

    addActionListener(this);
  }

  //implement dynamic fonts!
  void populate(){

 	  addItem("SanSerif");
 	  addItem("Serif");
 	  addItem("Monospaced");
  }

  public void actionPerformed(ActionEvent e){

 	  if(e.getSource() != this){
       System.err.println("FontsComboBox::unknown event source");
    }

 	  String fontName = (String)getSelectedItem();

    if(fontName.equals("SanSerif")){
       Actions.fireAction(this,"font-family-SansSerif");
    }
    else if(fontName.equals("Serif")){
       Actions.fireAction(this,"font-family-Serif");
    }
    else if(fontName.equals("Monospaced")){
       Actions.fireAction(this,"font-family-Monospaced");
    }
  }


}