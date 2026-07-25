/**
 * @(#)HostPort.java
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
package org.is.net;

import java.io.IOException;

//import org.is.io.Streamable;

/**
 * Simple structure for hostName:portNumber
 *
 * @version 1.0
 * @since   JDK1.0
 */
public final class HostPort implements /*Streamable,*/ Cloneable{

  //all data memebers must be public!
  public String host="localhost"; //default
  public int port;
  public int hashCode; //caching of hashcode speeds up lookup

  /**
   * Default constructor - necessary for serialization
   */
  public HostPort() {
  }

  /**
   * Default constructor
   */
  public HostPort(String host, int port){

    this.host=host;
    this.port=port;
    hashCode=this.toString().hashCode();
  }

  /**
   * Constructor
   */
  public HostPort(String host, String portString)throws NumberFormatException{

    this(host, Integer.parseInt(portString));
  }

  public int hashCode(){

    return hashCode;
  }

  public boolean equals(Object o){

    if(this==o) return true;
    else if(o==null || getClass()!=o.getClass()) return false;
    HostPort other=(HostPort)o;
    return port==other.port && host.equals(other.host);
  }

  public void setPortAsString(String portString)throws NumberFormatException{

    port=Integer.parseInt(portString);
  }

  public String toString(){

	  return host+":"+port;
  }

  public Object clone() throws CloneNotSupportedException{

     return super.clone();
  }  

    /*
  public void writeExternal(java.io.DataOutputStream out)throws IOException{

    out.writeUTF(host!=null?host:"");
    out.writeInt(port);
    out.writeInt(hashCode);
  }


  public void readExternal(java.io.DataInputStream in) throws IOException, ClassNotFoundException{

    host=(String)in.readUTF();
    port=in.readInt();
    hashCode=in.readInt();
  }
      */
}
