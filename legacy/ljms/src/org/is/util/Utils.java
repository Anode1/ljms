/**
 * @(#)Utils.java
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

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.io.PrintWriter;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.beans.BeanInfo;
import java.beans.BeanDescriptor;
import java.beans.Introspector;
import java.beans.IntrospectionException;
import java.awt.Component;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.Hashtable;

/**
 * Utilities class - simple set of useful static funcions which are used by
 * multiple classes from different packages
 *
 * @version 1.2 04/10/99
 * @since jdk1.0
 */
public class Utils{

  /**
   * Takes stackTrace from the Exception and transforms it into String
   */
  public static String stack2String(Throwable e){

        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        PrintWriter pw=new PrintWriter(baos,true);
        e.printStackTrace(pw);
        return baos.toString();
  }

  /**
   * Makes string JavaScript-safe by escaping double quotes and backslashes.
   */
  public static String safeString(String in){

         StringBuffer sb = new StringBuffer();

         int prev = 0;
         int next = 0;
         while((next = in.indexOf("\\", next)) >= 0){
               sb.append(in.substring(prev, next) + "\\\\");
               next++;
               prev = next;
         }
         sb.append(in.substring(prev));

         prev = 0;
         next = 0;
         in = sb.toString();
         sb = new StringBuffer();

         while((next = in.indexOf("'", next)) >= 0){
               sb.append(in.substring(prev, next) + "\\'");
               next++;
               prev = next;
         }
         sb.append(in.substring(prev));

         prev = 0;
         next = 0;
         in = sb.toString();
         sb = new StringBuffer();

         while((next = in.indexOf("\n", next)) >= 0){
               sb.append(in.substring(prev, next));
               next++;
               prev = next;
         }
         sb.append(in.substring(prev));

         return (sb.toString()).trim();
  }

    /**
     * Normalizes the given string.
     */
    public static String safeJS(String s){

        StringBuffer str = new StringBuffer();

        int len = (s != null) ? s.length() : 0;
        for (int i = 0; i < len; i++) {
            char ch = s.charAt(i);
            switch (ch) {
                case '<': {
                    str.append("&lt;");
                    break;
                }
                case '>': {
                    str.append("&gt;");
                    break;
                }
         /*
                case '&': {
                    str.append("&amp;");
                    break;
                }
                case '"': {
                    str.append("&quot;");
                    break;
                }
                case '\r':
                case '\n': {
                    str.append("&#");
                    str.append(Integer.toString(ch));
                    str.append(';');
                    break;
                }

                */
                default: {
                    str.append(ch);
                }
            }
        }
        return str.toString();
    }


 /**
  * Load bean from serialized stream
  */
  public static Object bytes2Bean (byte[] bytes){

    Object o=null;
    InputStream is=null;
    ObjectInputStream ois=null;

    try {
        if(bytes==null)return null;
        is=new ByteArrayInputStream(bytes);
        ois = new ObjectInputStream (is);
        o=ois.readObject();
        if(!checkCustomize(o.getClass()))  return null;        //not introspectable class

    }catch (Exception e) {
        //nothing - we'll get null if something wrong
    }finally{
        if(ois!=null)try{ois.close();}catch(IOException e){}
        if(is!=null)try{is.close();}catch(IOException e){}
    }
    return o;
  }

 /**
  * Save serialized bean
  */
  public static byte[] bean2Bytes(Object o) {

    byte[] bytes=null;
    ByteArrayOutputStream bos=null;
    ObjectOutputStream oos=null;
    try {
      if(o==null)return null;

      bos=new ByteArrayOutputStream();
      oos = new ObjectOutputStream(bos);
      oos.writeObject(o);
      oos.flush();
      bos.flush();
      bytes=bos.toByteArray();
    } catch (Exception e) {
        //javax.swing.JOptionPane.showMessageDialog(null,Resources.getString("Question.BeansPanel.Dialog.not_serialized"));
    }finally{
        if(oos!=null)try{oos.close();}catch(IOException e){}
        if(bos!=null)try{bos.close();}catch(IOException e){}
    }
    return bytes;
  }

 /**
  * Check if introspection is possible for this class
  */
  public static boolean checkCustomize(Class c){

    try {
        BeanInfo bi = Introspector.getBeanInfo (c);
        BeanDescriptor bd = bi.getBeanDescriptor();
    }catch (IntrospectionException e) {
        return false;
    }
    return true;
  }


 /**
  * Puts component me being centralized relative to the parent
  */
  public static void setCentalizedLocationRelativeMe(Component parent, Component me){

     if(parent==null)return;

     Point parentLoc=parent.getLocation();
     int x=0,y=0; //my future coordinates

     int parentX=parentLoc.x;
     int parentY=parentLoc.y;
     int parentW=parent.getSize().width;
     int parentH=parent.getSize().height;

     int w=me.getSize().width;
     int h=me.getSize().height;

     if(w<parentW)x=parentX+(parentW-w)/2;
     else x=parentX-(w-parentW)/2;

     if(h<parentH)y=parentY+(parentH-h)/2;
     else y=parentY-(h-parentH)/2;

     me.setLocation(x,y);
  }

 /**
  * Puts the component into the center of the screen
  */
  public static void setCentalizedLocation(Component me){

    int x=0,y=0; //my future coordinates
    Dimension dim=Toolkit.getDefaultToolkit().getScreenSize();
    int screenW=dim.width;
    int screenH=dim.height;

    int w=me.getSize().width;
    int h=me.getSize().height;

    if(w<screenW) x=(screenW-w)/2;
    else x=0;

    if(h<screenH) y=(screenH-h)/2;
    else y=0;

    me.setLocation(x,y);
//		setLocation((screen.width - getSize().width) / 2,	(screen.height - getSize().height) / 2);
  }


 /**
  * Returns the main parent frame for the component
  */
  public static Frame getParentFrame(Component c){

    while(c.getParent()!=null){
       c=c.getParent();
    }
    return (Frame)c;

  }

 /**
  * Returns the main parent frame for the component
  */
  public static String printParents(Component c){

    StringBuffer sb=new StringBuffer("");

    while(c.getParent()!=null){
       sb.append(c.getName());
       sb.append("/");
       c=c.getParent();
    }
    sb.append(c.getName());
    return sb.toString();
  }

 /**
  * Changes the cursor. This function is to walk around JSplitPane bug
  */
  /*
  public static void makeWaitCursor(Container c, boolean wait_cur){

      java.awt.Component components[] = c.getComponents();
      java.awt.Component current;

      for (int i = 0; i < components.length; i++){

        current=components[i];
        current.addMouseListener(new MouseAdapter() {}); //to walkaround cursor bug

        if(wait_cur){
          current.setCursor(new Cursor(Cursor.WAIT_CURSOR));
        }
        else{
          current.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        }

        current.repaint();

	      if(current instanceof Container){
	        makeWaitCursor((Container)current, wait_cur);
	      }
      }//for
  }
  */

 /*
    public static void filter(InputStream in, PrintWriter out, Hashtable strings) throws IOException,Exception {

        DataInputStream dis=new DataInputStream(new BufferedInputStream(in));
        String text="";
        String line=dis.readLine();
        while(line != null) {
            text+=line+"\n";
            line=dis.readLine();
        }
        System.out.println("Before filtering:"+text);
        out.print(filt(text,strings));
        dis.close();
    }
   */

    public static String filt(String s, Hashtable strings) throws IOException,Exception{

      char buffer[]=new char[s.length()];
      StringBuffer sb=new StringBuffer(s.length());
      s.getChars(0,s.length(),buffer,0);
      int j=0;
      while(j<s.length()-3){
        if(buffer[j]=='<' && buffer[j+1]=='!' && buffer[j+2]=='-' && buffer[j+3]=='-' ){
  				 j+=4; //skip "<!--"
  				 StringBuffer comment=new StringBuffer();
      			while(!(buffer[j]=='-' && buffer[j+1]=='-' && buffer[j+2]=='>')) {
     						comment.append(buffer[j]);
     						j++;
      			}
      			j+=3; //skip "-->"

						String commentS=comment.toString();
						System.out.println("comment="+commentS);

            //preprocessor comment:
  				  if(commentS.charAt(0)=='%' && commentS.charAt(commentS.length()-1)=='%'){ //usual comment
                    System.out.println("parameter="+commentS.substring(1,commentS.length()-1));
   				 					sb.append((String)strings.get(commentS.substring(1,commentS.length()-1)));
            }
            //usual comment:
  				  else{
 							  sb.append("<!--"+commentS+"-->");
           }
        }
        else{ //not comment:
            sb.append(buffer[j++]);
        }
      }

      //last 3 symbols:
      sb.append(buffer[j++]);
      sb.append(buffer[j++]);
      sb.append(buffer[j]);

      System.out.println("Output from the filter: "+sb.toString());

      return sb.toString();
    }


    public StringBuffer filterOutCRLF(String text){

		// remove any newlines from the text and replace with a space
		StringBuffer buf = new StringBuffer(text);
		int index;
		while ((index = indexOf(buf, (char) 13)) != -1) {
			// first delete all CR in the string
			/* JDK12_START */
		//	buf.deleteCharAt(index);
			/* JDK12_END */

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
    return buf;
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
  * Substitutes oldSubString by newSubString in string
  */
   public static String substitute(String string, String oldSubString, String newSubString){

       //System.out.println("substitute called with:"+string+","+oldSubString+","+newSubString);
       int index=string.indexOf(oldSubString);
       if(index==-1){
          return string; //nothing to substitute
       }

       String prefix=string.substring(0,index);
       //System.out.println(prefix);

       String result = prefix + newSubString +
                string.substring(index + oldSubString.length());

       //System.out.println("result:"+result);
       return result;
   }

    /**
     * Take the given string and chop it up into a series
     * of strings on whitespace boundries.  This is useful
     * for trying to get an array of strings out of the
     * resource file.
     */
    protected String[] tokenize(String input) {

	    String strings[];

	    Vector v = new Vector();
	    StringTokenizer t = new StringTokenizer(input);
	    while (t.hasMoreTokens()){
	      v.addElement(t.nextToken());
      }
	    strings = new String[v.size()];
      v.copyInto(strings);
	    return strings;
    }

  /**
   * Testing entry point
   */
  public static void main(String args[]) {

    try{
       //test something here
    }catch (Throwable t) {
       System.out.println("uncaught exception: " + t);
       t.printStackTrace();
       try{Thread.sleep(50000);}catch(InterruptedException ie){}
    }
  }

}//Utils
