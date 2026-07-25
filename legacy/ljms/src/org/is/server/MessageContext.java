/**
 * @(#)MessageContext.java
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
import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.jms.Message;
import javax.jms.JMSException;

import org.is.net.ServletContext;
import org.is.logmanager.*;

/**
 * Shared by all message clients (running in different threads) application data.
 * In other words it is a Message Router and all logic - how to deliver Messages
 * (list of all message receipients - notice: it is different from threads - is here)
 *
 * @since   JDK1.0
 */
public class MessageContext{

  protected Vector clients = new Vector(64);
  
  protected LogManager log=LogManager.getInstance();

  /**
   * Method called by threads themselves to add the client to the list
   */
  public void addClient(MessageServlet cc){

    clients.addElement(cc);

    if(log.debugLevel(DebugLevel.DEBUG_CLIENTS))log.printDebug("MessageContext::"+getClientsList());
    log.printEvent("MessageContext::"+clients.size()+" users connected");
  }

  /**
   * Called only from thread when exiting
   */
  public void removeClient(MessageServlet tempThread){

     //System.out.println("removeClient : "+name);

     if(clients.contains(tempThread)) {
     
        clients.removeElement(tempThread);

        LogManager.getInstance().printEvent("MessageContext:: "+tempThread.getAlias()+" has been removed");
        if(log.debugLevel(DebugLevel.DEBUG_CLIENTS))log.printDebug(getClientsList());
     }
  }

  public void real_broadcast(Message msg){

        MessageServlet to=null;
        
        for(Enumeration enum = clients.elements(); enum.hasMoreElements() ;){
            to=(MessageServlet)enum.nextElement();
            to.sendMessage(msg);
            if(log.debugLevel(DebugLevel.DEBUG_MESSAGE_DISTRIBUTION))log.printDebug("MessageContext::real_broadcast: Message "+msg+" sent to: "+to.getAlias());
        }
  }  

  /**
   * sends a message "msg" to the specified client
   */
  public void sendToClient(Message msg, MessageServlet to){

       MessageServlet aThread=null;
       for(Enumeration enum = clients.elements(); enum.hasMoreElements() ;){

            aThread=(MessageServlet)enum.nextElement();

            if (aThread.equals(to)) {
                aThread.sendMessage(msg);
                
                if(log.debugLevel(DebugLevel.DEBUG_MESSAGE_DISTRIBUTION))log.printDebug("MessageContext::"+msg+" sent to client: "+to.getAlias());

                break ;
            }
       }
  }

  /**
   * Empty method. Subclasses will implement it - how to distribute messages
   * between clients.
   */
  public void distribute(Message msg, MessageServlet sourceThread) throws JMSException{

  }

  /**
   * For debugging purposes only
   */
  protected String getClientsList(){

     StringBuffer sb=new StringBuffer("Clients:{");
     MessageServlet tempThread=null;
     for(Enumeration enum = clients.elements(); enum.hasMoreElements() ;) {
	      tempThread = (MessageServlet)enum.nextElement();
        sb.append(tempThread.getAlias());

        if(enum.hasMoreElements())sb.append(",");
     }
     sb.append("}");
     return sb.toString();
  }

  //there is no need to do finalization here since Threads will be
  //garbagecollected by an implementation of org.is.net.ThreadContainer (thread engine)
  // org.is.net.ThreadContainer - an implementation

}
