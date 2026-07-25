/**
 * @(#)Invoker.java
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

import java.lang.reflect.*;

/**
 * Generic purpose mathods invoker
 *
 * @since jdk1.0 
 */
public class Invoker{

  protected Object targetObject;
  protected Class targetClass;//not used for now

/**
 * constructor
 */
  public Invoker(Object targetObject){
    this.targetObject=targetObject;
  }

/**
 * constructor if we have class variable (static methods) - not used for now
 */
  public Invoker(Class targetClass){
    this.targetClass=targetClass;
  }

  /**
   * Simplest case when there are no argument passed to the method
   */
  public Object invoke(String methodToInvoke)throws NoSuchMethodException, InvocationTargetException, IllegalAccessException{

      Method method=targetObject.getClass().getMethod(methodToInvoke, null);
      return method.invoke(targetObject, null);
  }

  /**
   * Convenience method when there is one argument and we do not know type
   */
  public Object invoke(String methodToInvoke, Object parameter)throws NoSuchMethodException, InvocationTargetException, IllegalAccessException{

      Object[] parameters=null;
      Class[] types=null;
      if(parameter!=null){
        parameters=new Object[1];
        parameters[0]=parameter;
        types=new Class[1];
        types[0]=parameter.getClass();                //determining the type:
      }
      return this.invoke(methodToInvoke,types,parameters);
  }

  /**
   * Convenience method when there is one argument and we do not know type
   */
  public Object invoke(String methodToInvoke, Class type, Object parameter)throws NoSuchMethodException, InvocationTargetException, IllegalAccessException{

      Class[] types=new Class[1];
      types[0]=type;
      Object[] parameters=new Object[1];
      parameters[0]=parameter;
      return this.invoke(methodToInvoke,types,parameters);
  }

/**
 * Generic case without knowledge what types are
 */
  public Object invoke(String methodToInvoke, Object[] parameters)throws NoSuchMethodException, InvocationTargetException, IllegalAccessException{

      Class[] parameterTypes=null;
      if(parameters!=null){
        int numParams=parameters.length;
        parameterTypes=new Class[numParams];
        for(int i=0;i<numParams;i++){
            parameterTypes[i]=parameters[i].getClass();
        }
      }
      Method method=targetObject.getClass().getMethod(methodToInvoke, parameterTypes);
      return method.invoke(targetObject, parameters);
  }

/**
 * The most generic case
 */
  public Object invoke(String methodToInvoke, Class[] parameterTypes, Object[] parameters)throws NoSuchMethodException, InvocationTargetException, IllegalAccessException{

      Method method=targetObject.getClass().getMethod(methodToInvoke, parameterTypes);
      return method.invoke(targetObject, parameters);
  }


  /**
   * Convenience static method
   */
  public static Object invoke(Object targetObject, String methodToInvoke, Class[] parameterTypes, Object[] parameters)throws NoSuchMethodException, InvocationTargetException, IllegalAccessException{

      Method method=targetObject.getClass().getMethod(methodToInvoke, parameterTypes);
      return method.invoke(targetObject, parameters);
  }

}