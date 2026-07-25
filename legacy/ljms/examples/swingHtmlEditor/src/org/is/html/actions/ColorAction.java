package org.is.html.actions;

import java.awt.event.ActionEvent;
import java.awt.Color;
import javax.swing.JColorChooser;
import javax.swing.JTextPane;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.StyleConstants;

import org.is.html.TopManager;

/**
 * Foreground color action
 *
 * @since jdk1.2
 */
public class ColorAction extends GHTMLEditAction{

	public ColorAction(){

		super("fg-color");
	}

	public void action(ActionEvent e){

    Color color = JColorChooser.showDialog(TopManager.getGHTMLEditor().getFrame(), "Color Chooser", Color.black);
    if (color != null) {
       MutableAttributeSet attr = new SimpleAttributeSet();
       StyleConstants.setForeground(attr, color);
       editor.setCharacterAttributes(attr, false);
    }
    
		editor.repaint();
		editor.requestFocus();

  }

}

