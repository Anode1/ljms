package org.is.html;

import java.awt.Frame;
import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.net.URL;
import java.util.Vector;

//import netscape.javascript.JSObject;

import org.is.html.*;

/**
 * Applet version (vs application version which is in MainFrame class).
 * Applet version gets parameters from html file (not using config files),
 * makes callback on completion through JSObject (optionally) and does not use
 * LogManager (only System.out, System.err).
 *
 * @version 1.0
 * @since jdk1.2
 */
public class Applet extends javax.swing.JApplet{

  private JButton button;
//  private JSObject window;
  private String data;

  public void init(){

    getContentPane().setLayout(new BorderLayout());
    /*
    try{
       window = JSObject.getWindow(this);
    }catch(Exception ex){
       System.err.println(ex);
    }
   */
    TopManager.setApplet(this);
    TopManager.getGHTMLEditor();

    /*
    FormBase form=new FormBase();
    form.setDoc(new RealTimeArticle());
    */
    //getContentPane().add(form, BorderLayout.CENTER);


    button = new JButton("HTML Editor");
    getContentPane().add(button);
    button.addActionListener(new ActionListener(){
       public void actionPerformed(ActionEvent ev){
           TopManager.getGHTMLEditor().setHTMLChunk(getInitText());
       }
    });


        //
    /*
       FormBase form=new FormBase();
       form.setDoc(new RealTimeArticle());

       //wrap into frame for testing:
       JFrame frame = new JFrame("Forms submission Window");
	     frame.addWindowListener(new WindowAdapter() {
	        //public void windowClosing(WindowEvent e) {System.exit(0);}
    	 });
	     frame.getContentPane().add(form, BorderLayout.CENTER);
       frame.pack();
       Utils.setCentalizedLocation(frame);

	     frame.show();
       //
   */

   /*
    java.applet.AppletContext ac=getAppletContext();
    if(ac!=null){

      java.applet.Applet a=ac.getApplet("JSGateway");
      if(a!=null){
         ((AWTApplet)a).invoke();
         System.out.println("invoked");
      }
      else System.out.println("Applet is NULL");
    }
    else{
      System.out.println("Applet context is null");
    }
     */
  }

  private URL codeBase; //cached codebase for images loading

  /**
   * converts filename to URL
   */
  public URL getURL(String filename) {

    URL url = null;
    if (codeBase == null) {
      codeBase = getCodeBase();
    }
    try {
      url = new URL(codeBase, filename);
    } catch (java.net.MalformedURLException e) {
      System.err.println("Couldn't create image: badly specified URL");
      return null;
    }
    return url;
  }

  public String getInitText(){

     try{
     /*
         if(window==null){
            System.err.println("Applet::getInitText: JSObject window was lost?");
            window = JSObject.getWindow(this);
         }
         */
         String param=getParameter("content_area");
         if(param==null){
            System.err.println("param content_area is not defined");
            return "";
         }
         String value=null;
               /*
         value=(String)window.eval(param);
                 */
         if(value==null)
            return "";
         else{
            //System.out.println(value);
         }

         //String parms[] = {""};
         //window.call("appletInit", parms);

         return value;
     }
     catch(Exception e){
         System.err.println("getInitText:"+e);
     }
     return "";
  }

  public String getData(){

     return data;
  }

  public void setData(String data){

     this.data=data;
  }

  public void appletCallback(String result){
  /*
     //System.out.println("appletCallback() called");
     try{
         if(window==null){
            System.err.println("Applet::appletCallback: JSObject window was lost?");
            window = JSObject.getWindow(this);
         }


         String filteredResult=Utils.safeString(result);

         String param=getParameter("content_area");
         if(param==null){
            System.err.println("param content_area is not defined");
         }

         window.eval(param + " = \'\'");

         java.io.StringReader sr=new java.io.StringReader(filteredResult);
         int howMany = 0;
         char b[] = new char[127];
         while ((howMany = sr.read(b, 0, b.length)) != -1){
            window.eval(param + " = "+param + " + \'" + new String(b,0,howMany) + "\'");
         }


     }
     catch(Exception e){
         System.err.println("appletCallback:"+e);
         //e.printStackTrace();
     }
    */
  }


}
