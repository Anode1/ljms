/**
 * @(#)OrderedHashtable.java
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
package org.is.structures;

import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;

/**
 * Hashtable having array of keys enumerating them in different orders.
 * From another point of view - it is an indexed array of objects.
 *
 * @version 1.0
 * @since   JDK1.0
 */
public class OrderedHashtable{

    /**
     * View (or list)
     */
    private Vector view;
    private Hashtable hashtable;

    /**
     * Default constructor
     */
    public OrderedHashtable(){

       hashtable=new Hashtable();
       view=new Vector();
    }

    /**
     * Constructor. @see Hashtable
     */
    public OrderedHashtable(int capacity){

       hashtable=new Hashtable(capacity);
       view=new Vector(capacity);
    }

    /**
     * Constructor. @see Hashtable
     */
    public OrderedHashtable(int capacity, float factor){

       hashtable=new Hashtable(capacity, factor);
       view=new Vector(capacity);
    }

    /**
     * Overwrites default method
     */
    public Object get(String key){

       return hashtable.get(key);
    }

    /**
     * Overwrites default method
     */
    public synchronized Object put(String key, Object value){

      view.addElement(new String(key));
      return hashtable.put(key, value);
    }

    public synchronized Object removeElement(String key){

      if(!view.removeElement(key)){
         System.err.println("Ordered hashtable::remove:was not removed");
      }
      return hashtable.remove(key);
    }

    public String getKeyAt(int i){

      return (String)view.elementAt(i);
    }

    public Object getElementAt(int i){

      return hashtable.get(view.elementAt(i));
    }

    public int size(){

      return view.size();
    }

    /**
     * Removes a key from the middle of the view and puts it to the top (as the last element)
     */
    public synchronized void putTop(String key){
      /*
      Vector clone=(Vector)view.clone();
      for (int i=0; i<clone.size(); i++){
          String o=(String)view.elementAt(i);
          if(key.equals(o)){
             view.removeElementAt(i);
             view.addElement(key);
             return;
          }
      }
      */
    }

}