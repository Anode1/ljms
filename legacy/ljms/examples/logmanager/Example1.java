/**
 * @(#)Example1.java
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
 * LogManager example illustrating trivial usage of logmanager.
 * Logs directory (with default name: logs) will be created automatically
 * in the current user directory (if it is writable)
 *
 * @version 1.0
 * @since   JDK1.0
 */
public class Example1{


     public static void main(String[] args) {

       try{

         LogManager.err("This will be placed into stderr.log file");
         LogManager.out("This will be placed into standard output file: stdout.log");


         //The following is just another usage (files created once are remaining the same:
         //LogManager supports rotating logs which limits number of files and there size
         //by some threshold - default - 100K - defined in LogFile class):

		 LogManager log=LogManager.getInstance();
         log.printDebug("Some message");
         log.printError("This is an error string but we can also pass Exception as a parameter");

         System.out.println("Logs have been written");
       }
       catch(Exception e){
         e.printStackTrace();
       }

    }


}