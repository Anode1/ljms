/**
 * @(#)Server.java
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

import java.io.DataInputStream;

import org.is.net.Servlet;
import org.is.server.MessageServlet;
import org.is.server.MessageContext;
import org.is.io.FileUtils;
import org.is.jms.MessageImpl;

import javax.jms.Message;
import javax.jms.JMSException;

/**
 * Main Server entry point. Deals with parameters, initialization and start
 * main server class (org.is.BroadcastingServer).
 *
 * @since   JDK1.0
 */
public class Server{

  public final static int DEFAULT_CLIENT_PORT=6285;
  public final static int DEFAULT_SERVER_PORT=6111;

  public static int cport=DEFAULT_CLIENT_PORT;
  public static int sport=DEFAULT_SERVER_PORT;

  public static void main(String args[]){

    boolean cportSetAlready=false;
    boolean sportSetAlready=false;
    boolean nameSetAlready=false;
    boolean logDirSetAlready=false;

    String logPath=null;
    String serverName=null;    

    for (int i = 0; i < args.length; i++) {

	    String arg = args[i];

      if (arg.equals("-h") || arg.equals("h") || arg.equals("-help") || arg.equals("help")) {
		    printUsage();
		    return;
	    }
      else if (arg.equals("-cp") || arg.equals("cp") || arg.equals("-cport") || arg.equals("cport")){
       if(cportSetAlready){
           System.err.println("You have set port for clients more than once!");
           return;
        }
        String temp=args[++i];
        try{
           cport=Integer.parseInt(temp.trim());
           cportSetAlready=true;
        }
        catch(NumberFormatException nfe){
           System.out.println(nfe+" in "+temp);
		       printUsage();
           return;
        }
	    }
      else if (arg.equals("-sp") || arg.equals("sp") || arg.equals("-sport") || arg.equals("sport")){
       if(sportSetAlready){
           System.err.println("You have set server port more than once!");
           return;
        }
        String temp=args[++i];
        try{
           sport=Integer.parseInt(temp.trim());
           sportSetAlready=true;
        }
        catch(NumberFormatException nfe){
           System.out.println(nfe+" in "+temp);
		       printUsage();
           return;
        }
	    }
      else if (arg.equals("-s") || arg.equals("s") || arg.equals("-server") || arg.equals("server")){

        String tempHost=args[++i];
        String tempPort1=args[++i];
       /*
        try{
           Integer.parseInt(tempPort1.trim());  //only check if it is integer

           anotherHosts.addElement(tempHost.trim());
           otherServerAccessPortsVector.addElement(tempPort1.trim());

        }
        catch(NumberFormatException nfe){
           System.out.println(nfe+" in "+tempPort1);
		       printUsage();
           return;
        }
        */
	    }
      else if (arg.equals("-n") || arg.equals("n") || arg.equals("-name") || arg.equals("name")){
       if(nameSetAlready){
           System.err.println("name of the server option passed more than once!");
           return;
        }

        serverName=args[++i];
        nameSetAlready=true;
        
	    }
      else if (arg.equals("-l") || arg.equals("l") || arg.equals("-logs") || arg.equals("logs")){
       if(logDirSetAlready){
           System.err.println("-l option passed more than once!");
           return;
        }
        logPath=args[++i];
        logDirSetAlready=true;
	    }
      else{
		    String msg = "Unknown arg: " + arg;
        System.err.println(msg);
		    printUsage();
		    return;
	    }
	  }//for



    //MessageContext doing nothing:
    MessageContext context=new MessageContext();

    try{
      MessageServlet servlet=new MessageServlet(context);
      servlet.startService(cport);

      //while true do the following:
      //read input from the console (stdin) as lines, wrap those lines into
      //messages to all connected clients
      DataInputStream dis=new DataInputStream(System.in);

      while(true){

         String line=dis.readLine().trim();
         processLine(context, line);
      }
    }
    catch(Exception e){
       e.printStackTrace();;
    }

  }//main

  private static void processLine(MessageContext context, String line){

     try{
         Message msg=new MessageImpl();

         String smallCase=line.toLowerCase();
         String value=null;

         if(smallCase.startsWith(BMessage.CLEAR)){
            msg.setStringProperty(BMessage.CMD, BMessage.CLEAR);
            int index=line.indexOf(" ");
            if(index!=-1)value=line.substring(index);
            else value="";

            msg.setStringProperty(BMessage.VAL, value);
         }
         else if(smallCase.startsWith(BMessage.FILE)){
            msg.setStringProperty(BMessage.CMD, BMessage.CLEAR);

            int index=line.indexOf(" ");
            if(index!=-1){
               String file=line.substring(index).trim();
               value=FileUtils.fileToString(file);
            }
            else value="";

            msg.setStringProperty(BMessage.VAL, value);
         }
         else if(smallCase.startsWith(BMessage.EVAL)){
            msg.setStringProperty(BMessage.CMD, BMessage.EVAL);
            int index=line.indexOf(" ");
            if(index!=-1)value=line.substring(index);
            else value="";

            msg.setStringProperty(BMessage.VAL, value);
         }
         else{ //add
            msg.setStringProperty(BMessage.CMD, BMessage.ADD);
            if(smallCase.startsWith(BMessage.ADD)){
              int index=line.indexOf(" ");
              if(index!=-1)value=line.substring(index);
              else value="";

              msg.setStringProperty(BMessage.VAL, value);
            }
            else{   //any string typed
              msg.setStringProperty(BMessage.VAL, new String(line));
            }

         }

         context.real_broadcast(msg);
     }
     catch(Exception e){
         System.err.println("Error processing line:"+e);
     }
  }


  private static void printUsage(){

    System.err.println("usage: Server (options) ");
    System.err.println("  options:");
    System.err.println("  -h, h, -help, help               This help screen");
    System.err.println("  -n, n, name, -name               Name of this server");
    System.err.println("  -l, l, logs, -logs               Path to logs");
    System.err.println("  -cp, cp, -cport, cport           Main port for listening clients");
    System.err.println("                                   (default - "+DEFAULT_CLIENT_PORT+")");
    System.err.println("  -sp, sp, -sport, sport           Main port for other servers connections");
    System.err.println("                                   (default - "+DEFAULT_SERVER_PORT+")");
    System.err.println("EXAMPLE: if we run 3 servers on: 10.0.0.0 (on ports 1111/1112),");
    System.err.println(" 10.0.0.1 (on ports 1111/1112) and 10.0.0.2 (using ports 7771/7772)");
    System.err.println("1. run server instance on 10.0.0.0 by command:");
    System.err.println(">\"java -classpath server.jar Server -cp 1111 -sp 1112 -s 10.0.0.1 1112 -s 10.0.0.2 7772\"");
    System.err.println("2. run server instance on 10.0.0.1 by command:");
    System.err.println(">\"java -classpath server.jar Server -cp 1111 -sp 1112 -s 10.0.0.0 1112 -s 10.0.0.2 7772\"");
    System.err.println("3. run server instance on 10.0.0.2 by command:");
    System.err.println(">\"java -classpath server.jar Server -cp 7771 -sp 7772 -s 10.0.0.0 1112 -s 10.0.0.1 1112\"");
  }

}
