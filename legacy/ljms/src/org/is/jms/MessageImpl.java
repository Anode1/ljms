/**
 * @(#)MessageImpl.java
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

import java.util.Enumeration;
import java.io.IOException;
import java.io.DataInputStream;
import java.io.DataOutputStream;

import javax.jms.Message;
import javax.jms.JMSException;
import javax.jms.MessageFormatException;
import javax.jms.Destination;

import org.is.io.Streamable;

/**
 * Implementation of javax.jms.Message.<p>
 * Although this class is the base for all other message types, it can be used
 * sometimes (created directly - it is not abstract) where simple Message
 * should be sent (necessary information will be encoded in header Properties).
 * But it is better not to use only this message for all cases
 * (which is sure, possible - everything can be placed there) - just for purity
 * (see Sun's recomendations and balance them with 2 additional issues:
 * if we do not want to download extra classes to an applet, for example, we
 * will not use a subclass; the second issue: if data is just one field, creating
 * of the body (additional Properties object) + extra class probably is not worth
 * doing especially when Messages is the bottlenack. So, testing and desision
 * should be made for each particular application on optimization stage.
 *
 * @version 1.1
 * @since jdk1.0
 */
public class MessageImpl implements javax.jms.Message, Cloneable, Streamable{

  /**
   * Empty message is message without body - implementation of javax.jms.Message
   */
  public final static String EMPTY_MESSAGE  = "EMPTY";
  public final static String TEXT_MESSAGE   = "TEXT";
  public final static String MAP_MESSAGE    = "MAP";
  public final static String BYTE_MESSAGE   = "BYTE";
  public final static String OBJECT_MESSAGE = "OBJECT";
  public final static String STREAM_MESSAGE = "STREAM";

  //Some unused datamemebers have been commented for efficiency.
  //Uncomment them if complete JMS implementation will be built on this base

  private String type=EMPTY_MESSAGE;
  //private String id;
  //private byte[] correlationID;
  //private long timestamp;
  //private long expiration; //no need
  //private DestinationImpl destination;
  //private DestinationImpl replyTo;
  //private int priority;
  //private int deliveryMode=DeliveryMode.NON_PERSISTENT; //no need
  private PropertiesImpl props;

  /**
   * Constructor (used internally by the factories: either by this class built-in
   * deserializing factory or by Message factory in Session)
   */
  public MessageImpl(){

	  props = new PropertiesImpl(); //change this if more headers supposed to be used!
  }

  public String getJMSMessageID() throws JMSException{

    throw new IllegalArgumentException("id is not supported");
    //return id;  }
  public void setJMSMessageID(String id) throws JMSException{

    throw new IllegalArgumentException("id is not supported");
    //this.id=id;
  }

  public long getJMSTimestamp() throws JMSException{

    throw new IllegalArgumentException("timestamp is not supported");
    //return timestamp;
  }

  public void setJMSTimestamp(long timestamp) throws JMSException{

    throw new IllegalArgumentException("timestamp is not supported");
    //this.timestamp=timestamp;
  }

  public byte[] getJMSCorrelationIDAsBytes() throws JMSException{

     throw new IllegalArgumentException("CorrelationID is not supported");
  }

  public void setJMSCorrelationIDAsBytes(byte[] correlationID) throws JMSException{

     throw new IllegalArgumentException("CorrelationID is not supported");
  }

  public void setJMSCorrelationID(String correlationID) throws JMSException{

     throw new IllegalArgumentException("CorrelationID is not supported");
  }

  public String getJMSCorrelationID() throws JMSException{

     throw new IllegalArgumentException("CorrelationID is not supported");
  }

  public Destination getJMSReplyTo() throws JMSException{

     throw new IllegalArgumentException("replyTo is not supported");
     //return replyTo;
  }

  public void setJMSReplyTo(Destination replyTo) throws JMSException{

     throw new IllegalArgumentException("replyTo is not supported");
     //this.replyTo=(DestinationImpl)replyTo;
  }

  public Destination getJMSDestination() throws JMSException{

     throw new IllegalArgumentException("destination is not supported");
     //return destination;
  }

  public void setJMSDestination(Destination destination) throws JMSException{
    throw new IllegalArgumentException("destination is not supported");
    //this.destination=(DestinationImpl)destination;
  }

  public int getJMSDeliveryMode() throws JMSException{
 
    //return deliveryMode;
    throw new IllegalArgumentException("JMSRedeliveryMode is not supported");
  }

  public void setJMSDeliveryMode(int deliveryMode) throws JMSException{

    //this.deliveryMode=deliveryMode;
    throw new IllegalArgumentException("JMSRedeliveryMode is not supported");
  }

  public boolean getJMSRedelivered() throws JMSException{

     throw new IllegalArgumentException("JMSRedelivered is not supported");
  }
 
  public void setJMSRedelivered(boolean redelivered) throws JMSException{

     throw new IllegalArgumentException("JMSRedelivered is not supported");
  }

  public String getJMSType() throws JMSException{

     return type;
  }

  public void setJMSType(String type) throws JMSException{

     this.type=type;
  }
 
  public long getJMSExpiration() throws JMSException{
     throw new IllegalArgumentException("expiration is not supported");
     //return expiration;
  }
 
  public void setJMSExpiration(long expiration) throws JMSException{

     throw new IllegalArgumentException("expiration is not supported");
     //this.expiration=expiration;
  }

  public int getJMSPriority() throws JMSException{

     throw new IllegalArgumentException("priority is not supported");
     //return priority;
  }

  public void setJMSPriority(int priority) throws JMSException{

     throw new IllegalArgumentException("priority is not supported");
     //this.priority=priority;
  }

  public void clearProperties() throws JMSException{

     props=new PropertiesImpl();
  }

  public boolean propertyExists(String name) throws JMSException{

     return props.getProperty(name)!=null;
  }

  public boolean getBooleanProperty(String name) throws JMSException{

     return Converter.str2bool(props.getProperty(name));
  }

  public byte getByteProperty(String name) throws JMSException{

     return Converter.str2byte(props.getProperty(name));
  }

  public short getShortProperty(String name) throws JMSException{

     return Converter.str2byte(props.getProperty(name));
  }
 
  public int getIntProperty(String name) throws JMSException{

     return Converter.str2int(props.getProperty(name));
  }

  public long getLongProperty(String name) throws JMSException{

     return Converter.str2long(props.getProperty(name));
  }

  public float getFloatProperty(String name) throws JMSException{

     return Converter.str2float(props.getProperty(name));
  }

  public double getDoubleProperty(String name) throws JMSException{

     return Converter.str2double(props.getProperty(name));
  }

  public String getStringProperty(String name) throws JMSException{
  
     return props.getProperty(name);
  }

  public Object getObjectProperty(String name) throws JMSException{

     throw new IllegalArgumentException("getObjectProperty is not supported: use your own, custom serialization");
  }

  public Enumeration getPropertyNames() throws JMSException{

     return props.getPropertyNames();
  }

  public void setBooleanProperty(String name, boolean value)throws JMSException{

    props.addProperty(name, new Boolean(value).toString());
  }

  public void setByteProperty(String name, byte value)throws JMSException{

    props.addProperty(name, new Byte(value).toString());
  }

  public void setShortProperty(String name, short value)throws JMSException{

    props.addProperty(name, Short.toString(value));
  }

  public void setIntProperty(String name, int value)throws JMSException{

    props.addProperty(name, Integer.toString(value));
  }

  public void setLongProperty(String name, long value)throws JMSException{

    props.addProperty(name, Long.toString(value));
  }

  public void setFloatProperty(String name, float value)throws JMSException{

    props.addProperty(name, Float.toString(value));
  }

  public void setDoubleProperty(String name, double value)throws JMSException{

    props.addProperty(name, Double.toString(value));
  }

  public void setStringProperty(String name, String value) throws JMSException{

    //if(value==null) throw new MessageFormatException("MessageImpl::setStringProperty: attempt to insert null value for name:"+name);
    props.addProperty(name, value==null?"":value);
  }

  public void setObjectProperty(String name, Object value)throws JMSException{

    throw new IllegalArgumentException("setObjectProperty is not supported: use your own, custom serialization");
  }

  public void acknowledge() throws JMSException{

    throw new IllegalArgumentException("acknowledge is not supported");
  }

  /**
   * nothing to be cleared in this class (this class has no body) - this method
   * has been overriden by subclasses
   */
  public void clearBody() throws JMSException{
  }

  /**   * Streamifies this Message   */  public final void toStream(DataOutputStream out) throws IOException{

    out.writeUTF(type);

    //out.writeUTF(id);
    //out.writeLong(timestamp);
    //out.writeLong(expiration);
    //destination.toStream(out);
    //replyTo.toStream(out);
    //out.writeInt(priority);
    //out.writeInt(deliveryMode);
    props.toStream(out);

    writeBody(out);
    //out.flush();
  }

  /**
   * Creates MessageImpl from stream. <p>
   * This method is not used in the Message framework. It is done for
   * consistency only. Message factory has been used instead.
   */
  public final void fromStream(DataInputStream in) throws IOException{

    type=in.readUTF();
    headerFromStream(in);
  }

  /**
   * Internally used method, populating all header fields of this class from stream.
   * Notice: it is different from fromStream(), since type should be red by factory
   * i.e. in different method and would make this method inconsistent with
   * fromStream() methods in Streamable interface which serialize/deserialize
   * all object data members
   */
  private void headerFromStream(DataInputStream in) throws IOException{

    //notice: type has been red by Message factory

    //common for all subclasses of Message header fields:
    //id=in.readUTF();
    //timestamp=in.readLong();
    //expiration=in.readLong();
    //destination.fromStream(in);
    //replyTo.fromStream(in);
    //priority=in.readInt();
    //deliveryMode=in.readInt();
    props.fromStream(in);

    readBody(in);
  }

  protected void writeBody(DataOutputStream out) throws IOException{
    //no body in this Message - see subclasses having bodies
  }

  protected void readBody(DataInputStream in) throws IOException{
    //no body in this Message - see subclasses having bodies
  }

  /**
   * For debugging purposes only
   */
  public String toString(){
  
    StringBuffer sb=new StringBuffer("<MESSAGE>");
    sb.append(toStringInternal());
    sb.append("</MESSAGE>");
		return sb.toString();
  }

  /**
   * For debugging purposes only
   */
  protected String toStringInternal(){

    StringBuffer sb=new StringBuffer();
    //sb.append(destination.toString());
    sb.append(props.toString());
		return sb.toString();
  }

	public Object clone() throws CloneNotSupportedException{

    MessageImpl m=(MessageImpl)super.clone();
    return m;
  }

  public static final MessageImpl createMessage(DataInputStream in) throws JMSException{

    MessageImpl msg=null;
    try{
        String type=in.readUTF();

        if(EMPTY_MESSAGE.equals(type)){
           msg=new MessageImpl();
        }
        else if(TEXT_MESSAGE.equals(type)){
           //msg=new TextMessageImpl();
           throw new IllegalArgumentException("TextMessage type has been invalidated currently");
        }
        else if(MAP_MESSAGE.equals(type)){
           //msg=new MapMessageImpl();
           throw new IllegalArgumentException("MapMessageImpl has been invalidated currently");
        }
        else if(OBJECT_MESSAGE.equals(type)){
           //msg=new ObjectMessageImpl();
           throw new IllegalArgumentException("ObjectMessageImpl has been invalidated currently");
        }
        else if(BYTE_MESSAGE.equals(type)){
           //msg=new BytesMessageImpl();
           throw new IllegalArgumentException("BytesMessageImpl type has been invalidated currently");
        }
        else if(STREAM_MESSAGE.equals(type)){
           //msg=new StreamMessageImpl();
           throw new IllegalArgumentException("StreamMessageImpl type has been invalidated currently");
        }
        else{
           throw new JMSException("MessageImpl::fromStream unable to determine type of the message");
        }
        msg.headerFromStream(in);
        return msg;
    }
    catch(IOException e){
      throw new JMSException("MessageImpl::fromStream unable to read header of the message:"+e);
    }
  }  

  //debugging entry point
  public static void main(String[] args){
    try{
      MessageImpl m=new MessageImpl();
      m.setJMSDestination(new DestinationImpl("fff"));
      System.out.println(m);
      System.out.println(m.clone());
    }
    catch(Exception e){

    }
  }
  
}




