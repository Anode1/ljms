/**
 * @(#)HttpRequest.java
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

import java.io.IOException;
import java.io.InputStream;
import java.io.DataOutputStream;
import java.net.URLConnection;
import java.net.URL;

/**
 * Helper class for making one HTTP request (GET or POST).
 * <p>
 * Usage:<code>
 *<pre> URL url = new URL(urlStringToCGI);
 * or URL url = new URL(getCodeBase(), "/servlet/ServletName");  //for applets
 * HttpRequest msg = new HttpRequest(url);
 * InputStream in = msg.makeGetRequest(cgiParameters);
 * </pre></code>
 *
 * @version 1.0
 * @since   JDK1.0
 */
public class HttpRequest {

  /**
   * CGI or servlet to make request to
   */
  private URL cgi;

  /**
   * The constructor
   */
  public HttpRequest(URL cgi){

    this.cgi = cgi;
  }

  /**
   * Makes GET request to servlet or CGI with CGIParameters
   */
  public InputStream makeGetRequest(CGIParameters cgiparams) throws IOException {

    StringBuffer request = new StringBuffer(cgi.toExternalForm());

    if(cgiparams != null && cgiparams.size()>0){
      request.append("?");
      request.append(cgiparams.getEncoded());
    }

    URL url = new URL(request.toString());

    URLConnection con = url.openConnection();
    con.setUseCaches(false);

    return con.getInputStream();
  }

  /**
   * makes a POST request to servlet or CGI, posting CGIParameters.
   */
  public InputStream makePostRequest(CGIParameters cgiparams) throws IOException {

    String argString = null;
    if(cgiparams != null && cgiparams.size()>0) {
      argString = cgiparams.getEncoded();
    }
    else argString="";

    URLConnection con = cgi.openConnection();

    con.setDoInput(true);
    con.setDoOutput(true);
    con.setUseCaches(false);

    // do not remove this (dealing with Netscape bug)
    con.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

    DataOutputStream out = new DataOutputStream(con.getOutputStream());
    out.writeBytes(argString);
    out.flush();
    out.close();

    return con.getInputStream();
  }


}
