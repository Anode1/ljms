/**
 * @(#)Impl2Server.java
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
 * This is absolutely equivalent version to Impl1Server with one significant
 * difference: this class has dual interface for easier use from user's
 * perspective. Because of this the code is not so obvious since this class
 * contains both main thread and client threads logic, but user invokes
 * it just in one line:
 * new Impl2Server().startService(80);
 * Currently is not used - Impl3Server has been used instead.
 *
 * @version 1.0
 * @since   JDK1.0
 */
class Impl2Server implements IServer, IServlet, Runnable{

  //client datastructures:
  protected String name;
  protected Socket theSock;           //this thread's socket
  private ServletContext sc; //data structure used as thread container
  private Thread refToThread;

  //common datastructures:
  protected static LogManager log=LogManager.getInstance();

  //ref to the main thread as the bootstrap
  private transient Impl2Worker worker; //it is null if we start child thread


  public Impl2Server(){
  }

  /**
   * Sets the socket for the thread - used internally (in MainWorker)
   */
  public void setSocket(Socket theSock){

     this.theSock=theSock;
  }

  /**
   * Sets container for the thread - used internally (in MainWorker)
   */
  void setServletContext(ServletContext sc){

     this.sc=sc;
  }

  void setThread(Thread refToThread){

     this.refToThread=refToThread;
  }

  /**
   * Child thread
   */
  public final void run(){

    try{
      init();
      sc.addThread(this);
      service();
    }
    catch(ServletException e){
      log.printError("Impl2Thread::run:"+Utils.stack2String(e));
    }
    catch(Throwable t){
      log.printError("Impl2Thread::run:"+Utils.stack2String(t));
    }
    finally{
      disconnect();   //all finalization is here
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
    log.printDebug("Empty service called");
  }

  /**
   * This method has to be implemented in subclasses.
   */
  public void destroy(){
  }

  public void start(){

    if(refToThread!=null && refToThread.isAlive()){
      log.printError("Impl2Server::start: thread already started - ignored");
      return;
    }

    refToThread=new Thread(this);
    refToThread.setDaemon(true);
    refToThread.setName("Client thread");
    refToThread.setPriority(5);
    refToThread.setDaemon(true);  //continue working after main thread termination
    refToThread.start();

    log.out("Client thread started");
  }

  public void stop(){

    if(refToThread==null){
      log.printError("Impl2Server::stop: thread in null - can not be stopped");
      return;
    }
    refToThread.stop();    //we are not using interrupt to be compatible with java1.0 in applets
    refToThread=null;
  }

  public String getName(){

    return name;
  }

  public void setName(String name){

    this.name=name;
    if(refToThread!=null)refToThread.setName(new String(name));
  }

  public Object clone(){
    try{
      return super.clone();
    }
    catch(Exception e){
      throw new NullPointerException("Impl2Worker::Can't clone:"+e);
    }
  }

  /**
   * Gets a clone of all threads. This supposed to be used immediately and if
   * it is not up to date, user classes will get IOException working with
   * disconnected already socket
   */
  protected java.util.Vector getThreads(){

    return (java.util.Vector)sc.getThreads();    //clone() is synchronized in Vector
  }

  protected Socket getSocket(){

    return theSock;
  }

  /**
   * Called by ServletContext on shutdown only
   */
  public void disconnect(){

     destroy();

     sc.removeThread(this);

     if(theSock!=null){
        try{theSock.close();}catch(Exception ie){}
     }

     if(refToThread!=null){
        synchronized(refToThread){
            if(refToThread!=null){
              refToThread.stop();
              refToThread=null; //release immediately
            }
        }
     }

     //do not make theSock=null here. Users can have references to sock.
     //Anyway they supposed to get IOException
  }

  //////////// User thread methods ////////////

  /**
   * Starts the service. This method starts the main service thread (Worker)
   */
  public final void startService(int port) throws IOException{

    if(worker!=null){
      log.printError("Warning:: Service is already started - attempt to start it twice -- ignored");
      return;
    }
    worker=new Impl2Worker(port);
    worker.start();
  }

  /**
   * Stops the service. This method stops the main thread (Worker)
   */
  public final void stopService(){

    if(worker!=null){
      try{worker.stop();}catch(Exception e){}
    }
    worker=null;

  }




}
