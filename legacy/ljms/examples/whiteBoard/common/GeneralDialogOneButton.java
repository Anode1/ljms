/**
 * @(#)GeneralDialogOneButton.java
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

public class GeneralDialogOneButton  extends Dialog {

    Button butOK;
    String message1; String message2;


     public GeneralDialogOneButton ( Frame frame, String title, String message1, String message2, boolean modal) {

       super(frame,title,modal);
       this.message1 = message1;
       this.message2 = message2;
       createDialog();
     }

     public void createDialog() {
        

      GridBagLayout gridbag = new GridBagLayout();
	    GridBagConstraints constraints = new GridBagConstraints();
	    setLayout(gridbag);
    
    	buildConstraints(constraints, 0, 0, 3, 1, 100, 45);
	    constraints.fill = GridBagConstraints.NONE;
	    constraints.anchor = GridBagConstraints.CENTER;
      //constraints.anchor = GridBagConstraints.NORTH;
      Label question1 = new Label(message1);
	    gridbag.setConstraints(question1, constraints);
	    add(question1);

    	buildConstraints(constraints, 0, 1, 3, 1, 100, 45);
	    constraints.fill = GridBagConstraints.NONE;
	    constraints.anchor = GridBagConstraints.CENTER;
      Label question2 = new Label(message2);
	    gridbag.setConstraints(question2, constraints);
	    add(question2);


    	buildConstraints(constraints, 2, 2, 1, 1, 20,10);
	    constraints.fill = GridBagConstraints.NONE;
	    constraints.anchor = GridBagConstraints.SOUTH;
	    butOK = new Button("  OK  ");
	    gridbag.setConstraints(butOK, constraints);
	    add(butOK);

        
    }
    
    
    void buildConstraints(GridBagConstraints gbc, int gx, int gy, 
                                int gw, int gh,
                                int wx, int wy) {
	            gbc.gridx = gx;
	            gbc.gridy = gy;
	            gbc.gridwidth = gw;
	            gbc.gridheight = gh;
	            gbc.weightx = wx;
	            gbc.weighty = wy;
        }


    public void closeDialog(){
        hide()  ;
        dispose();
    }

    public boolean action(Event e, Object arg) {
          if (e.target instanceof Button)
                closeDialog() ;

          return true;
    }
    
    public boolean handleEvent(Event evt) {
        if(evt.id==Event.WINDOW_DESTROY)
             closeDialog() ;

        else if (evt.id == Event.ACTION_EVENT ) {

             if(evt.arg.equals(butOK.getLabel()))
                  closeDialog() ;


     }
     return true;
   }
}

