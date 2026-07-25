/** * @(#)LogManager.java
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

import java.io.ByteArrayOutputStream;import java.io.PrintWriter;import java.io.PrintStream;import java.io.FileOutputStream;/** * Log manager supporting 3 types of logs (3 chanels). Singleton class.<p>
 * Creates LogManagerWriterThread if not disabled sending messages to it not
 * wasting time waiting (has MessageQueue inside and runs with lower priority.
 * Default behaviour is distributed logging (sending of LogMessages to another
 * machine). If it fails - local logging will be switched on (threaded).
 * LogManager switches to disable state (and outputs everything into default
 * console only in one case: if it can't open log file, (for example - directory
 * is not accessible by the user or file exists created by another user). <p>
 * <p>Look main method (application entry point) for trivial example of usage
 *
 * This LogManager was developed before jms, so it uses not JMS compliant
 * messages. It would be fun to migrate to our lightweight messages for LogManager
 * as well to have more flexibility and to use existing transport for remote
 * LogMessages delivery.
 *
 * @see LogManagerBase
 * @see DebugLevel
 * @see MessageWriter
 * @see LogFile
 * @see LogManagerWriterThread
 * @version 4.0 09/06/99
 * @since jdk1.0
 */
public class LogManager extends LogManagerBase{

    /**
     * Flag indicating that the output goes to the default console rather
     * than to file or another machine as LogMessages
     */
    protected boolean disabled;

    /**
     * Specifies whether to spawn a separate thread for logging. Highly recommended
     * to leave this true not to wait in the main thread when log files will be opened
     * log printed and files be closed
     */
    protected boolean runDaemon=true;

    /**
     * Instance of this class - singleton. Optional and not supposed to be used
     * if more than one LogManager exists in the VM.
     */
    protected static LogManager instance;  //instance of this class - optional

    /**
     * Thread making message printing/sending in the background (not blocking
     * the main application (user) thread)
     */
    private Worker worker;

    /**
     * Message writer opening files, printing messages and closing files.
     * It gets LogManagerMessages with numbers and knows to which files
     * to write the message depending on this number
     * if daemon=true then this messageWriter is not used - worker has its own messageWriter
     */
    private MessageWriter messageWriter;

    //refs for convenience:
    private String home;
    private String log_dir;

    //not used now
    private PrintStream errPS, outPS;
    private boolean redirectStandard=false;

    /**
     * Default constructor used internally
     */
    private LogManager(){
    }

    /**
     * Returns the single instance of this singleton
     */
    public static LogManager getInstance(){

      if(instance==null){
        createInstance();
      }
      return instance;
    }

    /**
     * Creates an instance of LogManager which will put logs into default
     * user directory, creating logs directory inside it
     */
    public static LogManager createInstance(){

      return createInstance(System.getProperty("user.dir"));
    }

    /**
     * Creates an instance of this class which will put logs into path
     * creating logs directory inside it
     */
    public static LogManager createInstance(String path){

      return createInstance(path,"logs");
    }

    /**
     * Main create method: both path of log directory and its name should be
     * passed.
     * <p>
     * Application should create an instance explicitly using
     * <tt>createInstance()</tt> method (otherwise - log directory will be
     * created in user.dir) like this:
     * <pre>
     *     LogManager.createInstance("/var/log","foo_logs");
     * </pre>
     *
     * @param path the path where new log directory will be created
     * @param log_dir name of newly created log directory
     * @return the LogManager instance
     */
    public static synchronized LogManager createInstance(String path, String log_dir){

      if(instance!=null){   //already exists?
         //return;
         instance.stopDaemon();
      }

      instance=new LogManager();
      instance.init(path, log_dir);

      return instance;
    }

    /**
     * Initialization method - used only internally.
     * User uses createInstance() with appropriate arguments.
     */
    private void init(String home, String log_dir){

       this.home=home;
       this.log_dir=log_dir;

       if(disabled){
          return;
       }

       try{
          messageWriter=new MessageWriter(home, log_dir);
          messageWriter.init();

          if(runDaemon){
            worker=new Worker();
            worker.addLogListener(messageWriter);
            worker.start();
          }

       }
       catch(IllegalAccessException e){ //failure opening files
          System.err.println("Can't create MessageWriter - LogManager disabled");
          disabled=true;
          return;
       }

       if(redirectStandard){
          redirectStandard();
       }
    }

    /**
     * Optional and currently not used
     */
    private void redirectStandard(){

       try{
          String logDirectory=messageWriter.getDirectory();

          System.setErr(errPS=new PrintStream(new FileOutputStream(logDirectory+java.io.File.separator+"err_log"),true));
          System.setOut(outPS=new PrintStream(new FileOutputStream(logDirectory+java.io.File.separator+"out_log"),true));

       }
       catch(Exception e){
          instance.err("Failed to make System.out or System.err redirection:"+e);
       }
    }

    /**
     * Sets default console output
     */
    public void setDisabled(boolean disabled){

      this.disabled=disabled;

      if(!disabled){
        init(home, log_dir);
      }
    }

    /**
     * True if this LogManager is enabled
     */
    public boolean getDisabled(){

      return disabled;
    }

    /**
     * True if System.err and System.out have been redirected as well
     */
    public boolean isRedirectStandard(){

      return redirectStandard;
    }

   /**
    * Prints a message into event log file
    */
    public void printEvent(String message){

        StringBuffer sb=getPrefix();
        sb.append(message);
        sb.append(this.CR);

        if (disabled){
          System.out.print(sb.toString());
          return;
        }

        LogManagerMessage msg=new LogManagerMessage(0, sb.toString());
        if(runDaemon){
          worker.send(msg);
        }
        else
          messageWriter.onMessage(msg);
    }

    public void printError(String message){

       printError(message, true);
    }

   /**
    * Prints a message into error log file
    */
    public void printError(String message, boolean sendEMail){

        StringBuffer sb=getPrefix();
        sb.append(message);
        sb.append(this.CR);

        if(disabled){
          System.out.print(sb.toString());
          return;
        }

        LogManagerMessage msg=new LogManagerMessage(1, sb.toString());

        if(runDaemon){
          worker.send(msg);
        }
        else
          messageWriter.onMessage(msg);

       // if(sendEMail)sendByMail(message);          
    }

   /**
    * Convenience method: the same as printError(message)
    */
    public static void err(String message){

       if(instance==null){
         createInstance();
       }
       instance.printError(message);
    }

    public void printError(Exception e){

      printError(e, true);
    }

/**
 * Prints an exception into error log file
 */
    public void printError(Exception e, boolean sendEMail){

        String message=null;
        if(printStackTrace)message=stack2String(e);
        else message=e.toString();
        printError(message, sendEMail);
    }

    /**
     * Convenience method: the same as printError(e)
     */
    public static void err(Exception e){

       if(instance==null){
         createInstance();
       }
       instance.printError(e);
    }

    //DEBUG methods:

    /**
     * Prints a message into debug log file
     */
    public void printDebug(String message){

        StringBuffer sb=new StringBuffer(message);
        sb.append(this.CR);

        if (disabled) {
          System.out.print(sb.toString());
          return;
        }

        LogManagerMessage msg=new LogManagerMessage(2, sb.toString());

        if(runDaemon){
          worker.send(msg);
        }
        else{
          messageWriter.onMessage(msg);
        }
    }

    /**
     * Convenience method: the same as printDebug(message)
     */
    public static void out(String message){

       if(instance==null){
         createInstance();
       }
       instance.printDebug(message);
    }

    /**
     * Prints an exception into debug log file
     */
    public void printDebug(Exception e){

        StringBuffer sb=getPrefix();

        if(printStackTrace)sb.append(e.toString());
        else sb.append(stack2String(e));

        sb.append(this.CR);

        if(disabled){ System.out.print(sb.toString()); return; }

        LogManagerMessage msg=new LogManagerMessage(2, sb.toString());

        if(runDaemon){
          worker.send(msg);
        }
        else{
          messageWriter.onMessage(msg);
        }
    }

    /**
     * Convenience method: the same as printDebug(e)
     */
    public static void out(Exception e){

      if(instance==null){
        createInstance();
      }

      instance.printDebug(e);
    }

    /**
     * Returns true if we are in appropriate debug level and will print messages
     */
    public boolean debugLevel(int level){

       if ((level & debugLevel) == 0) return false;
       return true;
    }

   /**    * Lazy initialization static convenience analog of debugLevel    */   public static boolean dLevel(int level){
      if(instance==null){
          createInstance();
      }

      return instance.debugLevel(level);
   }
   /**    * Just for sure - never hurts    */    public void finalize(){      stopDaemon();    }    /**     * Stops worker thread     */    public synchronized void stopDaemon(){
    
      if(worker!=null){
        worker.stop();
        worker=null;
      }
    }

  /**
   * Extracts stackTrace from the Exception and transforms it into String.
   * Used in LogManager
   */
  public static String stack2String(Exception e){

     ByteArrayOutputStream baos=new ByteArrayOutputStream();
     PrintWriter pw=new PrintWriter(baos,true);
     e.printStackTrace(pw);
     return baos.toString();
  }

  /*
  private void sendByMail(String message){

     Config config=Config.getInstanceAsIs();
     if(config==null)return; //Config not initialized yet

     try{
        String mailTo=config.getHostProperty("mail.send_errors_to");
        String mailFrom=config.getHostProperty("mail.send_errors_from");
        String subject=config.getHostProperty("mail.subject");
        if(subject==null)subject="PDF service error";
        new MailHelper().send(mailTo, mailFrom, subject, message);
     }
     catch(Exception e){
        LogManager.err("Main::sendByMail:"+e);
     }
  }
  */

    /**
     * For testing purposes only
     */
    public static void main(String[] args) {

      try{
        //LogManager log=LogManager.createInstance("/gummo2/export/home/vgavrilov");
        //LogManager log=LogManager.createInstance("c:\\tmp","vm2_logs");
        LogManager log=LogManager.createInstance(System.getProperty("user.dir"),"test_logs");
        //log.setDisabled(false);
        log.err("bbb");
        log.out("ccc");

        if(log.debugLevel(LogManager.RESERVED_LEVEL_11))LogManager.out("eee");

        //System.out.println("standard output");
        //System.err.println("standard error output");

        log.printDebug("Dummy debug message (default debug level)");
        log.printEvent("Dummy event");
        log.printError(new NullPointerException("Dummy null pointer exception"));

        Thread.sleep(50000);
        //LogManager.err();
      }
      catch(Exception e){
        e.printStackTrace();
      }
    }

}

