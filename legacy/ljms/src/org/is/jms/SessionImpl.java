/**
 * @(#)SessionImpl.java
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
package org.is.jms;

import javax.jms.JMSException;
import javax.jms.MessageListener;
import javax.jms.TextMessage;
import javax.jms.MapMessage;
import javax.jms.StreamMessage;
import javax.jms.Message;
import javax.jms.BytesMessage;
import javax.jms.ObjectMessage;

/**
 * Implementation of javax.jms.Session
 *
 * @since jdk1.0
 */
public abstract class SessionImpl implements javax.jms.Session, Runnable{

  public String sessionID;
  private MessageListener listener;
  //private ExceptionListener errorHandler;
  private Thread worker;
  private volatile boolean stopped; //checked by run(); replaces Thread.stop()

  public SessionImpl(){
  }

  public void setSessionID(String sessionID){

    this.sessionID=sessionID;
  }

  public String getSessionID(){

    return sessionID;
  }

  public BytesMessage createBytesMessage() throws JMSException{

    BytesMessage msg=new BytesMessageImpl();
    return msg;
  }

  public MapMessage createMapMessage() throws JMSException{
  
    MapMessage msg=new MapMessageImpl();
    return msg;
  }

  public Message createMessage() throws JMSException{

    Message msg=new MessageImpl();
    return msg;
  }

  public ObjectMessage createObjectMessage() throws JMSException{

    ObjectMessage msg=new ObjectMessageImpl();
    return msg;
  }

  public ObjectMessage createObjectMessage(java.io.Serializable object) throws JMSException{

    ObjectMessage msg=new ObjectMessageImpl();
    msg.setObject((java.io.Serializable)object);
    return msg;
  }

  public StreamMessage createStreamMessage() throws JMSException{

    StreamMessage msg=new StreamMessageImpl();
    return msg;
  }

  public TextMessage createTextMessage() throws JMSException{

    TextMessage msg=new TextMessageImpl();
    return msg;
  }

  public TextMessage createTextMessage(String text) throws JMSException{

    TextMessage msg=new TextMessageImpl(text);
    return msg;
  }

  public boolean getTransacted() throws JMSException{
  
    return false;  //transacted mode not supported
  }

  public void commit() throws JMSException{

    throw new IllegalArgumentException("SessionImpl::commit() is not supported");
  }

  public void rollback() throws JMSException{

    throw new IllegalArgumentException("SessionImpl::rollback() is not supported");
  }

  public void recover() throws JMSException{

    throw new IllegalArgumentException("SessionImpl::recover() is not supported");
  }

  public MessageListener getMessageListener() throws JMSException{

     return listener;
  }

  public void setMessageListener(MessageListener listener) throws JMSException{
  
     this.listener=listener;
  }

  public synchronized void start(){

    if(worker==null){
       stopped=false;
       worker=new Thread(this);
       worker.setDaemon(true);       
       worker.start();
    }
  }

  public synchronized void close() throws JMSException{

    if(worker!=null){
       //Thread.stop() was unsafe when this was written and throws
       //UnsupportedOperationException on modern JVMs.
       stopped=true;
       worker.interrupt();
    }
    worker=null;
  }



}




