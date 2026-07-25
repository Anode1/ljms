package org.is.html.dialogs;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.io.*;
import java.net.URL;
import java.util.*;

import javax.swing.text.*;
import javax.swing.text.html.HTMLDocument;
import javax.swing.event.*;
import javax.swing.*;

/**
 * Html src editor (currently used only as a viewer i.e. not editable)
 *
 * @since jdk1.2
 */
public class GSrcEditor extends JTextPane{// implements CaretListener, KeyListener {

  private Document doc; //just a ref to single document
  private GSrcEditorKit skit;

  public GSrcEditor(){

     setEditable(false);

     skit=new GSrcEditorKit();
     this.setEditorKitForContentType("html/text",skit);
     this.setContentType("html/text");

     Keymap parent = getKeymap();

     getCaret().setBlinkRate(500);

		// addCaretListener(this);
		// addKeyListener(this);

     //addHyperlinkListener(new SimpleLinkListener(jep, urlField, statusBar));
	   //getDocument().addUndoableEditListener(undoHandler);

  }

  /*
  public void save(){

    StringWriter sw=new StringWriter();
    c.write(sw,doc,0,10);
  }
  */

  /**
   * Workaround a bug in DefaultStyledDocument.remove, which is
   * invoked when setText is invoked.
   */
  public void set(String text){

     doc=skit.createDefaultDocument();
		 setDocument(doc);

     //(new Loader(skit, new StringReader(text),doc)).start();

     try{
        skit.read(new BufferedReader(new StringReader(text)),doc,0);
     }
     catch(Exception e){
        e.printStackTrace();
     }

     setCaretPosition(0);

  }
 /*
  public synchronized void setTextAfter(final String text){

    Runnable worker = new Runnable(){
      public void run(){
        setText(text);
      }
    };
    SwingUtilities.invokeLater(worker);
  }
  */
  public boolean getScrollableTracksViewportWidth(){

    return false; // Sun is hard-coding true for JEditorPane/JTextPane!
  }

  private void clearText(){

    setText("");
  }


}
