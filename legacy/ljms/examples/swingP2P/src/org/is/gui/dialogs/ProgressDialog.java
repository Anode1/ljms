package org.is.gui.dialogs;

import javax.swing.border.*;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Random;

import org.is.gui.Images;

/**
 * Progress dialog.
 *
 * Not used for now
 */
public class ProgressDialog extends JWindow{

	public ProgressDialog(/*Frame f*/){

    //super();
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    getContentPane().setLayout(new BorderLayout());

		JPanel p = new JPanel(new BorderLayout());
    p.setBackground(Color.white);
		p.add(new JLabel(Images.getIcon("/images/amof15.jpg")), BorderLayout.CENTER);

		p.setBorder(new MatteBorder(1,1,1,1,Color.black));

    //this.getContentPane().add(p,BorderLayout.CENTER);
		setContentPane(p);

		Dimension screen = getToolkit().getScreenSize();
		pack();

		setLocation((screen.width - getSize().width) / 2,	(screen.height - getSize().height) / 2);
    show();
	}

  /*
  public synchronized void start(){

    if(worker==null){
      worker=new Thread(this);
    }
    if(!worker.isAlive()){
      worker.start();
    }
  }

  public synchronized void stop(){

    if(worker!=null){
      worker.interrupt();
    }
    worker=null;
  }

  public void run(){
    worker.setPriority(6);
    while(!disposed){
       repaint();
       //System.out.println("repaint");
       try{worker.sleep(100);}catch(InterruptedException ie){}
    }
  }
  */


       /*
  public void dispose(){

    disposed=true;
    if(worker!=null){
       worker.interrupt();
       worker=null;
    }
    super.dispose();
  }      */


}
