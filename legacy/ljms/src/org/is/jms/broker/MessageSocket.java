/**
 * @(#)MessageSocket.java
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

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;

import javax.jms.JMSException;
import javax.jms.Message;

import org.is.jms.MessageImpl;

/**
 * Message transport (MessagePortal) based on simple Socket transport
 *
 * @since   JDK1.0
 */
public class MessageSocket implements org.is.jms.MessagePortal{

    private Socket theSock;
    private DataInputStream in;
    private DataOutputStream out;

    private String host; //if ready socket is not passed
    private int port; //if ready socket is not passed

    public MessageSocket(String host, int port){

      this.host=host;
      this.port=port;
    }

    /**
     * Constructor used when we have already created socket like in multithreaded
     * server when Socket has been got from ServerSocket
     */
    public MessageSocket(Socket theSock){

      this.theSock = theSock;
    }

    public void connect() throws JMSException{

      try{
         synchronized(this){
          if(theSock==null){ //i.e. host/port were passed
            theSock = new Socket(host, port);
          }
         }
         in = new DataInputStream(new BufferedInputStream(theSock.getInputStream()));
         out = new DataOutputStream(new BufferedOutputStream(theSock.getOutputStream()));
         //start();
      }
      catch(Exception e){
         throw new JMSException("MessageSocket: can't connect to the server:"+e);
      }
    }

    public Message getMessage()throws JMSException{

      if(in==null)throw new JMSException("MessageSocket:: Not connected");

      try{
        MessageImpl mi=MessageImpl.createMessage(in);
        return mi;
      }
      catch(JMSException e){
        close();
        throw e;
      }

    }

    public void sendMessage(Message msg)throws JMSException{

      if(out==null)throw new JMSException("MessageSocket: Not connected");

      //if(!mi instanceof MessageImpl)throw new JMSException("SessionImpl:: can send only MessageImpl");
      MessageImpl mi=(MessageImpl)msg;

      try{
        synchronized(out){
          mi.toStream(out);
        }
        out.flush();
      }
      catch(IOException e){
        close();
        throw new JMSException("MessageSocket::Failed sending message: msg = "+msg+" ;exception = "+e);
      }
    }

    public void close() throws JMSException{

      try{
        if(in!=null){
          in.close();  //flush buffers if needed before close
        }
      }
      catch(Exception e){}

      try{
        if(out!=null){
          out.close();  //flush buffers if needed before close
        }
      }
      catch(Exception e){}

      try{
        if(theSock!=null){
          theSock.close();
        }
      }
      catch(Exception e){}
    }


}
