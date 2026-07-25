/**
 * @(#)FileUtils.java
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

import java.util.Vector;
import java.io.*;
import java.net.URL;

/**
 * File specific utilities - set of useful static funcions which are used by
 * multiple classes from different packages
 *
 * This file is a copy of org.common.util.Utils and org.is.pdf.util.Utils
 * - probably it's better to use only one instance of it (org.common.util.Util)
 *
 * @version 1.2 04/10/99
 * @since jdk1.0 
 */
public class FileUtils{

 /**
  * Put InputStream to file (creates or overwrites the file).
  * Not buffered - use BufferedOutputStream!
  */
  public static void stream2File(InputStream is, String fileName)throws IOException{

     FileOutputStream fos=null;
     try{
         fos = new FileOutputStream(fileName);
         int read = 0;
         while ((read = is.read()) != -1){
            fos.write((byte)read);
         }
     }finally{
         if(fos!=null)try{fos.close();}catch(IOException e){}
     }
  }

 /**
  * Takes a file path and returns all it's contents as one String
  */
  public static String fileToString(String file) throws IOException{

     return getFileContents(file).toString();
  }

 /**
  * Takes a file path and returns all it's contents as a StringBuffer
  */
  public static StringBuffer getFileContents(String file) throws FileNotFoundException, IOException{

      BufferedReader fr=null;
      StringBuffer out=new StringBuffer();
      try{            //! try here different buffers
        fr = new BufferedReader(new FileReader(file));
        int read = 0;
        while ((read = fr.read()) != -1){
          out.append((char)read);
        }
      }
      finally{
        if(fr!=null)try{fr.close();fr=null;}catch(IOException e){}
      }
      return out;
  }

  public static String getTextResource(String location) throws Exception{

    BufferedInputStream bis=null;
    int BUFF_SIZE=16384;
    ByteArrayOutputStream bos=new ByteArrayOutputStream(BUFF_SIZE);

    try{
       if(location == null)throw new NullPointerException("FileUtils::getTextResource: null is passed!");

       InputStream is=FileUtils.class.getClassLoader().getResourceAsStream(location);
       bis=new BufferedInputStream(is, BUFF_SIZE);

       int read = 0;
       byte b[] = new byte[BUFF_SIZE];
       while ((read = bis.read(b, 0, b.length)) != -1){
         bos.write(b, 0, read);
       }

       bos.flush();
       return bos.toString();
    }
    catch(Exception e){
      throw new Exception("FileUtils::getTextResource: Can't open resource:"+location);

    }
    finally{
       if(bis!=null)try{bis.close();bis=null;}catch(IOException e){}
       if(bos!=null)try{bos.close();bos=null;}catch(IOException e){}
    }

  }

  public static String fileToString2(String fileName) throws IOException{

      FileInputStream fis = null;
      String str = null;
      try{
          fis = new FileInputStream (fileName);
          int size = fis.available ();
          byte[] bytes = new byte [size];
          fis.read (bytes);
          str = new String (bytes);
      }finally {
          try {fis.close ();} catch (IOException e2){}
      }
      return str;
  }

  public static String htmlFile2String(String path, String dest) throws FileNotFoundException, IOException{

		StringBuffer buf = getFileContents(path);
		int index;
		while ((index = indexOf(buf, (char) 13)) != -1) {

			if (index + 1 < buf.length()) {
				String tail = buf.toString().substring(index + 1);
				buf.setLength(index);
				buf.append(tail);
			}
			else {		// length == index + 1
				buf.setLength(index);
			}
		}

		while ((index = indexOf(buf, (char) 10)) != -1) {
			// now replace all the LF with a space
			buf.setCharAt(index, ' ');
		}

    String result=buf.toString();

    PrintWriter pw=new PrintWriter(new FileWriter(dest));
    pw.print(result);
    pw.flush();
    pw.close();

    return result;
  }

  public static void string2File(String str, String fileName) throws IOException{

        FileOutputStream fos = null;
        try{
            fos = new FileOutputStream (fileName);
            fos.write (str.getBytes ());
        }finally{
            try{fos.close ();}catch (IOException e){}
        }
  }

  public static void main(String args[]) {
   /*  if(args==null || args.length<1){
        System.err.println("pass path as a param");
        System.exit(1);
     }
     */
     try{
       // htmlFile2String(args[0]);
       htmlFile2String("C:\\JBuilder3\\myprojects\\editor\\src\\org\\is\\editor\\test.html",
                        "C:\\JBuilder3\\myprojects\\editor\\src\\org\\is\\editor\\test1.html");
     }
     catch(Exception e){
        e.printStackTrace();
     }
  }

  public static int indexOf(StringBuffer buff, char c){

	  for (int i = 0; i < buff.length(); i++) {
		  if (c == buff.charAt(i)) {
			  return i;
		  }
	  }
	  return -1;
  }

 /**
  * Deletes the file hierarchy recursively
  */
  public static boolean deleteDirectory(String directoryName){

        //System.out.println("Trying to delete:"+directoryName);
        File directoryFile = new File(directoryName);
        if(!directoryFile.exists() || !directoryFile.canRead()){
          //System.out.println("Can't delete directory: "+directoryName);
          return true;
        }

        int entries = directoryFile.list().length;

        if (entries != 0){

            String [] files = new String [entries];
            files = directoryFile.list();

            File fileName=null;

            for (int i = 0; i < entries; i++){
                String temp=directoryName + File.separator + files[i];
                fileName = new File(temp);
                if (fileName.isDirectory())
                    deleteDirectory(temp);
                fileName.delete();
            }
        }

        if(!directoryFile.delete()){
          System.out.println("Can't clean a directory: "+directoryName+" -has been used by another program");
          return false;
        }
        return true;
    }

  /**
   * Moves a file. Works fast but only under Win. Look move for system
   * independant move
   */
  public static boolean moveNative(String from, String to){

      //System.out.println("move called from:"+from+" to:"+to);
      boolean success=false;
      try{
          File old=new File(from);
          File newF=new File(to);
          old.renameTo(newF);
          old.delete();
          success=true;
      }
      catch(Exception e){
          System.out.println("Utils: Error in moving files");
      }
      return success;
  }

  /**
   * Moves physical file
   */
  public static boolean move(String from, String to){

      //System.out.println("move called from:"+from+" to:"+to);
      boolean success=false;
      try{
          File old=new File(from);
          File newF=new File(to);
          success=renameTo(old,newF);
      }
      catch(Exception e){
          System.out.println("Utils: Error in moving files");
      }
      return success;
  }

 /**
  * The same as renameTo
  */
  public static boolean move(File oldfile, File newfile){

     return renameTo(oldfile, newfile);
  }

 /**
  * Physically renames a file (copies it, deleting the source)
  */
  public static boolean renameTo(File oldfile, File newfile) {

      boolean success=false;

      BufferedInputStream fis=null;
      FileOutputStream fos=null;

      try {
           fis = new BufferedInputStream(new FileInputStream(oldfile));
           fos = new FileOutputStream(newfile);
           int read = 0;
           byte b[] = new byte[100000];
           while ((read = fis.read(b, 0, b.length)) != -1){
                fos.write(b, 0, read);
                fos.flush();
           }

           fos.close(); fis.close();
           fis = null; fos=null;
           oldfile.delete();
           return true;
      }
      catch (Exception ex) {
          if (fos != null) {
              try { fos.close(); fos = null;
              }
              catch (IOException ex2) { /* ignore */ }
                  newfile.delete();
              }
              return false;
      }
      finally {
           try {
                if (fos != null) fos.close();
                if (fis != null) fis.close();
           }
           catch (IOException ex) { /* ignore */ }
      }
 }


 /**
  * Returns Vector of paths for all files for directory passed
  */
  public static Vector getFiles(String directoryPath){

     Vector filePaths=null;
     File directoryFile = new File(directoryPath);
     if(!directoryFile.exists() || !directoryFile.canRead() || !directoryFile.isDirectory()){
        return null; //nothing to do
     }

     int n = directoryFile.list().length;
     String [] directoryList;
     File aFile;

     if (n != 0){
                filePaths=new Vector();
                directoryList = new String[n];
                directoryList = directoryFile.list();

                for(int i = 0; i < n; i++){
                    String aFileName=directoryPath + File.separator + directoryList[i];
                    aFile = new File(aFileName);

                    if(!aFile.isDirectory() && aFile.canWrite()){
                         filePaths.addElement(aFileName);
                    }
                }
      }
      return filePaths;
  }

 /**
  * Returns file's extension. File may be presented both as path and or a filename
  */
  public static String getExtension(String path){

      if(path==null)return null;
      String filename=getFilename(path);
      int i=filename.lastIndexOf(".");
      if(i==-1){
         return "";
      }
      else{
         return filename.substring(i + 1);
      }
  }

 /**
  * Returns filename or the whole path without extension if exists
  */
  public static String getWithoutExtension(String path){

      if(path==null)return null;
      String filename=getFilename(path);
      int i=filename.lastIndexOf(".");
      if(i==-1){
         return path;
      }
      else{
         return path.substring(0, path.lastIndexOf("."));
      }
  }

 /**
  * Extracts the filename from the path
  */
  public static String getFilename(String path){

      if(path==null)return null;
      int i=path.lastIndexOf(File.separator);
      if(i==-1){
         return path;
      }
      else{
         return path.substring(i + 1);
      }
  }

 /**
  * Extracts directory name (path without filename) from the whole path.
  * If only filename been passed - returns "".
  * Warkaround for Solaris JDK1.2beta3 bug in File.getParent()
  */
  public static String getDirectory(String path){

      if(path==null)return null;
      int i=path.lastIndexOf(File.separator);
      if(i==-1){
         return "";
      }
      else{
         return path.substring(0, i);
      }
  }

 /**
  * Removes last slash in path.
  * Warkaround for Solaris JDK1.2beta bug: File.getParent() is not working)
  */
  public static String removeLastSlash(String path){

     return path.substring(0, path.length()-1);
  }

 /**
  * Create parent directories for the path. Does not deal with root directory
  */
  public static void createParentDirs(String filePath){

      try{
        String parent_dir=getDirectory(filePath);
        if(!parent_dir.equals("")){
          File dir=new File(parent_dir);
          dir.mkdirs();
        }
        else{
          System.out.println("Parent dir is not created");
        }
      }
      catch(Exception e){
         System.out.println("Parent dir is not created"+e);
      }
  }

 /**
  * Returns true is file exists and valid
  */
  public static boolean fileIsValid(String path){

      if(path==null)return false;
      boolean valid=false;
      try{
        File file=new File(path);
        if(!file.exists() || !file.canRead() || !file.canWrite())return false;
        return true;
      }
      catch(Exception e){
      }
      return valid;
  }

 /**
  * Returns true if directory is writable
  */
  public static boolean dirIsWritable(String path){

      if(path==null)return false;
      boolean valid=false;
      try{
        File file=new File(path);
        if(!file.isDirectory() || !file.exists() || !file.canRead() || !file.canWrite())return false;
        return true;
      }
      catch(Exception e){
      }
      return valid;
  }

  /**
   * Recursively deletes files in a directory (passed as a File object)
   */
  public static void recursiveDelete (File dirPath) {

    String [] ls = dirPath.list ();

    for (int idx = 0; idx < ls.length; idx++) {
      File file = new File (dirPath, ls [idx]);
      if (file.isDirectory ())
        recursiveDelete (file);
      file.delete ();
    }
  }


  public static void deleteAll (File dirPath) {

    recursiveDelete (dirPath);
  }

 /**
  * Recursively deletes all files and directories in the specified
  * directory. The specified directory is also removed.
  * This method hes been taken from some open source projects and not tested yet.
  * It is alternative method for recursiveDelete()
  */
  public static void deleteDirectory2(final String directoryPath) {

    final File directory = new File(directoryPath);
    if (directory.exists() && !directory.isDirectory()) {
      throw new IllegalArgumentException(directoryPath + " is not a directory");
    } // if

    // if the directory doesn't exist, our work is already done
    if (!directory.exists()) {
      return;
    } // if

    // first, find all files in this directory and delete them
    final String[] entries = directory.list();
    final int size = entries.length;
    File entry;

    for (int index = 0; index < size; ++index) {
      entry = new File(directoryPath + File.separatorChar + entries[index]);

      // if this entry is a file, delete it
      if (entry.isFile()) {
        entry.delete();
      } // if

      // if this entry is a directory, delete it and all its contents
      if (entry.isDirectory()) {
        deleteDirectory(directoryPath + File.separatorChar + entries[index]);
      } // if
    } // for index

    // finally, delete this empty directory
    directory.delete();
  } // deleteDirectory()

    // If successful, rmdir returns null.
    // Otherwise, rmdir returns the name of the file or directory
    // that could not be deleted.
    public static String rmdir( File dir ) {

	    String ret = null;
	    File[] contents = list( dir );
	    for ( int i = 0; i < contents.length; i++ ) {
	      if ( contents[ i ].isDirectory() ) {
		      if ( null != ( ret = rmdir( contents[ i ] ) ) )
		        return ret;
	        } else {
		      if ( ! contents[ i ].delete() )
		        return contents[ i ].getAbsolutePath();
	      }
	    }
	    if ( ! dir.delete() )
	    ret = dir.getAbsolutePath();
	    return ret;
    }

    public static File[] list( File dir ) {

	    String[] names = dir.list();
	    File[] ret = new File[ names.length ];
	    for ( int i = 0; i < names.length; i++ ) {
	      ret[ i ] = new File( dir, names[ i ] );
	    }
	    return ret;
    }

 /**
  * Copies the specified file to the specified directory.
  * This method has been taken from some open source projects and not tested yet
  */
  public static void copyFile(final String filePath, final String targetDirectory) throws IOException {

    BufferedInputStream in;
    BufferedOutputStream out;

    in = new BufferedInputStream(new FileInputStream(filePath));
    final String outPath = targetDirectory + File.separatorChar + getBasename(filePath);
    out = new BufferedOutputStream(new FileOutputStream(outPath));

    final int BUFFER_SIZE = 1024 * 8;
    final byte[] buffer = new byte[BUFFER_SIZE];
    int bytes;
    final long fileSize = new File(filePath).length();
    long length;

    // first, calculate the number of bytes to be copied. Then keep
    // copying in blocks until it's done.
    for (length = fileSize; length > 0; ) {

      // read in some bytes
      bytes = (length > BUFFER_SIZE ? BUFFER_SIZE : (int) length);
      bytes = in.read(buffer, 0, bytes);
      if (bytes < 0) {
        break;
      } // if
      
      // reduce the number of bytes to be copied
      length -= bytes;

      // write out those same bytes
      out.write(buffer, 0, bytes);
    } // for

    in.close();
    out.close();
  } // copyFile()

 /**
  * Returns the base name of the specified file path. Eg,
  * getBasename("hello/there/world") returns
  * "world". getBasename("hello") returns "hello".
  * This method hes been taken from some open source projects and not tested yet
  */
  public static String getBasename(final String filePath) {
    // work backwards looking for a file separator character

    // throw away the empty string case
    final int size = filePath.length();
    if (size == 0) {
      return filePath;
    } // if

    // throw away the case that has a trailing separator character
    if (filePath.charAt(size - 1) == File.separatorChar) {
      return "";
    } // if

    // find the substring from the last separator character to the end
    // of the string
    for (int index = size - 1; index >= 0; --index) {
      if (filePath.charAt(index) == File.separatorChar) {
        return filePath.substring(index, size);
      } // if
    } // for index

    // no separator character found. The filePath *is* the basename.
    return filePath;
  } // getBasename()

  static public URL fileToURL(String sfile){

    File file = new File(sfile);
    String path = file.getAbsolutePath();
    String fSep = System.getProperty("file.separator");
    if (fSep != null && fSep.length() == 1)
      path = path.replace(fSep.charAt(0), '/');
    if (path.length() > 0 && path.charAt(0) != '/')
      path = '/' + path;
    try 
    {
      return new URL("file", null, path);
    }
    catch (java.net.MalformedURLException e) 
    {
      // Can only happen if the file
      // protocol were not recognized
      throw new Error("unexpected MalformedURLException");
    }
  }



}
