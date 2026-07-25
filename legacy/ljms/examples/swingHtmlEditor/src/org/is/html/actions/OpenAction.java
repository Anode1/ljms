package org.is.html.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.text.JTextComponent;
import java.io.IOException;
import java.io.File;
import java.awt.FileDialog;
import java.awt.Frame;

import org.is.io.FileUtils;
import org.is.html.*;

/**
 * Open file action.
 * Used in application version of the editor 
 *
 * @since jdk1.2
 */
public class OpenAction extends GHTMLEditAction{

  private FileDialog fileDialog;

  public OpenAction(){

	  super("open");
	}

  public void action(ActionEvent e) {

    if(TopManager.isApplet()){
       javax.swing.JOptionPane.showMessageDialog(null, "File operations are not alowed in the applet due to security reasons");
       return;
    }

    Frame frame=TopManager.getGHTMLEditor().getFrame();

	  if (fileDialog == null) fileDialog = new FileDialog(frame,"Open...", FileDialog.LOAD);
    else fileDialog.setMode(FileDialog.LOAD);

	  fileDialog.show();

	  String file = fileDialog.getFile();
	  if (file == null) {
		  return;
    }
	  String directory = fileDialog.getDirectory();
	  File f = new File(directory, file);
	  if (f.exists()) {
		  try {

        /**
		    if(getGEditor().getDocument() != null){
			    getEditor().getDocument().removeUndoableEditListener(undoHandler);
        }
        */

		    //editor.set(FileUtils.fileToString(f.getAbsolutePath()));
        editor.setHTMLChunk(FileUtils.fileToString(f.getAbsolutePath()));


		    //doc.addUndoableEditListener(undoHandler);
		    //resetUndoManager();
		    frame.setTitle(Resources.getString("MainFrame.title")+" ["+file+"]");

		    editor.validate();
        
		  }catch (IOException ie) {
		    // should put in status panel
		    System.err.println("OpenAction::"+ie);
		  }
      catch (Exception ge) {
		    // should put in status panel
		    System.err.println("OpenAction::"+ge);
		  }

    }else{
		  // should put in status panel
		  System.err.println("OpenAction::No such file: " + f);
    }
	}

  /*
  public void doLoadCommand() {
    String msg;
    JFileChooser chooser = new JFileChooser();
    int status = chooser.showOpenDialog(this);
    if (status == JFileChooser.APPROVE_OPTION) {
      char data[];
      final Runnable doWaitCursor = new Runnable() {
        public void run() {
          setCursor (Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        }
      };
      Thread appThread = new Thread() {
        public void run() {
          try {
             SwingUtilities.invokeAndWait(doWaitCursor);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      };
      appThread.start(); 
      File f = chooser.getSelectedFile();
      try {

        // Clear out current document

        // Read in text file
        FileReader fin = new FileReader (f);
        BufferedReader br = new BufferedReader (fin);
        char buffer[] = new char[4096];
        int len;
        while ((len = br.read (buffer, 0, buffer.length)) != -1) {

          // Insert into pane

        }
        statusInfo.setText ("Loaded: " + f.getName());
      } catch (BadLocationException exc) {
        statusInfo.setText ("Error loading: " + f.getName());
      } catch (FileNotFoundException exc) {
        statusInfo.setText ("File Not Found: " + f.getName());
      } catch (IOException exc) {
        statusInfo.setText ("IOException: " + f.getName());
      }
      final Runnable undoWaitCursor = new Runnable() {
        public void run() {
        setCursor (Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
      };
      appThread = new Thread() {
        public void run() {
          try {
             SwingUtilities.invokeAndWait(undoWaitCursor);
          } catch (Exception e) {
            e.printStackTrace();
          }
        }
      };
      appThread.start(); 
    }
  }

  */
}

