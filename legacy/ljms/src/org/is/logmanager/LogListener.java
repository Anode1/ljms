/**
 * @(#)LogListener.java
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
package org.is.logmanager;

/**
 * Listener for log messages. All kinds of messages (all chanels: debug levels,
 * evenets, errors etc) have been multiplexed in LogMessage. So, listener gets
 * all messages using only what it interests on). For example, MessageWriter
 * prints messages into different files but TextArea in gui (logs tab) prints
 * only messages
 *
 * @version 1.1 10/14/99
 * @since jdk1.0
 */
public interface LogListener{

   public void onMessage(LogManagerMessage msg);
}
