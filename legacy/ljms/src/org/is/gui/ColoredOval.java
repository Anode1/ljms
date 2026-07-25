package org.is.gui;

import javax.swing.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

public class ColoredOval implements Icon{

    Color color;
    int radius=16;

    public ColoredOval(){
      this(Color.red);
    }

    public ColoredOval(Color c){
      color = c;
    }

    public ColoredOval(int radius, Color c){
      color = c;
      this.radius=radius;
    }

    public void paintIcon(Component c, Graphics g, int x, int y){
      g.setColor(color);
      g.fillOval(x, y, radius, radius);
    }

    public int getIconWidth(){
      return radius;
    }

    public int getIconHeight(){
      return radius;
    }

}
