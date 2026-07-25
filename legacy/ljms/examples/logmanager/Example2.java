/**
 * @(#)Example2.java
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
 * LogManager example
 *
 * @version 1.0
 * @since   JDK1.0
 */
public class Example2{


     public static void main(String[] args) {

       try{
         //LogManager log=LogManager.createInstance("/gummo2/export/home/vgavrilov");
         //LogManager log=LogManager.createInstance("c:\\tmp","vm2_logs");
         LogManager log=LogManager.createInstance(System.getProperty("user.dir"),"test_logs");
         //log.setDisabled(false);
         log.err("bbb");
         log.out("ccc");

         if(log.debugLevel(LogManager.RESERVED_LEVEL_11))LogManager.out("eee");

         //System.out.println("standard output");
         //System.err.println("standard error output");

         log.printDebug("Dummy debug message (default debug level)");
         log.printEvent("Dummy event");
         log.printError(new NullPointerException("Dummy null pointer exception"));

         Thread.sleep(50000);
         //LogManager.err();

         System.out.println("Logs have been written");
       }
       catch(Exception e){
         e.printStackTrace();
       }

    }


}