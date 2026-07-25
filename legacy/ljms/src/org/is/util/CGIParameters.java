/**
 * @(#)CGIParameters.java
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

import java.util.Hashtable;
import java.util.Vector;
import java.util.Enumeration;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;

/**
 * General CGI parameters class.
 * Supports multiple-value parameters
 * <p>
 * It's better to rewrite this class to use servletUtils class
 * to work with arrays of Strings vs Vectors (much faster)
 *
 * @version 1.0
 * @since   JDK1.0
 */
public class CGIParameters extends Hashtable{

  /**
   * Default constructor
   */
   public CGIParameters(){

      super();
   }

  /**
   * See corresponding superclass constructor
   */
   public CGIParameters(int capacity){

      super(capacity);
   }

  /**
   * See corresponding superclass constructor
   */
   public CGIParameters(int capacity, float factor){

      super(capacity, factor);
   }

   /**
    * constructor when all parameters have been taken from passed HttpServletRequest object
    */
   public CGIParameters(HttpServletRequest req){
   
      Enumeration enum;
      String paramName, paramValue[];
      Vector paramList;
      enum = req.getParameterNames();
      while(enum.hasMoreElements()){
           paramName = (String) enum.nextElement();
           paramValue = req.getParameterValues(paramName);

           if(paramValue.length == 1){
               //put(new String(paramName), encodeValue(req,paramValue[0]));
				  		 put(new String(paramName), paramValue[0]);
           }
           else{
               paramList = new Vector();
               for(int i=0;i<paramValue.length;i++){
                  //paramList.addElement(encodeValue(req,paramValue[i]));
									paramList.addElement(paramValue[i]);
               }
               put(new String(paramName), paramList);
                //log.printDebug(paramName + "=" + paramList);
           }
      }

   }

        /**
         * Adds one parameter value
         */
        public void addParam(String key, String value){

            if(key==null){
              System.err.println("CGIParameters::addParam:You are trying to set null key for a parameter!");
              return;
            }
            if(value==null){
              System.err.println("CGIParameters::addParam:You are trying to send null value for key:"+key);
              return;
            }
            Object existing=this.get(key);
            if(existing==null){//didn't exist before
                put(key, value);
            }
            else if(existing instanceof Vector){
               Vector existingVector=(Vector)existing;
               existingVector.addElement(value);
            }
            else if(existing instanceof String){ //change to Vector
               Vector newVector=new Vector(3);
               newVector.addElement((String)existing);
               newVector.addElement(value);
               put(key, newVector); //overwrite String
            }
        }

        /**
         * Adds all parameters from another CGIParameters class to this class
         */
        public void addParams(Hashtable anotherParms){

          for (Enumeration enum = anotherParms.keys(); enum.hasMoreElements() ;) {
              String aKey=(String)enum.nextElement();
              Object aValue=anotherParms.get(aKey);
              addParam(aKey,aValue);
          }
        }

        /**
         * Convenience method when we do not know in advance what type will bve passed
         */
        public void addParam(String key, Object o){

            if(o==null)return;
            if(o instanceof String)addParam(key,(String)o);
            else if(o instanceof Vector)addParam(key,(Vector)o);
            else System.err.println("CGIParameters::addParam: trying to add unsupported type of param: only String and Vector are allowed");

        }

        /**
         * Adds string array parameter
         */
        public void addParam(String key, Vector vector){

            if(key==null){
              System.err.println("CGIParameters::addParam:You are trying to set null key for a vector parameter!");
              return;
            }
            if(vector==null){
              System.err.println("CGIParameters::addParam:You are trying to send null value for key:"+key);
              return;
            }

            Object existing=get(key);
            if(existing==null){//didn't exist before
                put(key, vector);
            }
            else if(existing instanceof Vector){
               Vector v=(Vector)existing;
               for (Enumeration enum = vector.elements() ; enum.hasMoreElements() ;) {
                 String elem=(String)enum.nextElement();
                 if(elem!=null)v.addElement(elem);
               }
            }
            else if(existing instanceof String){ //change to Vector
               String ex=(String)existing;
               vector.addElement(ex);
               put(key, vector); //overwrite String
            }

        }

        /**
         * Removes parameter
         */
        public Object removeParam(String key){

            return remove(key);
        }

        private String encoded; //cached variable when called more than once  //!

        /**
         * Returns all parameters in the URLEncoded form String
         */
        public String getEncoded(){

          synchronized(this){
            if(encoded!=null)return encoded; //return from local cache
          }

          StringBuffer buf = new StringBuffer();

          String aKey=null;
          Object aValue=null;
          for (Enumeration enum = this.keys(); enum.hasMoreElements() ;) {
              aKey=(String)enum.nextElement();
              aValue=this.get(aKey);

              if(aValue instanceof String){ //change to Vector
                buf.append(URLEncoder.encode(aKey));
                buf.append("=");
                buf.append(URLEncoder.encode((String)aValue));
              }
              else if(aValue instanceof Vector){
                Vector v=(Vector)aValue;
                String elem=null;
                for (Enumeration vals = v.elements() ; vals.hasMoreElements() ;) {
                  elem=(String)vals.nextElement();
                  if(elem!=null){
                    buf.append(URLEncoder.encode(aKey));
                    buf.append("=");
                    buf.append(URLEncoder.encode(elem));
                  }
                  if(vals.hasMoreElements())buf.append("&");
                }
              }
              if(enum.hasMoreElements())buf.append("&");
          }
          String result=buf.toString();
          //encoded=buf.toString();   //put into local cache
          encoded=result;
          return result;
        }

       /**
        * For debugging purposes only. Otherwise use getEncoded()
        */      /*
        public String toString(){

          String result="";
          for (Enumeration enum = this.keys(); enum.hasMoreElements() ;) {
              String aKey=(String)enum.nextElement();
              Object aValue=get(aKey);
              result+=aKey.toString();
              if(aValue==null){
                result+="=null;";
              }
              else if(aValue instanceof String){
                result+="="+aValue.toString()+";";
              }
              else if(aValue instanceof Vector){
                result+="="+vector2String((Vector)aValue)+";";
              }
          }
          return result;
        }
                  */
       /**
        * Used by toString(). For debugging only.
        */
        private String vector2String(Vector v){
        
          if(v==null || v.size()==0)return "[]";
          StringBuffer result=new StringBuffer("[");
          for (Enumeration enum = v.elements(); enum.hasMoreElements() ;) {
              result.append((String)enum.nextElement());
              if(enum.hasMoreElements())result.append(";");
          }
          result.append("]");
          return result.toString();
        }


}