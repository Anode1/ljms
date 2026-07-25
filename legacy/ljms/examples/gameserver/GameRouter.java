/**
 * @(#)GameRouter.java
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

import java.io.IOException;
import java.util.Enumeration;
import java.util.Vector;
import java.util.Hashtable;
import java.util.StringTokenizer;

import javax.jms.Message;
import javax.jms.JMSException;

import org.is.server.MessageContext;
import org.is.server.MessageServlet;
import org.is.logmanager.*;

/**
 * Game server (concrete application) specific context - message router.
 * Structure for clients is used from the superclass - no new structures
 * has been added: only router methods (methods making Message distribution)
 * are overriden. More complex routers add new structures for clients - views.
 * Example - relay server (currently relay server example is broken) where
 * Relay server uses RelayRouter as the Context which holds  collection of
 * other connected (interconnected) servers.
 *
 * @since   JDK1.0
 */
public class GameRouter extends MessageContext{

  /**
   * Broadcasts a message to the group having the same groupName
   */
  public void group_broadcast(Message msg, MessageServlet source){

        MessageServlet toClient=null;
        for(Enumeration enum = clients.elements(); enum.hasMoreElements() ;){
            toClient=(MessageServlet)enum.nextElement();
            //there is no need to send the message to itself:
            //if (toClient.equals(source) ) continue;
            if(source.getPassword().equals(toClient.getPassword())){
              toClient.sendMessage(msg);    //from both to client
               // System.out.println("message "+msg.getCommand()+" sent to: "+toThread.getAlias());
            }
        }//for
  }

  /**
   * This is the main message handler - method called from Client threads.
   * It is some kind
   */
  public void distribute(Message msg, MessageServlet sourceThread) throws JMSException{

       if(msg==null){
          log.printError("Router::distribute:message is null! - skipped");
          return;
       }

       byte cmd=msg.getByteProperty("command");
       //System.out.println("Router::distribute:got command "+cmd);

       if(cmd==0){
          log.printError("Router::distribute:command is null! - skipped");
          return;
       }

       if(log.debugLevel(DebugLevel.DEBUG_MESSAGE_DISTRIBUTION))log.printDebug("Router::distribute:"+msg);


       if(cmd==CSMessage.LOGIN) {


         //we should send this dummy message to clean the system off
         //dead threads (when a user closes the browser, his socket is still open,
         //so the thread still be in the system)

         Message ms=new CSMessage(CSMessage.DUMMY);
         real_broadcast(ms);

         String newName = msg.getStringProperty("name")+"-"+Long.toString(System.currentTimeMillis()&0xff );

         sourceThread.setAlias(newName);



         ms=new CSMessage(CSMessage.LOGIN);
         try{
             ms.setStringProperty("name",newName);
         }
         catch(Exception ex){
             System.err.println("Router::distribute:Construction of CSMessage:"+ex);
         }
         sendToClient(ms, sourceThread);



         ms=new CSMessage(CSMessage.LIST);
         try{
             ms.setStringProperty("list",clientsListToString());
             ms.setStringProperty("new",newName);
         }
         catch(Exception ex){
             System.err.println("Router::distribute:Construction of CSMessage:"+ex);
         }
         real_broadcast(ms);

         LogManager.getInstance().printEvent("New Client: "+newName);

         //return ;

       }

       else if(cmd==CSMessage.STARTOVER_NOTIFICATION) {

           String g=msg.getStringProperty("guest");
           MessageServlet toThread=null;
           for(Enumeration enum = clients.elements(); enum.hasMoreElements() ;){

               toThread=(MessageServlet)enum.nextElement();

               if (toThread.getAlias().equals(g)) {
                    sendToClient(msg, toThread);
                    break;
                }
            }
        }

       else if(cmd==CSMessage.WHISPER) {


           String guest = msg.getStringProperty("guest");
           String host = msg.getStringProperty("host");

           MessageServlet toThread=null;
           for(Enumeration enum = clients.elements(); enum.hasMoreElements() ;){

               toThread=(MessageServlet)enum.nextElement();

               if (toThread.getAlias().equals(guest)||toThread.getAlias().equals(host))
                    sendToClient(msg, toThread);

            }

       }

      else if(cmd==CSMessage.READY) {

           String guest = msg.getStringProperty("guest");
           String host = msg.getStringProperty("host");
           
           MessageServlet toThread=null;
           for(Enumeration enum = clients.elements(); enum.hasMoreElements() ;){

               toThread=(MessageServlet)enum.nextElement();

               if (toThread.getAlias().equals(guest)||toThread.getAlias().equals(host))
                    sendToClient(msg, toThread);
            }

       }

       else if(cmd==CSMessage.INVITATION) {

          String guest = msg.getStringProperty("guest");

           MessageServlet toThread=null;
           for(Enumeration enum = clients.elements(); enum.hasMoreElements() ;){

               toThread=(MessageServlet)enum.nextElement();

               if (toThread.getAlias().equals(guest)) {
                    sendToClient(msg, toThread);
                    break;
                }
            }

       }

       else if(cmd==CSMessage.ACCEPT_INVITATION) {

           String guest = msg.getStringProperty("guest");
           String host = msg.getStringProperty("host");

           MessageServlet toThread=null;
           for(Enumeration enum = clients.elements(); enum.hasMoreElements() ;){

               toThread=(MessageServlet)enum.nextElement();

               if (toThread.getAlias().equals(guest)||toThread.getAlias().equals(host))
                    toThread.setPassword(host+"||"+guest);//let's set the new password to the both players;


               if (toThread.getAlias().equals(host))
                    sendToClient(msg, toThread);
               

          }


         Message ms=new CSMessage(CSMessage.LIST);
         try{
             ms.setStringProperty("list",clientsListToString());
         }
         catch(Exception ex){
             System.err.println("Router::distribute:Construction of CSMessage:"+ex);
         }
         real_broadcast(ms);

       }

       else if(cmd==CSMessage.REJECT_INVITATION) {


         String host = msg.getStringProperty("host");
         

         MessageServlet toThread=null;
         for(Enumeration enum = clients.elements(); enum.hasMoreElements() ;){

               toThread=(MessageServlet)enum.nextElement();

               if (toThread.getAlias().equals(host)) {
                    sendToClient(msg, toThread);
                    break;
                }
          }
       }

        else if(cmd==CSMessage.SET_PASSWORD_BACK) { //set the default group password == null to both players

           String host = msg.getStringProperty("host");
           String guest = msg.getStringProperty("guest");

           MessageServlet toThread=null;
           for(Enumeration enum = clients.elements(); enum.hasMoreElements() ;){

               toThread=(MessageServlet)enum.nextElement();

               if (toThread.getAlias().equals(guest) || toThread.getAlias().equals(host))
                    toThread.setPassword("null");//let's set the group password to the both players;


               if (toThread.getAlias().equals(guest) )
                    sendToClient(msg, toThread);


          }


           Message ms=new CSMessage(CSMessage.LIST);
           try{
               ms.setStringProperty("list",clientsListToString());
           }
           catch(Exception ex){
               System.err.println("Router::distribute:Construction of CSMessage:"+ex);
           }
           real_broadcast(ms);

        }


       else if(cmd==CSMessage.STARTOVER) {

             group_broadcast(msg, sourceThread);
       }

      
       else if(cmd==CSMessage.LOGOUT) {
          

            Message ms=new CSMessage(CSMessage.LIST);
            try{
               ms.setStringProperty("list",clientsListToString());
            }
            catch(Exception ex){
               System.err.println("Router::distribute:Construction of CSMessage:"+ex);
            }
            real_broadcast(ms);


            real_broadcast(msg);
	      }
        
        else if(cmd==CSMessage.SAY) {
             
             group_broadcast(msg, sourceThread);
        }


        else if(cmd==CSMessage.SOUND) {
             group_broadcast(msg, sourceThread);
        }

        else if(cmd==CSMessage.DATA){

             group_broadcast(msg, sourceThread);
        }

  }//handleInput


  /**
   * Overrides super's method adding application specific (game) functionality:
   * to send additional logout message when client lieves the group/looses connection
   * (probably closing browser).
   */
  public void removeClient(MessageServlet tempThread){


     String temp;
     String name=tempThread.getAlias();

     //System.out.println("removeClientThread : "+name+" / "+password);

     if(clients.contains(tempThread)) {
        clients.removeElement(tempThread);

        if(!name.equals("null")) {
        

        Message ms=new CSMessage(CSMessage.LIST);
        try{
           ms.setStringProperty("list",clientsListToString());
        }
        catch(Exception ex){
           System.err.println("Router::distribute:Construction of CSMessage:"+ex);
        }
        real_broadcast(ms);


        ms=new CSMessage(CSMessage.LOGOUT);
        try{
           ms.setStringProperty("host",name);
        }
        catch(Exception ex){
           System.err.println("Router::distribute:Construction of CSMessage:"+ex);
        }
        real_broadcast(ms);

        }

        LogManager.getInstance().printEvent("Client thread: "+name+" has been removed");
        if(log.debugLevel(DebugLevel.DEBUG_CLIENTS))log.printDebug(getClientsList());
     }
  }

  /**
   * Method used for packing all client names into String for sending to the
   * the client (applet) - application (chat) specific method
   */
  private String clientsListToString(){

     Vector clientsCopy=(Vector)clients.clone();

     StringBuffer sb=new StringBuffer("");
     int n=clientsCopy.size();

     String aName, aPassword;
     for (int i=0; i<n; i++ ){
          MessageServlet aClient = (MessageServlet)clientsCopy.elementAt(i);

          aName = aClient.getAlias();
          aPassword = aClient.getPassword();

          if(!aName.equals(null)) {
            sb.append(aName+"&"+aPassword);
            if(i<n-1)sb.append("&");
          }
     }
     return sb.toString();
  }


}
