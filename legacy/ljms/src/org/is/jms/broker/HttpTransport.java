/**
 * @(#)HttpTransport.java
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

import javax.jms.JMSException;
import javax.jms.Message;

import org.is.jms.MessageImpl;

/**
 * HTTP version of the message carrier made on top of servlet and URLConnection.
 * Not tested yet but should be much slower due to creation of many objects per request.
 * 
 *
 * @since   JDK1.0
 */
class HttpTransport implements org.is.jms.MessagePortal{

    private URL codebase;   //codebase for applets
    private String servlet;
    protected String myKey=null;

    public HttpTransport(String servlet){

      this.servlet = servlet;
    }

    /**
     * Constructor for using in applets
     */
    public HttpTransport(URL codebase, String servlet){

      this.codebase = codebase;
      this.servlet = servlet;
    }

    public void connect() throws JMSException{

      try{
          if(myKey!=null){
            System.err.println("Already connected");
          }

          URLConnection isCon=null;

          if(codebase==null) isCon = new URL(servlet).openConnection();
          else isCon = new URL(codebase, servlet).openConnection();

          isCon.setUseCaches(false);
          isCon.setDefaultUseCaches(false);
          isCon.connect();

          DataInputStream dis=new DataInputStream(isCon.getInputStream());
          myKey=dis.readUTF();
          dis.close();

      }
      catch(Exception e){
          throw new JMSException("MessagePortalHttpImpl: can't connect with the server"+e);
      }
    }

    public Message getMessage()throws JMSException{

      if(myKey==null){
         System.err.println("getMessage: Can't connect: myKey is null");
         return null;
      }

      URLConnection isCon = null;
      try{
          //GET
          if(codebase==null) isCon = new URL(servlet).openConnection();
          else isCon = new URL(codebase, servlet).openConnection();
          isCon.setUseCaches(false);
          isCon.setDefaultUseCaches(false);
          isCon.connect();

          DataInputStream in = new DataInputStream(new BufferedInputStream(isCon.getInputStream()));

          System.out.println("Something got from the server");

          //we are blocked here:
          MessageImpl msg = MessageImpl.createMessage(in);

          return msg;
      }
      catch(Exception e){
          throw new JMSException("MessagePortalHttpImpl::getMessage:"+e);
      }
    }

    public void sendMessage(Message msg)throws JMSException{

      if(myKey==null){
         System.err.println("sendMessage: Can't connect: myKey is null");
         return;
      }

      URLConnection outCon = null;
      try{
          //GET
          if(codebase==null) outCon = new URL(servlet).openConnection();
          else outCon = new URL(codebase, servlet).openConnection();

          outCon.setDoInput(true);
          outCon.setDoOutput(true);
          outCon.setUseCaches(false);
          outCon.setDefaultUseCaches(false);
          //outCon.setRequestProperty();
          // Work around a Netscape bug:
          outCon.setRequestProperty("Content-Type", "application/octet-stream");
          outCon.connect();

          DataOutputStream out = new DataOutputStream(new BufferedOutputStream(outCon.getOutputStream()));
          //if(!mi instanceof MessageImpl)throw new JMSException("SessionImpl:: can send only MessageImpl");
          MessageImpl mi=(MessageImpl)msg;
          mi.toStream(out);
          out.flush();
          out.close();
      }
      catch(Exception e){
         throw new JMSException("MessagePortalHttpImpl::sendMessage:"+e);
      }
    }

    public void close() throws JMSException{

    }

}
