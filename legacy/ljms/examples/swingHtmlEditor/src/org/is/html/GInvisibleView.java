package org.is.html;

import javax.swing.event.MouseInputAdapter;
import javax.swing.text.View;
import javax.swing.text.Element;
import javax.swing.text.Position;

/**
 * Invisible View <p>
 * Been used for such elements like HEAD, TITLE etc (visible by default) 
 *
 * @version 1.0
 * @since jdk1.2
 */
public class GInvisibleView extends View{

  public GInvisibleView(Element el) {
    super(el);
    //System.out.println("View created" );
    setSize(0,0);
  }

  public float getPreferredSpan(int x) { return 0; }

  public float getMinimumSpan(int axis) { return 0; }

  public void setSize(int width, int height) { super.setSize(0,0); }

  public float getMaximumSpan(int axis) { return 0; }

  public void paint(java.awt.Graphics g, java.awt.Shape s) { setSize(0,0); }

  public java.awt.Shape modelToView(int pos, java.awt.Shape a, Position.Bias b) { java.awt.Rectangle r = new java.awt.Rectangle(); return r; }

  public int viewToModel(float x, float y, java.awt.Shape a, Position.Bias[] biasReturn) { biasReturn[0] = Position.Bias.Backward; return 0; }

}
