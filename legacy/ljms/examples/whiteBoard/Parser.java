/**
 * @(#)Parser.java
 * Copyright (C) 2001 Vasili Gavrilov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

import java.util.StringTokenizer;
import java.awt.*;

public class Parser {
  MainDrawingPanel parent;


  public Parser(MainDrawingPanel parent) {
       this.parent = parent;
  }



  public void packData(){

     String str2Send  = new String();


     str2Send+=(Integer.toString(parent.penColor.getRGB())+",");

     int i=0;

     while (parent.X[i] !=-1 && i<100000) {
              str2Send+=(Integer.toString(parent.X[i])+","+Integer.toString(parent.Y[i])+",") ;
              i++;

     }
     
      CSMessage ms=new CSMessage(CSMessage.DATA);
      try{
          ms.setStringProperty("data",str2Send);

      }
      catch(Exception e){
          System.err.println("parser:Construction of CSMessage:"+e);
      }

      parent.cClient.stationManager.sendMessage(ms);

     //clean up
     for (int j=0 ; j<100000 ; j++  ){
        parent.X[j] = -1;
        parent.Y[j] = -1;
     }
     parent.cur = 0;
}



public void unpackData(String val){
 //System.out.println(val);
  
    int [] tempX , tempY;

    StringTokenizer st = new StringTokenizer(val, ",");
    int tokens = st.countTokens();

    if(tokens!=0){
    Color tempCol = parent.penColor;
    parent.penColor = new Color(Integer.parseInt(st.nextToken()));

     int len=(tokens-1)>>1;
     tempX = new int[len];
     tempY = new int[len];

          int j =0;
          while(st.hasMoreTokens()){
             tempX [j] = Integer.parseInt(st.nextToken());
             tempY [j] = Integer.parseInt(st.nextToken());
             j++;
          }


          parent.offScreenGraphics.setColor(parent.penColor);
          parent.lastX=tempX[0];
          parent.lastY=tempY[0];
          for (int k=1 ; k<tempX.length-1;){
            parent.drawLines(tempX[k], tempY[k]);
            k++;
          }

     parent.repaint();

     parent.penColor =  tempCol;

   } // if ( tokens != 0)
}
}