/**
 * @(#)Queue.java
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

import java.util.ArrayList;

/**
 * Simple queue implemented on Java2 ArrayList
 *
 * @version 1.0
 * @since jdk1.2
 */
public class Queue{

    private ArrayList data = new ArrayList();

    public synchronized void push(Object o){

        data.add(o);
        notify();
    }

    public synchronized Object pull(){

        while(data.size() == 0){
          try{
             wait();
          }catch (InterruptedException e){
            //ignore
          }
        }
        if(data.size()<1){
          return null;
        }
        Object o = data.get(0);
        data.remove(0);
        return o;
    }

    public int size(){

        return data.size();
    }    
    
}
