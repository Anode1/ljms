/**
 * @(#)PropertiesImpl.java
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
package org.is.jms;

import java.util.Properties;
import java.util.Enumeration;
import java.io.IOException;

import org.is.io.Streamable;

/**
 * Streamable implementation of Properties
 *
 * @see org.is.jms.Streamable
 * @since jdk1.0
 */
public class PropertiesImpl implements Streamable, Cloneable{

  private Properties props;

  public PropertiesImpl(){

	  props = new Properties();
  }

  public Properties getProperties(){

    return props;
  }

  public void setProperties(Properties props){

    this.props=props;
  }

  public String getProperty(String key){

    return props.getProperty(key);
  }

  public Enumeration getPropertyNames(){

    return props.keys();
  }

  public void addProperty(String key, String value){

    props.put(key, value);
  }

  public void toStream(java.io.DataOutputStream out) throws IOException{

      int n=props.size();

      //write the size
      out.writeInt(n);

      String key=null;
      String value=null;
      Enumeration enum = props.propertyNames();
    
      for( ; enum.hasMoreElements(); ){
        key=(String)enum.nextElement();
        out.writeUTF(key);

        value=props.getProperty(key);
        if(value==null)value="";
        out.writeUTF(value);
      }

  }

  public void fromStream(java.io.DataInputStream in) throws IOException{

      //read the size:
      int n=in.readInt();
      for(int i=0; i<n ;i++){
        String key=in.readUTF(in);
        String value=in.readUTF(in);
        props.put(key, value);
      }
  }

  /**
   * For debugging purposes only
   */
  public String toString(){

      StringBuffer sb=new StringBuffer("<PROPERTIES>");
      for(Enumeration enum = props.propertyNames(); enum.hasMoreElements() ;) {
          String aKey=(String)enum.nextElement();
          String aValue=props.getProperty(aKey);
          sb.append(aKey);
          sb.append('=');
          sb.append(aValue);
          if(enum.hasMoreElements())sb.append(" ");
      }
      sb.append("</PROPERTIES>");
		  return sb.toString();
  }

	public Object clone() throws CloneNotSupportedException{

    return (PropertiesImpl)super.clone();
  }

}




