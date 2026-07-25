package org.is.html.actions;

import java.awt.event.ActionEvent;
import javax.swing.text.*;
import javax.swing.text.html.HTML;

import org.is.html.*;

/**
 *
 * @since jdk1.2 
 */
public class DeleteNextCharAction extends GHTMLEditAction{

  public DeleteNextCharAction() {

    super(DefaultEditorKit.deleteNextCharAction);
  }

   //    getText(end - 1, 1).charAt(0) == NEWLINE[0]
  public void action(ActionEvent e)throws Exception{

    Caret caret = editor.getCaret();
    int dot = caret.getDot();
    if(dot>=doc.getLength())return;
    int mark = caret.getMark();
    if(dot != mark){
       if(TableUtils.breaksTables(doc, dot, mark)){ //not to break inconsistent selection
         editor.getToolkit().beep();
         return;
       }
       doc.remove(Math.min(dot, mark), Math.abs(dot - mark));
       //editor.getUI().

    }else{ // dot == mark

       if(lastInTD(dot)){
         removeListIfNeeded(dot);
         return;
       }

       doc.remove(dot, 1);

    }

    if(HTMLUtils.isWrappedInTag(editor, HTML.Tag.TD)){

      //editor.reload();
      //editor.getUI().getRootView().breakView();//.modelToView(editor, dot);
    }

  }

  private boolean lastInTD(int pos){

    Element charEl=doc.getCharacterElement(pos);

    Element ce=TableUtils.getWrappingTD(charEl);
    if(ce==null)return false;

    if(pos >= ce.getEndOffset()-1){
      return true;
    }
    return false;
  }

  private void removeListIfNeeded(int dot){

    try{
      Element charEl=doc.getCharacterElement(dot);
      Element ce=TableUtils.getWrappingTD(charEl);
      if(ce==null)return;

      if(dot!=ce.getStartOffset()){ //there is some content before (in TD)
        return;
      }

      doc.setOuterHTML(ce,"<td>&nbsp;</td>");
    }
    catch(Exception e){
      System.err.println("DeleteNextCharAction::RemoveListIfNeeded:"+e);
    }
  }


}
