/** * @(#)Worker.java
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
 */package org.is.logmanager;
import java.io.IOException;import java.util.Properties;
import java.util.Enumeration;
import java.util.Vector;

/**
 * LogManager daemon, printing messages taken from internal message queue
 *
 * @see LogManager
 * @see LogManagerBase
 * @version 1.1 09/06/99
 * @since jdk1.0
 */
public class Worker implements Runnable{

  /**
   * Worker thread
   */
  private /*volatile*/ Thread worker;

  /**
   * Priority of this worker thread.
   * Range: 1..10. Used if not ovewritten from system.properties.
   */
  protected int priority=5;

  /**
   * message queue implementation
   */
  protected Vector messages=new Vector();

  /**
   * Listeners (example - GUI)
   */
  private Vector listeners;

 /**
  * Sends a message to the queue
  */
  public synchronized void send(LogManagerMessage m){

     if(m==null)return;
     messages.addElement(m);
     notify();
  }

 /**
  * Pulls the first message from the queue
  */
  private synchronized LogManagerMessage pull(){

      while(messages.size()==0){
          try{
            wait();                   //wait while no messages
          }
          catch(InterruptedException e){
            //LogManager.getInstance().printError("LogManagerMessageQueue::pull:Interrupted");
          }
      }//while

      if(messages.size()<1){
        System.err.println("LogManagerWriterThread::shouldn't occur: messages size is less than 0");
        return null;
      }
      LogManagerMessage message=(LogManagerMessage)messages.elementAt(0);
      messages.removeElementAt(0);

      return message;
  }

  public void start(){

     if(worker != null){
        stop();
     }
     worker=new Thread(this);
     worker.setPriority(priority);
     worker.setDaemon(true);    //VM will not wait for this thread completion
     worker.setName("Log Manager helper thread");
     worker.start();
  }

  public synchronized void stop(){

     doCleanUp();

     if(worker!=null){
        worker.stop();
     }
     worker = null;
  }

  /**
   * Implement this method
   */
  private void doCleanUp(){

     int i=0;
     while(messages.size()>1 && i<10){  //wait until all messages will be printed out or 10 secs
        try{Thread.sleep(1000);}catch(InterruptedException ie){}
     }
  }

  public void addLogListener(LogListener listener){

     synchronized(this){
      if(listeners==null)listeners=new Vector(3);  //never more than 3
     }
     listeners.addElement(listener);
  }

  public synchronized void removeLogListener(LogListener listener){

     if(listeners!=null){
         Enumeration e = listeners.elements();
	       for (; e.hasMoreElements(); ) {
		         LogListener alistener = (LogListener) e.nextElement();
             if(alistener==listener){
		            listeners.removeElement(listener);
             }
	       }
     }
  }

  public synchronized void removeAllListeners(){

     if(listeners!=null){
        listeners=null;
     }
  }

/**
 * Thread waiting for a task event in the queue, and if it comes, calling handler
 */
  public void run(){

     LogManagerMessage message;

     Thread thisThread=Thread.currentThread();
     while(worker==thisThread && worker!=null){

         message=pull();

         //notify listeners if exist
         if(listeners!=null){
            Enumeration e = listeners.elements();
	          for (; e.hasMoreElements(); ) {
		            LogListener listener = (LogListener) e.nextElement();
		            listener.onMessage(message);
	          }
         }
     }
  }//run


}

