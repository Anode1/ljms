/** * @(#)LogManagerMessage.java
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
 */package org.is.logmanager;
/** * LogManager message sent by LogManager to LogManagerMessageThread (daemon) -
 * in the case of threaded version, to MessageWriter (if thread is not spawned)
 * or to the remote process
 *
 * @see LogManager
 * @see LogManagerBase
 * @see LogManagerWriterThread
 * @version 3.0 10/21/99
 * @since jdk1.0
 */
public class LogManagerMessage{

  /**
   * @serial
   * Channels:
   * 0 - event log (application events)
   * 1 - error log
   * 2 - debug log
   * MessageWriter knows how to map these numbers to file names
   */
  protected int channel;

  /**
   * @serial Message to be printed
   */
  protected String message;

  /**
   * Default constructor
   */
  public LogManagerMessage(int channel, String message){

    this.channel=channel;
    this.message=message;
  }

  /**
   * Returns the channel this message print to
   */
  public int getChanel(){

    return channel;
  }

  /**
   * Returns the message string
   */
  public String getMessage(){

    return message;
  }

}


