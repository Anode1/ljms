/**
 * @(#)ServerConnection.java
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
import java.net.InetAddress;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.net.Socket;

import javax.jms.Message;

import org.is.logmanager.*;
import org.is.util.Utils;
import org.is.net.HostPort;
import org.is.net.ServletException;
import org.is.jms.*;
import org.is.server.CSMessage;

/**
 * Thread for connection with another server.
 * Other servers are just a little bit different, special clients. RelayRouter
 * adds collection of servers to Context
 *
 * Not used in the last version.
 *
 * @since   JDK1.0
 */
public class ServerConnection extends org.is.server.MessageServlet{

  private HostPort hostPort;
  private boolean asClient;
  private OtherServersInf otherServersInf;

  public ServerConnection(RelayRouter router, OtherServersInf otherServersInf){

    super(router);
    this.otherServersInf=otherServersInf;
    this.setName("Server (<-)");
  }

  /**
   * This constructor is only used when we create ServerConnection by hand
   * when trying to connect with other servers in BroadcastingServer 
   */
  public ServerConnection(LSessionImpl connection,
                          HostPort hostPort,
                          RelayRouter router){

    super(router);
    this.connection=connection;
    this.hostPort=hostPort;
    asClient=true;
  }

  public void connectFromMe()throws IOException{

    if(!asClient)throw new IllegalArgumentException("ServerConnection::connect supposed to be used only when we connect from it to another server");
    ((RelayRouter)context).addAnotherServer(this);
    childThread=new Thread(this);
    childThread.setDaemon(true);
    this.setName("Server (->)");
  }

  public void service()throws ServletException{

    try{

      if(!asClient){

        Message hostPortMessage=connection.getMessage();

        String hostName=hostPortMessage.getStringProperty("name");
        String remotePortString=hostPortMessage.getStringProperty("value");

        int port=Integer.parseInt(remotePortString);

        hostPort=new HostPort(hostName, port);

        int serverNo=otherServersInf.getServer(hostPort.host, hostPort.port);
        if(serverNo==-1)throw new IllegalArgumentException("Parameters are not correct: got from another server:"+hostPort+" but have passed:"+otherServersInf);

        //send my host/port
        String myHostName=InetAddress.getLocalHost().getHostAddress();
        String myPortStr=Integer.toString(org.is.server.Server.sport);
        connection.sendMessage(new CSMessage(CSMessage.LOGIN, myPortStr, myHostName));

        synchronized(otherServersInf){
          otherServersInf.isConnectionExists()[serverNo]=true;
	        ((RelayRouter)context).addAnotherServer(this);
        }

        log.printDebug("<-- Server thread added:"+hostPort);
        if(log.debugLevel(DebugLevel.DEBUG_SERVER_CONNECTIONS_STATUS))log.printDebug("ServerConnection::After accepting another server:"+otherServersInf);
      }//if

      while(true){

         Message msg=getMessage();

         ((RelayRouter)context).handleMessage(msg, this);

         if(log.debugLevel(DebugLevel.DEBUG_ANOTHER_SERVERS_MESSAGES))log.printDebug(Thread.currentThread().getName()+"Message arrived:"+msg+" from another server");
         try{ Thread.sleep(20);} catch (InterruptedException e) {} //to be a good citizen
      }

    }
    catch(Throwable e){
      throw new ServletException(e);
    }
    finally{
      ((RelayRouter)context).removeServer(this);
    }
  }

  public HostPort getHostPort(){
  
    return hostPort;
  }

}
