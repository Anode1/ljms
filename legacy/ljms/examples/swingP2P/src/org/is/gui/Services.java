package org.is.gui;

import java.util.Vector;

/**
 * Container class for services
 *
 * @since jdk1.2
 */
public class Services{

  private Vector v=new Vector();

  public Services(){
  
    int n=strData.length;
    for(int i=0; i<n; i++){

       try{
          String[] aService=strData[i];
          Service s=new Service(aService[0], aService[1], Integer.parseInt(aService[2]));
          String iconPath=aService[3];
          if(iconPath!=null && !(iconPath.trim().equals(""))){
             s.setIcon(Images.getIcon(iconPath));
          }

          v.addElement(s);
       }
       catch(Exception e){
          System.err.println("Services::"+e);
       }
    }
  }

  private final String[][] strData = new String[][]{

    {"Chat",      "org.is.server.ClientConnection", "6100", "/images/0.gif"},
    {"Chat",      "org.is.server.ClientConnection", "6101", "/images/0.gif"},
    {"GNutella",  "org.is.nut.NutServer",        "6000", "/images/fury.gif"},
    {"Echo",      "org.is.net.EchoTest",         "6200", "/images/undo.gif"},

    {"Ftp",      "org.is.server.ClientConnection", "6021", "/images/repository.gif"},
    {"WWW",      "org.is.server.ClientConnection", "80", "/images/apache.gif"},
    {"smtp",     "org.is.net.EchoTest",         "6025", null},
    {"telnet",   "org.is.server.ClientConnection", "6023", null},
    {"ssh1",      "org.is.nut.NutServer",        "6000", null},
    {"ssh2",      "org.is.net.EchoTest",         "6200", null}
  };

  public Vector getServices(){

    return v;
  }

  public String toString(){
  
    return v.toString();
  }

  public static void main(String[] args){
  
    System.out.println(new Services().toString());
  }

}
