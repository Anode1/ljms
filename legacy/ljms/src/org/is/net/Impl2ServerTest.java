/**
 * @(#)Impl2ServerTest.java
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

import org.is.logmanager.*;

/**
 *
 * @version 1.0
 * @since   JDK1.0
 */
public class Impl2ServerTest extends Servlet{

  public void service(){

   try{

    ObjectInputStream ois=new ObjectInputStream(new BufferedInputStream(this.getSocket().getInputStream()));
    ObjectOutputStream oos=new ObjectOutputStream(new BufferedOutputStream(this.getSocket().getOutputStream()));
    oos.flush();

    System.out.println((String)ois.readObject());
    
    oos.writeObject("received");
    oos.flush();

    for(int i=0;i<1000;i++){
         String s=(String)ois.readObject();
         oos.writeObject("two");
         oos.flush();
    }

    DataInputStream dis=new DataInputStream(new BufferedInputStream(this.getSocket().getInputStream()));
    DataOutputStream dos=new DataOutputStream(new BufferedOutputStream(this.getSocket().getOutputStream()));

    for(int i=0;i<1000;i++){
         String s=(String)dis.readUTF();
         dos.writeUTF("three");
         dos.flush();         
    }


   }
   catch(ClassNotFoundException e){
    System.out.println(e);
   }
   catch (Exception e) {
    e.printStackTrace();
   }

  }

  public static void main(String[] args) {

    int port = 6100;

    LogManager log=LogManager.createInstance(System.getProperty("user.dir"),"logs_test");
    log.setDebugLevel(LogManager.DEBUG_ALL);    

    if(args!=null && args.length>0)
    try {
      port = Integer.parseInt(args[0]);
    }
    catch (NumberFormatException e) {
	    System.err.println("Error: Default port is taken");
    }

    try {
      Impl2ServerTest server = new Impl2ServerTest();
      server.startService(port);
    }
    catch (Exception e) {
      e.printStackTrace();
    }

  }



}