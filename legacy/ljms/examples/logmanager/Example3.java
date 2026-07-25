/**
 * @(#)Example3.java
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
 * LogManager example creating 3 types of files:
 * stderr, stdout and events. The last is used for application messages like:
 * "User X is connected" etc which are not debug messages but rather system logs.
 * Absolute path (where logs directory will be created) has been passed into
 * LogManager factory method as well as the name of the directory.
 *
 *
 * @version 1.0
 * @since   JDK1.0
 */
public class Example3{


     public static void main(String[] args) {

       try{
         LogManager log=LogManager.createInstance("/var/logs","VM2_logs");

         log.printEvent("Before Exception");
         try{

			throw new java.io.IOException("Foo exception");
		 }
         catch(java.io.IOException e){
			log.printError(e);
		 }
         catch(Exception e){
			log.printError(LogManager.stack2String(e));
		 }


         LogManager.getInstance().out("Debug message at the end");

         System.out.println("Logs have been written");
       }
       catch(Exception e){
         e.printStackTrace();
       }

    }


}