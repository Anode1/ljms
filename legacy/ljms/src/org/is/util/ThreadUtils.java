/**
 * @(#)ThreadUtils.java
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
package org.is.util;

import java.io.PrintWriter;

/**
 *
 * Thread utilities class - simple set of useful static funcions used by
 * multiple classes
 *
 * @since jdk1.0
 */
public class ThreadUtils{

  /**
   * Destroys all child ThreadGroups, killing all child Threads and their
   * ThreadGroups' threads before their (ThreadGroups) destroying.
   */
  public static void destroyAllChildThreadGroups(){

     ThreadGroup myGrp = Thread.currentThread().getThreadGroup();

     //firstly kill all child threads (excluding me):
     killAllChildThreads(myGrp);

     //destroy child thread groups:
     ThreadGroup subs[] = new ThreadGroup[myGrp.activeGroupCount()];

	   int n = myGrp.enumerate(subs, false);

	   for (int i=0; i<n; i++){
        ThreadGroup tg=subs[i];
        try{
          tg.checkAccess();
          tg.destroy();
          //System.out.println("ThreadGroup:" + tg.getName()+" destroyed");
        }catch (SecurityException e) {
          System.out.println("No permission to destroy threadGroup:" + tg.getName());
        }catch (IllegalThreadStateException ise){
          System.out.println("Can't destroy threadGroup:" + tg.getName()+":"+ise);
        }
     }

  }

  /**
   * Kills all child Threads in all child ThreadGroup of given ThreadGroup
   * including this ThreadGroup leaving all ThreadGroups undestroyed
   */
  public static void killAllChildThreads(ThreadGroup rootGrp){

    //kill all threads:
    Thread threads[] = new Thread[rootGrp.activeCount()];
    Thread me=Thread.currentThread();
    int n = rootGrp.enumerate(threads, false);

    for (int i=0; i<n; i++) {
       Thread t = threads[i];
       if(t==me){
          //System.out.println("skipped myself");
          continue; // don't commit suicide
       }
       if(!t.isDaemon()){
          try{
            t.checkAccess();
            //was t.stop(): unsafe when written, and it throws
            //UnsupportedOperationException on modern JVMs
            t.interrupt();
            try{t.join(3000);}catch(InterruptedException ie){}
            System.out.println("Thread: " + t.getName()+" is not daemon - was destroyed");
          }catch (SecurityException e) {
            System.out.println("No permission to stop thread: " + t.getName());
          }
       }
    }

    //recursively go into ThreadGroups:
    ThreadGroup subs[] = new ThreadGroup[rootGrp.activeGroupCount()];
	  n = rootGrp.enumerate(subs, false);
	  for (int i=0; i<n; i++){
      ThreadGroup tg=subs[i];
	    killAllChildThreads(tg);  //recursively
    }
	}

  /**
   * Just another name for threadInfo()
   */
  public static String dumpThreads(){

    return threadInfo();
  }

  public static String threadInfo(){

      StringBuffer sb=new StringBuffer("Threads under current thread:");
      dumpThreadGroup("", Thread.currentThread().getThreadGroup(), sb);
      return sb.toString();
  }

  public static String threadInfoFromRoot() {

      ThreadGroup tg = Thread.currentThread().getThreadGroup();
      while(true){
	      ThreadGroup tg2 = tg.getParent();
	      if (tg2 == null) break;
        tg = tg2;
      }
      StringBuffer sb=new StringBuffer("All threads:");
      dumpThreadGroup("", tg, sb);
      return sb.toString();
  }

  public static String CR=System.getProperty("line.separator");

  public static void dumpThreadGroup(String prefix, ThreadGroup tg, StringBuffer sb) {

    sb.append(CR + prefix);
	  //sb.append("Thread group " + tg + ", isDaemon = " + tg.isDaemon() + ":");

	  int nSubsEst = tg.activeGroupCount();
    ThreadGroup subs[] = new ThreadGroup[nSubsEst * 2];
	  int nSubsAct = tg.enumerate(subs, false);

    sb.append(CR + prefix);
	  //sb.append("  Subgroups: est=" + nSubsEst + ", act=" + nSubsAct);

	  for (int i=0; i<nSubsAct; ++i){
	    dumpThreadGroup(prefix + "  ", subs[i], sb);
    }
	  int nThreadsEst = tg.activeCount();
    Thread threads[] = new Thread[nThreadsEst * 2];
	  int nThreadsAct = tg.enumerate(threads, false);
	  //sb.append(CR + prefix + "  Threads: est=" + nThreadsEst + ", act=" + nThreadsAct);
	  for (int i=0; i<nThreadsAct; ++i){
	    sb.append(CR + dumpThread(prefix + "    ", threads[i]));
    }
  }

  public static String dumpThread(String prefix, Thread t) {

    StringBuffer sb=new StringBuffer(prefix);
    sb.append(t);
    //sb.append(", isDaemon = "); sb.append(t.isDaemon());
    if(!t.isAlive())sb.append("-dead");
    //String name=t.getName();
    //sb.append(", name = "); sb.append((name!=null?name:""));

	  return sb.toString();
  }

  /**
   * Testing entry point
   */
  public static void main(String args[]) {

    ThreadUtils instance=new ThreadUtils();
    instance.makeThreads();
    //System.out.println("Before:"+threadInfoFromRoot());
   // destroyAllChildThreadGroups();
  /*  try{
      Thread.sleep(5000);
    }
    catch(InterruptedException e){}
  */
    //System.out.println(threadInfoFromRoot());
    System.out.println(threadInfo());
  }

  /**
   * Testing case
   */
  public void makeThreads() {

      Thread t0=(new Thread("AAA"){
         public void run(){
         }
      });
      t0.setDaemon(true); t0.setPriority(1);
      t0.start();

      Thread t=(new Thread(){
         public void run(){
           while(true){
              try{Thread.sleep(100);}catch(InterruptedException ie){}
           }
         }
      });
      t.setDaemon(false);
      t.start();

      Thread t2=(new Thread("CCC"){
         public void run(){
           while(true){
              try{Thread.sleep(100);}catch(InterruptedException ie){}
           }
         }
      });
      t2.setDaemon(true);
      t2.start();
  }


}
