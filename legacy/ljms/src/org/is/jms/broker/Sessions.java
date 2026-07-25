/**
 * @(#)Sessions.java
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
package org.is.jms.broker;

import java.util.Hashtable;
import java.util.Enumeration;

import org.is.jms.LSessionImpl;

/**
 * Container (collection wrapper) for Session objects (LSessionImpl)
 *
 * @see Session
 * @version 1.0
 * @since   JDK1.0
 */
public class Sessions{

    private Hashtable hashtable;

    /**
     * Default constructor
     */
    public Sessions(){

       hashtable=new Hashtable();
    }

    /**
     * Constructor. @see Hashtable
     */
    public Sessions(int capacity){

       hashtable=new Hashtable(capacity);
    }

    /**
     * Constructor. @see Hashtable
     */
    public Sessions(int capacity, float factor){

       hashtable=new Hashtable(capacity, factor);
    }

    /**
     * Retreive a session by key
     */
    public LSessionImpl get(String pk){

       return (LSessionImpl)hashtable.get(pk);
    }

    /**
     * Adds a ready Session if it is exists already regenerating the key.
     */
    public LSessionImpl add(LSessionImpl s){

      s.setPK(generateUID());

      return (LSessionImpl)hashtable.put(s, s);
    }

    /**
     * Adds a session. Returns newly created session
     */
    public LSessionImpl add(){

      LSessionImpl s=new LSessionImpl(generateUID());

      return (LSessionImpl)hashtable.put(s, s);
    }

    public LSessionImpl remove(LSessionImpl s){

      return (LSessionImpl)hashtable.remove(s);
    }

    public int size(){

      return hashtable.size();
    }

   public final Enumeration keys(){

      return (Enumeration)hashtable.keys();
   }

   public final Enumeration elements(){

      return (Enumeration)hashtable.elements();
   }

  /**
   * For debugging purposes only
   */
  public String toString(){

     StringBuffer sb=new StringBuffer("All Clients: {");
     LSessionImpl c=null;
     for(Enumeration enum = hashtable.keys(); enum.hasMoreElements() ;) {
	      c = (LSessionImpl)enum.nextElement();
        //sb.append("[");
        sb.append(c.toString());
        //sb.append("]");

        if(enum.hasMoreElements())sb.append(",");
     }
     sb.append("}");
     return sb.toString();
  }

  public String generateUID(){

     String uid=Long.toString(System.currentTimeMillis());
     //check existing:
     int i=0;

     while(hashtable.get(uid)!=null && i++<10){
       uid=uid+","+(int)(Math.random() * 100);
     }

     return uid;
  }
  
}
