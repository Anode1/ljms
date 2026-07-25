/**
 * @(#)Utils.java
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
package common;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Point;

public class Utils{

public static void setCentalizedLocationRelativeMe(Component parent, Component me){

     if(parent==null)return;

     Point parentLoc=parent.getLocation();
     int x=0,y=0; //my future coordinates

     int parentX=parentLoc.x;
     int parentY=parentLoc.y;
     int parentW=parent.getSize().width;
     int parentH=parent.getSize().height;

     int w=me.getSize().width;
     int h=me.getSize().height;

     if(w<parentW)x=parentX+(parentW-w)/2;
     else x=parentX-(w-parentW)/2;

     if(h<parentH)y=parentY+(parentH-h)/2;
     else y=parentY-(h-parentH)/2;

     me.setLocation(x,y);
}


public static Frame getParentFrame(Component c){

   while(c!=null && !(c instanceof Frame)){
       c=c.getParent();
   }
   return (Frame)c;
}



}