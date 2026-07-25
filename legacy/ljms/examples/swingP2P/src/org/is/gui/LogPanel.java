package org.is.gui;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import java.awt.event.*;
import java.util.*;
import java.awt.*;

/**
 * Logs Panel (GUI for event logs)
 *
 * @version 1.0
 * @since jdk1.2
 */
public class LogPanel extends JPanel{

  private JTextArea ta;
  private int size;
  public static int MAX_SIZE=200000;

  public LogPanel(){

    setLayout(new BorderLayout());
    setBorder(BorderFactory.createEmptyBorder(5,5,5,5));

//north (table) panel:

    ta=new JTextArea();
    ta.setEnabled(false);
    ta.setEditable(false);

    JScrollPane sp=new JScrollPane();
    sp.getViewport().add(ta);

    add(sp,BorderLayout.CENTER);

    JPanel southP=new JPanel();
    southP.setLayout(new FlowLayout());
    add(southP,BorderLayout.SOUTH);

    JButton cb=new JButton("Clear");
    southP.add(cb);

    cb.addActionListener(new ActionListener(){
       public void actionPerformed(ActionEvent evt){
           //remove();
        SwingUtilities.invokeLater( new Runnable(){
          public void run() {
            remove();
          }
        });

       }
    });

  }//constructor

  public void addRecord(String s){

    if(size+s.length()>MAX_SIZE){
       remove();
    }
    ta.append(s);
  }

  public void remove(){
    ta.setText("");
  }

}
