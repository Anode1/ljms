/**
 * @(#)AlignableBox.java
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

import javax.swing.Box;
import javax.swing.BoxLayout;

/**
 * the same as javax.swing.Box but with alignment capability
 */
public class AlignableBox extends Box{

  protected float alignmentX=0;
  protected float alignmentY=0;

  public AlignableBox(int axis) {
     super(axis);
  }

  public static Box createHorizontalBox() {
     return new AlignableBox(BoxLayout.X_AXIS);
  }

  public static Box createVerticalBox() {
     return new AlignableBox(BoxLayout.Y_AXIS);
  }

  public float getAlignmentX() {
     return alignmentX;
  }

  public void setAlignmentX(float alignmentX) {
     this.alignmentX = alignmentX;
  }

  public float getAlignmentY() {
     return alignmentY;
  }

  public void setAlignmentY(float alignmentY) {
     this.alignmentY = alignmentY;
  }

}

