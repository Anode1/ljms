package org.is.html;

import java.net.URL;
import javax.swing.text.*;
import javax.swing.tree.TreeNode;
import javax.swing.SwingUtilities;
import javax.swing.event.*;
import javax.swing.undo.*;
import javax.swing.text.html.*;
import java.util.*;

/**
 * An implementation of HTMLDocument
 *
 * @version 1.0
 * @since jdk1.2
 */
public class GHTMLDocument extends javax.swing.text.html.HTMLDocument{// implements DocumentListener{

   protected HTMLEditorKit.Parser parser; //ref to parser created in GHTMLEditorKit

   public static char[] NEWLINE;

   static{
	    SimpleAttributeSet contentAttributeSet = new SimpleAttributeSet();
      ((MutableAttributeSet)contentAttributeSet).addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
	    NEWLINE = new char[1];
	    NEWLINE[0] = '\n';
   }

   public GHTMLDocument(){

      super();
      init();
   }

   public GHTMLDocument(StyleSheet styles){

      super(styles);
      init();
   }

   public GHTMLDocument(AbstractDocument.Content c, StyleSheet styles){

      super(c, styles);
      init();
   }

   private void init(){

     //System.out.println(this.getPreservesUnknownTags());
     //setPreservesUnknownTags(true);

     //addDocumentListener(this);
     //addToStyleHash(getParagraphElement(0));
   }

  /**
   * Overrides super's method
   */
   public HTMLEditorKit.ParserCallback getReader(int pos) {

	   return super.getReader(pos);
   }

  /**
   * Overrides super's method
   */
   public HTMLEditorKit.ParserCallback getReader(int pos, int popDepth, int pushDepth, HTML.Tag insertTag) {

     return super.getReader(pos, popDepth, pushDepth, insertTag);
   }

   /**
    * Returns a ref to the parser defined in GHTMLEditorKit
    */
   public HTMLEditorKit.Parser getParser() {

	   return parser;
   }

   /**
    * This method is called by GHTMLEditorKit
    */
   public void setParser(HTMLEditorKit.Parser parser){

     this.parser=parser;
   }

   /**
    * Convenience delegating method
    */
   public void insert(int pos, DefaultStyledDocument.ElementSpec[] data)throws BadLocationException{

     super.insert(pos, data);
   }

    /**
     * Default method has been overriden to insert UL as a wrapper to deal
     * correctly with the caret 
     */   /*
    protected AbstractElement createDefaultRoot() {

	writeLock();
	MutableAttributeSet a = new SimpleAttributeSet();
	a.addAttribute(StyleConstants.NameAttribute, HTML.Tag.HTML);
	BlockElement html = new BlockElement(null, a.copyAttributes());
	a.removeAttributes(a);
	a.addAttribute(StyleConstants.NameAttribute, HTML.Tag.BODY);
	BlockElement body = new BlockElement(html, a.copyAttributes());
	a.removeAttributes(a);
	a.addAttribute(StyleConstants.NameAttribute, HTML.Tag.P);
	BlockElement paragraph = new BlockElement(body, a.copyAttributes());
	a.removeAttributes(a);
	a.addAttribute(StyleConstants.NameAttribute, HTML.Tag.CONTENT);
	RunElement brk = new RunElement(paragraph, a, 0, 1);

	Element[] buff = new Element[1];

	buff[0] = brk;
	paragraph.replace(0, 0, buff);

	buff[0] = paragraph;
	body.replace(0, 0, buff);

	buff[0] = body;
	html.replace(0, 0, buff);

	writeUnlock();
	return html;
    }
            */
    /**
     * Updates an Element
     */
   public void updateElement(Element element, SimpleAttributeSet newAttr) {

     writeLock();
	   try{
	      DefaultDocumentEvent changes = new DefaultDocumentEvent(element.getStartOffset(), element.getEndOffset() , DocumentEvent.EventType.CHANGE);
  	    AttributeSet sCopy = element.getAttributes().copyAttributes(); //copy of all old attributes
    /*
	    Element[] added = new Element[0];
    	((AbstractDocument.BranchElement)element).replace(0, 0, added);
 */
	      MutableAttributeSet attr = (MutableAttributeSet)element.getAttributes();
	      changes.addEdit(new AttributeUndoableEdit(element, sCopy, false));

	      attr.removeAttributes(attr);
	      attr.addAttributes(newAttr);
	      changes.end();
	      fireChangedUpdate(changes);
	      fireUndoableEditUpdate(new UndoableEditEvent(this, changes));
        
	   }finally{
	      writeUnlock();
     }


    SwingUtilities.invokeLater( new Runnable(){
      public void run() {
        TopManager.getGHTMLEditor().reload();
      }
    });

   }

   public void replaceElement(Element e, String html){

    try{
       setOuterHTML(e, html);
    }
    catch(BadLocationException ex){
       System.err.println("GHTMLDocument::replaceElement:"+ex);
    }
    catch(java.io.IOException ex){
       System.err.println("GHTMLDocument::replaceElement:"+ex);
    }
    catch(Exception ex){
       System.err.println("GHTMLDocument::replaceElement:"+ex);
    }
  }
    /*
  //convenience method - not used anymore
  public void lock(){

    this.writeLock();
  }

  //convenience method - not used anymore
  public void unlock(){

    this.writeUnlock();
  }
      */

  /**
   * Updates documents changes but later, in separate thread
   */
  public void updateDocumentLater(){

    SwingUtilities.invokeLater(new Runnable(){
  	  public void run() {
         updateDocument();
  	  }
    });
  }

  /**
   * Updates the document in the same thread
   */
  public void updateDocument(){

    writeLock();
	  try{
	     DefaultDocumentEvent dde = new DefaultDocumentEvent(0, getLength(), DocumentEvent.EventType.CHANGE);
    	 dde.end();
		   fireChangedUpdate(dde);
	  }
    finally {
		   writeUnlock();
	  }
  }

       /*
  // This method indicates that the definition of the given style has changed. It
  // goes through each of the Elements that use the style and fires an event
  // indicating that the attributes for the Element have changed. This causes the
  // View to re-check the attributes and redraw.
  public void styleUpdated(Style style) {
    // Find the set of Elements that use this style . . .

    Hashtable ht = (Hashtable)styleHash.get(style);

    if (ht != null) {
      // somebody's using it if we get here.

      // Create a Vector of Elements that shouldn't be in this table because they
      // no longer use this Style (we don't remove them when they change Styles,
      // so they will still be here)
      Vector cleanUp = new Vector();

      // Update each Element . . .
      Enumeration e = ht.keys();
      while (e.hasMoreElements()) {
        Element el = (Element)e.nextElement();
        int start = el.getStartOffset();
        int end = el.getEndOffset();
        Style check = getLogicalStyle(start);

        // Fire an event only if this Element is still using this Style.
        if (check == style) {
          AbstractDocument.DefaultDocumentEvent ev = new AbstractDocument.DefaultDocumentEvent(start, end-start, DocumentEvent.EventType.CHANGE);
          fireChangedUpdate(ev);
        }
        else {
          // If not, remove this Element, since it no longer uses this Style
          cleanUp.addElement(el);
        }
      }

      // Clean up . . .
      e = cleanUp.elements();
      while (e.hasMoreElements()) {
        Element bad = (Element)e.nextElement();
        ht.remove(bad);
      }
    }
  }

  // Document Listener Methods

  // Call updateStyleHash() whenever text is inserted
  public void insertUpdate(DocumentEvent ev) { updateStyleHash(ev); }

  // Call updateStyleHash() whenever text is removed
  public void removeUpdate(DocumentEvent ev) { updateStyleHash(ev); }

  // Whenever attributes change, add the paragraph that was changed to our hash.
  public void changedUpdate(DocumentEvent ev) {
    int offset = ev.getOffset();
    Element para = getParagraphElement(offset);
    addToStyleHash(para);
  }

  // Internal Methods

  // Called to see if there are any added or removed Elements. If there are any,
  // we need to update our hash.
  protected void updateStyleHash(DocumentEvent ev) {
    DocumentEvent.ElementChange chg =  ev.getChange(getDefaultRootElement());
        
    if (chg != null) {

      // Something was added or removed (or both) . . .
      Element[] removed = chg.getChildrenRemoved();
      for (int i=0; i<removed.length; i++) {
        removeFromStyleHash(removed[i]);
      }

      Element[] added = chg.getChildrenAdded();
      for (int i=0;i<added.length;i++) {
        addToStyleHash(added[i]);
      }
    }
  }

  // Called to add an Element to our hash.
  protected void addToStyleHash(Element para) {
    AttributeSet attrs = para.getAttributes();
    if (attrs != null) {
      Style style = (Style)attrs.getResolveParent();
      if (style != null) {

        // We've got the Style, now see if we've got a set of Elements that
        // use this Style
        Hashtable ht = (Hashtable)styleHash.get(style);
        if (ht == null) {
          // First user of this Style . . .add a new set
          ht = new Hashtable();
          styleHash.put(style, ht);
        }
        // If this paragraph isn't already in the set, we add it. We really want
        // a Set, not a Hashtable, but to be JDK 1.1 friendly here, we'll use a
        // Hashtable with a throw-away value. We only care about the keys.
        if (ht.containsKey(para) == false) {
          ht.put(para, new Object());
        }
      }
    }
  }

  // Called to remove an Element from our hash
  protected void removeFromStyleHash(Element para) {
    AttributeSet attrs = para.getAttributes();
    if (attrs != null) {
      Style style = (Style)attrs.getResolveParent();
      if (style != null) {
        Hashtable ht = (Hashtable)styleHash.get(style);
        if (ht != null) {
          ht.remove(para);
        }
      }
    }
  }

  // This Hashtable maps from Style -> Hashtable<Element, null>. That is, each
  // key is a Style. The values are Hashtables, the keys of which are the
  // Elements that use the Style. The values of the inner Hashtables are useless
  // (we should use a "Set" data structure, but in JDK 1.1 there is none).
  private Hashtable styleHash = new Hashtable();
      */

  public void testInsertFromSpec(int offset)throws BadLocationException{

    super.insert(offset, createElementSpec());
  }

  public void testCreateFromSpec(){

     create(createElementSpec());
  }

	public Element[] getPathTo(Element fromEl, int offset) {

	  Stack elements = new Stack();
    Element e = fromEl;
	  int index;
	  while (!e.isLeaf()) {
		  elements.push(e);
		  e = e.getElement(e.getElementIndex(offset));
	  }
	  Element[] retValue = new Element[elements.size()];
	  elements.copyInto(retValue);
	  return retValue;
	}

  public Element getCommonParent(Element charElement1, Element charElement2){

 	  Element fromEl = getDefaultRootElement();
    Element[] path1 = getPathTo(fromEl, charElement1.getStartOffset());
    Element[] path2 = getPathTo(fromEl, charElement2.getStartOffset());

    Element commonParent=fromEl;
    for(int i=0; i<path1.length && path1[i]==path2[i]; i++) {
		  commonParent = path1[i];
    }
    return commonParent;
  }

  /**
   * Testing method
   */
  public static ElementSpec[] createElementSpec() {

    char[] c1 = "string1".toCharArray();
    char[] c2 = "string2".toCharArray();
    ElementSpec[] es = new ElementSpec[] {
      new ElementSpec(null, ElementSpec.EndTagType),  //hack: for some reason, end should precede start
      new ElementSpec(null, ElementSpec.StartTagType),
      new ElementSpec(null, ElementSpec.ContentType, c1, 0, c1.length),
      new ElementSpec(null, ElementSpec.EndTagType),
      new ElementSpec(null, ElementSpec.StartTagType),
      new ElementSpec(null, ElementSpec.ContentType, c2, 0, c2.length),
      new ElementSpec(null, ElementSpec.EndTagType),
    };
    return es;
  }

  /**
   * Removes one child element at index from e
   */
  public void removeElement(Element e, int index) throws BadLocationException{

    removeElements(e, index, 1);
  }

  /**
   * Removes count child elements from index
   */
  public void removeElements(Element e, int index, int count) throws BadLocationException{

	  writeLock();
	  try{
	     int start = e.getElement(index).getStartOffset();
	     int end = e.getElement(index + count - 1).getEndOffset();
	     if (end > getLength()) {
          //we do not deal with it - use superclass's private method for that (copy it here if needed)
		      //removeElementsAtEnd(e, index, count, start, end);
          System.err.println("GHTMLDocument::removeElement:We do not support this");
	     }
	     else{
		      removeElements(e, index, count, start, end);
	     }
	  }finally{
	     writeUnlock();
	  }
  }

  public Element wrapSelection(AttributeSet attrsOfNew, Element p, int index, int count) throws BadLocationException{

	  writeLock();
	  try{
	     int start = p.getElement(index).getStartOffset();
	     int end = p.getElement(index + count - 1).getEndOffset();
	     if (end > getLength()) {
          //we do not deal with it - use superclass's private method for that (copy it here if needed)
		      //removeElementsAtEnd(e, index, count, start, end);
          System.err.println("GHTMLDocument::wrapSelection:We do not support this");
          return null;
	     }
	     else{
		      return insertElement(attrsOfNew, p, index, count, start, end);
	     }
    }
    catch(Exception e){
       System.err.println("GHTMLDocument::wrapping failed:"+e);
       return null;
	  }finally{
	     writeUnlock();
	  }
  }


  private Element insertElement(AttributeSet attrsOfNew, Element p, int index, int count, int start, int end) throws BadLocationException {

	    Element[] removed = new Element[count];
	    Element[] added = new Element[1];
      added[0]=new BranchElement(p, attrsOfNew);
	    for(int counter = 0; counter < count; counter++) {
	      removed[counter] = p.getElement(counter + index);
	    }
	    DefaultDocumentEvent dde = new DefaultDocumentEvent(start, end - start, DocumentEvent.EventType.CHANGE);
    	((AbstractDocument.BranchElement)p).replace(index, removed.length, added);
    	dde.addEdit(new ElementEdit(p, index, removed, added));
	    UndoableEdit u = getContent().remove(start, end - start);
	    if (u != null) {
	      dde.addEdit(u);
	    }
	    postRemoveUpdate(dde);
	    dde.end();
	    fireRemoveUpdate(dde);
	    if (u != null) {
	      fireUndoableEditUpdate(new UndoableEditEvent(this, dde));
	    }
      return added[0];
  }


  /**
   * Called to remove child Elements when the end is not touched.
   */
  private void removeElements(Element e, int index, int count, int start, int end) throws BadLocationException {

	    Element[] removed = new Element[count];
	    Element[] added = new Element[0];
	    for(int counter = 0; counter < count; counter++) {
	      removed[counter] = e.getElement(counter + index);
	    }
	    DefaultDocumentEvent dde = new DefaultDocumentEvent(start, end - start, DocumentEvent.EventType.REMOVE);
    	((AbstractDocument.BranchElement)e).replace(index, removed.length, added);
    	dde.addEdit(new ElementEdit(e, index, removed, added));
	    UndoableEdit u = getContent().remove(start, end - start);
	    if (u != null) {
	      dde.addEdit(u);
	    }
	    postRemoveUpdate(dde);
	    dde.end();
	    fireRemoveUpdate(dde);
	    if (u != null) {
	      fireUndoableEditUpdate(new UndoableEditEvent(this, dde));
	    }
  }
  /*
  //not correct method: change to clone
  public void removeElements(Element[] elements) throws BadLocationException {

    writeLock();
	  try{
      for(int j=0; j<elements.length; j++) {
        Element element=elements[j];
        Element parent=element.getParentElement();
        int howMany=parent.getElementCount();
        for(int i=0; i<howMany ;i++){
           Element cur=parent.getElement(i);
           if(cur==element){
              removeElementsUnsynch(parent, i, 1);
              continue; //skip the rest
           }
        }
      }
	  }finally{
	     writeUnlock();
	  }

  }
   */

  public void removeElement(Element elem)throws BadLocationException{

    writeLock();
	  try{
       removeElementUnsync(elem);
    }
    finally{
	     writeUnlock();
    }
  }

  private void removeElementUnsync(Element elem)throws BadLocationException{

    Element parent=elem.getParentElement();
    if(parent==null){
       System.err.println("GHTMLDocument::removeElement:Trying to remove the root!");
       return;
    }
    int howMany=parent.getElementCount();
    for(int i=0; i<howMany ;i++){
        Element cur=parent.getElement(i);
        if(cur==elem){
           removeElementsUnsynch(parent, i, 1);
           break;
        }
    }
  }

  /**
   * Rewrite this!
   */
  public void removeElements(Vector elements)throws BadLocationException {

    writeLock();
	  try{
      for(Enumeration enum = elements.elements() ; enum.hasMoreElements() ;) {
        Element element=(Element)enum.nextElement();
        if(element!=null){
          removeElementUnsync(element);
        }
      }
	  }finally{
	     writeUnlock();
	  }

  }

  private void removeElementUnsynch(Element e, int index) throws BadLocationException{

    removeElementsUnsynch(e, index, 1);
  }

  private void removeElementsUnsynch(Element e, int index, int count) throws BadLocationException{

    int start = e.getElement(index).getStartOffset();
	  int end = e.getElement(index + count - 1).getEndOffset();
	  if (end > getLength()) {
       //we do not deal with it - use superclass's private method for that (copy it here if needed)
		   //removeElementsAtEnd(e, index, count, start, end);
       System.err.println("GHTMLDocument::removeElements:We do not support this");
	  }
	  else{
		   removeElements(e, index, count, start, end);
	  }
  }

	/**
	 * Creates a copy of this element, with a different
	 * parent.
   * <p>
   * this method is included into recent HTMLDocument class
   *
   * @param parent the parent element
   * @param clonee the element to be cloned
   * @return the copy
	 */
  public Element clone(Element parent, Element clonee) {

	  if(clonee.isLeaf()){
		  return createLeafElement(parent, clonee.getAttributes(), clonee.getStartOffset(),
    					 clonee.getEndOffset());
	  }
	  Element e = createBranchElement(parent, clonee.getAttributes());
	  int n = clonee.getElementCount();
	  Element[] children = new Element[n];
	  for(int i = 0; i < n; i++) {
		  children[i] = clone(e, clonee.getElement(i));
	  }
	  ((BranchElement)e).replace(0, 0, children);
	  return e;
	}



////////////////////////////////////////////////////////////////////////////////

	/**
	 * Convenience method with Caret passed
	 */
	public void replaceSelection(Caret caret, ElementSpec[] elements){

		int begin=Math.min(caret.getDot(), caret.getMark());
		int end=Math.max(caret.getDot(), caret.getMark());
    replaceSelection(begin, end, elements);
  }

	/**
	 * Replace the selection given by the caret with the given elements
	 */
	public void replaceSelection(int p0, int p1, ElementSpec[] elements){

		if(p0 != p1){
      try{
	     	 remove(p0, p1 - p0);
		  }catch (Exception exc) {
		     exc.printStackTrace();
		  }
		}
		try{
		 	 insert(p0, elements);
		}catch (Exception exc){
			 exc.printStackTrace();
		}
	}

	/**
   * Convenience method for creating ElementSpecs
   */
	public ElementSpec makeElementSpec(AttributeSet a, short type, String text ){

	    return new ElementSpec(a, type, text.toCharArray(), 0, text.length());
	}

	/**
	 * Return the block of elements containing the offset and length.
	 */
	public ElementSpec[] getElementSpecs(int offset, int length){

		Vector v = new Vector();
		Segment data = new Segment();

		// Loop over the leaf elements of the document, starting  with the element
    // containing the offset, and ending after length characters.
		// Note that we loop until we find the first element completely outside
    // of the desired range.
		int o = offset;
    int docLength=this.getLength();
    Element e = getCharacterElement(o);
		for(; o<docLength && e.getStartOffset() < offset + length;
			                          o = e.getEndOffset()){
      try{
        e = getCharacterElement(o);
			  BranchElement p	= (BranchElement)e.getParentElement();

			  // Get the start and end of the section of this leaf element that we need
			  int startoff = Math.max(offset, e.getStartOffset());
			  int endoff = Math.min(offset + length, e.getEndOffset());

			  // Get the start and end offsets of the parent of this leaf element
			  int pstartoff = p.getStartOffset();
			  int pendoff = p.getEndOffset();

			  // If this leaf is the first in its paragraph, then start a new paragraph
  		  if(startoff == pstartoff){
				  v.addElement(new ElementSpec(null, ElementSpec.EndTagType));
				  v.addElement(new ElementSpec(p.getAttributes(), ElementSpec.StartTagType));
			  }

			  // Get the leaf attributes
			  AttributeSet a = e.getAttributes();

			  // Get the text that we need from this leaf element
        if(endoff>startoff){
  		    getText(startoff, endoff - startoff, data);
  		    // Create an element spec associated with this leaf element
	 	      v.addElement(makeElementSpec(a,	ElementSpec.ContentType, data.toString()));
        }
		  }catch (Exception exc){
        System.err.println("GHTMLDocument::getElementSpecs:"+exc);
  	  }

		}//for

		int n = v.size();
		ElementSpec[] array = new ElementSpec[n];
		for(int i = 0; i < n; i++){
			array[i] = (ElementSpec)v.elementAt(i);
		}

		return array;
	}

	protected Element createLeafElement(Element parent, AttributeSet a,	int p0, int p1){

			return super.createLeafElement(parent, a, p0, p1);
	}

  /**
   * not used currently (because is not finished) 
   */
  public ElementSpec[] getElementSpecRecursive(Element parent){

     if(!(parent instanceof BranchElement)){
        System.err.println("GHTMLDocument::getElementSpecRecursive:Element passed is not BranchElement!");
        return null;
     }

     Vector v=new Vector();
		 v.addElement(new ElementSpec(null, ElementSpec.EndTagType));
		 v.addElement(new ElementSpec(parent.getAttributes(), ElementSpec.StartTagType));
     getElementSpecs(parent,v);

		 int n = v.size();
		 ElementSpec[] array = new ElementSpec[n];
		 for(int i = 0; i < n; i++){
       array[i] = (ElementSpec)v.elementAt(i);
		 }

		 return array;
  }

	/**
	 * Return the block of elements containing the offset and length.
   *
   * Not finished and has not been currently working
	 */
	public void getElementSpecs(Element p, Vector v){

    int begin=p.getStartOffset();
    int end=p.getEndOffset();
		AttributeSet a = p.getAttributes();

    if(p.isLeaf()){

  		Segment segment = new Segment();

			try{
				getText(begin, end - begin, segment);
			}catch ( Exception exc ){
				exc.printStackTrace();
			}

		  v.addElement(makeElementSpec(a,	ElementSpec.ContentType, segment.toString()));
    }
    else{

			 BranchElement node	= (BranchElement) p.getParentElement();

			 v.addElement(new ElementSpec(null, ElementSpec.EndTagType));
			 v.addElement(new ElementSpec(a, ElementSpec.StartTagType));

       int numChildren=node.getElementCount();
       for(int i=0; i<numChildren; i++){
           getElementSpecs(node.getElement(i),v);
       }
    }

	}


  /**
   * This method just copied from the superclass (there it is private, but we need it!)
   */
  public void insertHTML2(Element parent, int offset, String html/*, boolean wantsTrailingNewline*/)throws BadLocationException, java.io.IOException {

	if (parent != null && html != null) {
	    HTMLEditorKit.Parser parser = getParser();
	    if (parser != null) {
		int lastOffset = Math.max(0, offset - 1);
		Element charElement = getCharacterElement(lastOffset);
		Element commonParent = parent;
		int pop = 0;
		int push = 0;

		if (parent.getStartOffset() > lastOffset) {
		    while (commonParent != null &&
			   commonParent.getStartOffset() > lastOffset) {
			commonParent = commonParent.getParentElement();
			push++;
		    }
		    if (commonParent == null) {
			throw new BadLocationException("No common parent",
						       offset);
		    }
		}
		while (charElement != null && charElement != commonParent) {
		    pop++;
		    charElement = charElement.getParentElement();
		}
		if (charElement != null) {
		    // Found it, do the insert.

        //this is changed (to public constructor):
		    HTMLReader reader = new HTMLReader(offset, pop - 1, push,
						       null);
        //!

		    parser.parse(new java.io.StringReader(html), reader, true);
		    reader.flush();
		}
	    }
	}
    }


}



