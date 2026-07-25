/**
 * @(#)TestInvoker.java
 * Copyright (C) 2001 Vasili Gavrilov
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. 
 */
package org.is.util;

import java.awt.Frame;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.util.*;

/**
 * Testing application
 *
 * @version 1.0 02/08/00
 * @since jdk1.0 
 */
public class TestInvoker extends Frame{

  public TestInvoker() {

    validate();
    setVisible(true);
    addWindowListener( new WindowAdapter() {
	        public void windowClosing(WindowEvent e) {
              System.exit(0);
	        }
    });
  }

  public void test(){

    try{
        Invoker invoker=new Invoker(this); //against this class
        invoker.invoke("someMethod1");
        invoker.invoke("someMethod2","someArg");
        String[] array={"arg1","arg2","arg3"};
        invoker.invoke("someMethod3",array);

    }
    catch(Exception e){
      e.printStackTrace();
    }

  }

  public void someMethod1(){
     System.out.println("method1 has been invoked");
  }

  public void someMethod2(String arg){
     System.out.println("method2 has been invoked with arg:"+arg);
  }

  public void someMethod3(String arg1, String arg2, String arg3){
     System.out.println("method3 has been invoked with args:"+arg1+","+arg2+","+arg3);
  }


  public static void main(String[] args) {

    (new TestInvoker()).test();
  }

}
