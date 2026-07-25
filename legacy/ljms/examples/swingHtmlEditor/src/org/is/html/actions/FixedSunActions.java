package org.is.html.actions;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.text.JTextComponent;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTML;
import javax.swing.text.StyledEditorKit;

/**
 * Fixed Sun's classes been overriden. Mainly it is lost focus problem when
 * another component takes it (button or dialog) but it is supposed that the focus
 * should return to the TextPane.
 *
 * @since jdk1.2
 */
public class FixedSunActions{

  public static class ItalicAction extends HTMLEditorKit.ItalicAction{

	  public ItalicAction(){

		  super();    //name: font-italic
	  }

	  public void actionPerformed(ActionEvent e){

		  super.actionPerformed(e);

	    JEditorPane editor = getEditor(e);
	    if (editor != null) {
        editor.repaint();
		    editor.requestFocus();
      }
	  }

  }

///////////////////////////////////////////////////////////////////////////////

  public static class BoldAction extends HTMLEditorKit.BoldAction{

	  public BoldAction(){

		  super(); //the name is: font-bold
	  }

	  public void actionPerformed(ActionEvent e){

		  super.actionPerformed(e);

	    JEditorPane editor = getEditor(e);
	    if (editor != null) {
        editor.repaint();
		    editor.requestFocus();
      }
	  }
  }

///////////////////////////////////////////////////////////////////////////////

  public static class UnderlineAction extends HTMLEditorKit.UnderlineAction{

	  public UnderlineAction(){

		  super();    //name: font-underline
	  }

	  public void actionPerformed(ActionEvent e){

		  super.actionPerformed(e);

 	    JEditorPane editor = getEditor(e);
	    if (editor != null) {
        editor.repaint();
		    editor.requestFocus();
      }
	  }
  }

///////////////////////////////////////////////////////////////////////////////

  public static class CutAction extends DefaultEditorKit.CutAction{

    public CutAction(){

      super();
	  }

	  public void actionPerformed(ActionEvent e){

		  super.actionPerformed(e);

	    JTextComponent editor = getTextComponent(e);
	    if (editor != null) {
        editor.repaint();
		    editor.requestFocus();
      }
    }
  }

///////////////////////////////////////////////////////////////////////////////

  public static class CopyAction extends DefaultEditorKit.CopyAction{

    public CopyAction(){

      super();
	  }

	  public void actionPerformed(ActionEvent e){

		  super.actionPerformed(e);

	    JTextComponent editor = getTextComponent(e);
	    if (editor != null) {
        editor.repaint();
		    editor.requestFocus();
      }
    }
  }

///////////////////////////////////////////////////////////////////////////////

  public static class PasteAction extends DefaultEditorKit.PasteAction{

    public PasteAction(){

      super();
	  }

	  public void actionPerformed(ActionEvent e){

		  super.actionPerformed(e);

	    JTextComponent editor = getTextComponent(e);
	    if (editor != null) {
        editor.repaint();
		    editor.requestFocus();
      }
    }
  }

///////////////////////////////////////////////////////////////////////////////

  public static class FontFamilyAction extends StyledEditorKit.FontFamilyAction{

	  public FontFamilyAction(String nm, String f){

		  super(nm, f);    //name: font-underline
	  }

	  public void actionPerformed(ActionEvent e){

		  super.actionPerformed(e);

	    JEditorPane editor = getEditor(e);
	    if (editor != null) {
        editor.repaint();
		    editor.requestFocus();
      }
	  }
  }

///////////////////////////////////////////////////////////////////////////////

  public static class FontSizeAction extends StyledEditorKit.FontSizeAction{

	  public FontSizeAction(String nm, int s){

		  super(nm, s);    //name: font-underline
	  }

	  public void actionPerformed(ActionEvent e){

		  super.actionPerformed(e);


	    JEditorPane editor = getEditor(e);
	    if (editor != null) {
        editor.repaint();
		    editor.requestFocus();
      }
	  }
  }

///////////////////////////////////////////////////////////////////////////////

  public static class AlignmentAction extends StyledEditorKit.AlignmentAction{

	  public AlignmentAction(String nm, int a){

		  super(nm, a);    //name: font-underline
	  }

	  public void actionPerformed(ActionEvent e){

		  super.actionPerformed(e);


	    JEditorPane editor = getEditor(e);
	    if (editor != null) {
        editor.repaint();
		    editor.requestFocus();
      }
 	  }
  }

///////////////////////////////////////////////////////////////////////////////

///////////////////////////////////////////////////////////////////////////////  

}

