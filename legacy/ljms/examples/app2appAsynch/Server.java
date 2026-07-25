/**
 * @(#)Server.java
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

import org.is.net.Servlet;

/**
 *
 * Main Server entry point. Deals with parameters, initialization and start
 * main server class (org.is.BroadcastingServer).
 *
 * @since   JDK1.0
 */
public class Server{

  public static int port=6111;

  public static void main(String args[]){

    
    try{
      //create servlet passing application specific router into it:
      Servlet server=new org.is.server.MessageServlet(new MessageRouter());
      server.startService(port);
    }
    catch(Exception e){
       e.printStackTrace();;
    }

  }//main



}
