/**
 * @(#)PasswordDialog.java
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
package common;

import java.awt.*;
import StationManager;

public class PasswordDialog extends Panel{

  Button bOK;
  StationManager parent;
  public Choice nameChooser;


  public PasswordDialog( StationManager parent ) {

      this.parent = parent;
      setGUI();
            
   }
    
  public void setGUI(){

      setBounds(0,0, parent.d.width, parent.d.height);

      setLayout(null);

      Font f = new Font( "Arial", Font.BOLD, 16 );
      String title1 = "Please pick a name:";
      Label label1 = new Label(title1);
      label1.setFont(f);
      FontMetrics fm = label1.getFontMetrics(f);
      label1.setBounds((parent.d.width-fm.stringWidth (title1))/2, 100, fm.stringWidth(title1)+3,fm.getHeight());
      add(label1);


      Rectangle temp =  label1.getBounds();

      nameChooser = new Choice();
      for (int i=0 ; i<parent.nNames ; i++  )
        nameChooser.add(parent.names[i]);


      add(nameChooser);
      nameChooser.setBounds(temp.x, temp.y+50, temp.width, temp.height);

	    bOK = new Button("  OK  ");
      bOK.setFont(f);
      add(bOK);
      bOK.setBounds(temp.x+(temp.width-80)/2,temp.y+110, 80, 30);

      validate();


  }


    public boolean action(Event e, Object arg) {
            String name = nameChooser.getSelectedItem();
            if (e.target == bOK && name.length() > 0 ) {

                parent.composeName(name);
                setVisible(false);
                    
               }

          return true;
    }


}