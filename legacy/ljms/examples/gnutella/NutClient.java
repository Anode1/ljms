/**
 * @(#)NutClient.java
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
package org.is.nut;

import java.net.*;
import java.io.*;

import org.is.logmanager.*;
import org.is.net.*;

/**
 * GNutella client - not finished
 *
 * @version 1.0
 * @since   JDK1.0
 */
public class NutClient{

  public static void main(String[] args) {

    try {
       Socket sock=new Socket("localhost", 6100);

       OutputStream os=sock.getOutputStream();
       InputStream is=sock.getInputStream();

       os.write(NutServer.REQUEST_STRING.getBytes());

      /* while(true){

        //...

        //implement protocol here

       */

     }
     catch (Exception e) {
        e.printStackTrace();
     }
     System.out.println("exiting");
  }

}


