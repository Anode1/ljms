package org.is.html;

import java.awt.event.ActionEvent;
import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import java.util.Enumeration;

import org.is.util.Utils;

/**
 * Set of useful utilites which are used from different classes.
 *
 * @since jdk1.2
 */
public class TableUtils{

  /**
   * Returns wrapping TD Element, but it should return null if we are inside <table>
   * but not in <TD> (that is why we don't use getWrappingElementByTag but rewrite routine)
   * e been passed - character element
   */
  public static Element getWrappingTD(Element e){

    HTML.Tag currTag=null;
	  while (e != null &&
           (currTag=(HTML.Tag)e.getAttributes().getAttribute(StyleConstants.NameAttribute)) != HTML.Tag.TD &&
           currTag != HTML.Tag.TH){

      if(currTag == HTML.Tag.TABLE){
         System.err.println("HTMLUtils::getWrappingTD: notice: we were between <td> and <table>");
         return null; //we got wrapping TABLE, so we were not inside TD!
      }
	    e = e.getParentElement();
	  }
	  return e;
  }

  

  /**
   * Calculates the dimentions of the table
   */
  public static Dimension getTableDimensions(Element te){

    Dimension d=new Dimension();  // (0,0) - default
    if(te.getAttributes().getAttribute(StyleConstants.NameAttribute)!=HTML.Tag.TABLE){
       System.err.println("HTMLUtils::getTableDimentions: element passed is not Table element!");
       return null;
    }

    int _rowsNum=te.getElementCount(); //it is not number of rows - we have to check it
    if(_rowsNum==0)return d; //empty table for sure
    int rowsNum=0;
    //Here we have to skip all not Branch Elements and not TD/TR BranchElements,
    //i.e. garbage in table which may be there be not valid but existing not
    //breaking browsers
    int maxCols=0;
    for(int i=0; i<_rowsNum; i++){
       Element _tr=te.getElement(i);
       if(_tr instanceof HTMLDocument.BlockElement){
           HTMLDocument.BlockElement tr=(HTMLDocument.BlockElement)_tr;
           if(tr.getAttributes().getAttribute(StyleConstants.NameAttribute)==HTML.Tag.TR){
              //we could do getName.equals(HTML.Tag.TR.toString()) but this is slower!
              int numOfCols=tr.getElementCount();
              int colsNum=0;
              for(int j=0; j<numOfCols; j++){
                 Element _td=tr.getElement(i);
                 if(_td instanceof HTMLDocument.BlockElement){
                     HTMLDocument.BlockElement td=(HTMLDocument.BlockElement)_td;
                     HTML.Tag tag=(HTML.Tag)td.getAttributes().getAttribute(StyleConstants.NameAttribute);
                     if(tag==HTML.Tag.TD || tag==HTML.Tag.TH){
                       /*
                        String colspan=(String)td.getAttributes().getAttribute(HTML.Attribute.COLSPAN);
                        if(colspan!=null){
                          try{
                             int colspanInt=Integer.parseInt(colspan);
                             colsNum=colsNum+colspanInt-1;
                          }
                          catch(NumberFormatException nfe){
                             System.err.println("TableUtils::getTableDimensions:colspan is not number!");
                          }
                        }
                        */
                        colsNum++;
                     }
                 }
              }
              if(colsNum>maxCols){
                 maxCols=colsNum;  //collect the maximum number of columns
              }
              rowsNum++;
           }
       }
    }
    d.width=maxCols;
    d.height=rowsNum;
    return d;
  }

  /**
   * Checks if future selection from any index in e1 to e2 will not break tables,
   * i.e. both e1 and e2 may be in one TD or outside table
   */
  public static boolean breaksTables(Document doc, int p1, int p2){

    try{
      DefaultStyledDocument sd=(DefaultStyledDocument)doc;
      Element e1=sd.getCharacterElement(p1);
      Element e2=sd.getCharacterElement(p2);
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
      //System.out.println(HTMLUtils.element2String(e1)+"   ||  "+HTMLUtils.element2String(e2));
      return e1!=e2;
    }
    catch(Exception e){
      System.err.println("TableUtils::breakTables:"+e);
      return false;
    }
  }

  public static void tableConsistentRemove(Document doc, int p1, int p2){

    try{
      DefaultStyledDocument sd=(DefaultStyledDocument)doc;
      Element e1=sd.getCharacterElement(p1);
      Element e2=sd.getCharacterElement(p2);
      HTML.Tag currTag=null;
	    while(e1 != null &&
           (currTag=(HTML.Tag)e1.getAttributes().getAttribute(StyleConstants.NameAttribute)) != HTML.Tag.TD &&
           currTag!=HTML.Tag.TH){
	      e1 = e1.getParentElement();
	    }
	    while(e2 != null &&
           (currTag=(HTML.Tag)e2.getAttributes().getAttribute(StyleConstants.NameAttribute)) != HTML.Tag.TD &&
           currTag!=HTML.Tag.TH){
	      e2 = e2.getParentElement();
	    }
    }
    catch(Exception e){
      System.err.println("TableUtils::removeConsistentForTables:"+e);
    }

  }


  /**
   * Calculates the dimentions of the table returning Rectangle
   */
  public static Rectangle getPositionInTableAsRectangle(Element charE, Element te){

     return new Rectangle(getPositionInTable(charE, te), getTableDimensions(te));
  }

  /**
   * Calculates cursor's current position in the table
   * ce may be either character element or TD element (or any element) - the only restriction:
   * between ce and te there are no other table elements
   */
  public static Point getPositionInTable(Element ce, Element te){

    if(te.getAttributes().getAttribute(StyleConstants.NameAttribute)!=HTML.Tag.TABLE){
       System.err.println("HTMLUtils::getTableDimensions: element passed is not Table element!");
       return null;
    }

    Element myCurrTRElement=getWrappingTD(ce);
    if(myCurrTRElement==null){
      System.out.println("HTMLUtils::getPositionInTable: not in <td>?");
      return null; //we were not in TD
    }

    //double check if TD is in the same <table>
    if(myCurrTRElement.getParentElement()==null ||
       myCurrTRElement.getParentElement().getParentElement()==null ||
       myCurrTRElement.getParentElement().getParentElement()!=te){
       System.err.println("HTMLUtils::getPositionInTable:TD element been deterined is not in wrapping table!");
    }

    int rowsNum=te.getElementCount(); //it is not number of rows - we have to check it
    //System.out.println("rowsNum="+rowsNum);
    if(rowsNum==0){
      System.err.println("TMLUtils::getPositionInTable:rowsNum=0? - should not occur");
      return null; //empty table - not supposed to be here, but just to be sure
    }

    for(int i=0; i<rowsNum; i++){
       Element tr=te.getElement(i);
       int numOfCols=tr.getElementCount();
       //System.out.println(numOfCols);
       if(rowsNum==0){
          System.err.println("HTMLUtils::getPositionInTable:colsNum=0? - should not occur");
       }
       for(int j=0; j<numOfCols; j++){
           Element td=tr.getElement(j);
           if(td==myCurrTRElement)return new Point(j, i); //we count from (0,0)
           //System.out.println("("+j+","+i+")");
       }
    }
    return null;
  }

  /**
   * Returns row Element by index
   */
  public static Element getRowElement(Element te, int rowNum){

    int total=te.getElementCount();
    if(rowNum>=total){
       System.err.println("HTMLUtils::getRowElement: row>=total?");
       return null;
    }
    Element currentRowElement=te.getElement(rowNum);
    if(currentRowElement==null){
       System.err.println("HTMLUtils::getRowElement: null?");
       return null;
    }
    if(currentRowElement.getAttributes().getAttribute(StyleConstants.NameAttribute)!=HTML.Tag.TR){
       System.err.println("HTMLUtils::getRowElement is not TR?");
       return null;
    }
    return currentRowElement;
  }

  /**
   * We have to make this method safer and more intelligent
   */
  public static Element getColumnElement(Element tr, int col){

    int total=tr.getElementCount();
    if(col>=total){
       System.err.println("HTMLUtils::getRowElement: row>=total?");
       return null;
    }

    Element currentColElement=tr.getElement(col);
    if(currentColElement==null){
       System.err.println("HTMLUtils::getColumnElement is null?");
       return null;
    }
    if(currentColElement.getAttributes().getAttribute(StyleConstants.NameAttribute)!=HTML.Tag.TD){
       System.err.println("HTMLUtils::getColumnElement is not TD?");
       return null;
    }
    return currentColElement;
  }

}

