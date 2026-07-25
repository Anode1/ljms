package org.is.gui;

import javax.swing.Icon;

/**
 * Main Service container class
 *
 * @since jdk1.0
 */
public class Service{

  private int port;
  private String name;
  private String className;
  private boolean running;
  private Icon icon;

  public Service(String name, String className, int port){

    if(name==null)name="Unnamed";
    this.name=name;
    this.className=className;
    this.port=port;
  }

  public int getPort() {

    return port;
  }

  public void setPort(int newPort) {

    port = newPort;
  }

  public String getName() {

    return name;
  }

  public String getClassName() {

    return className;
  }

  public void setRunning(boolean r) {

    running = r;
  }

  public boolean getRunning() {

    return running;
  }

  public Icon getIcon() {

    return icon;
  }

  public void setIcon(Icon i) {

    icon=i;
  }

  public String toString(){

    StringBuffer sb=new StringBuffer("\n");
    sb.append(name);
    sb.append(":");
    sb.append(port);
    sb.append(":");
    sb.append(className);
    sb.append(":");
    sb.append(running?"running":"idle");
    return sb.toString();
  }

} 
