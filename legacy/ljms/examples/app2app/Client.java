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

import java.io.*;
import java.net.*;
import java.util.*;

import javax.jms.*;

import org.is.jms.*;
import org.is.jms.broker.MessageSocket;

public class Client extends Thread{

  LSessionImpl messageCentral;

  public void connect(){

    try{
      //initialize connection:
      messageCentral=new LSessionImpl();
      messageCentral.setMessageTransport(new MessageSocket("localhost", 6111));
      messageCentral.connect();                  //make connection (init)
    }
    catch(JMSException e){
      System.err.println("Error connecting:"+e);
    }
  }

  /**
   * Thread pooling messages. Notice that only if user wants to do it in it's
   * own thread, LSessionImpl session should be applied. Otherwise - it is simpler
   * to use LSessionRunnableImpl having thread like this built-in
   */
  public void run(){

    try{
       while(true){

          Message msg=messageCentral.getMessage(); //we are blocked here while there is no input

          String s=msg.getStringProperty("text");
          if(s==null)s="null message";  //just for case

          System.out.println("response from another node: "+s);

          try{Thread.sleep(40);}catch(InterruptedException e){}
       }
     }
     catch(JMSException e){
       System.err.println("Error:"+e);
       //notice: main thread should be notified about the error, so either
       //here we have to modify some shared with that thread variable/class
       //or see applet example (TrivialChat example) where ErrorListener has been
       //demonstrated in a concert with LSessionRunnableImpl
     }
  }

  public static void main(String args[]){

    Client client=new Client();

    try{

      client.connect();
      client.start();

      //while true do the following:
      //read input from the console (stdin) as lines, wrap those lines into
      //messages and send to the server.
      DataInputStream dis=new DataInputStream(System.in);
      int i=0;
      while(true){
         /*
         String line=dis.readLine();
         */
         String line="xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx";

         Message ms=new MessageImpl();
         ms.setStringProperty("data",line);
         ms.setStringProperty("text",Integer.toString(i));
         client.messageCentral.sendMessage(ms);
         i++;
      }

    }
    catch(JMSException e){
      //nothing
    }
    catch(Exception e){
      e.printStackTrace();
    }
    finally{
      try{client.messageCentral.close();}catch(JMSException jmse){}
      System.out.println("disconnected");
    }

  }//main


}
