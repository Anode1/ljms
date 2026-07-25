/** * @(#)DebugLevel.java
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
 */package org.is.logmanager;import java.lang.reflect.Field;
/**
 * This is just an example file defining degug application specific debug levels:
 * this file is not used and each application should have it's own file
 *
 * Container for all debug levels names (specific to the application)
 * used by LogManager for debugging. It is just a map from this names to
 * application not specific constants defined in LogManagerBase
 * This class just adds meaningful names to those constants.
 *
 * @see LogManagerBase
 * @see LogManager
 * @version 3.2 09/06/99
 * @since jdk1.0
 */
public class DebugLevel{
    /**
     * Switching of debugging. Supposed to be used when no debugging is needed in production
     */
    public static final int DEBUG_NONE                       = LogManager.DEBUG_NONE;

    /**
     * Prints only messages not having debug level, i.e. the following lines: <br>
     * <code>LogManager.out("foo message");</code>
     *
     */
    public static final int DEBUG_NOT_SPECIFIED              = LogManager.DEBUG_DEFAULT;

    public static final int DEBUG_DEFAULT                    = LogManager.DEBUG_DEFAULT;

    /**
     * Print all messages. Do not use this or switch it off after one time use because it is much printing
     */
    public static final int DEBUG_ALL                        = LogManager.DEBUG_ALL;

    public static final int DEBUG_START_STOP                 = LogManager.RESERVED_LEVEL_0;
    public static final int DEBUG_CACHE                      = LogManager.RESERVED_LEVEL_1;
    public static final int DEBUG_TEMPLATES                  = LogManager.RESERVED_LEVEL_2;
    public static final int DEBUG_BD_POOL                    = LogManager.RESERVED_LEVEL_3;
    public static final int DEBUG_DB_REQUESTS                = LogManager.RESERVED_LEVEL_4;
    public static final int DEBUG_BOUNCED_MAIL               = LogManager.RESERVED_LEVEL_5;
    public static final int DEBUG_LRU_CACHE                  = LogManager.RESERVED_LEVEL_6;

    //enter here custom patterns in the manner:

    public static final int DEBUG_DB = DEBUG_BD_POOL | DEBUG_DB_REQUESTS;
    public static final int DEBUG_ALL_BUT_DB = DEBUG_ALL & ~DEBUG_BD_POOL & ~DEBUG_DB_REQUESTS;
    public static final int DEBUG_BRIEF = DEBUG_DEFAULT | DEBUG_START_STOP;

    //MARINA's levels (game station):


    public static final int DEBUG_ANY_PLACE                 = LogManager.RESERVED_LEVEL_20;
    public static final int DEBUG_STARTING                  = LogManager.RESERVED_LEVEL_21;
    public static final int DEBUG_GENERIC_SERVER            = LogManager.RESERVED_LEVEL_22;
    public static final int DEBUG_SOCKET_CONNECTION         = LogManager.RESERVED_LEVEL_23;
    public static final int DEBUG_CLIENT_MESSAGES           = LogManager.RESERVED_LEVEL_24;
    public static final int DEBUG_ANOTHER_SERVERS_MESSAGES  = LogManager.RESERVED_LEVEL_25;
    public static final int DEBUG_MESSAGE_DISTRIBUTION      = LogManager.RESERVED_LEVEL_26;
    public static final int DEBUG_THREADS                   = LogManager.RESERVED_LEVEL_27;
    public static final int DEBUG_SERVER_CONNECTIONS_STATUS = LogManager.RESERVED_LEVEL_28;
    public static final int DEBUG_CLIENTS                   = LogManager.RESERVED_LEVEL_29;


    /**
     * Restores debugLevel (int) from String passed
     */
    public static int fromString(String string){

       int level=DEBUG_DEFAULT;
       try{
          level=getFieldAsInt(new DebugLevel(), string);
       }
       catch(Exception e){
          LogManager.err("There is no debug level: "+string+" be defined in DebugLevel class - DEBUG_NONE is set");
       }
       return level;
    }

    public static int getFieldAsInt(Object targetObject, String dataMember) throws NoSuchFieldException, IllegalAccessException{

      Field field=targetObject.getClass().getField(dataMember);
      return field.getInt(targetObject);
    }

}
