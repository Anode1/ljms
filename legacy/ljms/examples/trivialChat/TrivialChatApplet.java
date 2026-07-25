/**
 * @(#)TrivialChatApplet.java
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

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;

import javax.jms.*;

import org.is.jms.*;
import org.is.jms.broker.MessageSocket;

public class TrivialChatApplet extends java.applet.Applet implements MessageListener, ExceptionListener{

  LSessionRunnableImpl messageCentral;

  TextArea text;
  Label label;
  TextField input;
  String user;

  int port=6284;
  String host;

  public void init(){

    buildGUI();

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
    catch(NumberFormatException nfe){}
  }

  public void start(){

    try{
      messageCentral=new LSessionRunnableImpl();
      messageCentral.setMessageTransport(new MessageSocket(host, port));
      messageCentral.setMessageListener(this);
      messageCentral.setExceptionListener(this); //if not set - printing to console
      messageCentral.connect();                  //make connection (init)
      messageCentral.start();                    //start message delivery

      this.showStatus("Connected to:"+host);      
    }
    catch(JMSException e){
      System.err.println("Error connecting:"+e);
    }
  //  messageCentral.setMessageTransport(new HttpTransport("http://localhost/servlet/org.is.server.BaseServlet");
  }

  private void buildGUI(){

    user = getParameter("user");
    if (user == null) user = "anonymous";

    text = new TextArea();
    text.setEditable(false);
    label = new Label("Say: ");
    input = new TextField();
    input.setEditable(true);

    setLayout(new BorderLayout());
    Panel panel = new Panel();
    panel.setLayout(new BorderLayout());

    add("Center", text);
    add("South", panel);

    panel.add("West", label);
    panel.add("Center", input);
  }

  /**
   * Callback from Session when new message arrives
   */
  public void onMessage(Message msg){

    //System.out.println(msg.toString());
    try{
      String s=msg.getStringProperty("text");
      text.append(">");
      text.append(s!=null?s:"null");
      text.append("\n");
    }
    catch(Exception e){
      System.err.println("onMessage:"+e);
    }
  }

  public void stop(){

    try{
      messageCentral.close();
    }
    catch(Exception e){
    }
    //System.out.println("stopped");
  }

  private void sendStringMessage(String str){
  
    try {
       Message ms=new MessageImpl();
       ms.setStringProperty("text",str);
       messageCentral.sendMessage(ms);
    }
    catch (Exception e) {
      System.out.println("Can't send message: " + e);
    }
  }

  /**
   * Events coming from GUI
   */
  public boolean handleEvent(Event event){

    switch (event.id) {
      case Event.ACTION_EVENT:
        if (event.target == input) {
          sendStringMessage(input.getText());
          input.setText("");
          return true;
        }
    }
    return false;
  }

  public void onException(JMSException e){

    try{messageCentral.close();}catch(JMSException ex){}
    System.err.println("Connection Lost"+e);
  }



  
}
