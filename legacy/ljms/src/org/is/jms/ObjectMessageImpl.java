/**
 * @(#)ObjectMessageImpl.java
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
import java.io.Serializable;

import javax.jms.JMSException;

import org.is.io.Streamable;

/**
 * Lightweight implementation of javax.jms.ObjectMessage
 *
 * @version 1.0
 * @since jdk1.0
 */
class ObjectMessageImpl extends MessageImpl implements javax.jms.ObjectMessage, Cloneable, Streamable{

  private Serializable o;

  ObjectMessageImpl(){
  }

  public void clearBody() throws JMSException{
  
	  o = null;
  }

  public Serializable getObject() throws JMSException{

    return o;
  }

  public void setObject(Serializable o) throws JMSException{
  
    this.o=o;
  }

  public void writeBody(java.io.DataOutputStream out) throws IOException{

    //out.writeObject(o);
  }

  public void readBody(java.io.DataInputStream in) throws IOException{

    //o=in.readObject();
  }

  /**
   * For debugging purposes only
   */
  public String toString(){
  
    StringBuffer sb=new StringBuffer("<OBJECT_MESSAGE>");
    sb.append(super.toStringInternal());
    sb.append("<BODY>");
    sb.append(o.toString());
    sb.append("</BODY>");
    sb.append("</OBJECT_MESSAGE>");
		return sb.toString();
  }

	public Object clone() throws CloneNotSupportedException{

    ObjectMessageImpl m=(ObjectMessageImpl)super.clone();
    return m;
  }


}
