package org.is.html.actions;

import java.awt.event.ActionEvent;
import javax.swing.text.JTextComponent;
import java.awt.Toolkit;

import org.is.html.GHTMLEditor;
import org.is.html.GHTMLDocument;
import org.is.html.TopManager;

/**
 * Base class for all EditActions for GHTMLEditorKit
 *
 * @since jdk1.2
 */
public abstract class GHTMLEditAction extends GAbstractAction{

  protected GHTMLEditor editor;
  protected GHTMLDocument doc;

  public GHTMLEditAction(String nm) {

    super(nm);
  }

  /**
   * Action is defined in subclasses.
   * This method is called only if editor is not null and editable,
   * so subclasses should not worry about this.
   */
  public abstract void action(ActionEvent e)throws Exception;

  public final void actionPerformed(ActionEvent e){

    editor=retreiveGEditor();

    if((editor != null)&&(editor.isEditable())){
       try{
           doc=(GHTMLDocument)editor.getDocument();
           action(e);
           /*
           Caret caret = editor.getCaret();
           int dot = caret.getDot();
           int mark = caret.getMark();
           if(dot != mark){
               doc.remove(Math.min(dot, mark), Math.abs(dot - mark));
           }else if(dot < doc.getLength()){
                        doc.remove(dot, 1);
           }

           */
        }catch(Exception ex){
           //System.err.println("GHTMLEditAction::"+ex);
           ex.printStackTrace();
        }
    }
  }

  public final GHTMLEditor getGEditor() {

    return editor;
  }

  public final GHTMLDocument getGHTMLDocument(){

    return doc;
  }

  public static final GHTMLEditor retreiveGEditor() {

    return TopManager.getGHTMLEditor();
  }
   /*
  protected final GHTMLEditor getGHTMLEditor(ActionEvent e) {

  	if (e != null) {
	    Object o = e.getSource();
	    if (o instanceof GHTMLEditor) {
		    return (JTextComponent) o;
	    }
	  }
	  return getFocusedComponent();
  }
     */

  /*
  public static final int getFrom(Caret caret){

    return Math.min(caret.getDot(), caret.getMark());
  }

  public static final int getTo(Caret caret){

    return Math.max(caret.getDot(), caret.getMark());
  }
  */

}
