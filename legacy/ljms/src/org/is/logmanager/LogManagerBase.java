/**
 * @(#)LogManagerBase.java
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
package org.is.logmanager;

import java.util.Date;

/**
 * The base for LogManager class.
 *
 * @see LogManager
 * @version 3.0 09/06/99
 * @since jdk1.0
 */
public class LogManagerBase{

    public static final int DEBUG_NONE                = 0x0000;;
    public static final int DEBUG_ALL                 = 0xFFFFFFFF;

    /**
     * Default is used when LogLevel is not specified
     */
    public static final int DEBUG_DEFAULT             = (1<<0);
    public static final int RESERVED_LEVEL_0                 = (1<<1);
    public static final int RESERVED_LEVEL_1                 = (1<<2);
    public static final int RESERVED_LEVEL_2                 = (1<<3);
    public static final int RESERVED_LEVEL_3                 = (1<<4);
    public static final int RESERVED_LEVEL_4                 = (1<<5);
    public static final int RESERVED_LEVEL_5                 = (1<<6);
    public static final int RESERVED_LEVEL_6                 = (1<<7);
    public static final int RESERVED_LEVEL_7                 = (1<<8);
    public static final int RESERVED_LEVEL_8                 = (1<<9);
    public static final int RESERVED_LEVEL_9                 = (1<<10);
    public static final int RESERVED_LEVEL_10                = (1<<11);
    public static final int RESERVED_LEVEL_11                = (1<<12);
    public static final int RESERVED_LEVEL_12                = (1<<13);
    public static final int RESERVED_LEVEL_13                = (1<<14);
    public static final int RESERVED_LEVEL_14                = (1<<15);
    public static final int RESERVED_LEVEL_15                = (1<<16);
    public static final int RESERVED_LEVEL_16                = (1<<17);
    public static final int RESERVED_LEVEL_17                = (1<<18);
    public static final int RESERVED_LEVEL_18                = (1<<19);
    public static final int RESERVED_LEVEL_19                = (1<<20);
    public static final int RESERVED_LEVEL_20                = (1<<21);
    public static final int RESERVED_LEVEL_21                = (1<<22);
    public static final int RESERVED_LEVEL_22                = (1<<23);
    public static final int RESERVED_LEVEL_23                = (1<<24);
    public static final int RESERVED_LEVEL_24                = (1<<25);
    public static final int RESERVED_LEVEL_25                = (1<<26);
    public static final int RESERVED_LEVEL_26                = (1<<27);
    public static final int RESERVED_LEVEL_27                = (1<<28);
    public static final int RESERVED_LEVEL_28                = (1<<29);
    public static final int RESERVED_LEVEL_29                = (1<<30);
    public static final int RESERVED_LEVEL_30                = (1<<31);

    protected int debugLevel = DEBUG_DEFAULT;
//-----------------------------------------------------------------------------
    public static final int EVENTS_VERBOSE           = 0xFFFFFFFF;
    public static final int EVENTS_START_STOP        = (1<<0);
    //public static final int EVENTS_BRIEF  =  (some chosen pattern);
   //public static final int EVENTS_SILENT  =  (some chosen pattern);

    protected int eventLevel = EVENTS_VERBOSE;
//-----------------------------------------------------------------------------

    protected String CR=System.getProperty("line.separator");

    /**
     * Flag indicating whether to print the whole stack trace or just message
     * in the case of an exception
     */
    protected boolean printStackTrace=false; //default - if not specified in system.properties

    /**
     * Flag indicating whether to print header before messages
     */
    protected boolean printHeader=true;

    /**
     * Sets debugging mode
     */
    public void setDebugLevel(int debugLevel){

      this.debugLevel = debugLevel;
    }

    /**
     * Gets current debugging mode
     */
    public int getDebugLevel(){

      return debugLevel;
    }

    /**
     * Sets printStackTrace mode
     */
    public void setPrintStackTrace(boolean printStackTrace){

      this.printStackTrace=printStackTrace;
    }

    public boolean getPrintStackTrace(){

      return printStackTrace;
    }

    /**
     * Sets print header
     */
    public void setPrintHeader(boolean printHeader){

      this.printHeader=printHeader;
    }

    public boolean printHeader(){

      return printHeader;
    }

   /**
    * Returns header for messages
    */
    protected StringBuffer getPrefix() {

      StringBuffer sb=new StringBuffer("");      if(!printHeader)return sb;

      //sb.append(Thread.currentThread().getName());
      sb.append(" [");

      sb.append( new Date() );      sb.append("] ");
      return sb;
    }

}