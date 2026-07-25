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
import java.io.IOException;
import java.util.Vector;

import org.is.logmanager.*;
import org.is.io.FileUtils;
import org.is.io.FileProps;
import org.is.util.Utils;

import org.is.server.MessageServlet;
/**
 *
 * Main Server entry point. Deals with parameters, initialization and start
 * main server class (org.is.BroadcastingServer).
 *
 * @since   JDK1.0
 */
public class Server{// extends ThreadGroup{

  public static int DEFAULT_CLIENT_PORT=6111;
  public static int DEFAULT_SERVER_PORT=6112;

  private static boolean log_disabled;//=true;

  public static int sport=DEFAULT_SERVER_PORT; //will be changed if passed as a parameter

  private Server(){
 //   super("Server");
  }

  /**
   * Main entry point
   */
  public static void main(String args[]){


  LogManager log=null;

  try{
    int cport=DEFAULT_CLIENT_PORT; //client accept port of this server

    String serverName=null;

    Vector anotherHosts=new Vector();  //temp storage for hosts
    Vector otherServerAccessPortsVector=new Vector();  //temp storage for ports

    boolean cportSetAlready=false;
    boolean sportSetAlready=false;
    boolean nameSetAlready=false;
    boolean logDirSetAlready=false;

    String logPath=null;

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

    //SERVER NAME
    if(serverName==null){   //if a server name is not passed - generate it
      serverName=Integer.toString(cport);
    }

    //LOG MANAGER STUFF
    if(logPath==null)logPath=System.getProperty("user.dir");
    else{
        if(!FileUtils.dirIsWritable(logPath)){     //then try to open user.dir

          logPath=System.getProperty("user.dir");
        }
    }

    log=LogManager.createInstance(logPath,"logs"+serverName);

    //load properties:
    FileProps props=null;
    String path=logPath+java.io.File.separator+"server.conf";
    try{
      props=FileProps.getInstance().init(path);

      String debugLevelString=props.getProperty("debug_level");
      int dl=DebugLevel.fromString(debugLevelString.trim());
      log.setDebugLevel(dl);
      log.printDebug("Current debug level being set:"+debugLevelString);
    }
    catch(Exception e){
      log.setDebugLevel(DebugLevel.DEBUG_BRIEF);
      log.err("Server::can't load properties from:"+e);
    }


    if(log_disabled)log.setDisabled(true);

    log.printDebug("===============================================\n\r["+
                    new java.util.Date()+"]:  "+serverName+" started listening "+cport+"/"+sport);

    //INITIALIZE OTHER SERVERS ARRAYS:
    /*
    int howManyOtherHosts=anotherHosts.size();
    OtherServersInf otherServersInf=null;

    if(howManyOtherHosts!=0){      //if there are other hosts
       otherServersInf=new OtherServersInf(howManyOtherHosts);
       for(int i=0;i<howManyOtherHosts;i++){
         otherServersInf.otherServerHosts[i]=(String)anotherHosts.elementAt(i);
         try{
            otherServersInf.otherServerPorts[i]=Integer.parseInt((String)otherServerAccessPortsVector.elementAt(i));
         }catch(NumberFormatException nfe){
            System.err.println("Server::main: not correct port!");
            System.exit(1);
         }
       }
       anotherHosts=null;   //to GC
       otherServerAccessPortsVector=null; //to GC

       //print debug information about other servers passed as parameters
       StringBuffer sb=new StringBuffer("\r\n---------------------------------------------");
       sb.append("\r\nOther servers assumed to be run:{");
       if(log.debugLevel(DebugLevel.DEBUG_SERVER_CONNECTIONS_STATUS))log.printDebug("Server::main:"+otherServersInf.toString());
    }

    //Server relay feature has been switched off

    BroadcastingServer server=new BroadcastingServer(otherServersInf);
    server.start(cport, sport);
    */
    (new MessageServlet(new GameRouter())).startService(cport);

  }catch(Exception e){
    log.printError("Server::main:"+Utils.stack2String(e));
  }

  }//main

  public void uncaughtException(Thread t, Throwable e){
     System.err.println("Server::uncaughtException:"+e);
  }

  private static void printUsage(){

        System.err.println("usage: org.is.server.Server (options) ");
        System.err.println("  options:");
        System.err.println("  -h, h, -help, help               This help screen");
        System.err.println("  -n, n, name, -name               Name of this server");
        System.err.println("  -l, l, logs, -logs               Path to logs");
        System.err.println("  -cp, cp, -cport, cport           Main port for listening clients");
        System.err.println("                                   (default - "+DEFAULT_CLIENT_PORT+")");
        System.err.println("  -sp, sp, -sport, sport           Main port for other servers connections");
        System.err.println("                                   (default - "+DEFAULT_SERVER_PORT+")");
        System.err.println("EXAMPLE: if we run 3 servers on: 205.189.243.130 (on ports 1111/1112),");
        System.err.println(" 205.189.243.131 (on ports 1111/1112) and 205.189.243.132 (using ports 7771/7772)");
        System.err.println("1. run server instance on 205.189.243.130 by command:");
        System.err.println(">\"java -classpath server.jar org.is.server.Server -cp 1111 -sp 1112 -s 205.189.243.131 1112 -s 205.189.243.132 7772\"");
        System.err.println("2. run server instance on 205.189.243.131 by command:");
        System.err.println(">\"java -classpath server.jar org.is.server.Server -cp 1111 -sp 1112 -s 205.189.243.130 1112 -s 205.189.243.132 7772\"");
        System.err.println("3. run server instance on 205.189.243.132 by command:");
        System.err.println(">\"java -classpath server.jar com.gavr.server.Server -cp 7771 -sp 7772 -s 205.189.243.130 1112 -s 205.189.243.131 1112\"");
  }

}
