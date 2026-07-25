/**
 * @(#)QueueSessionImpl.java
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
import javax.jms.QueueBrowser;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.Queue;
import javax.jms.TemporaryQueue;

/**
 * Implementation of javax.jms.QueueSession
 *
 * @since jdk1.0
 */
public abstract class QueueSessionImpl extends SessionImpl implements javax.jms.QueueSession{

  public QueueSessionImpl(){
  }

  public QueueBrowser createBrowser(Queue queue) throws JMSException{

    return null;
  }

  public QueueBrowser createBrowser(Queue queue, String messageSelector) throws JMSException{

    return null;
  }

  public Queue createQueue(String queueName) throws JMSException{

    return null;
  }

  public QueueReceiver createReceiver(Queue queue) throws JMSException{

    return null;
  }

  public QueueReceiver createReceiver(Queue queue, String messageSelector) throws JMSException{

    return null;
  }

  public QueueSender createSender(String queueName) throws JMSException{

    return null;
  }

  public TemporaryQueue createTemporaryQueue() throws JMSException{

    return null;
  }

}




