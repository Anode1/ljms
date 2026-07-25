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
public class SplashWindow extends JWindow{// extends JFrame/* implements Runnable*/{

	private static SplashWindow pd;

	private JProgressBar progress;
  //private Thread worker;
  //private boolean disposed;

	public SplashWindow(/*Frame f*/){

    //super();

		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));

    getContentPane().setLayout(new BorderLayout());

		JPanel p = new JPanel(new BorderLayout());
    p.setBackground(Color.white);
	 //	p.add(new JLabel(Images.getIcon("logo")), BorderLayout.CENTER);
		p.add(new JLabel(Images.getIcon("/images/amof15.jpg")), BorderLayout.CENTER);   

		progress = new JProgressBar(0,7);
		progress.setStringPainted(true);
		progress.setBorderPainted(false);
		progress.setString("Starting...");
		progress.setBackground(Color.white);
		p.add(progress, BorderLayout.SOUTH);

		p.setBorder(new MatteBorder(1,1,1,1,Color.black));

    //this.getContentPane().add(p,BorderLayout.CENTER);
		setContentPane(p);

		Dimension screen = getToolkit().getScreenSize();
		pack();

		setLocation((screen.width - getSize().width) / 2,	(screen.height - getSize().height) / 2);
    show();
	}

	public static void showProgress(){

		pd = new SplashWindow();
    //try{Thread.sleep(200);}catch(Exception e){}
    //pd.start(); //start repainter
	}

	public static void hideProgress(){

		if(pd != null){
			pd.dispose();
			pd = null;
		}
	}

	public static void advanceProgress(){

		if(pd != null){
			pd.advance();
    }
	}

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
       worker.interrupt();
       worker=null;
    }
    super.dispose();
  }      */


}
