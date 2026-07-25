/**
 * @(#)BroadcastingServer.java
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

import java.io.*;
import java.net.*;

import org.is.logmanager.*;
import org.is.net.*;

import javax.jms.Message;

import org.is.jms.*;
import org.is.jms.broker.MessageSocket;

import org.is.server.CSMessage;

/**
 * Main broadcasting server class.
 * It instantiates necessary connections with other servers if needed
 * (getting information from the passed into constructor class) and
 * begins lestening for requests from clients.
 * Has been broken int the last version and is not used for now
 *
 * Usage: new BroadcastingServer(cport, sport, otherServersInf).start();
 *
 * Not used now. It is recommended to form groups of users around servers
 * using servlets to avoid global group of client which is inefficient from
 * all points of view.
 * This class has been switched off after the decision:
 * 1. to use Messages for initial handshaking (has been defined in this class)
 * rather than raw DataStream read/write. That improvement will make things more
 * universal and simple;
 * 2. not to use global broadcasting (not to hold global collection of all clients),
 * but to partition clients into groups and redirect groups to some separate
 * servers. Each such server will be independant and self-sufficient and each
 * client can bradcast only to participants of that physical server.
 *
 *
 * @since   JDK1.0
 */
public class BroadcastingServer{

  protected OtherServersInf otherServersInf;
  protected LogManager log=LogManager.getInstance();

  public BroadcastingServer(OtherServersInf otherServersInf){

    this.otherServersInf=otherServersInf;
  }

  public void start(int cport, int sport){

    try{

       RelayRouter applicationContext=new RelayRouter();


       if(otherServersInf!=null){ //try to connect to other servers as a client:
          tryToConnectToOtherServersAsClient(applicationContext);   //paticipate into bazaar
          (new ServerConnection(applicationContext, otherServersInf)).startService(sport);
       }

       (new org.is.server.MessageServlet(applicationContext)).startService(cport);

    }catch (IOException e) {
       log.printError("BroadcastingServer:: could not start:"+e);
       System.exit(1);;
    }

  }

  /**
   * Tries to connect to all host/port pairs passed as parameters
   */
  private void tryToConnectToOtherServersAsClient(RelayRouter applicationContext) throws UnknownHostException{

        String[] hosts=otherServersInf.getOtherServerHosts();
        int[] ports=otherServersInf.getOtherServerPorts();
        boolean[] alreadyConnected=otherServersInf.isConnectionExists();

        if(hosts==null)return;

        int n=hosts.length;
        for(int i=0; i<n; i++){

             if(alreadyConnected[i])continue;   //already exists

             alreadyConnected[i]=true;         //block

             Socket socket=null;

             try{
                 socket=new Socket(hosts[i], ports[i]);

                 LSessionImpl connection=new LSessionImpl();
                 connection.setMessageTransport(new MessageSocket(socket));
                 connection.connect();

                 //send my host/port at the begining:

                 String myHostStr=InetAddress.getLocalHost().getHostAddress();
                 String myPortStr=Integer.toString(org.is.server.Server.sport);

                 connection.sendMessage(new CSMessage(CSMessage.LOGIN, myPortStr, myHostStr));

                 //now read remote HostPort
                 Message msg=connection.getMessage();

                 String remotePortString=msg.getStringProperty("value");
                 int remotePort=Integer.parseInt(remotePortString);

                 String remoteHost=msg.getStringProperty("value");
                 HostPort hostPortOfAnother=new HostPort(remoteHost, remotePort);

                 if(remotePort!=ports[i] || !remoteHost.equals(hosts[i]))
                      throw new IOException("Another server "+hosts[i]+":"+ports[i]+" sent wrong information about himself:"+hostPortOfAnother);

                 int serverNo=otherServersInf.getServer(hostPortOfAnother.host, hostPortOfAnother.port);

                 if(serverNo==-1)throw new IOException("Parameters are not correct: got from another server:"+hostPortOfAnother+" but have passed into the program:"+otherServersInf);


                 ServerConnection server=new ServerConnection(connection, hostPortOfAnother, applicationContext);
                 server.connectFromMe();
                 log.printDebug("--> Server thread added:"+hostPortOfAnother);
             }
             catch(IOException e){

                 alreadyConnected[i]=false;

                 //log.printDebug("-X-> Failed to connect to another server:"+hashcode);
                 try{socket.close();}catch(Exception ie){}
             }
             catch(Exception pe){

                 alreadyConnected[i]=false;

                 log.printError("BroadcastingServer::tryToConnectToOtherServers:"+pe);
                 try{socket.close();}catch(Exception ie){}
             }


        }//for

        if(log.debugLevel(DebugLevel.DEBUG_SERVER_CONNECTIONS_STATUS))log.printDebug("After tryToConnectToOtherServers:"+otherServersInf.toString());
  }




}
