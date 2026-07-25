package org.is.html;

import javax.swing.*;
import java.awt.BorderLayout;

/**
 * Wrapping panel for editor (contains necessary controls)
 *
 * @version 1.0
 * @since jdk1.2
 */
public class GHTMLEditorPanel extends JPanel{

    protected GHTMLEditor editor;

    public GHTMLEditorPanel(){

      super();

      setLayout(new BorderLayout());
	    setBorder(BorderFactory.createEtchedBorder());

//editor:
      editor = new GHTMLEditor();

	    JScrollPane htmlScroller = new JScrollPane(); //editor
      htmlScroller.setViewportView(editor);

      add(htmlScroller, BorderLayout.CENTER);

//Toolbars:
      JPanel tbp=new JPanel();
      tbp.setLayout(new BoxLayout(tbp, BoxLayout.Y_AXIS));

      ToolBar toolBar1 = new ToolBar();
      tbp.add(toolBar1);

      ToolBar2 toolBar2 = new ToolBar2();
      tbp.add(toolBar2);

      add(tbp, BorderLayout.NORTH);      

  }

  public GHTMLEditor getEditor(){

     return editor;
  }


}
