/**
 * @(#)InputFilter.java
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
package org.is.io;

import java.io.*;

public class InputFilter extends FilterInputStream {

 byte LF = 10;
 byte CR = 13;
 int MAX_INPUT_SIZE = 100000;
 byte lineBuffer[] = new byte[MAX_INPUT_SIZE];
 int numBytes;

 public InputFilter(InputStream inStream) {
  super(inStream);
 }

 public int read() throws IOException {

  return in.read();
 }

 public String readLine() throws IOException {
 
  numBytes = 0;
  boolean finished = false;
  do{
   int i = read();
   if(i == -1) return null;
   byte b = (byte)i;
   if(b == LF) {
    if(numBytes>0) {
     if(lineBuffer[numBytes-1] == CR)
      return new String(lineBuffer,0,numBytes-1);
    }
   }
   lineBuffer[numBytes] = b;
   ++numBytes;
   if(numBytes>=MAX_INPUT_SIZE){
      throw new NullPointerException("The stream is bigger than allowed: possible attack!");
   }
  } while (!finished);
  
  return null;
 }

}
