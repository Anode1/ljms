/*
 * @(#)GHTMLEditor.java
 */
package org.is.html;

import java.beans.*;
import java.io.*;
import java.awt.event.*;
import java.awt.*;
import javax.swing.*;
import java.net.URL;
import java.util.*;
import javax.swing.text.html.*;
import javax.swing.text.*;
import javax.swing.event.*;
import java.awt.datatransfer.*;

import org.is.html.actions.Actions;
import org.is.util.*;

/**
 * Main GUI component of HTML Editor (subclassed JEditorPane)
 *
 * @version 1.0
 * @since jdk1.2
 */
public class GHTMLEditor extends JTextPane implements KeyListener{//,CaretListener{

  private GHTMLEditorKit kit;
  private Actions acts;
  private Document doc;
  private String buffer; //buffer for reloading
  private static ClipboardOwner defaultClipboardOwner = new ClipboardObserver();
  private static Clipboard clipboard = new Clipboard("LocalClipboard");

  public GHTMLEditor(){

     kit=new GHTMLEditorKit();

     acts=new Actions(kit); //register actions
     //System.out.println(acts.toString());

     setEditorKitForContentType("html/text",kit);
     //setEditorKit(kit);  //not neccessary
     setContentType("html/text");

     //Keymap parent = getKeymap();

     getCaret().setBlinkRate(500);

     addMouseListener(new PopUpMouseAdapter(this));
    // addMouseListener(new CursorMover(this));

     mapKeyBindings();
		 //addCaretListener(this);  //removed for performance
		 addKeyListener(this);      //!

     //addHyperlinkListener(new SimpleLinkListener(jep, urlField, statusBar));
	   //getDocument().addUndoableEditListener(undoHandler);
     
     setEditable(true);
     this.setSelectionColor(new Color(0,0,128));

     addHyperlinkListener(new HyperlinkListener() {
				public void hyperlinkUpdate(HyperlinkEvent evt) {
					if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
						URL thisPage = getPage();

            Frame f=getFrame();
						Cursor oldCursor = f.getCursor();

						try{
							f.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
							setPage(evt.getURL());
						}catch (IOException e) {
							System.out.println("GHTMLEditor::Page switch failed - revert to old page");
							try{
								setPage(thisPage);
							}catch (IOException ioexc) {
								System.out.println("GHTMLEditor::Failed to revert to old page");
							}
						}finally{
							f.setCursor(oldCursor);
						}
					}
				}
			});

  }


  /**
   * Overrides default key behaviour.
   * We have mixed key events processing (both keyEvent adapters and KeyMappings
   * because we don't now how to generate new paragraph for now without problems,
   * so we delegate default behaviour to the parent)
   */
  protected void processKeyEvent(KeyEvent e){

    int code=e.getKeyCode();
    int id=e.getID();
    if(id!=KeyEvent.KEY_PRESSED){
       super.processKeyEvent(e);
       return;
    }

    if(code==KeyEvent.VK_ENTER){
      handleEnterKeyEvent(e);
    }
    super.processKeyEvent(e);
  }

  private void handleEnterKeyEvent(KeyEvent e){

    try{
      Caret caret = getCaret();
      int dot = caret.getDot();
      GHTMLDocument doc=getGHTMLDocument();

      Element charE=doc.getCharacterElement(dot);

      if(HTMLUtils.firstInElement(charE, dot, HTML.Tag.UL)){
        Element el=HTMLUtils.getWrappingElementByTag(charE, HTML.Tag.UL);
        doc.insertBeforeStart(el,"<p></p>");
        reload();
        //System.out.println("first in UL");
        e.consume();
        return;
      }
      else if(HTMLUtils.firstInElement(charE, dot, HTML.Tag.OL)){
        Element el=HTMLUtils.getWrappingElementByTag(charE, HTML.Tag.OL);
        doc.insertBeforeStart(el,"<p></p>");
        //System.out.println("first in OL");
        reload();
        e.consume();
        return;
      }
      else if(HTMLUtils.firstInElement(charE, dot, HTML.Tag.TABLE)){
        Element el=HTMLUtils.getWrappingElementByTag(charE, HTML.Tag.TABLE);
        doc.insertBeforeStart(el,"<p></p>");
        Actions.fireAction(this,"break-action");
        e.consume();
        reload();
        //System.out.println("first in Table");
        return;
      }

      //if ENTER and the caret is in UL/OL - create new LI
      if(HTMLUtils.isWrappedInTag(this, HTML.Tag.UL)){
        Actions.fireAction(this,"InsertUnorderedListItem");
        e.consume();
      }
      else if(HTMLUtils.isWrappedInTag(this, HTML.Tag.OL)){
        Actions.fireAction(this,"InsertOrderedListItem");
        e.consume();
      }
      else if(HTMLUtils.isWrappedInTag(this, HTML.Tag.TD)){
        Actions.fireAction(this,"break-action");
        e.consume();
      }

    }
    catch(Exception ex){
       System.err.println("GHTMLEditor::handleEnterKeyEvent:"+ex);
    }
  }

	public void keyPressed(KeyEvent e) {
  /*
    //hack not allowing to delete ivisible header/title elements
		if(e.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			if(getCaretPosition() < 2) {
				e.setKeyCode(KeyEvent.VK_UNDEFINED);
			}
		} */
	}

	public void keyTyped(KeyEvent e){}
  
	public void keyReleased(KeyEvent e){}

  /*
	public void caretUpdate(CaretEvent e) {
    //hack not to allow manipulate with header/title elements
		if(e.getDot() < 1) {
      if(doc.getLength()>0)
			setCaretPosition(1);
		}
	}
  */

  /**
   * Workaround a bug in DefaultStyledDocument.remove, which is
   * invoked when setText is invoked.
   */
  public void set(String text){

     clearAllText();
     try{
        kit.read(/*new BufferedReader(*/new StringReader(text)/*)*/,doc,0);
     }
     catch(Exception e){
        e.printStackTrace();
     }
     //setCaretPosition(0);
  }

  public void clearAllText() {

    doc=kit.createDefaultDocument();
    doc.putProperty("IgnoreCharsetDirective", new Boolean(true));
		setDocument(doc);
  }

  /*
  public GHTMLDocument getDocument(){

    if(!(doc instanceof GHTMLDocument))
  }
  */

  /**
   * Gets text (string) from current document
   */
  public String getText(){

    StringWriter writer=new StringWriter();
    BufferedWriter buffer = new BufferedWriter(writer);
    try{
       kit.write(buffer, doc, 0, doc.getLength());
       buffer.flush();
    }
    catch (Exception ex) {
       System.err.println("GHTMLEditor::getText:"+ex);
    }
    finally{
       try{buffer.close();}catch(IOException ie){}
    }

    return writer.toString();
  }

  public String getHTMLChunk(){

    if(Resources.getBoolean("html_chunk")){
       //wrap additionally into html/body tags
      return HTMLUtils.extractBody(getText());
    }
    return getText();
  }

  public void setHTMLChunk(String htmlChunk){

    if(Resources.getBoolean("html_chunk")){
      set(HTMLUtils.wrapBody(htmlChunk));
    }
    else set(htmlChunk); //full html
  }

  /**
   * Reloads the whole document forcing reparsing (our hack to notify Views about
   * changes done against Elements structure)
   */
  public void reload(){

    try{
        Caret c=this.getCaret();
        int dot=c.getDot();
        int mark=c.getMark();
        Position dotPos=doc.createPosition(dot);
        Position markPos=doc.createPosition(mark);

        set(getText());

        //find current dot and mark and correct if not valid
        int newDocLength=getDocument().getLength();
        mark=markPos.getOffset();
        if(mark<0)mark=0;
	      if(mark>newDocLength)mark=newDocLength;
        dot=dotPos.getOffset();
        if(dot<0)dot=0;
	      if(dot>newDocLength)dot=newDocLength;

        this.setCaretPosition(mark);
        this.moveCaretPosition(dot);

        repaint();
		    requestFocus();
    }
    catch(Exception e){
        System.err.println("GHTMLEditor::reload: reload failed:"+e);
    }
  }

  /**
   * Overrides Editor Pane's method. The main purpose - to prevent replace
   * selection if it ends but not begins (or vice-versa) in a table
   */
  public void replaceSelection(String content){

    if(!isEditable()){
        getToolkit().beep();
        return;
    }
    Document doc = getStyledDocument();
    if(doc != null){
        try{
            Caret caret = getCaret();
            int p0 = Math.min(caret.getDot(), caret.getMark());
            int p1 = Math.max(caret.getDot(), caret.getMark());

            if(p0 != p1){
                //code added
                if(TableUtils.breaksTables(doc, p0, p1)){
                   getToolkit().beep();
                   return;
                }
                //
                doc.remove(p0, p1 - p0);
            }
            if(content != null && content.length() > 0) {
                doc.insertString(p0, content, getInputAttributes());
            }
        }catch (BadLocationException e) {
            getToolkit().beep();
        }
    }
  }

  /**
   * Cut overriden here to prevent text removal leaving table in inconsistent state
   */    /*
  public void cut(){

   	if(!isEditable() || !isEnabled()){
       getToolkit().beep();
       return;
    }

    try{
       Caret caret = getCaret();
       int p0 = Math.min(caret.getDot(), caret.getMark());
       int p1 = Math.max(caret.getDot(), caret.getMark());

       if(p0 != p1) {
          if(TableUtils.breaksTables(doc, p0, p1)){
             getToolkit().beep();
             return;
          }
       }
     }
     catch(Exception e){
       System.err.println("GHTMLEditor::cut:"+e);
     }
     super.cut();
  }
           */

  public void cut() {

    if(isEditable() && isEnabled()){
	    try{
         Caret caret = getCaret();
		     int p0 = Math.min(caret.getDot(), caret.getMark());
		     int p1 = Math.max(caret.getDot(), caret.getMark());
		     if (p0 != p1) {
            //Protect against breaking of tables
            if(TableUtils.breaksTables(doc, p0, p1)){
              getToolkit().beep();
              return;
            }
            //
		        Document doc = getDocument();
		        String srcData = doc.getText(p0, p1 - p0);
		        StringSelection contents = new StringSelection(srcData);
		        clipboard.setContents(contents, defaultClipboardOwner);
		        doc.remove(p0, p1 - p0);
		     }
	    }catch (BadLocationException e){
	    }
    }else {
      getToolkit().beep();
	  }
  }

  public void copy() {

    try{
        Caret caret = getCaret();
        int p0 = Math.min(caret.getDot(), caret.getMark());
        int p1 = Math.max(caret.getDot(), caret.getMark());
        if (p0 != p1) {
            Document doc = getDocument();
            String srcData = doc.getText(p0, p1 - p0);
            StringSelection contents = new StringSelection(srcData);
            clipboard.setContents(contents, defaultClipboardOwner);
        }
    }catch (BadLocationException e){
    }
  }

  public void paste() {

	  Transferable content = clipboard.getContents(this);
	  if(content != null){
  	   try{
		      String dstData = (String)(content.getTransferData(DataFlavor.stringFlavor));
		      replaceSelection(dstData);
	     }catch (Exception e){
		      getToolkit().beep();
	     }
  	}
  }


  private boolean isCrossingSelection(int p0, int p1){

    return false;
  }

     /*
  //not needed anymore (was used for ScrollBars)
  public boolean getScrollableTracksViewportWidth(){

    return false; // Sun is hard-coding true for JEditorPane/JTextPane!
  }
       */

  private void mapKeyBindings(){

     Keymap myMap=this.addKeymap("GHTMLEditorKeyMapBindings", super.getKeymap());

     //remove default Enter binding:
     myMap.removeKeyStrokeBinding(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0));

     this.loadKeymap(myMap, keyBindings, this.getActions());
     this.setKeymap(myMap);

     /*
      Keymap keymap = textPane.addKeymap("MyBindings", textPane.getKeymap());
      Action action = getActionByName(DefaultEditorKit.backwardAction);
      KeyStroke key = KeyStroke.getKeyStroke(KeyEvent.VK_B, Event.CTRL_MASK);
      keymap.addActionForKeyStroke(key, action);
     */
  }


  /*
  //how to do something with tags in dfs manner
  public Iterator getIterator(HTML.Tag t){

    if (t.isBlock()) {
      // TBD
      return null;
    }

    ElementIterator it = new ElementIterator(doc);
    javax.swing.text.Element elem;
    while ((elem = it.next()) != null){
      String sName = elem.getName();
      if (sName.equalsIgnoreCase("meta")){
        //do whatever you want
      }
    }

  */

  public Action[] getActions(){

    return TextAction.augmentList(super.getActions(), actions);
  }

  public GHTMLDocument getGHTMLDocument(){

    return (GHTMLDocument)super.getDocument();
  }

  public GHTMLEditorKit getGHTMLKit(){

    return kit;
  }

  protected static final Action[] actions=new Action[]{

  };

  static final JTextComponent.KeyBinding[] keyBindings={

     new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_P, KeyEvent.CTRL_MASK), "debug-action"),
     new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_MASK), "refresh-action"),
     new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.ALT_MASK), "set-read-only"),
     new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_W, KeyEvent.ALT_MASK), "set-writable"),
     new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_L, KeyEvent.ALT_MASK), "go-to-link"),     
     new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0), "help"),
     //new JTextComponent.KeyBinding(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "EnterKeyAction")
  };

  /*
  //not used for now
  class CursorMover extends MouseAdapter{

     private GHTMLEditor editor;

     public CursorMover(GHTMLEditor editor){

        this.editor=editor;
     }

     public void mousePressed(MouseEvent e) {

        handle(e);
     }

     private void handle(MouseEvent e){

        Point p=e.getPoint();
    //    int s1=editor.getSelectionStart();
    //    int s2=editor.getSelectionEnd();

  	    Position.Bias bias[] = new Position.Bias[1];
  	    int offset = editor.getUI().viewToModel(editor, p, bias);

        //System.out.println(offset);

        if(offset>0){
          editor.getCaret().setDot(offset);
    //      editor.setSelectionStart(s1);
    //      editor.setSelectionEnd(s2);
          editor.repaint();
        }
     }

  }//CursorMover
    */

    Frame f;

    public Frame getFrame(){

      if(f==null)f=JOptionPane.getFrameForComponent(this);
      return f;
    }
   /*
    private boolean lastChar(){

       if()
       return false;
    }*/

    static class ClipboardObserver implements ClipboardOwner {

        public void lostOwnership(Clipboard clipboard, Transferable contents) {
        }
    }

}
