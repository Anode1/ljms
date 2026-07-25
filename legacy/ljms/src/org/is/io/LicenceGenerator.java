/**
 * @(#)LicenceGenerator.java
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
package org.is.io;

import java.io.*;
import java.util.*;

/**
 * Filter writing licence at the begining of all src files
 *
 * @since jdk1.0
 */
public class LicenceGenerator{

  static String licence;

  public LicenceGenerator(){

     StringWriter sw=new StringWriter();
     PrintWriter pw=new PrintWriter(sw);
     pw.println(" * Copyright (C) 2001 Vasili Gavrilov");
     pw.println(" *");
     pw.println(" * This program is free software; you can redistribute it and/or");
     pw.println(" * modify it under the terms of the GNU General Public License");
     pw.println(" * as published by the Free Software Foundation; either version 2");
     pw.println(" * of the License, or any later version.");
     pw.println(" *");
     pw.println(" * This program is distributed in the hope that it will be useful,");
     pw.println(" * but WITHOUT ANY WARRANTY; without even the implied warranty of");
     pw.println(" * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the");
     pw.println(" * GNU General Public License for more details.");
     pw.println(" *");
     pw.println(" * You should have received a copy of the GNU General Public License");
     pw.println(" * along with this program; if not, write to the Free Software");
     pw.println(" * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.");
     pw.close();
     licence=sw.toString();
  }

  /**
   * Process directory
   */
  public void processDir(String directoryName){

        File directoryFile = new File(directoryName);
        if(!directoryFile.exists() || !directoryFile.canRead()){
          return;
        }

        int entries = directoryFile.list().length;

        if (entries != 0){

            String [] files = new String [entries];
            files = directoryFile.list();

            File file=null;

            for (int i = 0; i < entries; i++){
                String temp=directoryName + File.separator + files[i];
                file = new File(temp);
                if (file.isDirectory()){
                  processDir(temp);
                }
                else{
                  processFile(file);
                }
            }
        }
  }

  /**
   * Process file
   */
  public void processFile(File file){

    try{
      String fileName=file.getAbsolutePath();
      System.out.println("Converting:"+fileName);
    
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
    catch(Exception e){
      e.printStackTrace();
    }
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

      String dirPath;
      try{

        if(args.length>0)dirPath=args[0];
        else{
           System.err.println("Usage: java org.is.io.LicenceGenerator <dirPath>");
           return;
        }

        LicenceGenerator g=new LicenceGenerator();
        g.processDir(args[1]);

        Thread.sleep(50000);
      }
      catch(Exception e){
        e.printStackTrace();
      }
  }

} 
