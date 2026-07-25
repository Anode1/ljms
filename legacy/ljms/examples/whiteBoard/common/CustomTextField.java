/**
 * @(#)CustomTextField.java
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

import java.awt.*;
import java.awt.event.*;

import org.is.jms.broker.MessageSocket;
import StationManager;
import CSMessage;

public class CustomTextField extends TextField  {
    
    StationManager parent;

    public CustomTextField ( StationManager parent){
        
        super(40);
        
        this.parent = parent;
        

        this.addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent e){
                if (e.getKeyCode()==KeyEvent.VK_ENTER){ sendText(); }

            }
        });

    }


   public void sendText(){
          if (getText().length()>0) {

             CSMessage ms=new CSMessage(CSMessage.SAY);
             try{
                      ms.setStringProperty("sentence",getText());
                      ms.setStringProperty("name",parent.name);
              }
             catch(Exception ex){
                      System.err.println("CustomTextField:Construction of CSMessage:"+ex);
             }
             parent.sendMessage(ms);

		        setText("");
            requestFocus();
          }
   }

}

