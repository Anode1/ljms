package org.is.html.dialogs;

import javax.swing.border.*;
import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.util.Random;

import org.is.html.Images;

/**
 * Progress dialog.
 *
 * Not used for now
 */
public class ProgressDialog extends JWindow{// extends JFrame/* implements Runnable*/{

	private JProgressBar progress;
  //private Thread worker;
  //private boolean disposed;

	public ProgressDialog(/*Frame f*/){

    //super();

		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    this.getContentPane().setLayout(new BorderLayout());

		JPanel p = new JPanel(new BorderLayout());
    p.setBackground(Color.white);
		//p.add(new JLabel(Images.getIcon("logo")), BorderLayout.CENTER);
		progress = new JProgressBar(0,7);
		progress.setStringPainted(true);
		progress.setBorderPainted(false);
		progress.setString("HTML Editor is starting...");
		progress.setBackground(Color.white);
		p.add(progress, BorderLayout.CENTER);
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
      worker.stop();
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

	public void advance(){

		try{   /*
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run(){ */
					progress.setValue(progress.getValue() + 1);
          //progress.validate();
          progress.repaint();
          /*
				}
			});
			Thread.yield();
      */
		}
		catch(Exception e){
			System.err.println(e);
		}
	}
       /*
  public void dispose(){

    disposed=true;
    if(worker!=null){
       worker.stop();
       worker=null;
    }
    super.dispose();
  }      */

  
}
