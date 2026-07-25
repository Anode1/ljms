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

import java.io.DataInputStream;

import org.is.net.Servlet;
import org.is.server.MessageServlet;
import org.is.server.MessageContext;
import org.is.jms.MessageImpl;

import javax.jms.Message;
import javax.jms.JMSException;

/**
 *
 * Main Server entry point. Deals with parameters, initialization and start
 * main server class (org.is.BroadcastingServer).
 *
 * @since   JDK1.0
 */
public class Server{

  public static int port=6111;


  public static void main(String args[]){

    //MessageContext doing nothing:
    MessageContext context=new MessageContext();

    try{
      MessageServlet servlet=new MessageServlet(context);
      servlet.startService(port);

      //while true do the following:
      //read input from the console (stdin) as lines, wrap those lines into
      //messages to all connected clients
      DataInputStream dis=new DataInputStream(System.in);

      while(true){

         String line=dis.readLine();

         Message msg=new MessageImpl();
         msg.setStringProperty("text",line);
         context.real_broadcast(msg);
      }
    }
    catch(Exception e){
       e.printStackTrace();;
    }

  }//main



}
