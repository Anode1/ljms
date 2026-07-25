/**
 * @(#)BaseServlet.java
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
package org.is.jms.broker;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.ServletException;
import javax.servlet.*;
import javax.servlet.http.*;

import javax.jms.Message;

import org.is.logmanager.*;
import org.is.io.FileUtils;
import org.is.jms.broker.Sessions;
import org.is.structures.Client;
import org.is.jms.MessageImpl;
import org.is.jms.LSessionImpl;

/**
 * Servlet base for http implementation of message broker.
 *
 * Not used currently. MessageSocket will be subclassed instead. 
 *
 * @version 1.1
 * @since jdk1.0
 */
public class BaseServlet extends HttpServlet{

  private static Sessions clients = new Sessions(64);
           
  public void init(ServletConfig sc)throws ServletException{

     super.init(sc);
     String appRoot=sc.getInitParameter("applicationRoot");
     if(appRoot==null)appRoot=System.getProperty("user.dir");
     //System.out.println("applicationRoot="+appRoot);
     LogManager log=LogManager.createInstance(appRoot, "server_logs");
     if(!FileUtils.dirIsWritable(appRoot)){
        System.err.println("Directory "+appRoot+" is not writable - LogManager disabled");
        log.setDisabled(true);
     }

     String debugLevelString=System.getProperty("debug_level"); //change to actual config file
     if(debugLevelString==null)debugLevelString="DEBUG_BRIEF";
     int dl=DebugLevel.fromString(debugLevelString.trim());
     log.setDebugLevel(dl);

     String restartString="--------------------------------------\r\nLog started at:"
             +new java.util.Date()+" (debug level="+debugLevelString+")";
     log.out(restartString);

  }

 /**
  * HTTP GET is used for inbound connection (InputStream from a user)
  */
  public void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException{

    LSessionImpl client=null;

    String idString=req.getHeader("id");
    if(idString==null){ //not logged in
       client=clients.add();
       LogManager.out("New client added:"+idString);
    }
    else client=clients.get(idString);
    if(client==null){
       LogManager.err("client is null but session been passed is not null - ignored");
       return;
    }

    try{
      res.setContentType("text/plain");

      DataOutputStream out = new DataOutputStream(new BufferedOutputStream(res.getOutputStream()));
      /*
      //We are blocked here if no messages:
      Message msg=(Message)client.getNextMessage();
      if(msg==null){
         LogManager.err("Null message not sent to the client - skipped");
         return;
      }

      ((MessageImpl)msg).toStream(out);
      //LogManager.out("Message sent to the client:"+msg);
      */
    }
    catch(Exception e){
    }
    finally{
      //set timer   remove client from the list
    }

    //LogManager.out("GET EXIT");

    //LogManager.out("Exiting from doGet()");
  }

 /**
  * HTTP POST is used for outbound connection (OutputStream to a user)
  */
  public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

    //LogManager.out("POST");
    try{

      String idString=req.getHeader("id");
      if(idString==null){ //not logged in
        LogManager.err("POST without id - ignored");
        return;
      }
      /*
      Client client=(Client)clients.getSession(idString);
      if(client==null){
        LogManager.err("POST: No such client in Client list:"+client.getPK());
        return;
      }

      DataInputStream in=new DataInputStream(new BufferedInputStream(req.getInputStream()));
      Message msg=MessageImpl.createMessage(in);

      broadcastMessage(msg);
      */

    }
    catch(Exception e){
    }
    finally{
      res.setStatus(res.SC_NO_CONTENT);
    }
    //LogManager.out("POST EXIT");
  }

  void distributeMessage(Message msg){
    /*
    Client client=(Client)clients.get("foo");
    if(client==null){
       //LogManager.err("No such client:"+pk);
       return;
    }
    client.addMessage(msg);
    */
  }

  void broadcastMessage(Message msg){
   /*
    String aKey=null;
    Client aValue=null;
    for (Enumeration enum = clients.keys(); enum.hasMoreElements() ;) {
      aKey=(String)enum.nextElement();
      Client client=(Client)clients.getSession(aKey);
      client.addMessage(msg);
    }
    */
  }

}


