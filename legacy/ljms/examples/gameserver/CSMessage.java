/**
 * @(#)CSMessage.java
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

import org.is.jms.MessageImpl;

/**
 * MessageImpl extension used in Server application (just holder for
 * app specific message constants and convenience constructor)
 *
 * @since   JDK1.0
 */
public class CSMessage extends MessageImpl{

  public static final byte DUMMY=0;
  public static final byte LOGIN=1;
  public static final byte LOGOUT=2;
  public static final byte SAY=3;
  public static final byte DATA=4;
  public static final byte LIST=5;
  public static final byte STARTOVER=6;
  public static final byte INVITATION=7;
  public static final byte ACCEPT_INVITATION=8;
  public static final byte REJECT_INVITATION=9;
  public static final byte SET_PASSWORD_BACK=10;
  public static final byte SOUND=11;
  public static final byte STARTOVER_NOTIFICATION=12;
  public static final byte WHISPER=13;
  public static final byte READY=14;  

  public CSMessage(byte command, String value, String name){

    super();
    try{
      setByteProperty("command", command);
      setStringProperty("value", (value!=null?value:""));
      setStringProperty("name", (name!=null?name:""));
      //setStringProperty("sessionID",(sessionID!=null?sessionID:""));
    }
    catch(Exception e){
      System.err.println("Construction of CSMessage:"+e);
    }
  }

  public CSMessage(byte command){

    super();
    try{
      setByteProperty("command", command); 
    }
    catch(Exception e){
      System.err.println("Construction of CSMessage:"+e);
    }
  }

}
