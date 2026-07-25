/** * @(#)TagsFilter.java
 * Copyright (C) 2001 Vasili Gavrilov *
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
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */package org.is.io;
import java.io.InputStream;
import java.io.BufferedInputStream;import java.io.IOException;

/**
 * Tags filter filtering out tags and calling handler (callbacks on each tag occurence)
 *
 * @since jdk1.0
 */
class TagsFilter extends BufferedInputStream{

    private int depth;
    private ParserCallback callback;

    public TagsFilter(InputStream in, ParserCallback callback){

        super(in);
        this.callback=callback;
    }

    public TagsFilter(InputStream in, int size, ParserCallback callback){

        super(in, size);
        this.callback=callback;
    }

    public int read() throws IOException{

        int c=super.read();
        if(c!='<')return c;

        mark(1024);        //'<' is red

        int cc=super.read();        if (cc != '!' ) {
          reset();
          return c;
        }

        //"<!" has been red:
        int ccc=super.read();
        if (ccc != '-' ) {
          reset();
          return c;
        }

        //"<!-" was red:
        int cccc=super.read();
        if (cccc != '-' ) {
          reset();
          return c;
        }

        //"<!-- " was red

        StringBuffer buffer=new StringBuffer(128);

        int aCommentChar=super.read();

        while(aCommentChar!=-1){

          if(aCommentChar=='<'){ //not valid comment!
              reset();
              return c;
          }
          if(aCommentChar=='-'){   //first '-'
              int second=super.read();
              if(second==-1){
                  reset();
                  return -1;
              }
              if(second=='-'){
                    int third=super.read();
                    if(third==-1){
                       reset();
                       return -1;
                    }
                    if(third=='>'){
                       //comment is finished!

                       //template.processComment(buffer,depth);
                       return super.read();  //read next
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
          aCommentChar=super.read(); //next char
        }//while

        //if we are here, comment is not finished
        reset();
        return c;
    }

    public int read(char cbuf[], int off, int len) throws IOException{

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

