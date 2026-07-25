/**
 * @(#)ConnectionImpl.java
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

import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.ConnectionMetaData;
import javax.jms.Message;

/**
 * Implementation of javax.jms.Connection
 *
 * @version 1.1
 * @since jdk1.0
 */
public abstract class ConnectionImpl implements javax.jms.Connection{

  public String clientID;
  private ExceptionListener errorHandler;

  public ConnectionImpl(){
  }

  public void setClientID(String clientID){

    this.clientID=clientID;
  }

  public String getClientID(){

    return clientID;
  }

  public ConnectionMetaData getMetaData() throws JMSException{

    return null;
  }  

  public ExceptionListener getExceptionListener() throws JMSException{

    return errorHandler;
  }

  public void setExceptionListener(ExceptionListener errorHandler) throws JMSException{

    this.errorHandler=errorHandler;
  }

  public void start() throws JMSException{

  }

  public void stop() throws JMSException{

  }

  public void close() throws JMSException{
  
  }

}




