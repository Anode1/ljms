package org.is.html;

import java.awt.Color;
import java.awt.Font;

/**
 * Utilites class containing factories making different types of objects
 * from strings (just collection of static methods).
 *
 * @since jdk1.0
 */
public class Factory{

  public static boolean str2bool(String s){

     if(s==null || s.equals(""))return false;
     if(s.equals("TRUE")||s.equals("true"))return true;
     else return false;
  }

  public static int str2fontStyle(String s){

    if(s.equals("BOLD")){
        return Font.BOLD;
    }
    else if(s.equals("ITALIC")){
        return Font.ITALIC;
    }
    else if(s.equals("PLAIN")){
        return Font.PLAIN;
    }
    else if(s.equals("ITALIC_BOLD")){
        return (Font.ITALIC|Font.BOLD);
    }
    else{
        System.err.println("Factory::str2fontStyle: not resolved:"+s+" -default is taken");
        return Font.PLAIN;
    }
  }

 /**
  * Makes Color objects from the string specified according to [CSS2-color]
  * Fixed currently supported: black|blue|cyan|darkGray|gray|green|lightGray|magenta|orange|pink|red|white|yellow
  * or you can define as rgb format: #rrggbb
  */
  public static Color getColor(String s){

    Color color=null;

    if(s.startsWith("#")){
       return fromRGB(s);
    }
    else if(s.equals("black")){
       color=color.black;
    }
    else if(s.equals("blue")){
       color=color.blue;
    }
    else if(s.equals("cyan")){
       color=color.cyan;
    }
    else if(s.equals("darkGray")){
       color=color.darkGray;
    }
    else if(s.equals("gray")){
       color=color.gray;
    }
    else if(s.equals("green")){
       color=color.green;
    }
    else if(s.equals("lightGray")){
       color=color.lightGray;
    }
    else if(s.equals("magenta")){
       color=color.magenta;
    }
    else if(s.equals("orange")){
       color=color.orange;
    }
    else if(s.equals("pink")){
       color=color.pink;
    }
    else if(s.equals("red")){
       color=color.red;
    }
    else if(s.equals("white")){
       color=color.white;
    }
    else if(s.equals("yellow")){
       color=color.yellow;
    }
    return color;
  }

  /**
   * Constructs Color object from rgb values
   */
  private static Color fromRGB(String tag){

    Color color=null;

    try{
       String rgb=tag.substring(1);
       int r=Integer.parseInt(rgb.substring(0,2),16);
       int g=Integer.parseInt(rgb.substring(2,4),16);
       int b=Integer.parseInt(rgb.substring(4,6),16);
       return new Color(r,g,b);
    }
    catch(Exception e){
      //LogManager.err(e);
    }
    return color;
  }

}
