/** * @(#)LogFile.java
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
import java.io.File;import java.io.PrintWriter;import java.io.IOException;import java.io.FileWriter;/** * Writes one message to corresponding file. Opens and closes
 * the file not blocking it. <p>
 * This class been used by LogManager class and not supposed to be used when
 * distributed logging system is used.
 * <p>Supports rotating logs.
 *
 * @version 1.1 10/14/99
 * @since jdk1.0
 */
public class LogFile{

  protected int max_file_length=100000; //100k default threshold  protected final String directory;  protected final String originalFileName; // protected String absoluteFileName;  protected String absoluteFileName;  private PrintWriter pw;  private boolean disabled;  /**
   * Default constructor.
   */
  public LogFile(String directory, String originalFileName){

    this.directory=directory;
    this.originalFileName=originalFileName;
  }

  public void init(){

    absoluteFileName=directory+File.separator+getLastFile();
  }

  /**
   * Resets default threshold
   */
  public void setMaxLength(int max_file_length){

    this.max_file_length=max_file_length;
  }


 /**
  * Prints message to the file
  */
  public void println(String msg){

        if(disabled)System.out.println(msg);

        if(msg==null){
          msg="null";
        }

        try{
            File file=new File(absoluteFileName);
            if(file.exists()){
              if(!file.canWrite()){
                System.err.println("LogFile::can't write:next file created");
                nextFile();
              }
              else if(file.length() > max_file_length){ //not to have too big files
                nextFile();
              }
            }

            pw=new PrintWriter(new FileWriter(absoluteFileName, true));
            pw.print(msg);
            pw.close();
        }
        catch(IOException e){
          System.err.println("LogFile::unable to print message to file - LogFile disabled:"+e);
          disabled=true;
          println(msg);
        }
  }

  private boolean rotate=false;  //switching not used for now
  private int num=0;

  /**
   * Makes new name for file (when threshold for the current one has been exceeded)
   */
  private void nextFile(){

     String lastName=getLastFile();

     if(lastName.equals(originalFileName)){
        absoluteFileName=directory+File.separator+originalFileName+".0";
     }
     else{

        try{
          //num=Integer.parseInt(lastName.substring(originalFileName.length()+1));
          num++;
          if (num==5) {deleteFileSet(5); num=5; }
          if (num==10) {deleteFileSet(0); num=0; }
        }catch(Exception e){
        }
        absoluteFileName=directory+File.separator+originalFileName+"."+Integer.toString(num);
     }

  }

  /**
   * Returns most recently created file name (originalFileName.N with maximal N or
   * originalFileName if N is not an integer)
   */
  private String getLastFile(){

     String lastFileName=originalFileName;

     File directoryFile = new File(directory);

     int num=-1;
     String[] directoryList = directoryFile.list();

     for(int i = 0; i < directoryList.length; i++){
        if(!directoryList[i].startsWith(originalFileName))continue; //not this file (prefix)
        try{
            //find extension:
            String ext=directoryList[i].substring(originalFileName.length()+1);
            //System.out.println(ext);
            int temp=Integer.parseInt(ext);
            if(temp>num){
              num=temp;
              lastFileName=directoryList[i];
            }
        }
        catch(Exception e){
        }
     }
     //System.out.println("lastFileName:"+lastFileName);
     return lastFileName;
  }

 private String whatFile;
 private File aFile;

 private void deleteFileSet(int startNum) {

    for(int i = startNum; i < startNum+5; i++){
        whatFile=directory+File.separator+originalFileName+"."+Integer.toString(i);
        aFile=new File(whatFile);
        try{
            aFile.delete();
        }
        catch(Exception e){ System.err.println("LogFile::unable to delete file:"+whatFile+" because of "+e);}
     }
 }


}


