package org.is.html.dialogs;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

import org.is.util.Utils;
import org.is.html.Resources;
import org.is.html.TopManager;

/**
 * Dialog showing source of html
 */
public class SourceDialog extends JDialog{

  private GSrcEditor srcEditor;

  public SourceDialog() {

    super(TopManager.getGHTMLEditorFrame(),true);

    setSize(new Dimension(800,600));

    //setResizable(false);

    Utils.setCentalizedLocation(this);

    getContentPane().setLayout(new BorderLayout());

    srcEditor = new GSrcEditor();

	  JScrollPane srcScroller = new JScrollPane(srcEditor);

    getContentPane().add(srcScroller, BorderLayout.CENTER);
  }

  public void setText(String text){

    srcEditor.set(text);
  }


}
