package org.is.html;

import javax.swing.*;
import java.net.URL;
import java.util.Hashtable;

import org.is.gui.ColoredBox;

/**
 * Main images repository. Works with both application and Applet contexts 
 *
 * @version 1.2 04/10/99
 * @since jdk1.0
 */
public class Images{

  private static Hashtable cache=new Hashtable(64);

  public static Icon getIcon(String name){

		Icon icon = (Icon)cache.get(name);
		if(icon != null){
			return icon;
    }
    return createIcon(name);
  }

  private static Icon createIcon(String name){

    try{

      /*
		  if(iconName.startsWith("file:")){
			  icon = new ImageIcon(iconName.substring(5));
		  } */
      String path=Resources.getString("image."+name);

      if(path==null){
         System.err.println("Images::createIcon: path is not found in Resources for name:"+name);
         return new ColoredBox();  //not to return null crashing everything from one side
                                  //and not to make hundreds of checkings in user classes
                                  // - from another (if Icon is required then it assumed to be existing)
      }

      URL url=null; /*
      if(TopManager.isApplet()){
        url=TopManager.getApplet().getURL(path);
        //System.out.println("Image loaded from applet");
      }
      else{ //application:
      */
        // icon=new ImageIcon(path);    //this works, but the following is more universal (can be in jar)
        url=Images.class.getClassLoader().getResource(path);
     // }

      if(url==null){
        System.err.println("Images::createIcon: url is null for path:"+path);
        return new ColoredBox();
      }

      ImageIcon icon=new ImageIcon(url);   
  		cache.put(name, icon);
      return icon;

    }
    catch(Exception e){
      System.err.println("Images::createIcon:"+e);
      return new ColoredBox();
    }
	}

  
}
