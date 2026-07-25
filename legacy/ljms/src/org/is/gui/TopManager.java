package org.is.gui;

import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import javax.swing.*;
import javax.swing.tree.*;
import javax.swing.event.*;
import javax.swing.Timer;
import java.awt.*;

import org.is.gui.dialogs.SplashWindow;
import org.is.util.Utils;

/**
 * Top manager. Registration point for all singletons.
 * The only repository of static (global) variables in the application.
 * If we'll want to support multiple instances of the editor for one VM,
 * we have to change only this class
 *
 * @since jdk1.2
 */
public class TopManager{

  //private static Applet applet;  //default - application (and this is null in that case)

  public static Locale[] supportedLocales;
  public static Locale defaultLocale=Locale.CANADA;   //save this for user

  private static MainFrame mainFrame;

  public static MainFrame getMainFrame(){
     return mainFrame;
  }

  public static void setMainFrame(MainFrame f){
     mainFrame=f;
  }

  public static void init(){

    try{
        Resources.load(defaultLocale);

        //caching for splash screen:
       // Images.getIcon("logo");

        setLanguage();

        setSystemLaF();

     }
     catch(Exception e){
        System.err.println("TopManager::init:"+e);
     }
  }

  private static void setLanguage(){

    //supported locales:
    int number_locales=Integer.parseInt(Resources.getString("Locales.size"));
    supportedLocales=new Locale[number_locales];
    for(int i=0;i<number_locales;i++){
       String l=Resources.getString("Locales.Language."+i);
       String c=Resources.getString("Locales.Country."+i);
       supportedLocales[i]=new Locale(l,c);
    }
  }

  private static void setSystemLaF(){

    LookAndFeel oldLF = UIManager.getLookAndFeel();
	  String laf = UIManager.getSystemLookAndFeelClassName();
	  try{
	     UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	  }catch (Throwable exc) {
        try{
	         UIManager.setLookAndFeel(oldLF);
        }catch (Throwable e) {
        }
  	}
  }

  /**
   * Sets look and feel taken from ResourceBundles
   */
  private static void setLaF(){

      LookAndFeel oldLF = UIManager.getLookAndFeel();

      String className=Resources.getString("look_and_feel_class");
      if(className==null){
         setSystemLaF();
         return;
      }

      try{
	       Class cl = Class.forName(className);
	       LookAndFeel lf = (LookAndFeel)cl.newInstance();
	       if (!lf.isSupportedLookAndFeel()){
        	 return;
  	     }
      }catch (Throwable t){
         return;
      }

      try{
     	   UIManager.setLookAndFeel(className);
      }catch (Throwable t){
		     // Ignore all exceptions in l&f
         try {
	         UIManager.setLookAndFeel(oldLF);
         }catch (Throwable e){
         }
      }

  }//laf

}

