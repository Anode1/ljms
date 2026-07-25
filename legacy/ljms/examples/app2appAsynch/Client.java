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

public class Client implements MessageListener, ExceptionListener{

  LSessionRunnableImpl messageCentral;
  int port=6111;
  String host="localhost";

  public void connect(){

    try{
      //initialize connection:
      messageCentral=new LSessionRunnableImpl();
      messageCentral.setMessageTransport(new MessageSocket(host, port));
      messageCentral.setMessageListener(this);
      messageCentral.setExceptionListener(this); //if not set - printing to console
      messageCentral.connect();                  //make connection (init)
      messageCentral.start();                    //start message delivery
    }
    catch(JMSException e){
      System.err.println("Error connecting:"+e);
    }
  }

  /**
   * Callback from Session when new message arrives
   */
  public void onMessage(Message msg){

    //System.out.println(msg.toString());
    try{
      String s=msg.getStringProperty("text");
      if(s==null)s="null"; //just for case
      System.out.println("Received from another node>"+s);
    }
    catch(Exception e){
      System.err.println("onMessage:"+e);
    }
  }

  public void onException(JMSException e){
    //we do nothing here since we have session's close() in finally , in main
    //thread and JMSException will be thrown anyway when connection is invalid
    //when user will send a new message. For purity probably it's better to
    //invalidate session right here and to exit from the application immediately 
  }

  public static void main(String args[]){

    Client client=new Client();

    try{

      client.connect();  

      //while true do the following:
      //read input from the console (stdin) as lines, wrap those lines into
      //messages and send to the server.
      DataInputStream dis=new DataInputStream(System.in);
      while(true){
      
         String line=dis.readLine();

         Message ms=new MessageImpl();
         ms.setStringProperty("text",line);
         client.messageCentral.sendMessage(ms);
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
