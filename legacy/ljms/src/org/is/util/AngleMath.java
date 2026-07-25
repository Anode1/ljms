/**
 * @(#)AngleMath.java
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

/**
 * Some cached mathematical functions (tables for those).
 * These tables has been created only once (singleton) and after we have very
 * fast access to them (vs using of Math package)
 *
 * @version 1.0
 * @since   JDK1.0
 */
public final class AngleMath {

  /**
   * Ref to the single instance of this singleton
   */
  private static AngleMath instance;

  /**
   * Size of all arrays (tables for functions have to be cached)
   */
  private final int TABLE_SIZE = 360;    //1 degree resolution is sufficient for the most cases

  private int HALF_TABLE = TABLE_SIZE/2;

  /**
   * Coefficient for degree to radians transformation
   */
  private double DEG_TO_RAD = (2.0f * Math.PI) / 360.0f;

  /**
   * Coefficient for radians to degree transformation
   */
  private double RAD_TO_DEG = 1.0 / DEG_TO_RAD;

  //functions tables:
  private double cos_table[];
  private double sin_table[];
  private double atan_table[][];
  private double sqrt_table[][];

  /**
   * Default constructor, used only internally
   */
  private AngleMath(){

    init();
  }

  /**
   * Returns the instance of this singleton
   */
  public static AngleMath getInstance(){

    if(instance==null)instance=new AngleMath();
    return instance;
  }

  /**
   * Initializer, called internally from the constructor only.
   * Fills the tables.
   */
  private void  init(){

 	   cos_table  = new double[TABLE_SIZE];
	   sin_table  = new double[TABLE_SIZE];
     atan_table = new double[TABLE_SIZE][TABLE_SIZE];
     sqrt_table = new double[TABLE_SIZE][TABLE_SIZE];

     double temp;

     for (int i=0; i<TABLE_SIZE; i++) {
	         temp = DEG_TO_RAD*(double)i;
	         cos_table[i] = Math.cos(temp);      //System.out.print((double)cos_table[i]+" ");
	         sin_table[i] = Math.sin(temp);      //System.out.print((double)sin_table[i]+" ");
     }

     double x,y;

     for (int i=0; i<TABLE_SIZE; i++)
          for (int j=0; j<TABLE_SIZE; j++){
              x= i - HALF_TABLE;
              y= j - HALF_TABLE;

              temp = Math.atan2(x,y);
              temp *= RAD_TO_DEG;


              if (temp < 0.0)
                 temp += 360.0;

              atan_table[i][j]=temp;
              //if(i%45==0 && j%45==0)System.out.println("i="+i+" j="+j+" atan="+(temp-180));

              sqrt_table[i][j]= (double)Math.sqrt(x*x+y*y);
      }

  }//init


    /**
     * Cos functon. Argument to be passed is degrees
     */
    public double cos(int degree) {

        if (degree >= 360)
	         degree = degree % 360;

        else if (degree < 0){
           degree = (-degree)%360;
        }

        return cos_table[degree];
    }

    /**
     * Sin sunction. Argument to be passed is degrees
     */
    public double sin(int degree) {

        if (degree >= 360)
	         degree = degree % 360;

        else if (degree < 0){
           degree = (-degree)%360;
           return -sin_table[degree];
        }

        return sin_table[degree];
    }

  /**
   * Computes angle between two vectors
   */
  public double computeAngle(int v1x,int v1y){

     return atan2(v1x, v1y);
  }

  /**
   * The same as computeAngle. For convenience has the same name as in Math package
   */
  public double atan2(int v1x, int v1y){

     int halfOgTableSize=TABLE_SIZE>>1;
     while(Math.abs(v1x)>=halfOgTableSize || Math.abs(v1y)>=halfOgTableSize){
        v1x=v1x>>1;
        v1y=v1y>>1;
     }
     v1x+=halfOgTableSize;
     v1y+=halfOgTableSize;

     return atan_table[v1x][v1y];
  }

  /**
   * Computes magnitude
   */
  public double computeMagnitude(int v1x,int v1y) {

     return sqrt_table[v1x][v1y];
  }

  /**
   * Returns random number in the range 0..Max
   */
  public int getRand(int Max) {

     return (int)(Math.random() * Max);
  }

  /**
   * Transforms Degrees to Radians
   */
  public double deg2Rad(double deg){

     return DEG_TO_RAD*deg;
  }

  /**
   * Transforms Radians to Degrees
   */
  public double rad2Deg(double rad){

     return RAD_TO_DEG*rad;
  }

}
