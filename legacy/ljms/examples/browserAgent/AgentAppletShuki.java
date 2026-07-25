import java.awt.event.*;
import java.applet.*;
import java.awt.*;
import java.net.URL;
import java.util.Vector;

import org.is.http.*;
import netscape.javascript.JSObject;

/**
 * Invisible Applet working as a message terminal for LJMS.
 *
 * @version 1.0
 * @since jdk1.0 (+ netscape.javascript.JSObject must be supported natively by
 * the browser)
 */
public class AgentApplet extends Applet implements HttpRequestListener{

  private JSObject window;
  private PeriodicHttpRequestor requestor;

  public void init(){

    try{
       window = JSObject.getWindow(this);

       CGIParameters params=new CGIParameters();
       params.addParam("client_name","bbb");
       params.addParam("chat_room","0");
       params.addParam("cmd","refresh");
       
       URL url=new URL(getParameter("url"));

       requestor=new PeriodicHttpRequestor(this, url, params);

    }catch(Exception ex){
       System.err.println(ex);
    }
  }

  public void start(){
     requestor.start();
  }

  public void stop(){
     requestor.interrupt();
  }

  public void dataReady(byte[] data){
    try{
     String[] parms={new String(data)};
     window.call("refreshData", parms);
    }
    catch(Exception e){
     System.err.println(e);
    }

  }
  

}
