package org.is.gui;

import java.awt.Toolkit;
import java.util.MissingResourceException;
import java.util.Locale;
import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Main resources class. Firstly we search a resource in System Properties, if
 * no such resource there - in ResourceBundles. We try to load ResourceBundles
 * firstly from DB table using corresponding ResourceBundle classes:
 * DBResourceBundle[_xy][_XY]. See these classes.
 *
 * @since jdk1.2
 */
public class Resources{

  private static ResourceBundle bundle;
  private static ResourceBundle keyBindings;

  public static void load(Locale locale)throws MissingResourceException{

    bundle=ResourceBundle.getBundle("org.is.gui.bundles.Bundle", locale);
    keyBindings=ResourceBundle.getBundle("org.is.gui.bundles.KeyBindings", locale);
    
    if(bundle==null)throw new NullPointerException("Resources::can't find ResourceBundles!");
  }

  public static String getString(String key) throws MissingResourceException {

	  return bundle.getString(key);
  }

  public static List getStringList(String key, String delim) throws MissingResourceException {

    return getStringList(key, delim, false);
  }

  public static List getStringList(String key) throws MissingResourceException {

    return getStringList(key, "\t\n\r\f", false);
  }

  public static List getStringList(String key, String delim, boolean returnDelims) throws MissingResourceException {

    List result = new ArrayList();
    StringTokenizer st = new StringTokenizer(getString(key), delim, returnDelims);

    while(st.hasMoreTokens()){
       result.add(st.nextToken());
    }
    return result;
  }

  public static boolean getBoolean(String key){

	  String b = getString(key);

	  if (b.equals("true")){
	    return true;
	  }else if (b.equals("false")){
	    return false;
	  }else{
	    throw new RuntimeException("Malformed boolean, key="+key);
	  }
  }

  public static int getInteger(String key){

	  String i = getString(key);

	  try{
	     return Integer.parseInt(i);
	  } catch (NumberFormatException e) {
	     throw new RuntimeException("Malformed integer, key="+key);
	  }
  }

  public static String getKeyBinding(String key){

     return keyBindings.getString(key);
  }

}
