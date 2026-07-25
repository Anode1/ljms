/**
 * @(#)MapMessageImpl.java
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

import java.io.IOException;
import java.util.Enumeration;

import javax.jms.JMSException;
import javax.jms.MessageFormatException;

import org.is.io.Streamable;

/**
 * Lightweight implementation of javax.jms.MapMessage
 *
 * @version 1.0
 * @since jdk1.0
 */
class MapMessageImpl extends MessageImpl implements javax.jms.MapMessage, Cloneable, Streamable{

  private PropertiesImpl props;

  MapMessageImpl(){

	  props = new PropertiesImpl();
  }

  public void clearBody() throws JMSException{

	  props = new PropertiesImpl();
  }

  public boolean getBoolean(String name) throws JMSException{

     return Converter.str2bool(props.getProperty(name));
  }

  public byte getByte(String name) throws JMSException{
  
     return Converter.str2byte(props.getProperty(name));
  }

  public short getShort(String name) throws JMSException{

     return Converter.str2byte(props.getProperty(name));
  }

  public char getChar(String name) throws JMSException{

     return Converter.str2char(props.getProperty(name));
  }

  public int getInt(String name) throws JMSException{

     return Converter.str2int(props.getProperty(name));
  }

  public long getLong(String name) throws JMSException{

     return Converter.str2long(props.getProperty(name));
  }

  public float getFloat(String name) throws JMSException{

     return Converter.str2float(props.getProperty(name));
  }

  public double getDouble(String name) throws JMSException{

     return Converter.str2double(props.getProperty(name));
  }

  public String getString(String name) throws JMSException{
  
     return props.getProperty(name);
  }

  public byte[] getBytes(String name) throws JMSException{

     String s=props.getProperty(name);
     if(s==null)throw new MessageFormatException("MapMessageImple::getBytes:null");

     return s.getBytes();
  }

  public Object getObject(String name) throws JMSException{

     throw new IllegalArgumentException("getObject is not supported: use your own, custom serialization");
  }

  public Enumeration getMapNames() throws JMSException{
  
     return props.getPropertyNames();
  }

  public void setBoolean(String name, boolean value) throws JMSException{

    props.addProperty(name, new Boolean(value).toString());
  }

  public void setByte(String name, byte value) throws JMSException{

    props.addProperty(name, new Byte(value).toString());
  }

  public void setShort(String name, short value) throws JMSException{

    props.addProperty(name, Short.toString(value));
  }

  public void setChar(String name, char value) throws JMSException{

    props.addProperty(name, new String(value+""));
  }

  public void setInt(String name, int value) throws JMSException{

    props.addProperty(name, Integer.toString(value));
  }

  public void setLong(String name, long value) throws JMSException{

    props.addProperty(name, Long.toString(value));
  }

  public void setFloat(String name, float value) throws JMSException{

    props.addProperty(name, Float.toString(value));
  }

  public void setDouble(String name, double value) throws JMSException{
  
    props.addProperty(name, Double.toString(value));
  }

  public void setString(String name, String value) throws JMSException{

    if(value==null) throw new MessageFormatException("MapMessageImpl::setString: attempt to insert null value for name:"+name);
    props.addProperty(name, value);
  }

  public void setBytes(String name, byte[] value) throws JMSException{

    if(value==null) throw new MessageFormatException("MapMessageImpl::setBytes: attempt to insert null value for name:"+name);
    props.addProperty(name, new String(value));
  }

  public void setBytes(String name, byte[] value, int offset, int length)	throws JMSException{

    if(value==null) throw new MessageFormatException("MapMessageImpl::setBytes: attempt to insert null value for name:"+name);
    props.addProperty(name, new String(value, offset, length));
  }

  public void setObject(String name, Object value) throws JMSException{

    throw new IllegalArgumentException("setObject is not supported: use your own, custom serialization");
  }

  public boolean itemExists(String name) throws JMSException{

    return props.getProperty(name)!=null;
  }

  public void writeBody(java.io.DataOutputStream out) throws IOException{

    props.toStream(out);
  }

  public void readBody(java.io.DataInputStream in) throws IOException{

    props.fromStream(in);
  }

  /**
   * For debugging purposes only
   */
  public String toString(){

    StringBuffer sb=new StringBuffer("<MAP_MESSAGE>");
    sb.append(super.toStringInternal());
    sb.append("<BODY>");
    sb.append(props.toString());
    sb.append("</BODY>");
    sb.append("</MAP_MESSAGE>");
		return sb.toString();
  }

	public Object clone() throws CloneNotSupportedException{

    MapMessageImpl m=(MapMessageImpl)super.clone();
    return m;
  }

  //debugging entry point
  public static void main(String[] args){
    try{
      MapMessageImpl m=new MapMessageImpl();
      m.setJMSDestination(new DestinationImpl("fff"));
      //m.setText("text");
      System.out.println(m);
      System.out.println(m.clone());
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

}
