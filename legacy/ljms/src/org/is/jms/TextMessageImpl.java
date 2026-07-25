/**
 * @(#)TextMessageImpl.java
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

import javax.jms.TextMessage;
import javax.jms.JMSException;

import org.is.io.Streamable;

/**
 * Lightweight implementation of javax.jms.TextMessage
 *
 * @since jdk1.0
 */
public class TextMessageImpl extends MessageImpl implements TextMessage, Cloneable, Streamable{

  private String text;

  TextMessageImpl(){

    text="";
  }

  TextMessageImpl(String text){

    this.text=text;
  }

  public void clearBody() throws JMSException{
  
	  text="";
  }

  public String getText() throws JMSException{

    return text;
  }

  public void setText(String text) throws JMSException{
  
    this.text=text;
  }

  public void writeBody(java.io.DataOutputStream out) throws IOException{

    out.writeUTF(text);
  }

  public void readBody(java.io.DataInputStream in) throws IOException{

    text=in.readUTF();
  }

  /**
   * For debugging purposes only
   */
  public String toString(){
  
    StringBuffer sb=new StringBuffer("<TEXT_MESSAGE>");
    sb.append(super.toStringInternal());
    sb.append("<BODY>");
    sb.append(text);
    sb.append("</BODY>");
    sb.append("</TEXT_MESSAGE>");
		return sb.toString();
  }

	public Object clone() throws CloneNotSupportedException{

    TextMessageImpl m=(TextMessageImpl)super.clone();
    return m;
  }

  //debugging entry point
  public static void main(String[] args){
    try{
      TextMessageImpl m=new TextMessageImpl();
      m.setJMSDestination(new DestinationImpl("fff"));
      m.setText("...text...");
      System.out.println(m);
      System.out.println(m.clone());
    }
    catch(Exception e){
      e.printStackTrace();
    }
  }

}
