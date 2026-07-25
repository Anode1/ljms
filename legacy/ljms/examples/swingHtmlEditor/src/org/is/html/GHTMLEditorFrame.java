package org.is.html;

import java.awt.event.*;
import javax.swing.*;
import java.awt.Toolkit;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Container;
import java.util.Locale;
import javax.swing.event.*;

import org.is.util.Utils;
import org.is.gui.StatusBar;

/**
 * Main editor frame
 *
 * @version 1.0
 * @since jdk1.2
 */
public class GHTMLEditorFrame extends JFrame{

    protected MainFrameMenuBar menuBar;
    protected StatusBar statusBar;
    protected GHTMLEditor editor;

    GHTMLEditorFrame(){

      super();

	    //setTitle(Resources.getString("MainFrame.title"));

      Container contentPane = getContentPane();
      contentPane.setLayout(new BorderLayout());

      GHTMLEditorPanel editorPanel = new GHTMLEditorPanel();
      editor=editorPanel.getEditor();
      contentPane.add(editorPanel, BorderLayout.CENTER);

      menuBar = new MainFrameMenuBar(this);
	    setJMenuBar(menuBar);

      //StatusBar:
      statusBar=new StatusBar();
      contentPane.add(statusBar, BorderLayout.SOUTH);

      pack();

      //get Dimensions of our frame from properties
      try{
         int f_width=Resources.getInteger("MainFrame.size.x");
         int f_height=Resources.getInteger("MainFrame.size.y");
	       this.setSize(new Dimension(f_width,f_height));
      }
      catch(Exception nfe){this.setSize(new Dimension(720,575));} //if not defined in Bundles

      Utils.setCentalizedLocation(this);

	    show();
  }

  public GHTMLEditor getEditor(){
  
     return editor;
  }

  public void dispose(){

     super.dispose();
     TopManager.disposeGHTMLEditorFrame();
  }

/**
 * Entry point of the application
 */
  public static void main(String args[]) {

    //check java version:
    String vers = System.getProperty("java.version");
    if(vers.compareTo("1.3") < 0){
       System.out.println("VM must be 1.3 or higher - exiting");
       System.exit(1);
    }

    try{
       GHTMLEditor editor=TopManager.getGHTMLEditor();
       editor.setHTMLChunk(new TextProducer().getText());
    }catch (Throwable t) {
       System.out.println("uncaught exception: " + t);
       t.printStackTrace();
       try{Thread.sleep(50000);}catch(InterruptedException ie){}
    }
  }

}
