package org.is.html.dialogs;

import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import javax.swing.*;

import org.is.html.*;

/**
 * Class used in previous project which should be removed because it is
 * simpler to have separate buttons for each GUI (using last swing's layouts).
 * This class was useful when buttons where awt Buttons and Layout was complicated
 * enough to make it in each GUI. In swing it is simpler to use BoxLayout instead
 * having simple buttons been instantiated
 */
 public class JButtonsBar extends JPanel{

    public JButton apply_button, redo_button, undo_button, cancel_button;

    public JButtonsBar(){

      GridBagConstraints c = new GridBagConstraints();
      GridBagLayout layout = new GridBagLayout();
      setLayout(layout);

      apply_button=new JButton(Resources.getString("Buttons.Ok_button"));
      redo_button=new JButton(Resources.getString("Buttons.Redo_button"));
      undo_button=new JButton(Resources.getString("Buttons.Undo_button"));
      cancel_button = new JButton(Resources.getString("Buttons.Cancel_button"));


      c.gridx=2; c.gridy=0; c.gridwidth=1; c.gridheight=1; c.fill=GridBagConstraints.NONE; c.weightx=0.5; c.weighty=0; c.insets=new Insets(0,5,5,5); c.anchor=GridBagConstraints.EAST;
      layout.setConstraints(apply_button,c);
      add(apply_button,c);
      apply_button.setMinimumSize(new Dimension(200,25));

      apply_button.addActionListener(new java.awt.event.ActionListener(){
        public void actionPerformed(java.awt.event.ActionEvent event){

		    }
      });



      c.gridx=3; c.gridy=0; c.gridwidth=1; c.gridheight=1; c.fill=GridBagConstraints.NONE; c.weightx=0; c.weighty=0; c.insets=new Insets(0,5,5,5); c.anchor=GridBagConstraints.EAST;
      layout.setConstraints(redo_button,c);
      add(redo_button,c);
      redo_button.setMinimumSize(new Dimension(200,25));

      redo_button.addActionListener(new java.awt.event.ActionListener(){
        public void actionPerformed(java.awt.event.ActionEvent event){
		    }
      });
      redo_button.setEnabled(false);


      c.gridx=4; c.gridy=0; c.gridwidth=1; c.gridheight=1; c.fill=GridBagConstraints.NONE; c.weightx=0; c.weighty=0; c.insets=new Insets(0,5,5,5); c.anchor=GridBagConstraints.EAST;
      layout.setConstraints(undo_button,c);
      add(undo_button,c);
      undo_button.setMinimumSize(new Dimension(200,25));

      undo_button.addActionListener(new java.awt.event.ActionListener(){
        public void actionPerformed(java.awt.event.ActionEvent event){

		    }
      });
      undo_button.setEnabled(false);

      c.gridx=5; c.gridy=0; c.gridwidth=1; c.gridheight=1; c.fill=GridBagConstraints.NONE; c.weightx=0; c.weighty=0; c.insets=new Insets(0,5,5,5); c.anchor=GridBagConstraints.EAST;
      layout.setConstraints(cancel_button,c);
      add(cancel_button,c);
      cancel_button.setMinimumSize(new Dimension(200,25));

  }//constructor


}
