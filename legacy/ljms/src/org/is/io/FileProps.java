/**
 * @(#)FileProps.java
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

import java.util.Properties;
import java.util.Enumeration;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.File;
//import java.util.ResourceBundle;
import java.io.IOException;

/**
 * Properties container. <p>
 * Here we agree with Apache ideology: to have only one config file
 * (2 - maximum) for easier configuration.
 *
 * @version 1.1
 * @since jdk1.2
 */
public class FileProps{

    private Properties props;
    private static FileProps instance;

    //private ResourceBundle bundle;

    public FileProps(){
       props=new Properties();
    }

    public static FileProps getInstance(){

       if(instance==null)instance=new FileProps();
       return instance;
    }

    public FileProps init(String path) throws IOException{

       props.load(new FileInputStream(path));
       return this;
    }

    public String getProperty(String key){

      String prop=props.getProperty(key);
      if(prop==null)System.err.println("Config::getHostProperty: property "+key+" is null!");
	    return prop;
    }

    public int getIntegerProperty(String key){

      String prop=props.getProperty(key);
      if(prop==null){
        System.err.println("Config::getIntegerProperty: property "+key+" is null!");
        return 0;
      }
      try{
        return Integer.parseInt(prop);
      }
      catch(NumberFormatException nfe){
        throw new NullPointerException("Config::getIntegerProperty: property "+key+" is not integer!");
      }
    }

    public long getLongProperty(String key){

      String prop=props.getProperty(key);
      if(prop==null){
        throw new NullPointerException("Config::getLongProperty: property "+key+" is null!");
      }
      try{
        return Long.parseLong(prop);
      }
      catch(NumberFormatException nfe){
        throw new NullPointerException("Config::getLongProperty: property "+key+" is not long!");
      }
    }

    public boolean getBooleanProperty(String key){

      String prop=props.getProperty(key);
      if(prop==null){
        throw new NullPointerException("Config::getBooleanProperty: property "+key+" is null!");
      }
	    if(prop.equals("true") || prop.equals("TRUE")){
	      return true;
	    }else if (prop.equals("false") || prop.equals("FALSE")){
	      return false;
	    }else{
	      throw new NullPointerException("Config::getBooleanProperty: property with key="+key+" is not boolean");
	    }
    }

    public Properties getProperties(){

	    return props;
    }

       /*
    public List getStringList(String key, String delim)	throws MissingResourceException {

        return getStringList(key, delim, false);
    }

    public List getStringList(String key) throws MissingResourceException {

        return getStringList(key, "\t\n\r\f", false);
    }

    public List getStringList(String key, String delim, boolean returnDelims)	throws MissingResourceException {

        List result = new ArrayList();
        StringTokenizer st = new StringTokenizer(getString(key), delim, returnDelims);

        while(st.hasMoreTokens()){
            result.add(st.nextToken());
        }
        return result;
    }     */    

    public static Properties merge(Properties props1, Properties props2){

      if(props1==null || props2==null)return props1;
      for(Enumeration e = props2.keys(); e.hasMoreElements() ;){
        String key=(String)e.nextElement();
        String value=props2.getProperty(key);
        props1.put(key,value);
      }
      return props1;
    }

}
