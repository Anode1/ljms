/**
 * @(#)MessageProducerImpl.java
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

/**
 * Implementation of javax.jms.MessageProducer
 *
 * @version 1.1
 * @since jdk1.0
 */
public class MessageProducerImpl implements javax.jms.MessageProducer{

  public MessageProducerImpl(){
  }

  public void close() throws JMSException{
  }

  public int getDeliveryMode() throws JMSException{

    return 0;
  }

  public boolean getDisableMessageID() throws JMSException{

    return false;
  }

  public boolean getDisableMessageTimestamp() throws JMSException{
  
    return false;
  }

  public int getPriority() throws JMSException{

    return 5;
  }

  public long getTimeToLive() throws JMSException{

    return 0;
  }

  public void setDeliveryMode(int deliveryMode) throws JMSException{

  }

  public void setDisableMessageID(boolean value) throws JMSException{

  }

  public void setDisableMessageTimestamp(boolean value) throws JMSException{

  }

  public void setPriority(int deliveryMode) throws JMSException{

  }

  public void setTimeToLive(long timeToLive) throws JMSException{

  }

}




