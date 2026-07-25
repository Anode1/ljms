/**
 * @(#)Impl1Thread.java
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

import org.is.logmanager.*;
import org.is.util.Utils;

/**
 *
 * Client thread.
 * Currently is not used - Impl3Server has been used instead.
 *
 * @version 1.1
 * @since   JDK1.0
 */
public class Impl1Thread extends Thread implements IServlet{

  //public String name;                 //primary key for this thread
  protected Socket theSock;           //this thread's socket
  protected ServletContext sc; //data structure used as thread container
  protected int priority;    //priority for this thread
  protected static LogManager log=LogManager.getInstance();

  public Impl1Thread(){

    setDaemon(true);
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

  public void run(){

    try{
      init();
      sc.addThread(this);
      service();
      destroy();
    }
    catch(ServletException e){
      log.printError("Impl3Thread::run:"+Utils.stack2String(e));
    }
    catch(Throwable t){
      log.printError("Impl3Thread::run:"+Utils.stack2String(t));
    }
    finally{
      disconnect();
    }

  }

  public Socket getSocket(){

    return theSock;
  }

  /**
   * Called by ServletContext on shutdown only
   */
  public void disconnect(){

     destroy();

     sc.removeThread(this);

     if(theSock!=null){
        try{theSock.close();}catch(IOException ie){}
     }
     theSock=null;      //release resorces as soon as possible
  }

}
