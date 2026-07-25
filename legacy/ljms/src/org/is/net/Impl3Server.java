/**
 * @(#)Impl3Server.java
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
import org.is.util.*;

/**
 * This is equivalent version to Impl2Server with Worker incorporated into this class
 * Usage:
 * new Impl3Server().startService(80);
 *
 * @version 1.0
 * @since   JDK1.0
 */
public class Impl3Server implements IServer, IServlet, Runnable, Cloneable{

  private String name;
  private Socket theSock;           //client thread's socket
  private ServletContext sc; //data structure (ref) used as thread container

  private ServerSocket serverSocket;  //serverSocket used by us as a flag also (if null - client thread)

  //refs to corresponding threads:
  protected Thread childThread;
  private Thread mainThread;

  protected LogManager log=LogManager.getInstance();

  /**
   * This constructor is used when client thread is created (in this class)
   */
  protected Impl3Server(){
  }

  /**
   * Sets the socket for the thread - used internally (in MainWorker)
   */
  public final void setSocket(Socket theSock){

     this.theSock=theSock;
  }


  /**
   * Used only when this thread is used as client but created by hand rather than
   * automatically (self-connected servers - for example)
   */
  public final void start(){

    childThread.start();
  }

  private final void runMain(){

     if(LogManager.dLevel(DebugLevel.DEBUG_STARTING))LogManager.out("Begin listening: localhost:"+serverSocket.getLocalPort());
     try{
        if(sc==null){  //if external one has not been passed
          sc=new ServletContext();
        }

        while(true){

           Socket s = serverSocket.accept();
           Impl3Server clone = (Impl3Server)clone();
           clone.serverSocket = null;
           clone.sc=sc;
           clone.theSock=s;
           Thread t=new Thread(clone,"Client");
           t.setPriority(5);
           t.setDaemon(true);
           t.start(); 
        }//while
     }
     catch(Throwable e){
        LogManager.err("Impl3Server::runMain: exiting from the main loop:"+Utils.stack2String(e));
     }
     finally{
        onShutdown();
        if(LogManager.dLevel(DebugLevel.DEBUG_SERVER_CONNECTIONS_STATUS))LogManager.out("Impl3Server::Service: exiting");
     }
     return;
  }

  /**
   * Child thread
   */
  public final void run(){

    if(serverSocket != null){  //we use serverSocket as a flag here to distinguish between threads: server and clients
      runMain();       //this will be only after start
      return;
    }

    try{
      sc.addThread(this);
      init();    //1
      service(); //2
    }
    catch(ServletException e){
      log.printError("Impl3Thread::run:"+Utils.stack2String(e));
    }
    catch(Throwable t){
      log.printError("Impl3Thread::run:"+Utils.stack2String(t));
    }
    finally{
      disconnect();   //all this class finalization is there

      if(log.debugLevel(DebugLevel.DEBUG_THREADS))log.printDebug("Thread "+Thread.currentThread().getName()+" removed.");
      //if(log.debugLevel(DebugLevel.DEBUG_THREADS))log.printDebug("Thread info:"+ThreadUtils.threadInfo());
    }

  }

  /**
   * This method has to be implemented in subclasses.
   */
  public void init() throws ServletException{
  }

  /**
   * This method has to be implemented in subclasses.
   */
  public void service() throws ServletException{

    LogManager.out("Served by empty service (in Impl3Server)");
  }

  /**
   * This method has to be implemented in subclasses.
   */
  public void destroy(){
  }

  public String getName(){

    return name;
  }

  public final void setName(String name){

    this.name=name;
    if(childThread!=null)childThread.setName(new String(name));
  }

  public Object clone(){
    try{
      return super.clone();
    }
    catch(Exception e){
	    disconnect();
      throw new NullPointerException("Impl3Server::Can't clone:"+e);
    }
  }

  protected Socket getSocket(){

    return theSock;
  }

  public final ServletContext getServletContext(){

     return sc;
  }

  /**
   * Sets new servlet context
   */
  public final void setServletContext(ServletContext sc){

     if(sc!=null){
        log.printError("Impl3Server::setServletContext: ServletContext can be set only once!");
     }
     this.sc=sc;
  }

  /**
   * Forses to close client connection immediately - user classes can use this
   * method. For example - if Message server has been implemented and some error
   * occured in Message reading or there is a security violation (some check failed)
   * then user class (extension of org.is.net.Servlet) will call this method.
   */
  protected final void disconnect(){

    //callback to user class overriden destroy() method first
    try{
      destroy();
    }
    catch(Throwable e){
      log.printError("Impl3Server::disconnect: error in destroy(): "+Utils.stack2String(e));
    }

    //remove Thread from Thread context
    sc.removeThread(this);

    if(theSock!=null){
      try{theSock.close();}catch(Exception ie){}
    }

     //do not make theSock=null here. Users can have references to sock.
     //Anyway they supposed to get IOException
  }

  //////////// User thread methods ////////////

  /**
   * Starts the service. This method starts the main service thread (Worker)
   */
  public final void startService(int port) throws IOException{

    serverSocket = new ServerSocket(port);
    mainThread = new Thread(this, "Main Thread");
    mainThread.setPriority(9);   //serve faster than all other threads
    mainThread.setDaemon(false); //remain at the background
    mainThread.start();
  }

  /**
   * Stops the service. This method stops the main thread (Worker)
   */
  public final void stopService(){

    onShutdown();    //cleanup
    
    if(mainThread!=null){
      synchronized(mainThread){
        if(mainThread!=null){
          mainThread.interrupt();
          mainThread=null;
        }
      }
    }
  }

  /**
   * Called by Impl2Server on shutdown only. Makes cleanup of everything
   */
  private void onShutdown(){

    //close accepting socket:
    if(serverSocket!=null){
       try{serverSocket.close();}catch(Exception ie){}
       serverSocket=null;
    }

    if(sc==null){
       LogManager.err("Impl3Server::onShutdown:container is null: cannot shutdown");
       return;
    }

    //close all client sockets and stop threads
    java.util.Vector threads=sc.getThreads();  
    int size=threads.size();
    for(int i=0; i<size; i++){
       IServlet t=(IServlet)threads.elementAt(i);
       t.destroy();
    }

  }



}
