package org.is.html;

import java.awt.event.*;
import java.awt.Component;
import java.awt.Point;
import javax.swing.text.Position;

/**
 * Simple Adapter class used for PopupMenu appearence after right click
 *
 * @since jdk1.2 
 */
public class PopUpMouseAdapter extends MouseAdapter{

     private GHTMLEditor editor;

     public PopUpMouseAdapter(GHTMLEditor editor){
     
        this.editor=editor;
     }
        /*
     public void mousePressed(MouseEvent e) {

        handle(e);
     }    */

     public void mouseReleased(MouseEvent e) {

        handle(e);

     }

     private void handle(MouseEvent e){

        if(e.isPopupTrigger()){
           int x=e.getX();
           int y=e.getY();
           Point p=e.getPoint();

           int s1=editor.getSelectionStart();
           int s2=editor.getSelectionEnd();

           PopUpMenu popup=new PopUpMenuForHTML(editor, x, y);
           popup.show(editor, x, y);

           //returnCaretBack(p, s1, s2);

        }
     }

     private void returnCaretBack(Point p, int s1, int s2){

  	    Position.Bias bias[] = new Position.Bias[1];
  	    int pos = editor.getUI().viewToModel(editor, p, bias);

        //System.out.println(offset);

        if(pos>0){
          editor.getCaret().setDot(pos);
          editor.setSelectionStart(s1);
          editor.setSelectionEnd(s2);
          editor.repaint();
        }
     }




}



