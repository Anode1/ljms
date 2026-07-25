/**
 * @(#)HttpRequestor.java
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
package org.is.util;

import java.io.InputStream;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

/**
 * Thread making communication with the server, getting results and returning them
 * to the requestor.
 * It the responsibility of the user class to cast the result.
 * <p> This class is used also without creating of the thread by invoking of the
 * method getBytes()
 *
 * @version 1.0
 * @since   JDK1.0
 */
public class HttpRequestor implements Runnable{

 protected HttpRequestListener listener;

 protected CGIParameters params;

 protected URL url;

 /**
  * flag indicating degug mode
  */
 protected boolean debug;

 /**
  * Worker thread
  */
 protected Thread thread;

/**
 * Priority of the worker thread
 */
 protected int priority=5;

 public HttpRequestor(URL url){

        this.url=url;
 }

 public HttpRequestor(URL url, CGIParameters params){

        this.params=params;
        this.url=url;
 }

 public HttpRequestor(HttpRequestListener listener, URL url, CGIParameters params){

        this.listener=listener;
        this.params=params;
        this.url=url;
 }

 /**
  * Starts worker thread.
  */
  public synchronized void start(){

    if (thread == null){
			  thread = new Thread(this);
			  thread.setPriority(priority);
        thread.setDaemon(true);
			  thread.start();
	  }
  }

 /**
  * Stops worker thread.
  */
  public synchronized void stop(){

    if(thread!=null){
      //thread.interrupt();
      thread.stop();    //we have to support old browsers jdk1.0
      thread = null;
    }
  }

 /**
  * The body of this thread
  */
 public void run(){
 
    try{
      listener.dataReady(getBytes()); //callback
    }
    catch(Exception e){
      System.err.println("HttpRequestor::run:error in http request");
    }
 }

 /**
  *
  */
 public byte[] getBytes()throws Exception{

    ByteArrayOutputStream bos=null;
    BufferedInputStream bis=null;

    try{

       if(url == null){
           throw new NullPointerException("There is no url passed to HttpRequestor!");
       }
       HttpRequest httpRequest=new HttpRequest(url);

       bos=new ByteArrayOutputStream(16384);
       bis=new BufferedInputStream(httpRequest.makeGetRequest(params),16384);

       int read = 0;
       byte b[] = new byte[16384];
       while ((read = bis.read(b, 0, b.length)) != -1){
         bos.write(b, 0, read);
       }

       bos.flush();

    }
    finally{
       if(bis!=null)try{bis.close();}catch(Exception e){}
       if(bos!=null)try{bos.close();}catch(Exception e){}
       return bos.toByteArray();
    }
 }

 /**
  * Sets debugging mode
  */
 public void setDebug(boolean debug){

    this.debug=debug;
 }

 public void finalize() {

    if(thread!=null)stop();
 }

}