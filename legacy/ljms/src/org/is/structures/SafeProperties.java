/**
 * @(#)SafeProperties.java
 */
package org.is.structures;

import java.util.Properties;

import org.is.logmanager.LogManager;

/**
 * The same as Properties but also prints error message if some property was not found
 * For debugging purposes.
 *
 * @version 1.0
 * @since jdk1.0
 */
public class SafeProperties extends Properties{

 /**
  * Default constructor
  */
  public SafeProperties(){
  }

  /**
   * Constructor. See corresponding superclass's constructor
   */
  public SafeProperties(Properties p){

    super(p);
  }


/**
 * Overwrites the default method to have notification of the user before
 * null pointer exception in the caller
 */
  public String getProperty(String key){

     if(key==null){
        LogManager.getInstance().printError("SafeProperties::trying to get a null property");
        return null;
     }
     String result=super.getProperty(key);
     if(result==null){
        LogManager.getInstance().printError("You are trying to get a property that does not exist in property file - "+key);
     }
     return result;
  }

  public String getNotSafe(String key){

     return super.getProperty(key);
  }

}
