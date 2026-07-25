/**
 * @(#)Server.java
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
 * Telnet example. Illustrates how to build any protocol servers on top of Servlet.
 * It is very primitive demonstration - not full-fledged Telnet (just demo how to build it).
 *
 * @version 1.0
 * @since   JDK1.0
 */
public class Server extends Servlet{

  public static final int LINE_SIZE=255;

  public void service()throws ServletException{

   String user=null;
   try{

    InputStream is=this.getSocket().getInputStream();
    OutputStream os=this.getSocket().getOutputStream();

    int i=0;
    byte buffer[]=new byte[LINE_SIZE];

    //write login prompt:
    String login="Enter Login name:>";
    os.write(login.getBytes());
    os.flush();

    //read login
    i=is.read(buffer,0,LINE_SIZE);
    user=new String(buffer, 0, i);

    //write prompt for password:
    String pprompt="Enter password:>";
    os.write(pprompt.getBytes());
    os.flush();

    //read passwd typed:
    i=is.read(buffer,0,LINE_SIZE);
    String passwd=new String(buffer,0,i);

    //Allow to login with any password!
    System.out.println("User "+user+" logged in");

    String prompt=">";
    byte[] promptBytes=prompt.getBytes();
    os.write(promptBytes);
    os.flush();

    String cmd="";

    //main session loop:
    while(true){

      //read command:
      i=is.read(buffer,0,LINE_SIZE);
      cmd=new String(buffer,0,i);
      cmd=cmd.trim(); //remove trailing spaces/CR/LF
      //System.out.println(cmd+" invoked");

      //write prompt:
      os.write(promptBytes);
      os.flush();

      if("exit".equals(cmd)){
         disconnect();   //we can also just return from service() method

      }
      else if("quit".equals(cmd)){
         return;
      }
      else{
         //whatever
      }
    }


   }
   catch (IOException e) {
     // it's OK
   }
   catch (Exception e) {
     e.printStackTrace();
   }
   finally{
     System.out.println("User "+user+" logged out");
   }

  }

  public static void main(String[] args) {

    int port = 23;

    if(args!=null && args.length>0)
    try {
      port = Integer.parseInt(args[0]);
    }
    catch (NumberFormatException e) {
	    System.err.println("Error: Default port is taken");
    }

    try{
      Server server = new Server();
      server.startService(port);
    }
    catch (Exception e) {
      e.printStackTrace();
    }
  }


}