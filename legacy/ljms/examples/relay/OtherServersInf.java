/**
 * @(#)OtherServersInf.java
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
package org.is.server.relay;

/**
 * Container for all distributed servers information
 *
 * Was used with BroadcastingServer and not used now.
 * Probably these structures will be useful in Servlet distributing load
 * (partitioning users into groups: redirecting new users to different servers)
 *
 * @since   JDK1.0
 */
public class OtherServersInf{

    public String[] otherServerHosts;
    public int[] otherServerPorts;
    public boolean[] connectionExists;

    public OtherServersInf(int howManyOtherHosts){
    
       otherServerHosts=new String[howManyOtherHosts];
       otherServerPorts=new int[howManyOtherHosts];
       connectionExists=new boolean[howManyOtherHosts];

       for(int i=0; i<howManyOtherHosts; i++){
           connectionExists[i]=false;
       }
    }

    public int getListeningPort(String ip){
    
        if(otherServerHosts==null)throw new NullPointerException("OtherServerInf::not initialized!");
        for(int i=0; i<otherServerHosts.length; i++){
           if(ip.equals(otherServerHosts[i])) return otherServerPorts[i];
        }
        return 0;
    }

    public int getServer(String ip, int port){
    
        for(int i=0; i<connectionExists.length; i++){
           if(port==otherServerPorts[i] && ip.equals(otherServerHosts[i])) return i;
        }
        return -1;  //if no in list
    }

    /**
     * Returns true if all connections been established
     */
    public boolean allServersConnected(){

      if(connectionExists==null)return true; //no need to connect
      int num=connectionExists.length;

      for(int i=0; i<num; i++){
        if(!connectionExists[i]){
          return false;
        }
      }
      return true;
    }

    /**
     * Returns true if all connections established
     */
    public boolean noServersConnected(){

      if(connectionExists==null)return true; //no need to connect
      int num=connectionExists.length;

      for(int i=0; i<num; i++){
        if(connectionExists[i]){
          return false;
        }
      }
      return true;
    }

    public String[] getOtherServerHosts(){

      return otherServerHosts;
    }

    public int[] getOtherServerPorts(){

      return otherServerPorts;
    }
    public boolean[] isConnectionExists(){

      return connectionExists;
    }

    /**
     * For debugging purposes only
     */
    public String toString(){

        if(otherServerHosts==null)return ("{}");

        StringBuffer sb=new StringBuffer("{");
        for(int i=0; i<otherServerHosts.length; i++){
           sb.append("{");
           sb.append(otherServerHosts[i]);
           sb.append(":");
           sb.append(otherServerPorts[i]);
           sb.append(":");
           sb.append(connectionExists[i]);
           sb.append("}");
        }
        sb.append("}");
        return sb.toString();
    }


}
