/**
 * @(#)NutServer.java
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
 * Test GNutella server - not finished
 *
 * @version 1.0
 * @since   JDK1.0
 */
public class NutServer extends Servlet{

  final static String REQUEST_STRING="GNUTELLA CONNECT/0.4\n\n";
  final static String RESPONSE_STRING="GNUTELLA OK\n\n";

  public void service()throws ServletException{

   try{

    InputStream is=this.getSocket().getInputStream();
    OutputStream os=this.getSocket().getOutputStream();

    //os.write(REQUEST_STRING.getBytes());

    os.write(RESPONSE_STRING.getBytes());

    while(true){

      int c=is.read();

      //... implement protocol here
    }


   }
   catch (IOException e) {
     // it's OK
   }
   catch (Exception e) {
     e.printStackTrace();
   }

  }
 
  public static void main(String[] args) {

    int port = 6100;

    if(args!=null && args.length>0)
    try {
      port = Integer.parseInt(args[0]);
    }
    catch (NumberFormatException e) {
	    System.err.println("Error: Default port is taken");
    }

    try{
      NutServer server = new NutServer();
      server.startService(port);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }


}