package org.is.html;

import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import javax.swing.*;    
import javax.swing.text.JTextComponent;
import javax.swing.tree.*;
import javax.swing.event.*;
import javax.swing.Timer;
import java.awt.*;

import org.is.html.dialogs.SourceDialog;
import org.is.html.dialogs.ProgressDialog;

/**
 * Top manager. Registration point for all singletons.
 * The only repository of static (global) variables in the application.
 * If we'll want to support multiple instances of the editor for one VM,
 * we have to change only this class
 *
 * @since jdk1.2
 */
public class TopManager{

    private static Applet applet;  //default - application (and this is null in that case)

    public static Locale[] supportedLocales;
    public static Locale defaultLocale=Locale.CANADA;   //save this for user
                                     // =Locale.CANADA_FRENCH;
    private static GHTMLEditorFrame htmlEditorFrame;
    private static SourceDialog d;

    public static void init(){

      String vers = System.getProperty("java.version");
      if(vers.compareTo("1.3") < 0){
         System.err.println("VM must be 1.3 or higher - exiting");
         return;
      }
      //System.out.println(vers);

      try{

        Resources.load(Locale.CANADA);

        //System.out.println("Resources loaded");

        //supported locales:
        int number_locales=Integer.parseInt(Resources.getString("Locales.size"));
        supportedLocales=new Locale[number_locales];
        for(int i=0;i<number_locales;i++){
          String l=Resources.getString("Locales.Language."+i);
          String c=Resources.getString("Locales.Country."+i);
          supportedLocales[i]=new Locale(l,c);
        }

        setLookAndFeel();

        //caching for future
        Images.getIcon("logo");

      }
      catch(Exception e){
        System.err.println("TopManager::init:"+e);
      }
    }

    public static GHTMLEditorFrame getGHTMLEditorFrame(){

      if(htmlEditorFrame==null){
         createGHTMLEditorFrame();
      }
      //htmlEditorFrame.toFront();
      return htmlEditorFrame;
    }

    public static GHTMLEditor getGHTMLEditor(){

      return getGHTMLEditorFrame().getEditor();
    }

    private static synchronized void createGHTMLEditorFrame(){

      if(htmlEditorFrame==null){
         init();
         htmlEditorFrame=new GHTMLEditorFrame();
      }
    }

    public static synchronized void disposeGHTMLEditorFrame(){
    
      htmlEditorFrame=null;
    }

    public static SourceDialog getDialog(){

      if(d==null){
         d=new SourceDialog();
      }

      //workaround for Sun's repaint bug: 4265726, 4189244 etc
		  Timer t = new Timer(20, new ActionListener() {
		    public void actionPerformed(ActionEvent ae) {
		        d.show();
		    }
		  });
		  t.setRepeats(false);
		  t.start();

      return d;
    }

    public static Applet getApplet(){

      return applet;
    }

    public static void setApplet(Applet a){

      applet=a;
    }

    public static boolean isApplet(){

      return applet!=null;
    }

	public static void showProgressDialog(){

		pd = new ProgressDialog();
	}

	public static void hideProgressDialog(){

		if(pd != null){
			pd.dispose();
			pd = null;
		}
	}

	public static void advanceProgress(){

		if(pd != null){
			pd.advance();
    }
	}

	// private members
	private static ProgressDialog pd;

 /**
  * Tries to setup a look and feel according to user's preferences
  */
  private static void setLookAndFeel(){

	  // Force SwingApplet to come up in the System L&F
	  String laf = UIManager.getSystemLookAndFeelClassName();
	  try{
	     UIManager.setLookAndFeel(laf);
	     // If you want the Cross Platform L&F instead, comment out the above line and
	     // uncomment the following:
	     // UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
	  }catch (UnsupportedLookAndFeelException exc) {
	      System.err.println("Warning: UnsupportedLookAndFeel: " + laf);
	  }catch (Exception exc) {
	      System.err.println("Error loading " + laf + ": " + exc);
  	}

  }

  /**
   * Sets look and feel taken from ResourceBundles
   */
  private void setUserLaf(){
//user preference laf:

      String className=Resources.getString("look_and_feel_class");
      if(className==null)return;
      LookAndFeel oldLF = UIManager.getLookAndFeel();

      try{
	      Class cl = Class.forName(className);
	      LookAndFeel lf = (LookAndFeel)cl.newInstance();
	      if (!lf.isSupportedLookAndFeel()){
        	 className = null;
  	    }

     	  if(className == null){
           return;
        }

      }catch (Throwable t) {
        return;
      }

      try{
     	  UIManager.setLookAndFeel(className);
      }catch (Throwable t) {
		    // Ignore all exceptions in l&f
        try {
	         UIManager.setLookAndFeel(oldLF);
        }catch (Throwable e) {
           System.err.println("Error in L&F");
        }
      }

  }//laf

}

