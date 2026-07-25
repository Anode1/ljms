package org.is.html.dialogs;

import javax.swing.*;
import java.awt.Frame;

import org.is.html.*;
import org.is.util.Utils;

/**
 * Base dialog for elements insertion
 */
public class InsertDialog extends JDialog{

  private boolean cancelled;

  public InsertDialog() {

    super(TopManager.getGHTMLEditor().getFrame(), true);
  }

  public boolean isCancelled(){

    return cancelled;
  }

  public void setCancelled(boolean cancelled){

    this.cancelled=cancelled;
  }

}
