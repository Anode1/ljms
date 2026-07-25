/**
 * @(#)MessagePortal.java
 * Copyright (C) 2001 SrcPortal
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
import javax.jms.Message;

/**
 * Message distributor interface abstracting user classes from transport.
 * This class has no interface in JMS - it is a substitute for several JMS
 * classes to simplify usage in applet context when there is only one Queue.
 * It plays role of: Queue, Connection, MessageConsumer, MessageProducer etc.
 * Will be removed when full JMS support will be implemented.
 *
 * @version 1.0
 * @since   JDK1.0
 */
public interface MessagePortal{

    public abstract void connect() throws JMSException;

    public abstract void sendMessage(Message msg) throws JMSException;

    public abstract Message getMessage() throws JMSException;

    public abstract void close() throws JMSException;

}
