/**
 * @(#)MessageUDPSender.java
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
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.net.InetAddress;

import javax.jms.JMSException;
import javax.jms.Message;

import org.is.jms.MessageImpl;

/**
 * Message portal implementation (broker) based on UDP transport.
 * Not finished.
 *
 * Not used now - connection oriented MessageSocket class based on Socket
 * is used instead for reliability. This class probably will be removed soon.
 *
 * @since   JDK1.0
 */
public class MessageUDPSocket implements org.is.jms.MessagePortal, Runnable{

    private String host;
    private int port;
    private Thread receiver;

    public MessageUDPSocket(String host, int port){

      this.host=host;
      this.port=port;
    }

    /**
     * Dummy method to implement MessagePortal. Probably it's better to
     * remove connect() from MessagePortal at all making lazy initialization
     */
    public void connect() throws JMSException{
    
       if(receiver==null){
          receiver=new Thread(this);
          receiver.start();
       }
    }

    public Message getMessage()throws JMSException{

      throw new JMSException("MessageUDPSocket:: can't get Messages from MessageUDPSocket - only sending is allowed: use UDPServer");
    }

    public void sendMessage(Message msg)throws JMSException{

      //if(!mi instanceof MessageImpl)throw new JMSException("SessionImpl:: can send only MessageImpl");
      ByteArrayOutputStream baos=new ByteArrayOutputStream();
      DataOutputStream out = new DataOutputStream(baos);

      try{
        MessageImpl mi=(MessageImpl)msg;      
        mi.toStream(out);
        out.flush();
        byte[] bytes=baos.toByteArray();
        if(bytes.length > 30000){  //correct this MAX size
           throw new JMSException("MessageUDPSocket::Failed sending message: msg = "+msg+": too big to be fit into UDP packet");
        }

        DatagramPacket p=new DatagramPacket(bytes, bytes.length, InetAddress.getByName(host), port);
        DatagramSocket ds=new DatagramSocket();
        ds.send(p);
        ds.close();
      }
      catch(JMSException e){
        throw e;
      }
      catch(Exception e){
        throw new JMSException("MessageUDPSocket::Failed sending message: msg = "+msg+" ;exception = "+e);
      }

    }

    /**
     * Stops receiver of UDP packets
     */
    public void stop(){

       if(receiver!=null){
          receiver.stop();
          receiver=null;
       }
    }

    public void run(){

    }

    /**
     * Dummy method to implement MessagePortal.
     */
    public void close() throws JMSException{

       stop();
    }


}
