package org.is.html;

import java.awt.*;
import java.awt.event.*;
import java.beans.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.swing.*;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Position;
import javax.swing.event.*;
import javax.swing.plaf.*;
import java.util.EventListener; 

/**
 * We probably will want to implement our own caret in the future
 */
public class GCaret extends DefaultCaret{

    /**
    protected Highlighter.HighlightPainter getSelectionPainter() {
	    return DefaultHighlighter.DefaultPainter;
    }
*/

     /*
    public void focusGained(FocusEvent e) {
    // PATCH: If component isn't editable, the caret should also reactivate on focus gained,
    // only disabled components have no caret. Otherwise marking text in a not etable text component
    // would only work with the mouse.

    if (component.isEditable()) {
	    if (component.isEnabled()) {
	      setVisible(true);
	      setSelectionVisible(true);
	    }
    }
      */


}

