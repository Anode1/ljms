/**
 * @(#)MessageConsumerImpl.java
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
import javax.jms.Message;

/**
 * Implementation of javax.jms.MessageConsumer
 *
 * @version 1.1
 * @since jdk1.0
 */
public class MessageConsumerImpl implements javax.jms.MessageConsumer{

  private MessageListener messageListener;

  public MessageConsumerImpl(){
  }

  public void close() throws JMSException{
  }

  public Message receive() throws JMSException{
    return null;
  }

  public Message receive(long timeOut) throws JMSException{
    return null;
  }

  public Message receiveNoWait() throws JMSException{
    return null;
  }

  public String getMessageSelector() throws JMSException{

    return null;
  }

  public MessageListener getMessageListener() throws JMSException{

    return messageListener;
  }

  public void setMessageListener(MessageListener ml) throws JMSException{

    messageListener=ml;
  }
}




