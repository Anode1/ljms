/** * @(#)HtmlCommentsFilter.java
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
 */package org.is.io;
import java.io.FilterReader;import java.io.IOException;
import java.io.BufferedReader;

/**
 * Processor of html comments in a file.
 * Filters out html comments making callback to Template when finds each.
 * @see Templates
 * @see IBrandedWebComponent
 *
 * @version 1.0
 * @since jdk1.0
 */
class HtmlCommentsFilter extends FilterReader {

    private int depth;

   /**
    * Default constructor
    * Notice: we use BufferedReader here to support mark
    */
    public HtmlCommentsFilter(int depth, BufferedReader in){

        super(in);
        //this.template=template;
        this.depth=depth;
    }

    public int read() throws IOException {

        int c=in.read();
        if(c!='<')return c;

        in.mark(128);        //'<' is red

        int cc=in.read();        if (cc != '!' ) {
          in.reset();
          return c;
        }

        //"<!" has been red:
        int ccc=in.read();
        if (ccc != '-' ) {
          in.reset();
          return c;
        }

        //"<!-" was red:
        int cccc=in.read();
        if (cccc != '-' ) {
          in.reset();
          return c;
        }

        //"<!-- " was red

        StringBuffer buffer=new StringBuffer(128);

        int aCommentChar=in.read();

        while(aCommentChar!=-1){

          if(aCommentChar=='<'){ //not valid comment!
              in.reset();
              return c;
          }
          if(aCommentChar=='-'){   //first '-'
              int second=in.read();
              if(second==-1){
                  in.reset();
                  return -1;
              }
              if(second=='-'){
                    int third=in.read();
                    if(third==-1){
                       in.reset();
                       return -1;
                    }
                    if(third=='>'){
                       //comment is finished!

                       //template.processComment(buffer,depth);
                       return in.read();  //read next
                    }
                    else{
                       buffer.append("--");
                       buffer.append(third);
                    }
              }
              else{
                   buffer.append('-');
                   buffer.append(second);
              }
          }
          else{
              buffer.append((char)aCommentChar);
          }
          aCommentChar=in.read(); //next char
        }//while

        //if we are here, comment is not finished
        in.reset();
        return c;
    }

    public int read(char cbuf[], int off, int len) throws IOException {

        for (int i=off; i<len; i++) {
            int c = read();
            if (c == -1) {
                return i - off;
            }
            cbuf[i] = (char) c;
        }
        return len;
    }

 }

