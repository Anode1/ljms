/**
 * @(#)BytesMessageImpl.java
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
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.DataInputStream;

import javax.jms.JMSException;
import javax.jms.MessageNotReadableException;
import javax.jms.MessageNotWriteableException;

import org.is.io.Streamable;

/**
 * Lightweight implementation of javax.jms.BytesMessage
 *
 * @version 1.1
 * @since jdk1.0
 */
class BytesMessageImpl extends MessageImpl implements javax.jms.BytesMessage, Cloneable, Streamable{

  //aux streams (we do not make them transient since anyway do not serialize this object):
  private ByteArrayOutputStream bos;
  private DataOutputStream dos;
  private ByteArrayInputStream bis;
  private DataInputStream dis;

  private boolean writemode;  //flag indicating current mode: read or write mode

  
  public BytesMessageImpl(){

	  bos = new ByteArrayOutputStream();
    dos = new DataOutputStream(bos);
    writemode=true;
  }

  public boolean readBoolean() throws JMSException{

    if(writemode) throw new MessageNotReadableException("Cannot read in write mode");

    try{
       return dis.readBoolean();
		}catch(Exception e) {
			 JMSException ee = new JMSException("Can't read from byte stream");
			 ee.setLinkedException(e);
			 throw(ee);
		}
  }

  public byte readByte() throws JMSException{

    if(writemode) throw new MessageNotReadableException("Cannot read in write mode");

    try{
       return dis.readByte();
		}catch(Exception e) {
			 JMSException ee = new JMSException("Can't read from byte stream");
			 ee.setLinkedException(e);
			 throw(ee);
		}
  }

  public int readUnsignedByte() throws JMSException{

    if(writemode) throw new MessageNotReadableException("Cannot read in write mode");

    try{
       return dis.readUnsignedByte();
		}catch(Exception e) {
			 JMSException ee = new JMSException("Can't read from byte stream");
			 ee.setLinkedException(e);
			 throw(ee);
		}
  }

  public short readShort() throws JMSException{

    if(writemode) throw new MessageNotReadableException("Cannot read in write mode");

    try{
       return dis.readShort();
		}catch(Exception e) {
			 JMSException ee = new JMSException("Can't read from byte stream");
			 ee.setLinkedException(e);
			 throw(ee);
		}
  }

  public int readUnsignedShort() throws JMSException{

    if(writemode) throw new MessageNotReadableException("Cannot read in write mode");

    try{
       return dis.readUnsignedShort();
		}catch(Exception e) {
			 JMSException ee = new JMSException("Can't read from byte stream");
			 ee.setLinkedException(e);
			 throw(ee);
		}
  }

  public char readChar() throws JMSException{

    if(writemode) throw new MessageNotReadableException("Cannot read in write mode");

    try{
       return dis.readChar();
		}catch(Exception e) {
			 JMSException ee = new JMSException("Can't read from byte stream");
			 ee.setLinkedException(e);
			 throw(ee);
		}
  }

  public int readInt() throws JMSException{

    if(writemode) throw new MessageNotReadableException("Cannot read in write mode");

    try{
       return dis.readInt();
		}catch(Exception e) {
			 JMSException ee = new JMSException("Can't read from byte stream");
			 ee.setLinkedException(e);
			 throw(ee);
		}
  }

  public long readLong() throws JMSException{

    if(writemode) throw new MessageNotReadableException("Cannot read in write mode");

    try{
       return dis.readLong();
		}catch(Exception e) {
			 JMSException ee = new JMSException("Can't read from byte stream");
			 ee.setLinkedException(e);
			 throw(ee);
		}
  }

  public float readFloat() throws JMSException{

    if(writemode) throw new MessageNotReadableException("Cannot read in write mode");

    try{
       return dis.readFloat();
		}catch(Exception e) {
			 JMSException ee = new JMSException("Can't read from byte stream");
			 ee.setLinkedException(e);
			 throw(ee);
		}
  }

  public double readDouble() throws JMSException{

    if(writemode) throw new MessageNotReadableException("Cannot read in write mode");

    try{
       return dis.readDouble();
		}catch(Exception e) {
			 JMSException ee = new JMSException("Can't read from byte stream");
			 ee.setLinkedException(e);
			 throw(ee);
		}
  }

  public String readUTF() throws JMSException{

    if(writemode) throw new MessageNotReadableException("Cannot read in write mode");

    try{
       return dis.readUTF();
		}catch(Exception e) {
			 JMSException ee = new JMSException("Can't read from byte stream");
			 ee.setLinkedException(e);
			 throw(ee);
		}
  }

  public int readBytes(byte[] value) throws JMSException{

    if(writemode) throw new MessageNotReadableException("Cannot read in write mode");

    try{
       return dis.read(value);
		}catch(Exception e) {
			 JMSException ee = new JMSException("Can't read from byte stream");
			 ee.setLinkedException(e);
			 throw(ee);
		}
  }

  public int readBytes(byte[] v, int l)throws JMSException{

    if(writemode) throw new MessageNotReadableException("Cannot read in write mode");

    try{
       return dis.read(v,0,l);
		}catch(Exception e) {
			 JMSException ee = new JMSException("Can't read from byte stream");
			 ee.setLinkedException(e);
			 throw(ee);
		}
  }

  public void writeBoolean(boolean v)throws JMSException{

    if(!writemode) throw new MessageNotWriteableException("Cannot write in read mode");
    try{
       dos.writeBoolean(v);
    }
    catch(IOException e){
			 JMSException ee = new JMSException("Can't write into stream");
			 ee.setLinkedException(e);
			 throw(ee);
    }
  }

  public void writeByte(byte v) throws JMSException{

    if(!writemode) throw new MessageNotWriteableException("Cannot write in read mode");
    try{
      dos.writeByte(v);
    }
    catch(IOException e){
			 JMSException ee = new JMSException("Can't write into stream");
			 ee.setLinkedException(e);
			 throw(ee);
    }
  }

  public void writeShort(short v) throws JMSException{

    if(!writemode) throw new MessageNotWriteableException("Cannot write in read mode");
    try{
       dos.writeShort(v);
    }
    catch(IOException e){
			 JMSException ee = new JMSException("Can't write into stream");
			 ee.setLinkedException(e);
			 throw(ee);
    }
  }

  public void writeChar(char v) throws JMSException{

    if(!writemode) throw new MessageNotWriteableException("Cannot write in read mode");
    try{
      dos.writeChar(v);
    }
    catch(IOException e){
			 JMSException ee = new JMSException("Can't write into stream");
			 ee.setLinkedException(e);
			 throw(ee);
    }
  }

  public void writeInt(int v) throws JMSException{

    if(!writemode) throw new MessageNotWriteableException("Cannot write in read mode");
    try{
      dos.writeInt(v);
    }
    catch(IOException e){
			 JMSException ee = new JMSException("Can't write into stream");
			 ee.setLinkedException(e);
			 throw(ee);
    }
  }

  public void writeLong(long v) throws JMSException{

    if(!writemode) throw new MessageNotWriteableException("Cannot write in read mode");
    try{
       dos.writeLong(v);
    }
    catch(IOException e){
			 JMSException ee = new JMSException("Can't write into stream");
			 ee.setLinkedException(e);
			 throw(ee);
    }
  }

  public void writeFloat(float v) throws JMSException{

    if(!writemode) throw new MessageNotWriteableException("Cannot write in read mode");
    try{
      dos.writeFloat(v);
    }
    catch(IOException e){
			 JMSException ee = new JMSException("Can't write into stream");
			 ee.setLinkedException(e);
			 throw(ee);
    }
  }

  public void writeDouble(double v) throws JMSException{

    if(!writemode) throw new MessageNotWriteableException("Cannot write in read mode");
    try{
       dos.writeDouble(v);
    }
    catch(IOException e){
			 JMSException ee = new JMSException("Can't write into stream");
			 ee.setLinkedException(e);
			 throw(ee);
    }
  }

  public void writeUTF(String v) throws JMSException{

    if(!writemode) throw new MessageNotWriteableException("Cannot write in read mode");
    try{
      dos.writeUTF(v);
    }
    catch(IOException e){
			 JMSException ee = new JMSException("Can't write into stream");
			 ee.setLinkedException(e);
			 throw(ee);
    }
  }

  public void writeBytes(byte[] v) throws JMSException{

    if(!writemode) throw new MessageNotWriteableException("Cannot write in read mode");
    try{
       dos.write(v);
    }
    catch(IOException e){
			 JMSException ee = new JMSException("Can't write into stream");
			 ee.setLinkedException(e);
			 throw(ee);
    }
  }

  public void writeBytes(byte[] v, int off, int l)throws JMSException{

    if(!writemode) throw new MessageNotWriteableException("Cannot write in read mode");

    try{
       dos.write(v, off, l);
    }
    catch(IOException e){
			 JMSException ee = new JMSException("Can't write into stream");
			 ee.setLinkedException(e);
			 throw(ee);
    }
  }

  public void writeObject(Object value) throws JMSException{

    throw new IllegalArgumentException("writeObject is not supported: use your own, custom serialization");
  }

  public void reset() throws JMSException{

    if(writemode){
      try{
        dos.flush(); //autoflashes bos
        dis=new DataInputStream(new ByteArrayInputStream(bos.toByteArray()));
        writemode=false;
      }
      catch(IOException e){
        JMSException ee = new JMSException("Can't flush byte stream");
			  ee.setLinkedException(e);
			  throw(ee);
      }
    }
    else{
       //alread in read mode!
    }

  }

  public void clearBody() throws JMSException{

	  bos = new ByteArrayOutputStream();
    //writemode=true;     //writable again!
  }

  public void writeBody(java.io.DataOutputStream out) throws IOException{

    //if(bytes.size()>Integer.MAX_VALUE) no need to check :-)

    if(writemode){
       try{
          reset();
       }
       catch(JMSException e){
          throw new IOException("Error streaming body bytes");
       }
    }

    super.toStream(out);

    //body:
    byte[] bytes=bos.toByteArray();

    int howMany=bytes.length;

    out.writeInt(howMany);
    out.write(bytes, 0, howMany);
  }

  public void readBody(java.io.DataInputStream in) throws IOException{

    super.fromStream(in);

    int howMany=in.readInt();

    bos=new ByteArrayOutputStream(howMany);
    dos=new DataOutputStream(bos);

    for(int i=0; i<howMany; i++){
       bos.write(in.read());
    }
    
    dos.flush();
    bos.close();
    writemode=false;
  }

  /**
   * For debugging purposes only
   */
  public String toString(){
  
    StringBuffer sb=new StringBuffer("<BYTES_MESSAGE>");
    sb.append(super.toStringInternal());
    sb.append("...bytes...");
    sb.append("</BYTES_MESSAGE>");
		return sb.toString();
  }

	public Object clone() throws CloneNotSupportedException{

    BytesMessageImpl m=(BytesMessageImpl)super.clone();
    return m;
  }

}




