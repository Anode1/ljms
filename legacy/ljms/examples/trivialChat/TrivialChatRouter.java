/**
 * @(#)TrivialChatRouter.java
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

import javax.jms.Message;
import javax.jms.JMSException;

import org.is.server.MessageServlet;

/**
 * Application specific Context for TrivialChat example.
 * @since   JDK1.0
 */
public class TrivialChatRouter extends org.is.server.MessageContext{

  public void distribute(Message msg, MessageServlet sourceThread) throws JMSException{

      //System.out.println(msg+" arrived");

      real_broadcast(msg);

  }


}
