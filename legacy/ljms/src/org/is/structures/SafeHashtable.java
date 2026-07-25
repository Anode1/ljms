/**
 * @(#)SafeHashtable.java
 */
package org.is.structures;

import java.util.Hashtable;

import org.is.logmanager.LogManager;

/**
 * The same as Hashtable but additionally prints message if some key was not found.
 * For debugging purposes.
 *
 * @version 1.0
 * @since jdk1.0
 */
public class SafeHashtable extends Hashtable{

    /**
     * Default constructor
     */
    public SafeHashtable(){

       super();
    }

    /**
     * Constructor. See corresponding superclass's constructor
     */
    public SafeHashtable(int capacity){

       super(capacity);
    }

    /**
     * Constructor. See corresponding superclass's constructor
     */
    public SafeHashtable(int capacity, float factor){

       super(capacity, factor);
    }

/**
 * Overwrites the default method to have notification of the user before
 * null pointer exception in the caller
 */
        public Object get(String key){
        
            if(key==null){
              LogManager.getInstance().printError("SafeHashtable::trying to get null");
              return null;
            }
            Object result=super.get(key);
            if(result==null){
              LogManager.getInstance().printError("You are trying to get a not-existing value in SafeHashtable object by key:"+key);
            }
            return result;
        }

        public Object getNotSafe(String key){
            return super.get(key);
        }

}