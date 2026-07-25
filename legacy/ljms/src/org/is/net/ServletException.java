/**
 * @(#)ServletException.java
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
package org.is.net;

import java.io.IOException;

/**
 * Implementation of javax.servlet.ServletException
 *
 * @since   JDK1.0
 */
public class ServletException extends Exception{

   private Throwable rootCause;

   public ServletException(){
   }

   public ServletException(Throwable rootCause){
   
     this.rootCause=rootCause;
   }

   public ServletException(String message){

     super(message);
   }

   public ServletException(String message, Throwable rootCause){

     super(message);
     this.rootCause=rootCause;
   }

   public Throwable getRootCause(){

     return rootCause;
   }

}
