/**
 * @(#)Impl2Worker.java
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

import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;

import org.is.logmanager.*;
import org.is.util.*;

/**
 * Main thread body and structures.
 * Currently is not used - Impl3Server has been used instead.
 *
 * @version 1.0
 * @since   JDK1.0
 */
class Impl2Worker implements Runnable, Cloneable{

  public static final int SERVER_PRIORITY = 9;   //do it faster than child Threads
  private static LogManager log=LogManager.getInstance();

  private int port;
  private ServerSocket serverSocket;
  protected ServletContext sc;
  private Thread thread;

  public Impl2Worker(int port){

     this.port=port;
  }

  public final void run(){

    Socket s=null;

    try{

      serverSocket = new ServerSocket(port);

      if(sc==null)sc=new ServletContext();

      if(log.debugLevel(DebugLevel.DEBUG_STARTING))log.printDebug("Begin listening: localhost:"+serverSocket.getLocalPort());

      while(true){
         try{
            s = serverSocket.accept();

            Impl2Server serv=(Impl2Server)clone();   //client thread
            serv.setServletContext(sc);
            serv.setSocket(s);
            serv.start();
         }
         catch(Exception e){
            log.printError("Impl2Server::run: error accepting socket:"+Utils.stack2String(e));
         }
      }
    }
    catch(Exception ex){
      log.printError("MainWorker::run: exiting from the main thread ("+serverSocket+"):"+Utils.stack2String(ex));
    }
    finally{
      onShutdown();
      System.out.println("Worker shutdown");
    }

  }//run

  public void start(){

    if(thread!=null && thread.isAlive()){
      log.printError("MainWorker::start: thread already started - ignored");
      return;
    }

    thread=new Thread(this);
    thread.setPriority(SERVER_PRIORITY);
    thread.setName("Main server thread (Worker)");
    thread.setDaemon(false);  //continue working after main thread termination
    thread.start();
  }

  public void stop(){

    if(thread==null){
      log.printError("MainWorker::stop: thread in null - can not be stopped");
      return;
    }
    onShutdown();
    thread.stop();    //we are not using interrupt to be compatible with java1.0 in applets
    thread=null;
  }

  /**
   * Called by Impl2Server on shutdown only. Makes cleanup of everything
   */
  private void onShutdown(){

    //close accepting socket:
    if(serverSocket!=null){
       try{serverSocket.close();}catch(IOException ie){}
       serverSocket=null;
    }

    if(sc==null){
       log.printError("MainWorker::onShutdown:container is null: cannot shutdown");
       return;
    }

    //close all client sockets and stop threads
    java.util.Vector threads=sc.getThreads();  //copy
    int size=threads.size();

    for(int i=0; i<size; i++){
       IServlet t=(IServlet)threads.elementAt(i);
       t.destroy();
    }

  }


}
