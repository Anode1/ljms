package org.is.html.actions;

import java.awt.event.ActionEvent;
import javax.swing.text.*;
import javax.swing.text.html.HTML;

import org.is.html.TableUtils;
import org.is.html.HTMLUtils;

/**
 *
 * @since jdk1.2 
 */
public class DeletePrevCharAction extends GHTMLEditAction{

  public DeletePrevCharAction() {

    super(DefaultEditorKit.deletePrevCharAction);
  }

  public void action(ActionEvent e)throws Exception{

    Caret caret = editor.getCaret();
    int dot = caret.getDot();
    int mark = caret.getMark();
    if(dot != mark){
       if(TableUtils.breaksTables(doc, dot, mark)){ //not to break inconsistent selection
         editor.getToolkit().beep();
         return;
       }

       if(breaksTableFromRight(Math.min(dot, mark))){
         editor.getToolkit().beep();
         return;
       }

       doc.remove(Math.min(dot, mark), Math.abs(dot - mark));
       //
    }
    else if(dot > 0){ //dot==mark

       if(firstInTD(dot)){
          return;
       }

       if(breaksTableFromRight(dot)){
          return;
       }

       doc.remove(dot - 1, 1);

    }

    if(HTMLUtils.isWrappedInTag(editor, HTML.Tag.TD)){
      //editor.reload();
      //editor.getUI().modelToView(editor, dot);
    }
  }

  private boolean firstInTD(int pos){

    Element ce=TableUtils.getWrappingTD(doc.getCharacterElement(pos));
    if(ce==null)return false;

    if(pos<ce.getStartOffset()+1)return true;
    return false;
  }

  private boolean breaksTableFromRight(int pos){

    if(pos<1)return false;

    Element myCharEl=doc.getCharacterElement(pos);
    Element prevCharEl=doc.getCharacterElement(pos-1);
    if(myCharEl==prevCharEl)return false; //the same

    Element table=HTMLUtils.getWrappingElementByTag(prevCharEl, HTML.Tag.TABLE);
    if(table==null)return false; //no table

    if(table.getEndOffset()==pos)return true;

/*
    HTML.Tag currTag=null;
	  while(e1 != null &&
          (currTag=(HTML.Tag)e1.getAttributes().getAttribute(StyleConstants.NameAttribute))!=HTML.Tag.TD &&
           currTag!=HTML.Tag.TH){
	    e1 = e1.getParentElement();
	  }
	  while(e2 != null &&
          (currTag=(HTML.Tag)e2.getAttributes().getAttribute(StyleConstants.NameAttribute))!=HTML.Tag.TD &&
           currTag!=HTML.Tag.TH){
	    e2 = e2.getParentElement();
	  }
*/

    return false;
  }

}
