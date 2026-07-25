/**
 * @(#)LRUCache.java
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

import org.is.logmanager.*;

/**
 * Generic LRU Cache implementation. Supports timing for objects which are valid
 * only withing some time interval. If interval is not passed - expiry time
 * is made to be 1 year. Weight of each object by default is equal to 1 and in this
 * case INITIAL_SIZE and maxSize constants define number of obejcts in the cache.
 * If more presise control is needed - pass object sizes in bytes or other metrics.
 *
 * @author Vasili Gavrilov
 * @version 2.0
 * @since jdk1.0
 */
public final class LRUCache{

    /**
     * initial size of hashtable during it's initialization
     */
    private int INITIAL_CACHE_SIZE = 100;

    /**
     * The maximum number of bytes allowed to be stored the cache.
     */
    private int maxSize = 100;   // in objects

    /**
     * Current size of the cache
     */
    private int currentCacheSize;

    /**
     * Hashtable of objects
     */
    private Hashtable cache;

    /**
     * Head of the linked list
     */
    private Node head;

    /**
     * Tail of linked list
     */
    private Node tail;

    private LogManager log=LogManager.getInstance();

    public LRUCache(){

        this.reload();
    }

    public LRUCache(int maxSize){

        this.maxSize=maxSize;
        this.reload();
    }

    /**
     * Return the CacheableObject associated with the given id
     * or null if not existing
     */
    public synchronized Object get(Object id) {

        Node node = (Node)cache.get(id);
        if(node == null){
            if(log.debugLevel(DebugLevel.DEBUG_LRU_CACHE))LogManager.out("Cache:: not found in cache:"+id);
            return null;
        }
        long now=System.currentTimeMillis();
        if(now>node.creationTime+node.lifeTime){
            if(log.debugLevel(DebugLevel.DEBUG_LRU_CACHE))LogManager.out("Cache:: expired object found (discarded):"+id);
            this.delete(id);
            return null;
        }

        removeNodeFromLRU(node);
        //go to the fornt
        insertNodeIntoLRU(node);
        if(log.debugLevel(DebugLevel.DEBUG_LRU_CACHE))LogManager.out("Cache:: found in cache:"+id);
        return node.value;
    }

    /**
     * Updates an item in the cache setting indefinite lifetime and one byte weight
     */
    public synchronized void update(Object id, Object value){

       update(id, value, 1); //for debugging - one byte only
    }

    /**
     * Updates an item in the cache setting indefinite lifetime and one byte weight
     */
    public synchronized void update(Object id, Object value, long lifetime){

       update(id, value, 1, lifetime); //for debugging - one byte only
    }

    public static final long FOREVER=1000*3600*24*365;

    /**
     * Updates an item in the cache setting indefinite lifetime
     */
    public synchronized void update(Object id, Object value, int size){

       update(id, value, size, FOREVER);  //one year - default
    }

    /**
     * Updates an item in the cache
     */
    public synchronized void update(Object id, Object value, int size, long lifeTime) {

        boolean wasAlreadyInCache=cache.containsKey(id);

        //remove old value if exists such
        if (wasAlreadyInCache){
            delete(id);
        }

        while (currentCacheSize + size > maxSize) {
            if (!deleteLRU()) break;
        }

        if (currentCacheSize + size > maxSize){ //for case when we don't want to throw away
            return;
        }

        Node node = new Node();
        node.id = id;
        node.value = value;
        node.size = size;
        node.lifeTime=lifeTime;
        node.creationTime=System.currentTimeMillis();
        cache.put(id, node);
        insertNodeIntoLRU(node);
        currentCacheSize += size;

        if (wasAlreadyInCache){
            if(log.debugLevel(DebugLevel.DEBUG_LRU_CACHE))LogManager.out("Cache:: updated:"+id);
        }
        else{
            if(log.debugLevel(DebugLevel.DEBUG_LRU_CACHE))LogManager.out("Cache:: added:"+id);
        }
    }

    /**
     * Removes an item from the cache
     */
    public synchronized Object delete(Object id) {

        Node node = (Node)cache.remove(id);
        if (node == null){
            if(log.debugLevel(DebugLevel.DEBUG_LRU_CACHE))LogManager.out("Cache:: was not removed - not existed:"+id);
            return null;
        }
        removeNodeFromLRU(node);
        currentCacheSize -= node.size;
        if(log.debugLevel(DebugLevel.DEBUG_LRU_CACHE))LogManager.out("Cache:: removed:"+id);
        return node.value;
    }

    /**
     * Returns true if there is element with passed id in the cache
     */
    public synchronized boolean containsKey(Object id) {

        return cache.containsKey(id);
    }

    /**
     * For debugging purposes only
     */
    public String toString(){

        return cache.toString();
    }

    private void insertNodeIntoLRU(Node node){

        node.next = head;
        node.prev = null;
        if (head != null){
            head.prev = node;
        }
        head = node;
        if (tail == null){
            tail = node;
        }
    }

    /**
     * No need to synchronize this method - it has been used in another synch block only
     */
    private boolean deleteLRU() {

        if (tail == null){
            return false;
        }

        currentCacheSize -= tail.size;
        cache.remove(tail.id);
        removeNodeFromLRU(tail);
        return true;
    }

    /**
     *  Delete LRU item
     */
    private void removeNodeFromLRU(Node node) {

        if (node.prev != null)
            node.prev.next = node.next;
        else
            head = node.next;
        if (node.next != null)
            node.next.prev = node.prev;
        else
            tail = node.prev;
    }

    /**
     * Returns current cache size
     */
    public synchronized int getCurrentCacheSize(){

        return currentCacheSize;
    }

    /**
     * Returns current cache size
     */
    public int getMaxSize(){

        return maxSize;
    }

    /**
     * Sets MAXIMAL cache size
     */
    public void setMaxSize(int maxSize){

        this.maxSize=maxSize;
        this.reload();
    }

    /**
     * Removes all items from the cache (refreshes everything)
     */
    public synchronized void reload(){

        if(cache==null)cache=new Hashtable(INITIAL_CACHE_SIZE);
        else cache.clear();

        head=null;
        tail=null;
        currentCacheSize=0;
    }

    /*
     * Aux class for implementing linked list (needed for realization of LRU
     * algorithm)
     */
    private class Node {

        int size;
        Node prev;
        Node next;
        Object id;
        Object value;
        long creationTime;
        long lifeTime;

       /**
        * For debugging purposes only
        */
        public String toString(){

           StringBuffer sb=new StringBuffer("[");
           sb.append(value!=null?value:"");
           sb.append(" size="+Integer.toString(size));
           sb.append("]");
           return sb.toString();
        }          
    }//Node

    /**
     * For testing purposes only. Please do not remove this - it shows how to use it
     * and is for testing if something goes wrong
     *
     * Please, do not remove this testing body
     */
    public static void main(String[] args) {

      try{

        LogManager log=LogManager.getInstance();
        log.setDebugLevel(LogManagerBase.DEBUG_ALL);
        log.setDisabled(true);

        LRUCache cache=new LRUCache();
        cache.setMaxSize(2);

        log.printDebug(cache.toString());

        cache.update("a","a");

        log.printDebug(cache.toString());

        cache.update("a","a");

        log.printDebug(cache.toString());

        cache.update("b","b");

        log.printDebug(cache.toString());

        cache.update("c","c");

        log.printDebug(cache.toString());

        cache.get("c");

        log.printDebug(cache.toString());

        Thread.sleep(10000);
      }
      catch(Exception e){
        e.printStackTrace();
      }
    }


}