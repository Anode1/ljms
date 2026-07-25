package org.is.html;

import java.net.MalformedURLException;
import java.net.URL;
import java.io.Serializable;
import java.io.IOException;
import javax.swing.event.MouseInputAdapter;
import java.awt.event.MouseEvent;
import java.awt.Point;
import java.awt.event.ActionEvent;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.text.html.parser.Parser;

import org.is.html.actions.*;

/**
 * Wrapper for HTMLEditorKit
 *
 * @version 1.0
 * @since jdk1.2 
 */
public class GHTMLEditorKit extends HTMLEditorKit{

  private static Parser parser = new GParserDelegator();

  public GHTMLEditorKit(){ 
  }

  /**
   * Has been overriden to implement our own callback from parser
   */
  public Document createDefaultDocument() {

	  StyleSheet styles = getStyleSheet();
	  StyleSheet ss = new StyleSheet();

	  ss.addStyleSheet(styles);

	  GHTMLDocument doc = new GHTMLDocument(ss);
	  doc.setParser(getParser());
	  doc.setAsynchronousLoadPriority(4);
    //doc.setAsynchronousLoadPriority(-1); //PageLoader thread won't be started and the content should be loaded synchronous
	  doc.setTokenThreshold(100);
	  return doc;
  }

  public HTMLEditorKit.Parser getParser(){

    return parser;
  }

  public ViewFactory getViewFactory(){

    return new GHTMLFactory();
  }
            /*
 public void updateInputAttributes(Element element, MutableAttributeSet set){

    createInputAttributes(element, set);
 }
              */
  /**
   * Overriden super's method: workaround for bug #4228340
   */
  protected void createInputAttributes(Element element, MutableAttributeSet set) {

    super.createInputAttributes(element, set);
	  Object o = set.getAttribute(StyleConstants.NameAttribute);
    if(o == HTML.Tag.BR ){
        set.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
    }
  }


//  LinkController myController = new LinkController();
  /*
  public void install(JEditorPane c){

    c.addMouseListener(myController);
    c.addMouseMotionListener(myController);
    //super.install(c);
  }
   */

   /*
  public static class LinkController extends MouseInputAdapter implements Serializable {

    URL currentUrl = null;

    public void mouseClicked(MouseEvent e) {

      JEditorPane editor = (JEditorPane) e.getSource();

      if (!editor.isEditable()) {
	      Point pt = new Point(e.getX(), e.getY());
	      int pos = editor.viewToModel(pt);
	      if (pos >= 0) {
	        activateLink(pos, editor, JUMP);
      	}
      }
    }

    public void mouseMoved(MouseEvent e) {

      JEditorPane editor = (JEditorPane) e.getSource();

      if (!editor.isEditable()) {
	      Point pt = new Point(e.getX(), e.getY());
	      int pos = editor.viewToModel(pt);
	      if (pos >= 0) {
	        activateLink(pos, editor, MOVE);
      	}
      }
    }

    protected void activateLink(int pos, JEditorPane html, int type) {

      Document doc = html.getDocument();
      if (doc instanceof HTMLDocument) {
	    HTMLDocument hdoc = (HTMLDocument) doc;
	    Element e = hdoc.getCharacterElement(pos);
	    AttributeSet a = e.getAttributes();
	    AttributeSet anchor = (AttributeSet) a.getAttribute(HTML.Tag.A);
	    String href = (anchor != null) ?
	      (String) anchor.getAttribute(HTML.Attribute.HREF) : null;
    	boolean shouldExit = false;

	    HyperlinkEvent linkEvent = null;
	    if (href != null) {
	      URL u;

	      try {
	        u = new URL(hdoc.getBase(), href);
	      } catch (MalformedURLException m) {
	        u = null;
	      }

	    if ((type == MOVE) && (u!=null) && (!u.equals(currentUrl))) {
	      linkEvent = new HyperlinkEvent(html, HyperlinkEvent.EventType.ENTERED, u, href);
	      currentUrl = u;
	  }
	  else if (type == JUMP) {
	    linkEvent = new HyperlinkEvent(html,
			HyperlinkEvent.EventType.ACTIVATED, u, href);
	    shouldExit = true;
	  }
	  else {
	    return;
	  }
	  html.fireHyperlinkUpdate(linkEvent);
	}
	else if (currentUrl != null) {
	  shouldExit = true;
	}
	if (shouldExit) {
	  linkEvent = new HyperlinkEvent(html, HyperlinkEvent.EventType.EXITED, currentUrl, null);
	  html.fireHyperlinkUpdate(linkEvent);
	  currentUrl = null;
	}
      }
    }
  }
     */

   //==========================================================================
   // ACTIONS
   //==========================================================================

   //constants:


    private static final Action[] defaultActions = {

      //fixing focus Sun's focus bug by overriding basic events from StyledEditorKit:
      new org.is.html.actions.FixedSunActions.BoldAction(),
      new org.is.html.actions.FixedSunActions.ItalicAction(),
      new org.is.html.actions.FixedSunActions.UnderlineAction(),
	    new org.is.html.actions.FixedSunActions.FontFamilyAction("font-family-SansSerif", "SansSerif"),
	    new org.is.html.actions.FixedSunActions.FontFamilyAction("font-family-Monospaced", "Monospaced"),
	    new org.is.html.actions.FixedSunActions.FontFamilyAction("font-family-Serif", "Serif"),
	    new org.is.html.actions.FixedSunActions.FontSizeAction("font-size-8", 8),
	    new org.is.html.actions.FixedSunActions.FontSizeAction("font-size-10", 10),
	    new org.is.html.actions.FixedSunActions.FontSizeAction("font-size-12", 12),
	    new org.is.html.actions.FixedSunActions.FontSizeAction("font-size-14", 14),
	    new org.is.html.actions.FixedSunActions.FontSizeAction("font-size-16", 16),
	    new org.is.html.actions.FixedSunActions.FontSizeAction("font-size-18", 18),
	    new org.is.html.actions.FixedSunActions.FontSizeAction("font-size-24", 24),
	    new org.is.html.actions.FixedSunActions.FontSizeAction("font-size-36", 36),
	    new org.is.html.actions.FixedSunActions.FontSizeAction("font-size-48", 48),
	    new org.is.html.actions.FixedSunActions.AlignmentAction("left-justify", StyleConstants.ALIGN_LEFT),
	    new org.is.html.actions.FixedSunActions.AlignmentAction("center-justify", StyleConstants.ALIGN_CENTER),
	    new org.is.html.actions.FixedSunActions.AlignmentAction("right-justify", StyleConstants.ALIGN_RIGHT),
      new org.is.html.actions.FixedSunActions.CutAction(),
      new org.is.html.actions.FixedSunActions.CopyAction(),
      new org.is.html.actions.FixedSunActions.PasteAction(),
      //fixed but defined in this class (due to package protected access in Sun's classes):
      new InsertHRAction(),
      new ReadOnlyAction(),
      new WritableAction(),

      //events implemented by our GEditorKit:
      new org.is.html.actions.NewAction(),
      new org.is.html.actions.OpenAction(),
      new org.is.html.actions.SaveAction(),
      new org.is.html.actions.ShowSourceAction(),
      new org.is.html.actions.ShowModelAction(),
      new org.is.html.actions.ColorAction(),
      new org.is.html.actions.NewAction(),
      new org.is.html.actions.UndoAction(),
      new org.is.html.actions.RedoAction(),
      new org.is.html.actions.InsertImageAction(),
      new org.is.html.actions.ImageAction(),
      new org.is.html.actions.PhotoAction(),
      new org.is.html.actions.RefreshAction(),
      new org.is.html.actions.InsertTableAction(),
      new org.is.html.actions.LinkAction(),
      new org.is.html.actions.BreakAction(),
      new org.is.html.actions.InsertTableColumnBeforeAction(),
      new org.is.html.actions.InsertTableColumnAction(),
      new org.is.html.actions.InsertTableRowBeforeAction(),
      new org.is.html.actions.InsertTableRowAction(),
      new org.is.html.actions.DeleteTableColumnAction(),
      new org.is.html.actions.DeleteTableRowAction(),
      new org.is.html.actions.TableDeleteAction(),

      new org.is.html.actions.InsertUnorderedListItemAction(),
      new org.is.html.actions.InsertOrderedListItemAction(),
      new org.is.html.actions.ListOrderedAction(),
      new org.is.html.actions.ListUnorderedAction(),

      new org.is.html.actions.DeletePrevCharAction(),
      new org.is.html.actions.DeleteNextCharAction(),
      //new org.is.actions.EnterKeyAction(), //we don't use it for now (problem is in the default
                                                 //behaviour when P element should be generated)
                                                 //
      new org.is.html.actions.GoToLinkAction(),
      new org.is.html.actions.HelpAction(),
      new org.is.html.actions.DebugAction()
    };

    /**
     * Returns all actions assotiated wit this EditorKit
     */
    public Action[] getActions() {

	    return TextAction.augmentList(super.getActions(), this.defaultActions);
    }

    /**
     * This kit aware action
     */
    public static abstract class GHTMLEditorAction extends HTMLTextAction{

      public GHTMLEditorAction(String nm){

	      super(nm);
    	}

      protected final GHTMLEditor getGHTMLEditor(ActionEvent e) {

	      return TopManager.getGHTMLEditor();
	    }

	    public GHTMLDocument getGHTMLDocument(JEditorPane e) {

		    return (GHTMLDocument)e.getDocument();
	    }

      protected final GHTMLEditorKit getGHMLEditorKit(JEditorPane e) {

	      return (GHTMLEditorKit)e.getEditorKit();
      }
    }

/////////////////// OVERRIDEN ACTIONS /////////////////////////////
//         (All other actions are in actions package)
    /**
     * An abstract Action providing some convenience methods that may
     * be useful in inserting HTML into an existing document.
     * <p>NOTE: None of the convenience methods obtain a lock on the
     * document. If you have another thread modifying the text these
     * methods may have inconsistant behavior, or return the wrong thing.
     */
    /*public */static abstract class HTMLTextAction extends StyledTextAction {
	public HTMLTextAction(String name) {
	    super(name);
	}

	/**
	 * @return HTMLDocument of <code>e</code>.
	 */
	protected HTMLDocument getHTMLDocument(JEditorPane e) {
      //out fix: not to have null if the source is not the Editor

	    Document d =TopManager.getGHTMLEditor().getDocument();
      //

	    if (d instanceof HTMLDocument) {
	    	return (HTMLDocument) d;
	    }
	    throw new IllegalArgumentException("document must be HTMLDocument");
	}

	/**
	 * @return HTMLEditorKit for <code>e</code>.
	 */
  protected HTMLEditorKit getHTMLEditorKit(JEditorPane e) {
	    EditorKit k = e.getEditorKit();
	    if (k instanceof HTMLEditorKit) {
		    return (HTMLEditorKit) k;
	    }
	    throw new IllegalArgumentException("EditorKit must be HTMLEditorKit");
	}

	/**
	 * Returns an array of the Elements that contain <code>offset</code>.
	 * The first elements corresponds to the root.
	 */
	protected Element[] getElementsAt(HTMLDocument doc, int offset) {

	    return getElementsAt(doc.getDefaultRootElement(), offset, 0);
	}

	/**
	 * Recursive method used by getElementsAt.
	 */
	private Element[] getElementsAt(Element parent, int offset, int depth) {

	    if (parent.isLeaf()) {
		    Element[] retValue = new Element[depth + 1];
		    retValue[depth] = parent;
		    return retValue;
	    }
	    Element[] retValue = getElementsAt(parent.getElement(parent.getElementIndex(offset)), offset, depth + 1);
	    retValue[depth] = parent;
	    return retValue;
	}

	/**
	 * Returns number of elements, starting at the deepest leaf, needed
	 * to get to an element representing <code>tag</code>. This will
	 * return -1 if no elements is found representing <code>tag</code>,
	 * or 0 if the parent of the leaf at <code>offset</code> represents
	 * <code>tag</code>.
	 */
	protected int elementCountToTag(HTMLDocument doc, int offset, HTML.Tag tag) {

	    int depth = -1;
	    Element e = doc.getCharacterElement(offset);
	    while (e != null && e.getAttributes().getAttribute(StyleConstants.NameAttribute) != tag) {
		    e = e.getParentElement();
		    depth++;
	    }
	    if (e == null) {
		    return -1;
	    }
	    return depth;
	}

	/**
	 * Returns the deepest element at <code>offset</code> matching
	 * <code>tag</code>.
	 */
	  protected Element findElementMatchingTag(HTMLDocument doc, int offset, HTML.Tag tag) {

	    Element e = doc.getDefaultRootElement();
	    Element lastMatch = null;
	    while (e != null) {
		    if (e.getAttributes().getAttribute(StyleConstants.NameAttribute) == tag) {
		      lastMatch = e;
    		}
		    e = e.getElement(e.getElementIndex(offset));
	    }
	    return lastMatch;
    }
  }

	public void insertHTMLSafe(JEditorPane editor, HTMLDocument doc, int offset,
                            String html, int popDepth, int pushDepth, HTML.Tag addTag)throws IOException, BadLocationException{

        ElementIterator it = new ElementIterator(doc.getDefaultRootElement());
        Element head = null;
        while ((head = it.next()) != null) { //find body
          if(HTMLUtils.matchNameAttribute(head, HTML.Tag.HEAD)){
            break;
          }
        }
        if(head==null){
          System.err.println("GHTMLEditorKit::insertHTML:head==null?");
          return;
        }

        if(offset>head.getEndOffset()){  //if insertHTML will go into body for sure
          //System.out.println("super called");
   		    super.insertHTML(doc, offset, html, popDepth, pushDepth, addTag);
          return;
        }

        //System.out.println("in head");
        it = new ElementIterator(doc.getDefaultRootElement());
        Element body = null;
        while ((body = it.next()) != null) { //find body
          if(HTMLUtils.matchNameAttribute(body, HTML.Tag.BODY)){
            doc.insertAfterStart(body, html);
            return;
          }
        }
        System.err.println("GHTMLEditorKit::insertHTML: Body is not found?");
	}
     

    /**
     * InsertHTMLTextAction can be used to insert an arbitrary string of HTML
     * into an existing HTML document. At least two HTML.Tags need to be
     * supplied. The first Tag, parentTag, identifies the parent in
     * the document to add the elements to. The second tag, addTag,
     * identifies the first tag that should be added to the document as
     * seen in the HTML string. One important thing to remember, is that
     * the parser is going to generate all the appropriate tags, even if
     * they aren't in the HTML string passed in.<p>
     * For example, lets say you wanted to create an action to insert
     * a table into the body. The parentTag would be HTML.Tag.BODY,
     * addTag would be HTML.Tag.TABLE, and the string could be something
     * like &lt;table&gt;&lt;tr&gt;&lt;td&gt;&lt;/td&gt;&lt;/tr&gt;&lt;/table&gt;.
     * <p>There is also an option to supply an alternate parentTag and
     * addTag. These will be checked for if there is no parentTag at
     * offset.
     */
  public static class InsertHTMLTextAction extends HTMLTextAction {
	  public InsertHTMLTextAction(String name, String html,
				    HTML.Tag parentTag, HTML.Tag addTag) {
	    this(name, html, parentTag, addTag, null, null);
	}

	public InsertHTMLTextAction(String name, String html,
				    HTML.Tag parentTag,
				    HTML.Tag addTag,
				    HTML.Tag alternateParentTag,
				    HTML.Tag alternateAddTag) {
	    this(name, html, parentTag, addTag, alternateParentTag,
		 alternateAddTag, true);
	}

	/* public */
	InsertHTMLTextAction(String name, String html,
				    HTML.Tag parentTag,
				    HTML.Tag addTag,
				    HTML.Tag alternateParentTag,
				    HTML.Tag alternateAddTag,
				    boolean adjustSelection) {
	    super(name);
	    this.html = html;
	    this.parentTag = parentTag;
	    this.addTag = addTag;
	    this.alternateParentTag = alternateParentTag;
	    this.alternateAddTag = alternateAddTag;
	    this.adjustSelection = adjustSelection;
	}

	/**
	 * A cover for HTMLEditorKit.insertHTML. If an exception it
	 * thrown it is wrapped in a RuntimeException and thrown.
   * Also it is more safe (not inserts into HEAD like it occurs with superclass)
	 */
	protected void insertHTML(JEditorPane editor, HTMLDocument doc, int offset,
                            String html, int popDepth, int pushDepth, HTML.Tag addTag) {

	    try{
		    getHTMLEditorKit(editor).insertHTML(doc, offset, html, popDepth, pushDepth, addTag);
	    }catch (IOException ioe) {
		    throw new RuntimeException("Unable to insert: " + ioe);
	    }catch (BadLocationException ble) {
		    throw new RuntimeException("Unable to insert: " + ble);
	    }
	}

  protected void insertAtBoundary(JEditorPane editor, HTMLDocument doc,
					int offset, Element insertElement,
					String html, HTML.Tag parentTag,
					HTML.Tag addTag) {
	    insertAtBoundry(editor, doc, offset, insertElement, html,
			    parentTag, addTag);
	}

	protected void insertAtBoundry(JEditorPane editor, HTMLDocument doc,
				       int offset, Element insertElement,
				       String html, HTML.Tag parentTag,
				       HTML.Tag addTag) {
	    // Find the common parent.
	    Element e;
	    Element commonParent;
	    boolean isFirst = (offset == 0);

	    if (offset > 0 || insertElement == null) {
		e = doc.getDefaultRootElement();
		while (e != null && e.getStartOffset() != offset &&
		       !e.isLeaf()) {
		    e = e.getElement(e.getElementIndex(offset));
		}
		commonParent = (e != null) ? e.getParentElement() : null;
	    }
	    else {
		// If inserting at the origin, the common parent is the
		// insertElement.
		commonParent = insertElement;
	    }
	    if (commonParent != null) {
		// Determine how many pops to do.
		int pops = 0;
		int pushes = 0;
		if (isFirst && insertElement != null) {
		    e = commonParent;
		    while (e != null && !e.isLeaf()) {
			e = e.getElement(e.getElementIndex(offset));
			pops++;
		    }
		}
		else {
		    e = commonParent;
		    offset--;
		    while (e != null && !e.isLeaf()) {
			e = e.getElement(e.getElementIndex(offset));
			pops++;
		    }

		    // And how many pushes
		    e = commonParent;
		    offset++;
		    while (e != null && e != insertElement) {
			e = e.getElement(e.getElementIndex(offset));
			pushes++;
		    }
		}
		pops = Math.max(0, pops - 1);

		// And insert!
		insertHTML(editor, doc, offset, html, pops, pushes, addTag);
	    }
	}

	boolean insertIntoTag(JEditorPane editor, HTMLDocument doc,
			      int offset, HTML.Tag tag, HTML.Tag addTag) {
	    Element e = findElementMatchingTag(doc, offset, tag);
	    if (e != null && e.getStartOffset() == offset) {
		insertAtBoundary(editor, doc, offset, e, html,
				 tag, addTag);
		return true;
	    }
	    else if (offset > 0) {
		int depth = elementCountToTag(doc, offset - 1, tag);
		if (depth != -1) {
		    insertHTML(editor, doc, offset, html, depth, 0, addTag);
		    return true;
		}
	    }
	    return false;
	}

	void adjustSelection(JEditorPane pane, HTMLDocument doc, 
			     int startOffset, int oldLength) {
	    int newLength = doc.getLength();
	    if (newLength != oldLength && startOffset < newLength) {
		if (startOffset > 0) {
		    String text;
		    try {
			text = doc.getText(startOffset - 1, 1);
		    } catch (BadLocationException ble) {
			text = null;
		    }
		    if (text != null && text.length() > 0 &&
			text.charAt(0) == '\n') {
			pane.select(startOffset, startOffset);
		    }
		    else {
			pane.select(startOffset + 1, startOffset + 1);
		    }
		}
		else {
		    pane.select(1, 1);
		}
	    }
	}

  public void actionPerformed(ActionEvent ae) {
        //our fix not to have null when button is pressed
           JEditorPane editor=TopManager.getGHTMLEditor();

        //


	    if (editor != null) {
		HTMLDocument doc = getHTMLDocument(editor);
		int offset = editor.getSelectionStart();
		int length = doc.getLength();
		boolean inserted;
		// Try first choice
		if (!insertIntoTag(editor, doc, offset, parentTag, addTag) &&
		    alternateParentTag != null) {
		    // Then alternate.
		    inserted = insertIntoTag(editor, doc, offset,
					     alternateParentTag,
					     alternateAddTag);
		}
		else {
		    inserted = true;
		}
		if (adjustSelection && inserted) {
		    adjustSelection(editor, doc, offset, length);
		}
	    }
	}
     
	/** HTML to insert. */
	protected String html;
	/** Tag to check for in the document. */
	protected HTML.Tag parentTag;
	/** Tag in HTML to start adding tags from. */
	protected HTML.Tag addTag;
	/** Alternate Tag to check for in the document if parentTag is
	 * not found. */
	protected HTML.Tag alternateParentTag;
	/** Alternate tag in HTML to start adding tags from if parentTag
	 * is not found and alternateParentTag is found. */
	protected HTML.Tag alternateAddTag;
	/** True indicates the selection should be adjusted after an insert. */
	boolean adjustSelection;
    }




/////////////////////////////////////////////////////////////////////////

  static class InsertHRAction extends InsertHTMLTextAction {

	  InsertHRAction(){

	    super("InsertHR", "<hr>", null, HTML.Tag.IMPLIED, null, null, false);
	  }

    public void actionPerformed(ActionEvent ae){

	    JEditorPane editor = getEditor(ae);
	    if (editor != null) {
		    HTMLDocument doc = getHTMLDocument(editor);
		    int offset = editor.getSelectionStart();
		    Element paragraph = doc.getParagraphElement(offset);
		    if (paragraph.getParentElement() != null) {
		      parentTag = (HTML.Tag)paragraph.getParentElement().getAttributes().getAttribute(StyleConstants.NameAttribute);
		      super.actionPerformed(ae);

          editor.requestFocus();
		    }
	    }
	  }

  }//InsertHRAction


///////////////////////////////////////////////////////////////////////////////

    static class ReadOnlyAction extends TextAction {

        ReadOnlyAction() {
            super("set-read-only");
        }

        public void actionPerformed(ActionEvent e) {
            JTextComponent target = getTextComponent(e);
            if (target != null) {
                target.setEditable(false);

                //patch:
                Caret caret=target.getCaret();
                caret.setVisible(false);
	              caret.setSelectionVisible(false);
                //
            }
        }
    }

    static class WritableAction extends TextAction {

        WritableAction() {
            super("set-writable");
        }

        public void actionPerformed(ActionEvent e) {
            JTextComponent target = getTextComponent(e);
            if (target != null) {
                target.setEditable(true);
                //patch:
                Caret caret=target.getCaret();
                caret.setVisible(true);
	              caret.setSelectionVisible(true);
                //

            }
        }
    }

}
