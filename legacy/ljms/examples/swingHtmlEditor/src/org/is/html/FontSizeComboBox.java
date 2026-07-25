package org.is.html;

import javax.swing.JComboBox;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import java.awt.event.ActionListener;
import java.awt.Dimension;

import org.is.html.actions.Actions;

public class FontSizeComboBox extends JComboBox implements ActionListener{

  public FontSizeComboBox(){

    populate();

    Dimension dim=new Dimension(50,24);
    setPreferredSize(dim);
    setMinimumSize(dim);
    setMaximumSize(dim);

    addActionListener(this);
  }

  //implement dynamic fonts!
  void populate(){

    //available font sizes. We can do more but anyway they will be approximated but these ones
 	  addItem("8");
 	  addItem("10");
 	  addItem("12");
 	  addItem("14");
 	  addItem("16");
 	  addItem("18");
 	  addItem("24");
 	  addItem("36");
 	  addItem("48");
  }

  public void actionPerformed(ActionEvent e){

 	  if(e.getSource() != this){
       System.err.println("FontsComboBox::unknown event source");
    }

 	  String sizeString = (String)getSelectedItem();
    //System.out.println(sizeString);
    if(sizeString.equals("8")){
       Actions.fireAction(this,"font-size-8");
    }
    else if(sizeString.equals("10")){
       Actions.fireAction(this,"font-size-10");
    }
    else if(sizeString.equals("12")){
       Actions.fireAction(this,"font-size-12");
    }
    else if(sizeString.equals("14")){
       Actions.fireAction(this,"font-size-14");
    }
    else if(sizeString.equals("16")){
       Actions.fireAction(this,"font-size-16");
    }
    else if(sizeString.equals("18")){
       Actions.fireAction(this,"font-size-18");
    }
    else if(sizeString.equals("24")){
       Actions.fireAction(this,"font-size-24");
    }
    else if(sizeString.equals("36")){
       Actions.fireAction(this,"font-size-36");
    }
    else if(sizeString.equals("48")){
       Actions.fireAction(this,"font-size-48");
    }

  }

}
