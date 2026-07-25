package org.is.html;

import java.awt.*;
import javax.swing.event.*;
import javax.swing.text.*;
import javax.swing.text.html.*;

/**
 * Dummy element -- not used for now but will be needed when we'll implement our
 * own views
 *
 * @version 1.0
 * @since jdk1.2
 */
public class DummyElement implements Element{

    public Document doc;
    public AttributeSet attr;

    Element parent;

    public DummyElement() {
     super();
     doc = new HTMLDocument(/*new StubStyleSheet()*/);
     attr = new SimpleAttributeSet();
    }

    public Document getDocument() { return doc; }
    public Element getParentElement() { return parent; }
    public String getName() { return "StubElement"; }
    public AttributeSet getAttributes() { return attr; }
    public int getStartOffset() { return 0; }
    public int getEndOffset() { return 3; }
    public int getElementIndex(int offset) { return 0; }
    public int getElementCount() { return 1; }
    public Element getElement(int index) { return this; }
    public boolean isLeaf() { return true; }
    public int getLineLimit() {return 0;}
    public void stub_setAttributes(AttributeSet attr) {
     this.attr=attr;
    }

  /**
   * Inner class defining the View
   */
  class StubView extends View {

    public StubView(Element elem) {
        super(elem);
    }

    public View getParent() {
        return this;
    }

    public float getPreferredSpan(int axis) { return 0;}
    public float getMinimumSpan(int axis) { return 0;}
    public float getMaximumSpan(int axis) { return 0;}
    public void preferenceChanged(View child, boolean width, boolean height) {}
    public float getAlignment(int axis) { return 0;}
    public void paint(Graphics g, Shape allocation) {}
    public void setParent(View parent) {
      this.parent = parent;
    }
    public int getViewCount() { return 0; }
    public View getView(int n) {return this;}
    public Shape getChildAllocation(int index, Shape a) {
      return null;
    }
    public int getNextVisualPositionFrom(int pos, Position.Bias b, Shape a,
                                         int direction, Position.Bias[] biasRet) {
      return 0;
    }
    public Shape modelToView(int pos, Shape a, Position.Bias b) throws BadLocationException {
      return null;
    };

    public int viewToModel(float x, float y, Shape a, Position.Bias[] biasReturn) {
      return 0;
    }

    public void insertUpdate(DocumentEvent e, Shape a, ViewFactory f) {}
    public void removeUpdate(DocumentEvent e, Shape a, ViewFactory f) {}
    public void changedUpdate(DocumentEvent e, Shape a, ViewFactory f) {}

    public Document getDocument() {
        return elem.getDocument();
    }

    public int getStartOffset() {
        return elem.getStartOffset();
    }

    public int getEndOffset() {
        return elem.getEndOffset();
    }

    public Element getElement() {
        return elem;
    }

    public AttributeSet getAttributes() {
        return elem.getAttributes();
    }

    public View breakView(int axis, int offset, float pos, float len) {
        return this;
    }

    public View createFragment(int p0, int p1) {
        return this;
    }

    public int getBreakWeight(int axis, float pos, float len) {return 0;}

    public int getResizeWeight(int axis) {return 0;}

    public void setSize(float width, float height) {}

    public Container getContainer() {
        View v = getParent();
        return (v != null) ? v.getContainer() : null;
    }

    public ViewFactory getViewFactory() {
        View v = getParent();
        return (v != null) ? v.getViewFactory() : null;
    }

    public int viewToModel(float x, float y, Shape a) {return 0;}

    private View parent;

    private Element elem;
  }


}

