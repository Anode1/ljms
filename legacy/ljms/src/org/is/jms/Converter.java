/**
 * @(#)Converter.java
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
package org.is.jms;

import javax.jms.MessageFormatException;

/**
 * Auxiliary class with all primitive types conversions class methods in one
 * place. This class is used by Message and it's subclasses using these conversions.
 * For convenience it transforms native exceptions into JMSExceptions
 * (to reuse maximum of the code)
 *
 * @version 1.1
 * @since jdk1.0
 */
public class Converter{

   public static boolean str2bool(String s) throws MessageFormatException{

      if(s==null){
        throw new MessageFormatException("Converter::str2bool:null");
      }
      else if(s.equals("true")){
         return true;
      }
      else if(s.equals("false")){
         return false;
      }

      String lc=s.toLowerCase();
      
      if(lc.equals("true"))return true;
      else if(lc.equals("false"))return false;
      else throw new MessageFormatException("Converter::str2bool:not boolean");
   }

   public static int str2int(String s) throws MessageFormatException{

      if(s==null){
        throw new MessageFormatException("Converter::str2int:null");
      }

      try{
        return Integer.parseInt(s);
      }
      catch(NumberFormatException e){
        throw new MessageFormatException("Malformed integer");
      }
   }

   public static long str2long(String s) throws MessageFormatException{

      if(s==null){
        throw new MessageFormatException("Converter::str2long:null");
      }
      try{
        return Long.parseLong(s);
      }
      catch(NumberFormatException e){
        throw new MessageFormatException("Malformed long");
      }
   }

   public static float str2float(String s) throws MessageFormatException{
   
      if(s==null){
        throw new MessageFormatException("Converter::str2float:null");
      }
      try{
        return Float.valueOf(s).floatValue();
      }
      catch(NumberFormatException e){
        throw new MessageFormatException("Malformed float");
      }
   }

   public static double str2double(String s) throws MessageFormatException{

      if(s==null){
        throw new MessageFormatException("Converter::str2double:null");
      }
      try{
        return Double.valueOf(s).doubleValue();
      }
      catch(NumberFormatException e){
        throw new MessageFormatException("Malformed double");
      }
   }

   public static short str2short(String s) throws MessageFormatException{

      if(s==null){
        throw new MessageFormatException("Converter::str2short:null");
      }

      try{
        return Short.parseShort(s);
      }
      catch(NumberFormatException e){
        throw new MessageFormatException("Malformed short");
      }
   }

   public static byte str2byte(String s) throws MessageFormatException{

      if(s==null){
        throw new MessageFormatException("Converter::str2byte:null");
      }
      try{
        return Byte.parseByte(s);
      }
      catch(NumberFormatException e){
        throw new MessageFormatException("Malformed byte");
      }
   }

   public static char str2char(String s) throws MessageFormatException{

      if(s==null){
        throw new MessageFormatException("Converter::str2char:null");
      }
      try{
        if(s.equals(""))return (char)0;  //throw Exception maybe?
        return s.charAt(0);
      }
      catch(NumberFormatException e){
        throw new MessageFormatException("Malformed byte");
      }
   }
}
