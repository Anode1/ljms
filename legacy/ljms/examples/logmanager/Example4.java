/**
 * @(#)Example4.java
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
import org.is.logmanager.*;

/**
 * LogManager example illustrating how to use DebugLevels.
 * Look into JavaDocs (DebugLevel, LogManagerBase, LogManager classes - how to define
 * new debug levels and use combinations of those)
 *
 * @version 1.0
 * @since   JDK1.0
 */
public class Example4{


     public static void main(String[] args) {

       try{
         LogManager log=LogManager.createInstance("/var/logs");  //'logs' will be created
         log.setDebugLevel(DebugLevel.DEBUG_BRIEF);

         //...


         if(log.debugLevel(DebugLevel.DEBUG_BRIEF))LogManager.out("Starting of the service");
         //...
         if(log.debugLevel(DebugLevel.DEBUG_BD_POOL))LogManager.out("DB query: ...");
         if(log.debugLevel(DebugLevel.DEBUG_CACHE))LogManager.out("State of the cache: ...");

         System.out.println("Logs have been written");
       }
       catch(Exception e){
         e.printStackTrace();
       }
    }


}