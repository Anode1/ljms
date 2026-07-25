/**
 * @(#)Servlet.java
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

/**
 * Plays the role of javax.servlet.Servlet but is lightweight. Uses org.is.net
 * multithreaded server framework and abstracts its implementation: user
 * extends Servlet class not dealing with threads, thread pools etc.
 * To use another server implementation (where ImplXServer - another implementation),
 * we change this facade class.
 *
 * @since   JDK1.0
 */
import java.io.IOException;

public class Servlet extends Impl3Server{
       
}
