/**
 * @(#)LogAnalizer.java
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
package org.is.logmanager;

import java.io.*;
import java.util.*;

/**
 * Auxiliary class for parsing and analizing of events.log files
 * (users count etc) - making some kind of statistics.
 * This file is rewritten for a particular context and left here just
 * for reference if more general (and beautiful) class will be needed
 *
 * @since jdk1.0
 */
public class LogAnalizer{

  String fileName;

  public LogAnalizer(String filename) {
    fileName= filename;
  }

  public void analize()throws IOException {

    System.out.println("Analizing:"+fileName);
    DataInputStream dis=new DataInputStream(new FileInputStream(fileName));

    int lineNum=0;
    int max=0;
    String text="";
    String line=dis.readLine();
    while(line != null) {
       int temp=analizeLine(lineNum,line);
       if(temp>max)max=temp;
       line=dis.readLine();
       lineNum++;
    }

    System.out.println("----------------------");
    System.out.println("MAX:"+max);
    dis.close();
  }

  protected int analizeLine(int lineNum,String line){


    if(!line.startsWith("Clients threads"))return 0;

    int num=0;
    StringTokenizer st1=new StringTokenizer(line, "/");
    while(st1.hasMoreTokens()){
       num++;
       st1.nextToken();
    }

    System.out.print("Line "+lineNum+":"+Integer.toString(num)+";");
    return num;
  }

  public static void main(String[] args) {

      try{

        //args=new String{"stdout0.log","stdout1.log","stdout2.log","stdout3.log","stdout4.log","stdout5.log"};

        for(int i=0;i<args.length;i++){
           System.out.println(args[i]);
        }

        LogAnalizer count=new LogAnalizer("c:\\tmp\\stdout7.log");
        count.analize();

        Thread.sleep(50000);
        //LogManager.err();
      }
      catch(Exception e){
        e.printStackTrace();
      }
  }

}
