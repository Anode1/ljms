/**
 * @(#)RelayRouter.java
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
package org.is.server.relay;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.jms.Message;

import org.is.server.MessageContext;
import org.is.server.MessageServlet;

import org.is.logmanager.*;

/**
 * Shared by all client threads apllication data
 *
 * Class is not used now. Was used in concert with ServerConnection
 * (on the place of Context) adding list of servers to it and having
 * additional message routing rules (message relay)
 *
 * @since   JDK1.0
 */
public class RelayRouter extends MessageContext{

  private Vector servers = new Vector(3);  //pointers to all server Threads

  public RelayRouter(){
  }
  
  public void addAnotherServer(ServerConnection sc){

    servers.addElement(sc);
    if(log.debugLevel(DebugLevel.DEBUG_CLIENTS))log.printDebug(getServersList());
  }

  /**
   * broadcasts a message "msg"
   */
  public void broadcast(Message msg, ServerConnection sourceThread){
  /*
    if(sourceThread instanceof ClientConnection){
      if(log.debugLevel(DebugLevel.DEBUG_MESSAGE_DISTRIBUTION))log.printDebug("broadcast:"+msg.getCommand()+" from:"+((ClientConnection)sourceThread).getAlias());
    }
    else if(sourceThread instanceof ServerConnection){
      if(log.debugLevel(DebugLevel.DEBUG_MESSAGE_DISTRIBUTION))log.printDebug("broadcast:"+msg.getCommand()+" from:"+((ServerConnection)sourceThread).getHostPort());
    }
    else{
       log.printError("broadcast: unknown type");
    }

    //ALL SERVERS:
    synchronized(servers){

        ServerConnection toThread=null;

        for(Enumeration enum = servers.elements(); enum.hasMoreElements() ;){

            toThread=(ServerConnection)enum.nextElement();

            if(sourceThread instanceof ServerConnection){     //from server to a server
                  //nothing
            }
            else if(sourceThread instanceof ClientConnection){ //from client to a server
                if(!sourceThread.equals(toThread))toThread.sendMessage(msg);
                if(log.debugLevel(DebugLevel.DEBUG_MESSAGE_DISTRIBUTION))log.printDebug("msg:"+msg.getCommand()+" sent to server : "+toThread.getHostPort());
            }
            else log.printError("ThreadsContainer::unknown instance");
        }
        
    }//synchronized

    //ALL CLIENTS:
    //synchronized(clients){

        ClientConnection toThread=null;

        for(Enumeration enum = clients.elements(); enum.hasMoreElements() ;){

            toThread=(ClientConnection)enum.nextElement();

            //there is no need to send the message to itself:
            //if (toThread.equals(sourceThread) ) continue;

            if( ((ClientConnection)sourceThread).getPassword().equals(toThread.getPassword())){
              toThread.sendMessage(msg);    //from both to client
               // System.out.println("message "+msg.getCommand()+" sent to: "+toThread.getAlias());
            }
        }//for

   // }//synchronized

  }

public void real_broadcast(Message msg, MessageConnection sourceThread){

    if(sourceThread instanceof ClientConnection){
      if(log.debugLevel(DebugLevel.DEBUG_MESSAGE_DISTRIBUTION))log.printDebug("real_broadcast:"+msg.getCommand()+" from:"+((ClientConnection)sourceThread).getAlias());
    }
    else if(sourceThread instanceof ServerConnection){
      if(log.debugLevel(DebugLevel.DEBUG_MESSAGE_DISTRIBUTION))log.printDebug("real_broadcast:"+msg.getCommand()+" from:"+((ServerConnection)sourceThread).getHostPort());
    }
    else{
       log.printError("real_broadcast: unknown type");
    }

    //ALL SERVERS:
    synchronized(servers){

        ServerConnection toThread=null;

        for(Enumeration enum = servers.elements(); enum.hasMoreElements() ;){

            toThread=(ServerConnection)enum.nextElement();

            if(sourceThread instanceof ServerConnection){     //from server to a server
                  //nothing
            }
            else if(sourceThread instanceof ClientConnection){ //from client to a server
                if(!sourceThread.equals(toThread))toThread.sendMessage(msg);
                if(log.debugLevel(DebugLevel.DEBUG_MESSAGE_DISTRIBUTION))log.printDebug("real_broadcast::msg:"+msg.getCommand()+" sent to server : "+toThread.getHostPort());
            }
            else log.printError("ThreadsContainer::real_broadcast:unknown instance");
        }
        
    }//synchronized

    //ALL CLIENTS:
    synchronized(clients){

        ClientConnection toThread=null;

        for(Enumeration enum = clients.elements(); enum.hasMoreElements() ;){
            toThread=(ClientConnection)enum.nextElement();
            toThread.sendMessage(msg);
        }//for

    }//synchronized
      */
  }

  /**
   * sends a message "msg" to the specified client
   */
  public void sendToClient(Message msg, MessageServlet sourceThread){
     /*

       ClientConnection toThread=null;
       for(Enumeration enum = clients.elements(); enum.hasMoreElements() ;){

            toThread=(ClientConnection)enum.nextElement();

            if (toThread.equals(sourceThread)) {
                toThread.sendMessage(msg);
                if(log.debugLevel(DebugLevel.DEBUG_MESSAGE_DISTRIBUTION))log.printDebug("msg "+msg.getCommand()+" sent to client: "+toThread.getAlias());
                break ;
            }
       }
       */

  }
       
  /**
   * here is where we handle any input received from a Client
   * This method is called by ClientThread directly
   */
  public void handleMessage(Message msg, ServerConnection sourceThread) {

       /*
       if(msg==null){
          log.printError("ThreadsContainer::handleInput:message is null!");
          return;
       }

       byte cmd=msg.getCommand();

       if(cmd==0){
          log.printError("ThreadsContainer::handleInput:command is null!");
          return;
       }

       if(log.debugLevel(DebugLevel.DEBUG_MESSAGE_DISTRIBUTION))log.printDebug("ThreadContainer::handleMessage: message="+msg);

       String val=msg.getValue();
       String name=msg.getName();
       Message message=null;   //for new messages


       if(cmd==CSMessage.LOGIN) {

         //System.out.println(getClientsList());
         //we should send this dummy message to clean the system off
         //dead threads (when a user closes the browser, his socket is still open,
         //so the thread still be in the system)
         real_broadcast(new Message(CSMessage.DUMMY,null, name), (ClientConnection)sourceThread);


         String newName = name+"-"+Long.toString(System.currentTimeMillis()&0xff );

         if(sourceThread instanceof ClientConnection)
           ((ClientConnection)sourceThread).setAlias(newName);

         message = new Message(CSMessage.LOGIN, null, newName);
         sendToClient(message, (ClientConnection)sourceThread);


          message = new Message(CSMessage.LIST, clientsListToString(), newName);
          real_broadcast(message, sourceThread);

          message = new Message(CSMessage.ENTER, newName+" has entered.", newName);
          real_broadcast(message, sourceThread);

          LogManager.getInstance().printEvent("New Client: "+newName);


         return ;

       }


       else if(cmd==CSMessage.INVITATION) {

           StringTokenizer st = new StringTokenizer(val, ",");
           String whomToInvite = st.nextToken();
           String whatGame = st.nextToken();
                                                                                                     
           ClientConnection toThread;


           for(Enumeration enum = clients.elements(); enum.hasMoreElements() ;){

               toThread=(ClientConnection)enum.nextElement();

               if (toThread.getAlias().equals(whomToInvite)) {
                    msg.setValue(name+","+whatGame);
                    sendToClient(msg, toThread);
                    break;
                }
            }
            

          return ;
       }
       
       else if(cmd==CSMessage.ACCEPT_INVITATION) {
           StringTokenizer st = new StringTokenizer(val, "||");
           String whoInvited = st.nextToken();
           String target = st.nextToken();

           //System.out.println("ACCEPT_INVITATION:: whoInvited = "+whoInvited+"; target = "+target );

           ClientConnection toThread;

           for(Enumeration enum = clients.elements(); enum.hasMoreElements() ;){

               toThread=(ClientConnection)enum.nextElement();

               if (toThread.getAlias().equals(target)||toThread.getAlias().equals(whoInvited)) {
                    toThread.setPassword(val);//let's set the new password to the both players;
                    sendToClient(msg, toThread);//and send this new password to both of them;

                }

              message = new Message(CSMessage.LIST, clientsListToString(), name);
              real_broadcast(message, sourceThread);
          }
       }

       else if(cmd==CSMessage.REJECT_INVITATION) {

           StringTokenizer st = new StringTokenizer(val, ",");
           String target = st.nextToken();
           String whoRejected = st.nextToken();
           ClientConnection toThread;

         for(Enumeration enum = clients.elements(); enum.hasMoreElements() ;){

               toThread=(ClientConnection)enum.nextElement();

               if (toThread.getAlias().equals(target)) {
                    msg.setValue(whoRejected);
                    sendToClient(msg, toThread);
                    break;
                }
          }
       }

        else if(cmd==CSMessage.SET_PASSWORD_BACK) { //set the default group password == null to both players

           StringTokenizer st = new StringTokenizer(val, "||");
           String whoInvited = st.nextToken();
           String whoIsInvited = st.nextToken();

      
           String source, target;
           //let's define who has left the game, so we'll send an appropriate message to the second player
           if(((ClientConnection)sourceThread).getAlias().equals(whoInvited)) {
              source = whoInvited;
              target = whoIsInvited;
           }
           else {
              source = whoIsInvited;
              target = whoInvited;
           }

           ClientConnection toThread;

           for(Enumeration enum = clients.elements(); enum.hasMoreElements() ;){

               toThread=(ClientConnection)enum.nextElement();

               if (toThread.getAlias().equals(target)){
                    message=new Message(CSMessage.ALONE, source, name);
                    sendToClient(message, toThread);
                    
                    toThread.setPassword("null");//let's set the group password to the both players;
                    sendToClient(msg, toThread);//and send this new password to both of them;

               }
               else if (toThread.getAlias().equals(source)) {
                    toThread.setPassword("null");//let's set the group password to the both players;
                    sendToClient(msg, toThread);//and send this new password to both of them;

                }
          }
             message = new Message(CSMessage.LIST, clientsListToString(), name);
             real_broadcast(message, sourceThread);
        }


       else if(cmd==CSMessage.STARTOVER) {
             broadcast(msg, sourceThread);// broadcast
       }

       else if(cmd==CSMessage.REMOVE) {
       
         if(sourceThread instanceof ClientConnection)
           removeClient((ClientConnection)sourceThread);

       }
       else if(cmd==CSMessage.LOGOUT) {
          
          if(sourceThread==null){
              log.printError("sourceThread==0?");
              return;
          }

 	        if(sourceThread instanceof ServerConnection){


             message = new Message(CSMessage.LIST, clientsListToString(), name);
             real_broadcast(message, sourceThread);

             msg.setValue(name+" has left.");
             real_broadcast(msg, sourceThread);
          }
	      }
        
        else if(cmd==CSMessage.SAY) {

             //in order not to duplicate the message "aClient says: " when sent by a server
             if(sourceThread instanceof ClientConnection)
                  msg.setValue(name+" says: "+val);
             else
                  msg.setValue(val);  //if from another server thread

             broadcast(msg, sourceThread);

             return ;
        }

        else if(cmd==CSMessage.SOUND) {
             broadcast(msg, sourceThread);
        }
        else if(cmd==CSMessage.DATA){

              broadcast(msg, sourceThread);
        }
        //else log.printError("ThreadsContainer::handleInput:unknown command:"+cmd);

        */
  }//handleInput


  /**
   * Called only from thread when exiting
   */

  /**
   * Called only from thread when exiting
   */
  public void removeServer(ServerConnection tempThread){

     servers.removeElement(tempThread);
     
     if(log.debugLevel(DebugLevel.DEBUG_CLIENTS))log.printDebug("dead server thread "+tempThread.getHostPort()+" removed");
     if(log.debugLevel(DebugLevel.DEBUG_CLIENTS))log.printDebug(getServersList());
  }

  /**
   * For debugging purposes
   */  
  public  String getServersList(){

      StringBuffer sb=new StringBuffer("Other working servers threads:{");
      ServerConnection tempThread=null;
      for(Enumeration enum = servers.elements(); enum.hasMoreElements() ;) {
         tempThread=(ServerConnection)enum.nextElement();
         sb.append(tempThread.getHostPort());
         if(enum.hasMoreElements())sb.append(",");
      }
      sb.append("}");
      return sb.toString();
  }

  //there is no need to do finalization here since Threads will be
  //garbagecollected by an implementation of org.is.net.ThreadContainer (thread engine)
  // org.is.net.ThreadContainer - an implementation

}
