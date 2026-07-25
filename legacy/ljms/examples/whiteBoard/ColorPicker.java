/**
 * @(#)ColorPanel.java
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

/**
 * Notice that this class is not optimized and made for demo only
 */
import java.awt.*;

public class ColorPicker extends Canvas {

      Image   offScreenImage;
      Graphics offScreenGraphics;
	    Button   colorIndicator ;
	    
	    int width, height;
	    MainDrawingPanel target;
	    Image palette;
	    
	    public Color color2Pass;
	    Color[][] colors = {
                          {new Color( 0,0,0 ), new Color( 88,88,88 ),new Color( 120,120,120 ),new Color(166,166,166 ),
                            new Color( 255,255,255 ),new Color( 192,255,157 ),new Color( 236,244,145 ),new Color( 246,231,9 ),
                            new Color( 255,192,0 ),new Color( 247,199,151), new Color( 246,165,121 ),new Color( 244,142,83 ),
                            new Color( 247,120,8 ),new Color( 245,71,11 ),new Color( 231,25,8 ),new Color( 194,18,12 )

	                       },
	                       
	                        {new Color( 11,128,136 ),new Color( 0,166,163 ),new Color( 10,232,246 ),new Color( 0,186,238 ),
                            new Color( 75,135,253 ),new Color( 40,41,247 ),new Color( 61,0,178 ),new Color( 87,0,138 ),
                            new Color( 127,9,194 ),new Color( 169,14,227 ),new Color( 214,107,243 ),new Color( 247,89,211 ),
                            new Color( 249,64,159 ),new Color( 213,0,153 ),new Color( 183,24,104 ),new Color( 215,55,88 )

	                       },

                         {new Color( 0,104,102 ),new Color( 11,142,92 ),new Color( 0,117,63 ),new Color( 32,91,39 ),
                            new Color( 26,144,0 ),new Color(  9,222,25 ),new Color( 167,226,17 ),new Color( 130,153,0 ),
                            new Color( 168,156,9 ),new Color( 174,113,17 ),new Color( 141,88,24 ),new Color( 136,56,22 ),
                            new Color( 104,40,8 ),new Color( 124,62,52 ),new Color( 151,88,86 ),new Color( 180,131,126)

	                       }
	                       };
	    int nHor=16;
	    int nVer=3;

        
        public  ColorPicker(Button  colorIndicator,Image palette, MainDrawingPanel target) {
            
                this.colorIndicator = colorIndicator;
                this.palette = palette;
                width = palette.getWidth(null);
                height = palette.getHeight(null);
                this.target = target;


                color2Pass = new Color(0,0,255);//default value
                
	              setSelectedColor();
                               
                repaint();
        }

        
 
        public void paint(Graphics g) {
                


                if (offScreenImage == null ||
                    offScreenImage.getHeight(null) != height ||
                    offScreenImage.getWidth(null) != width ) {
                        offScreenImage = createImage(width, height);
                        offScreenGraphics = offScreenImage.getGraphics();
                
                

                        
                }


                offScreenGraphics.drawImage(palette, 0, 0, this);
                g.drawImage(offScreenImage, 0, 0, this);
		        
               
        }
        
        
        
        public void setSelectedColor(){
               
               colorIndicator.setBackground(color2Pass);
               colorIndicator.validate();
               target.penColor=color2Pass;

                
                
        }
        
        
        
        public boolean mouseDown(java.awt.Event evt, int x, int y) {
            
               if (x>0&&x<width&&y>0&&y<height) {
                   
                    int i = (int) x*nHor/width; 
                    int j = (int) y*nVer/height;
                    
                    color2Pass = colors[j][i];
                    setSelectedColor();
                    
               }

               return true;
        }
        
        
        public void update(Graphics g) {
                paint(g);
        }  
        
     


       
        
}