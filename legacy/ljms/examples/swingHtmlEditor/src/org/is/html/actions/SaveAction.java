package org.is.html.actions;

import java.awt.event.ActionEvent;
import java.awt.Frame;
import java.awt.FileDialog;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.swing.JEditorPane;
import javax.swing.AbstractAction;

import org.is.io.FileUtils;
import org.is.html.TopManager;

/**
 * Save into file action.
 * Used in application version of the editor
 *
 */
public class SaveAction extends GHTMLEditAction{

  private FileDialog fileDialog;

	public SaveAction() {

	  super("save");
	}

  public void action(ActionEvent e){

    if(TopManager.isApplet()){
       javax.swing.JOptionPane.showMessageDialog(null, "File operations are not alowed in the applet due to security reasons");
       return;
    }

	  Frame frame = TopManager.getGHTMLEditor().getFrame();
	  if (fileDialog == null)fileDialog = new FileDialog(frame);
    fileDialog.setMode(FileDialog.SAVE);
    fileDialog.show();
    String file = fileDialog.getFile();
    if (file == null)return;
    String directory = fileDialog.getDirectory();
    File f = new File(directory, file);
    try{
       //String str=editor.getText();
       String str=editor.getHTMLChunk();
       if(str==null)return;
       FileUtils.string2File(str, f.getAbsolutePath());

	  }catch (Exception ex) {
		   System.err.println("SaveAction::"+ex);
	  }
	}

  /*
    private void doSaveAs () {
        FileDialog fileDialog = new FileDialog (this, "Save As...", FileDialog.SAVE);
        fileDialog.show ();
        if (fileDialog.getFile () == null)
            return;
        fileName = fileDialog.getDirectory () + File.separator + fileDialog.getFile ();

        doSave (fileName);
    }
  */



}
