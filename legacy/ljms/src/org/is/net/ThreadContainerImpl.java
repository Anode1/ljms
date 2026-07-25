/**
 * @(#)ThreadContainerImpl.java
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
package org.is.net;

import java.util.Vector;

import org.is.logmanager.LogManager;

/**
 * Simple implementation of the Context interface.
 * In the future it may be rewritten using more efficient implementations of
 * Hashtable
 *
 * @since   JDK1.0
 */
class ThreadContainerImpl implements ThreadContainer{

   private Vector threads=new Vector(64);
   protected LogManager log=LogManager.getInstance();

   /**
    * Method adding thread to the container. Used internally.
    */
   public final void addThread(IServlet t){

      threads.addElement(t);
   }

   /**
    * Method removing thread from the container. Used internally.
    */
   public final void removeThread(IServlet t){
   
      threads.removeElement(t);
   }

   /**
    * Thread safe method to get Vector of client threads (copy of it).
    * Client thread is responsible for checking of the validity of socket etc
    * since at the time of using of the copy it may be not in synch with the main
    * Vector of client threads. We make copy for efficiency not to have this part
    * synchronized and to run input/output operations in separate threads 
    */
   public final Vector getThreads(){

      return (Vector)threads.clone();    //clone is synchronized in Vector
   }

}
