/**
 * @(#)AgentApplet.java
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

import java.awt.event.*;
import java.awt.*;
import java.net.URL;
import java.util.Vector;

import netscape.javascript.JSObject;

import javax.jms.*;

import org.is.jms.*;
import org.is.jms.broker.MessageSocket;

/**
 * Invisible Applet working as a message terminal for LJMS.
 *
 * @version 1.0
 * @since jdk1.0 (+ netscape.javascript.JSObject must be supported natively by
 * the browser)
 */
public class AgentApplet extends java.applet.Applet implements MessageListener, ExceptionListener{

  private JSObject window;
  private String data;
  LSessionRunnableImpl messageCentral;
  int port=6111;
  String host;//="localhost";

  public void init(){

    try{
       window = JSObject.getWindow(this);
    }catch(Exception ex){
       System.err.println(ex);
    }

    //check AgentApplet presence
    java.applet.AppletContext ac=getAppletContext();
    if(ac==null){
       System.out.println("getAppletContext()==null?");
       return;
    }
    String appletName="AgentApplet";
    java.applet.Applet a=ac.getApplet(appletName);
    if(a==null){
       System.out.println("Applet "+appletName+" is NULL!");
       return;
    }

    host=getCodeBase().getHost();
    //try to redefine host from applet parameter:
    /*
    String hostFromParam=getParameter("host");
    if(hostFromParam!=null){
       host=new String(hostFromParam);
    }
    */

    //try to redefine port from applet parameter:
    try{
       String portStr=getParameter("port");
       if(portStr!=null)port=Integer.parseInt(portStr);
    }
    catch(NumberFormatException nfe){
       System.err.println("port specified in parameters is not a number!");
    }

  }

  public void start(){

    try{
      messageCentral=new LSessionRunnableImpl();
      messageCentral.setMessageTransport(new MessageSocket(host, port));
      messageCentral.setMessageListener(this);
      messageCentral.setExceptionListener(this); //if not set - printing to console
      messageCentral.connect();                  //make connection (init)
      messageCentral.start();                    //start message delivery

      System.out.println("Connected to:"+host);
    }
    catch(JMSException e){
      System.err.println("Error connecting to:"+host+":"+port+":"+e);
    }
  }

  /**
   * Callback from Session when new message arrives
   */
  public void onMessage(Message msg){

    //System.out.println(msg.toString());
    try{

         String cmd=msg.getStringProperty(BMessage.CMD);
         String val=msg.getStringProperty(BMessage.VAL);

         if(cmd.equals(BMessage.ADD)){
            String[] parms={val};
            window.call("addContent", parms);
         }
         else if(cmd.equals(BMessage.CLEAR)){
            String[] parms={val};
            window.call("setContent", parms);
         }
         else if(cmd.equals(BMessage.EVAL)){

         }
         else System.err.println("handleMessage: unknown message command");

    }
    catch(Exception e){
      System.err.println("onMessage:"+e);
    }
  }


  private void clearForm(){


  }

  public void stop(){

    try{
      messageCentral.close();
    }
    catch(Exception e){
    }
    //System.out.println("stopped");
  }


  public void sendMessage(String text){

     try{
         if(window==null){
            System.err.println("Applet::getInitText: JSObject window was lost?");
            window = JSObject.getWindow(this);
         }
         Message ms=new MessageImpl();
         ms.setStringProperty("text",text);
         messageCentral.sendMessage(ms);
     }
     catch(Exception e){
         System.err.println("Can't send message:"+e);
     }
  }

  public void onException(JMSException e){

    try{messageCentral.close();}catch(JMSException ex){}
    System.err.println("Connection Lost"+e);
  }

}
