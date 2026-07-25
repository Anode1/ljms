package org.is.html;

import javax.swing.text.*;
import javax.swing.text.html.*;
import java.awt.Dimension;

/**
 * Model for tables used for cells spanning.
 * Not completed. Not used currently.
 *
 * @version 1.2 11/23/00
 * @since jdk1.0
 */
public class Grid{

    public Element[][] cell;
    public boolean[][] empty;

    public Grid(Element te){

      Dimension dim=TableUtils.getTableDimensions(te);
      cell=new Element[dim.height][dim.width];
      empty=new boolean[dim.height][dim.width];

      //firstly initialize spanned
      int index=0;
      for(int row=0; row<dim.height; row++){
         Element tr = te.getElement(row);
         int n=tr.getElementCount();
         for(int i=0; i<n; i++){
            Element td = tr.getElement(i);
            if(td==null) throw new RuntimeException("HTMLUtils::getColumnElement: one element is null?");

            AttributeSet attrs=td.getAttributes();
            HTML.Tag tag=(HTML.Tag)attrs.getAttribute(StyleConstants.NameAttribute);
            if(tag!=HTML.Tag.TD && tag!=HTML.Tag.TH)throw new RuntimeException("HTMLUtils::getColumnElement is not TD or TH?");

            String colspan=(String)attrs.getAttribute(HTML.Attribute.COLSPAN);
            String rowspan=(String)attrs.getAttribute(HTML.Attribute.ROWSPAN);
            if(colspan!=null || rowspan!=null){
              try{
                int sc=Integer.parseInt(colspan);
                int rs=Integer.parseInt(rowspan);
                for(int k=0; k<rs-1; k++){
                   for(int l=0; l<rs-1; l++){
                       empty[row+k][i+l]=true;
                       cell[row+k][i+l]=td;  //point to Spanning element
                   }
                }
              }
              catch(NumberFormatException nfe){
                System.err.println("TableUtils::getColumnElement: COLSPAN/ROWSPAN attribute found is not number:"+colspan);
              }
            }//if

         }//for
      }//for



    }//constructor

    public String toString(){

      int height=cell.length;
      int width=cell[0].length;

      StringBuffer sb=new StringBuffer();
      for(int row=0; row<height; row++){
         for(int col=0; col<width; col++){
            sb.append(empty[row][col]);
            if(col<width-1)sb.append(", ");
         }
         sb.append("\n");
      }
      return sb.toString();
    }


}


