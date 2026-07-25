/**
 * @(#)EchoServer.java
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
import java.net.*;
import java.io.*;

import org.is.net.*;

/**
 * Test echo service
 *
 * @version 1.0
 * @since   JDK1.0
 */
public class EchoServer extends Servlet{

  public void service()throws ServletException{

   try{

    InputStream is=this.getSocket().getInputStream();
    OutputStream os=this.getSocket().getOutputStream();

    while(true){
         int c=is.read();

         System.out.print((char)c);

         os.write(c);
         os.write(":-)".getBytes());
         os.flush();
    }


   }
   catch (IOException e) {
     // e.printStackTrace();
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
      EchoServer server = new EchoServer();
      server.startService(port);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }


}