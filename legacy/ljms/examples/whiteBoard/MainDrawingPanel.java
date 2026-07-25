/**
 * @(#)MainDrawingPanel.java
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
import java.awt.*;
import java.awt.image.*;

public class MainDrawingPanel extends Panel  {

  Parser parser;
  public Image  offScreenImage ,palette ;
  Graphics offScreenGraphics ;
  public Color  penColor;

  public CClient cClient;
  public int [] X, Y;
  int w0, h0, cur;


public  MainDrawingPanel( CClient cClient) {
  
  this.cClient = cClient;
  this.palette = cClient.stationManager.palette;

  penColor = Color.red;

  X = new int [100000] ;
  Y = new int [100000] ;
  for (int i=0 ; i<100000 ; i++  ){
     X[i] = -1;
     Y[i] = -1;
  }
 cur = 0;

  w0 = 400; h0= 235;
  setBounds(0, 0 , w0 , h0);
  parser = new Parser (this);


}

/////////////////PROCESS SENT DATA////////////////////

public void updateDrawingPanel(String val){

    parser.unpackData(val);
}

/////////////////PAINT ROUTINE////////////////////////



public void update (Graphics g) {

     paint (g) ;
}

public void paint(Graphics g) {


   if (offScreenImage == null ||
             offScreenImage.getHeight(null) != h0 ||
             offScreenImage.getWidth(null) != w0 ) {

                offScreenImage = createImage(w0, h0);
                offScreenGraphics = offScreenImage.getGraphics();
                offScreenGraphics.setColor(Color.white);
                offScreenGraphics.fillRect(0,0,w0,h0);
                offScreenGraphics.setColor(Color.black);
    }

   offScreenGraphics.setColor(penColor);
   g.drawImage(offScreenImage, 0, 0, this);
   
 }


/////////////////MOUSE ROUTINE//////////////////////////

int lastX,lastY;


public boolean mouseDown(Event e, int x, int y) {


    lastX = x; lastY = y;

    X[cur] = x; Y[cur] = y;
    cur++;


    return true;
  }


public boolean mouseDrag(Event e, int x, int y) {


    drawLines(x,y);

    repaint();

    X[cur] = x; Y[cur] = y;
    cur++;

    return true;
}



public boolean mouseUp(Event evt, int x, int y){


   X[cur] = x; Y[cur] = y;
   cur++;
   parser.packData();

   repaint();

   return true;

}

public void drawLines(int x, int y){

   offScreenGraphics.drawLine(lastX, lastY, x, y);

   lastX = x; lastY = y;

}



}
