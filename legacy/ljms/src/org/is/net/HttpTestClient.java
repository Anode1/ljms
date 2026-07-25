/**
 * @(#)HttpTestClient.java
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
package org.is.net;

import java.net.*;
import java.io.*;

/**
 * Testing service
 *
 * @version 1.0
 * @since   JDK1.0
 */
public class HttpTestClient extends Servlet{

  public static void main(String[] args) {

    try {
          //POST
         // URL outURL = new URL(codebase, outServlet);
          URL outURL=new URL("http://localhost:6100/org.is.net.EchoTest");
          URLConnection outCon = outURL.openConnection();
          
          outCon.setDoInput(true);
          outCon.setDoOutput(true);
          outCon.setUseCaches(false);
          outCon.setDefaultUseCaches(false);
          //outCon.setRequestProperty();
          // Work around a Netscape bug:
          outCon.setRequestProperty("Content-Type", "application/octet-stream");
          //outCon.connect();

          OutputStream os=outCon.getOutputStream();
      /*
       while(true){

         int c=System.in.read();
         os.write(c);
         os.flush();

         int n=is.available();
         for(int i=0; i<n; i++){
            System.out.print((char)is.read());
         }
       }
       */

          os.write(2);
          os.flush();
          os.close();
          outCon.getInputStream(); //only after this we get real connection
                                   //URLConnection collects Input into internal
                                   //BufferInputStream

     }
     catch (Exception e) {
        e.printStackTrace();
     }
     System.out.println("exiting");
     try{Thread.sleep(60000);}catch(InterruptedException ie){};
  }

}


