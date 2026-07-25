package org.is.html;

import java.util.Properties;
import java.util.Enumeration;

/**
 * Substitution for Sun's MutableAttributeSet for our parsing tree
 *
 * Not used in current version
 * @version 1.0
 * @since jdk1.2  
 */
public class Attributes{

  private Properties props;

 /**
  * Default constructor
  */
  public Attributes(){

    props=new Properties();
  }

  /**
   * Convenience constructor
   */
  public Attributes(Properties p){

    props=new Properties(p);
  }

  public void addAttribute(String key, String value){

    props.put(key, value);
  }

  public void removeAttribute(String key){

    props.remove(key);
  }

  public String getAttribute(String key){

    return props.getProperty(key, ""); //never null
  }

  public String getAllAttributesAsString(){

    StringBuffer sb=new StringBuffer("");
    for(Enumeration enum = props.keys() ; enum.hasMoreElements() ;) {
        String aKey=(String)enum.nextElement();
        sb.append(aKey);
        sb.append("=");
        String aValue=getAttribute(aKey);
        sb.append(aValue);
        if(enum.hasMoreElements())sb.append(" ");  //separator
    }
    return sb.toString();
  }

  public String toString(){

     return getAllAttributesAsString();  //for now
  }

}
