/**
 * @(#)Client.java
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
 * Telnet client example.
 * Demonstrates how to built a simple protocol on top of Servlet
 *
 * @version 1.0
 * @since   JDK1.0
 */
public class Client{

  public static final int LINE_SIZE=Server.LINE_SIZE;  //the same as server

  public static void main(String[] args) {

    try {

       Socket sock=new Socket("localhost", 23);

       OutputStream os=sock.getOutputStream();
       InputStream is=sock.getInputStream();

       int i=0;
       byte buffer[]=new byte[LINE_SIZE];
       //String response=null;

       //main shell loop:
       while(true){

         i=System.in.read(buffer,0,LINE_SIZE);

         os.write(buffer,0,i);
         os.flush();

         int n=is.available();
         for(int j=0; j<n; j++){
            System.out.print((char)is.read());
         }
       }
     }
     catch (IOException e) {
     }
     System.out.println("exiting");
  }

}


