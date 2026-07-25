/**
 * @(#)MessageServlet.java
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
package org.is.server;

import java.io.IOException;

import javax.jms.JMSException;
import javax.jms.Message;

import org.is.logmanager.*;
import org.is.util.Utils;
import org.is.net.*;
import org.is.jms.*;
import org.is.jms.broker.MessageSocket;

/**
 * Message servlet.
 *
 * @see Servlet
 * @since   JDK1.0
 */
public class MessageServlet extends Servlet{

  protected MessageContext context;
  protected LSessionImpl connection = null;

  protected String name="null";
  protected String group="null";

  public MessageServlet(MessageContext context){

    this.context=context;
    this.setName("Client");
  }

  public void init() throws ServletException{

    try{
      connection=new LSessionImpl();
      connection.setMessageTransport(new MessageSocket(getSocket())); //get ready Socket from superclass
      connection.connect();
    }
    catch(Exception e){
      throw new ServletException(e);
    }
  }

  public void service()throws ServletException{

  
    context.addClient(this);

    try{ 

      while(true){

         Message msg=getMessage();

         context.distribute(msg, this);

         if(log.debugLevel(DebugLevel.DEBUG_CLIENT_MESSAGES))log.printDebug("ClientConnection::Message "+msg+" came from client:"+this.getAlias());

         try{ Thread.sleep(20);} catch (InterruptedException e) {} //to be a good citizen if too many messages
      }
    }
    catch(JMSException e){
      //no report - it is normal (client can disconnect any time)
      //log.printDebug("ClientConnection:"+e);
    }
    catch(Exception e){
      //probably there is a problem
      log.printDebug("ClientConnection::Warning:not JMSException occured:"+e);
    }
    finally{
      context.removeClient(this);
      if(log.debugLevel(DebugLevel.DEBUG_CLIENT_MESSAGES))log.printDebug("ClientConnection::client:"+this.getAlias()+" removed");
    }
  }

  /**
   * Sends message. This method is called by other threads when sending messages
   * by broadcasting or only to this particular client so it does not throw any
   * Exceptions been silent
   */
  public void sendMessage(Message msg){
                        
    try{
       connection.sendMessage(msg);
    }catch (JMSException e) {
       this.disconnect(); //force disconnect
       //log.printDebug("ClientConnection:"+e);
    }catch (Exception e) {
       disconnect(); //force disconnect
       log.printError("ClientConnection::Warning:not JMSException occured:"+e);
    }
       
  }

  /**
   * Gets message from the socket. Returns message if connection is valid and
   * message has been deserialized successfully or throws exception otherwise.
   * In the case of exception, subclass or user class should remove client
   * structure from the list explicitly (thread will be removed automatically
   * by the thread engine - server framework implementation (org.is.net) will do it
   * (in the same way like ejb container deals with caching, pooling etc)
   */
  public Message getMessage() throws JMSException{

     Message msg=connection.getMessage();
     return msg;
  }

  public void setPassword (String g){

     group = g;
  }

  public String getPassword(){

     return group;
  }

  public void setAlias(String n){

     name = n;
  }

  public String getAlias(){

     return name;
  }  

}
