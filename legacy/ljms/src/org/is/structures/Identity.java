/**
 * @(#)Identity.java
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
package org.is.structures;

/**
 * Base class for objects which can be considered as identities (having primary keys).
 * This class makes caching of hashcode for faster operations when frequently
 * stored in/retrieved from Hashtables. 
 *
 * @version 1.0
 * @since jdk1.1
 */
public class Identity{

  private String pk;
  private int hashCode; // hashcode caching -- speeds up lookup (if we do multiple put/get)

  /**
   * Default constructor. Notice: this constructor does not generates uid (primary
   * key) for this object. 
   */
  public Identity(){
  }

  public Identity(String pk){

    setPK(pk);
  }

  public final int hashCode(){

    return hashCode;
  }

  /**
   * Needed for hashcode caching
   */
  public final boolean equals(Object o){

    if(this==o) return true;
    else if(o==null || getClass()!=o.getClass()) return false;
    Identity other=(Identity)o;
    return pk.equals(other.pk);
  }

  public final void setPK(String pkn){

     pk=pkn;
     hashCode=pkn.hashCode();
  }

  public final String getPK(){

    return pk;
  }

  /**
   * For debugging purposes only
   */
  public String toString(){

	  return "Identity: "+pk+"(hashCode="+hashCode+")";
  }

}
