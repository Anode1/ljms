/** * @(#)MessageWriter.java
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
 */package org.is.logmanager;

import java.io.File;
/** * Writes messages to corresponding files.
 * It is a demultiplexor for messages.
 * Supports rotating logs. This class is used by LogManagerWriter thread
 * or if threaded behaviour is switched off - directly by LogManager
 *
 * @see LogManager
 * @see LogManagerBase
 * @see LogFile
 * @see LogManagerWriterThread
 * @version 1.1 10/14/99
 * @since jdk1.0
 */
public class MessageWriter implements LogListener{

  //LogFile objects contains such functionality as Rotating logs etc
  protected LogFile eventFile;  protected LogFile errorFile;
  protected LogFile debugFile;

  //base names for log files (not incuding incrementing number):
  public static String eventFileName="events.log";  public static String errorFileName="stderr.log";
  public static String debugFileName="stdout.log";

  private String home;
  private String log_dir;
  private String directory;

  /**
   * Default constructor. Log files will be put into directory log_dir.
   * The last will be created in user default directory if home is null
   */
  public MessageWriter(String home, String log_dir){

    this.home=home;
    this.log_dir=log_dir;
  }

  /**
   * Throws exception if
   */
  public synchronized void init() throws IllegalAccessException{

    if(home==null){          //create anyway in user.dir
        home=System.getProperty("user.dir");
        System.err.println("MessageWriter: null home was passed. logs were put into: "+home);
    }

    if(log_dir==null){       //create "log" if name for log dir is not passed
        log_dir="logs";
    }

    String path=home + File.separator + log_dir;

    //containing directory first:
    File dirPath=new File(path);
    
    if(!dirPath.exists()){

          //try to create containing directory:
          if(!dirPath.mkdirs()){ //if can't create directory (write access is prohibited)
              throw new IllegalAccessException("Can't create log directory:"+path+" -message writer disabled");
          }
          //System.out.println("MessageWriter:created");
    }
    else{//exists
          if(!dirPath.canRead() || !dirPath.canWrite()){ //can't read and write
              throw new IllegalAccessException("Can't read/write into log directory:"+path+" -message writer disabled");
          }
    }

    directory=home + File.separator + log_dir;

    eventFile=new LogFile(directory, eventFileName); eventFile.init();
    errorFile=new LogFile(directory, errorFileName); errorFile.init();
    debugFile=new LogFile(directory, debugFileName); debugFile.init();

  }

  public String getDirectory(){

    return directory;
  }

/**
 * Prints message passed to the file specified by filePath
 */
  public void onMessage(LogManagerMessage msg){

        int channel;
        String message=null;

        if(msg==null){
          channel=1;
          message="MessageWriter::printMessage: Message is null!";
        }
        else{
          channel=msg.getChanel();
          message=msg.getMessage();
          if(message==null)message="null";
        }

        if(channel==0){
          eventFile.println(message);
        }
        else if(channel==1){
          errorFile.println(message);
        }
        else if(channel==2){
          debugFile.println(message);
        }
        else{
          System.err.println("MessageWriter: unknown channel:"+channel+" - redirected to stdout ");
          System.out.println(message);
          return;
        }

   }


}


