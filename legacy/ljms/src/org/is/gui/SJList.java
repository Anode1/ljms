package org.is.gui;

import javax.swing.*;
import java.awt.Component;
import java.awt.Font;

/**
 * Component made from JList containing iconed labels
 *
 * @since jdk1.2
 */
public class SJList extends JList{

 public SJList(){

    setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    setCellRenderer(new TheRenderer());
    setVisibleRowCount(4);
 }

 class TheRenderer extends DefaultListCellRenderer{

    ///protected final Font font1=new Font("Serif", Font.PLAIN, 12);
    //protected final Font font2=new Font("Serif", Font.BOLD, 14);

    TheRenderer(){
    }

    public Component getListCellRendererComponent(JList list,
                  Object value,            // value to display
                  int index,               // cell index
                  boolean isSelected,      // is the cell selected
                  boolean cellHasFocus){
      Component retValue = super.getListCellRendererComponent(
		    list, value, index, isSelected, cellHasFocus
 	    );

      Service s=(Service)value;
      //this.setOpaque(true);

      String serviceName = s.getName();
      this.setText(serviceName);

      Icon icon=s.getIcon();
      if(icon!=null){
         this.setIcon((Icon)icon);
      }
      /*
   	  if (isSelected) {
         this.setBackground(list.getSelectionBackground());
	       this.setForeground(list.getSelectionForeground());
         this.setFont(font2);
	    }
      else {
	       this.setBackground(list.getBackground());
	       this.setForeground(list.getForeground());
         this.setFont(font1);
	    }
      */
	    //setEnabled(list.isEnabled());
	    //setFont(list.getFont());
      return retValue;
    }
 }

}
