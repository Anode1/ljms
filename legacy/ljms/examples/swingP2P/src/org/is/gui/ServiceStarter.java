/** * @(#)ServiceStarter.java
 */
package org.is.gui;
import java.io.IOException;import javax.swing.JOptionPane;import org.is.net.Servlet;/** * Service starter thread
 *
 * @since jdk1.0
 */
public class ServiceStarter extends Thread{

  private Service service;

  /**
   * Priority of this helper thread.
   * Range: 1..10. Default is used if not ovewritten in system.properties.
   */
  protected int priority=4;

  ServiceStarter(Service service){

     setPriority(priority);
     this.service=service;
  }

  public void run(){

     int port=service.getPort();

     try{
	      Class cl = Class.forName(service.getClassName());
	      Servlet s = (Servlet)cl.newInstance();
        s.startService(port);
     }
     catch(java.net.BindException se){
        JOptionPane.showMessageDialog(null, "Port "+port+" has been used by one of the services - specify another port", "Warning", JOptionPane.WARNING_MESSAGE, null);
     }
     catch(Throwable e){
        JOptionPane.showMessageDialog(null, "Starting of "+service.getName()+" failed:"+e, "Error", JOptionPane.WARNING_MESSAGE, null);
     }

  }//run


}

