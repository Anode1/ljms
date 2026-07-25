package org.is.html.bundles;

import java.util.ListResourceBundle;

/**
 * Key Bindings class - not used for now.
 * Currently key bindings are set in GHTMLEditor itself
 *
 * @since jdk1.2
 */
public class KeyBindings extends ListResourceBundle{

  public static final Object[][] contents={

      {"help","F1"},
      {"refresh-action","C+r"},
      {"debug-action","C+p"},
      {"copy","F2"},
      {"cut", "F3"}
  };

  public Object[][] getContents(){

     return contents;
  }
     /*
.VK_P, KeyEvent.CTRL_MASK), "debug-action"),
    // new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_MASK), "refresh-action")
     */
}
