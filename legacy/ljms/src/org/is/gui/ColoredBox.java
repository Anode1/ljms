/**
 * @(#)ColoredBox.java
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
 */
package org.is.gui;

import javax.swing.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

public class ColoredBox implements Icon{

    Color color;
    int width=10;
    int height=10;

    public ColoredBox(){
      this(Color.black);
    }

    public ColoredBox(Color c){
      color = c;
    }

    public void paintIcon(Component c, Graphics g, int x, int y){
      g.setColor(color);
      g.fillRect (x, y, getIconWidth(), getIconHeight());
    }

    public int getIconWidth(){

      return width;
    }

    public int getIconHeight(){

      return height;
    }

  }
