/**
 * @(#)Impl1Server.java
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
package org.is.net;

import java.net.Socket;
import java.net.ServerSocket;
import java.io.InputStream;
import java.io.IOException;

import org.is.logmanager.*;
import org.is.util.Utils;

/**
 * Generic server spawning thread for each socket request and that thread will
 * deal with input/output handling from that socket.
 * <p>
 * Usage is:
 * Impl1Server server=new Impl1Server();
 * server.setClass("org.MyServerClassName");
 * server.startService(27);
 * were org.MyServerClassName is class implementing IServlet interface
 * (subclass of Servlet).
 * Currently is not used - Impl3Server has been used instead.
 *
 * @version 1.0
 * @since   JDK1.0
 */
class Impl1Server implements IServer, Runnable{

  private int port;
  private Thread worker;
  private ServerSocket serverSocket;
  private int priority=9;   //do it faster than child Threads
  private String clazz;    //particular class to instantiate (servlet)
  private ServletContext context;

  protected static LogManager log=LogManager.getInstance();

  public Impl1Server(){
  }

  public void setClass(){

     this.clazz=clazz;
  }

  public final void startService(int port) throws IOException{

     if(serverSocket!=null){
        log.printError("Impl1Server::start: attempt to start server twice - ignored");
        return;
     }

     serverSocket = new ServerSocket(port);
     worker = new Thread(this, "Main ServerSocket thread");
     worker.setPriority(priority);
     worker.start();
  }

  public final void stopService(){

     if(serverSocket!=null){
        try{serverSocket.close();}catch(IOException e){}
     }

     serverSocket=null;
     //was worker.stop(): closing the socket above already unblocks accept(),
     //and stop() throws UnsupportedOperationException on modern JVMs
     if(worker!=null)worker.interrupt();
  }

  public final void run(){

    context=new ServletContext();

    try{

      if(log.debugLevel(DebugLevel.DEBUG_STARTING))log.printDebug("Begin listening: localhost:"+serverSocket.getLocalPort());

      Socket s=null;
      while(true){
         try{
            s = serverSocket.accept();

            Impl1Thread t=null;

            if(clazz==null){  //to be safe - if not passed from the user
              t=new Impl1Thread();
            }
            else{
              try{
                t=(Impl1Thread)Class.forName(clazz).newInstance();
              }
              catch(Exception e){
                log.printError("Impl1Server::run: error instantiating servlet class:"+clazz+":"+e);
                throw e;
              }
            }

            t.setServletContext(context);
            t.setSocket(s);
            t.start();
         }
         catch(IOException e){
            log.printError("Impl1Server::run: error accepting socket:"+Utils.stack2String(e));
         }
      }
    }
    catch(Exception ex){
      log.printError("Impl1Server::run: exiting from the main loop ("+serverSocket+"):"+Utils.stack2String(ex));
    }
    finally{

      onShutdown();

      if(serverSocket!=null){
        try{serverSocket.close();}catch(IOException ie){}
      }
    }

  }//run

  /**
   * Called by Impl2Server on shutdown only
   */
  public void onShutdown(){

   // if(context!=null)context.onShutdown();

    if(serverSocket!=null){
       try{serverSocket.close();}catch(IOException ie){}
       serverSocket=null;
    }
  }



}
