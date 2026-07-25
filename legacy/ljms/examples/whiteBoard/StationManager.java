/**
 * @(#)StationManager.java
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
import java.applet.*;
import java.net.Socket;
import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.IOException;
import java.awt.*;

import org.is.jms.broker.MessageSocket;
import org.is.jms.LSessionImpl;

import common.*;

import javax.jms.JMSException;
import javax.jms.Message;

public class StationManager extends Applet implements Runnable{

       public LSessionImpl connection = null;
       private TextField inputField;
       private TextArea outputArea;
       private Button typeButton;
       public String name, theHost = null;
       public Dimension d ;
       private static final int NUM_ATTEMPTS=10;
       public int nNames;
       public String [] names;
       public Frame frame;
       private Thread kicker;
       private MediaTracker tracker ;
       public Image palette;

       private Button label;
       private boolean loginDone, haveBeenNotifiedAlready ;

       Panel activeApplet = null;


  public void init() {

       int thePort = 0;
	     try{	thePort = Integer.parseInt(getParameter("port"));}catch(Exception e){
          System.err.println(e);
       }
       if( thePort == 0) thePort = 1113;

       connection=new LSessionImpl();
       connection.setMessageTransport(new MessageSocket(getCodeBase().getHost(), thePort));

       int numAttempts=0;

       boolean connected=false;

       while (!connected) {

         if(numAttempts>NUM_ATTEMPTS){
            System.err.println("Can't connect after "+NUM_ATTEMPTS+" attempts");
            return;
         }

         try{
           connection.connect();
           connected=true;
         }
         catch(Exception e){
            System.err.println("Attempt "+numAttempts+" failed");
            try{ Thread.sleep( 3000 ); } catch(InterruptedException ie){}
            numAttempts++;
         }
       }

       setFont( new Font("TimesRoman" , Font.PLAIN, 16)) ;
 
       d = this.size();

       frame = Utils.getParentFrame(this) ;
      
       nNames = Integer.parseInt(getParameter("nNames"));
       names = new String [nNames];
       for (int i=0 ; i<nNames ; i++  )
          names [i] =  getParameter("name_"+i);

       PasswordDialog passwordDialog = new PasswordDialog( this );
       setLayout(new BorderLayout());
       add("Center", passwordDialog);
       validate();

      loadImages();

  } //init
  


 public void composeName(String name){


     Message ms=new CSMessage(CSMessage.LOGIN);
      try{
          ms.setStringProperty("name",name);

      }
      catch(Exception e){
          System.err.println("Stationmanager::composeName:Construction of CSMessage:"+e);
      }
      sendMessage(ms);
 }


  boolean doneLoading ;

  public boolean loadImages() {
  
   tracker = new MediaTracker(this);

   palette = getImage(getDocumentBase(), getParameter("palette"));
   tracker.addImage( palette  , 0 );

   try {
                tracker.waitForID(0);
                doneLoading = true ;

   }
   catch(InterruptedException e) {
                System.err.println ("Error loading images");

   }

   return (doneLoading);
  }



 public void run() {

    try{

       while (!loginDone) {

         try{Thread.sleep(100); }
         catch (InterruptedException e) {}

         Message message=connection.getMessage();


         byte cmd = message.getByteProperty("command");

	       if(cmd==CSMessage.LOGIN) {

           name=message.getStringProperty("name");
           loginDone = true;
         }



      }//while (!loginDone)

      
      while (!doneLoading) {

         try{Thread.sleep(100); }
         catch (InterruptedException e) {}

       }//while (!doneLoading)


      setApplet();


       while (true){
           
           Message message=connection.getMessage(); //wait untill a message available

            if(message==null)return;


            byte cmd = message.getByteProperty("command");


           if(cmd==CSMessage.DATA)

                 ((CClient)activeApplet).draw_p.updateDrawingPanel(message.getStringProperty("data"));



			     else if(cmd==CSMessage.SAY)
               
                outputArea.appendText(message.getStringProperty("name")+" says: "+message.getStringProperty("sentence") + "\n");


    

    }//while

  }//try

  catch(Exception e){
    System.err.println(e);
    connectionLost();
  }
  finally{
  }

  }//run

  public void start(){
		if (kicker == null){
			kicker = new Thread(this);
			kicker.start();
		}
  }


  public void stop() {

       try{
        connection.close();
       }
       catch(Exception e){}


       if(kicker!=null) kicker.stop();
       kicker = null;
  }




 public void setApplet(){


      setLayout(new BorderLayout());

      Panel p1 = new Panel();
      p1.setLayout(new FlowLayout());
      activeApplet = new CClient(this);
      p1.add(activeApplet);
      add(p1, BorderLayout.NORTH);

      Panel p2 = new Panel();
      p2.setLayout(null);
      outputArea = new TextArea();
      outputArea.setEditable(false);
      outputArea.setBackground(Color.white);
      outputArea.setBounds(10,10, 390,60);
      p2.add(outputArea);
      add(p2, BorderLayout.CENTER);

      Panel p3= new Panel();
      p3.setLayout(new FlowLayout());
      inputField = new CustomTextField(this);
      inputField.requestFocus();
      p3.add(inputField);

      typeButton = new Button("Send");
      p3.add(typeButton);
      add(p3, BorderLayout.SOUTH);
      validate();

  }



  public void connectionLost(){

      if (!haveBeenNotifiedAlready) {
          haveBeenNotifiedAlready = true ;


          synchronized(this){
          ConnectionLostDialog connectionLostDialog = new ConnectionLostDialog(frame, "Attention!", "You have logged out.", "Come back soon!", true );

          connectionLostDialog.resize(250, 150);
          Utils.setCentalizedLocationRelativeMe(frame, connectionLostDialog);
          connectionLostDialog.show();
          connectionLostDialog.toFront();
          }


          if(kicker!=null){
              kicker.stop();
              kicker = null;
          }

      }
  }




  public boolean handleEvent(Event e){

     if( e == null ) { // just in case
        return true;
     }

  
     if (e.id == Event.ACTION_EVENT ) {

          if(e.target == typeButton) {

                if(inputField.getText().length()>0){


                  Message ms=new CSMessage(CSMessage.SAY);
                  try{
                      ms.setStringProperty("sentence",inputField.getText());
                      ms.setStringProperty("name",name);
                  }
                  catch(Exception ex){
                      System.err.println("Stationmanager::handleEvent:Construction of CSMessage:"+ex);
                  }
                  sendMessage(ms);

		              inputField.setText("");
                  inputField.requestFocus();
                }


	              return true;
           }

     }//evt.id == Event.ACTION_EVENT


     // if the Event is not one of the ones we can handle, we should pass it along the chain-of-command to our super-class
	   return super.handleEvent(e);;
  }





    /**
     * Wrapper consuming Exceptions thrown by connection
     */
    public void sendMessage(Message msg){

        try{
            connection.sendMessage(msg);
        }
        catch(JMSException e){
            System.err.println("StationManager::sendMessage: message sending failed:"+e);
        }
    }

    public Message getMessage(){

        try{
           return connection.getMessage();
        }
        catch(JMSException e){
           System.err.println("StationManager::getMessage: get message failed:"+e);
        }
        return null;
    }

} 



