package org.is.gui;

import java.awt.event.*;
import javax.swing.*;
import java.awt.Toolkit;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.Locale;
import javax.swing.event.*;

import org.is.gui.actions.ExitAction;
import org.is.util.Utils;
import org.is.gui.dialogs.SplashWindow;

/**
 * Main frame and entry point for GUI for P2P application (portal)
 *
 * @since jdk1.2
 */
public class MainFrame extends JFrame{

    protected MainFrameMenuBar menuBar;
    protected StatusBar statusBar;
    protected JTabbedPane tp;

    MainFrame(){

      super();

      TopManager.setMainFrame(this);

      SplashWindow.showProgress();

      SplashWindow.advanceProgress();

      //GUI:
      //setBackground(Color.lightGray);
	    setTitle(Resources.getString("MainFrame.title"));

      JPanel contentPane = (JPanel)getContentPane();

      SplashWindow.advanceProgress();//2

      contentPane.setLayout(new BorderLayout());

      contentPane.add(new MainPanel(),BorderLayout.CENTER);
//

      JPanel toolBarsWrappingPanel=new JPanel();
      toolBarsWrappingPanel.setLayout(new BoxLayout(toolBarsWrappingPanel, BoxLayout.Y_AXIS));
      contentPane.add(toolBarsWrappingPanel, BorderLayout.NORTH);

      JPanel wp = new JPanel();
      wp.setLayout(new BoxLayout(wp, BoxLayout.Y_AXIS));

      ToolBar toolBar1 = new ToolBar();
      wp.add(toolBar1);
      /*
      ToolBar2 toolBar2 = new ToolBar2();
      wp.add(toolBar2);
      */
      contentPane.add(wp, BorderLayout.NORTH);

      SplashWindow.advanceProgress();//3

	    menuBar = new MainFrameMenuBar(this);
	    setJMenuBar(menuBar);

      //StatusBar:
      statusBar=new StatusBar();
      contentPane.add(statusBar, BorderLayout.SOUTH);

			addWindowListener(new WindowAdapter(){
			   public void windowClosing(WindowEvent evt) {
					  (new ExitAction()).exit();
  			 }
			});

      SplashWindow.advanceProgress();//4

      SplashWindow.advanceProgress();//5

      pack();

      //get Dimensions of our frame from properties
      try{
         int f_width=Resources.getInteger("MainFrame.size.x");
         int f_height=Resources.getInteger("MainFrame.size.y");
	       this.setSize(new Dimension(f_width,f_height));
      }
      catch(Exception nfe){
         this.setSize(new Dimension(720,575));  //if not defined in Bundles
      }

      Utils.setCentalizedLocation(this);

      SplashWindow.advanceProgress();//7

      SplashWindow.hideProgress();

	    show();

      try{
        setIconImage(((ImageIcon)Images.getIcon("0")).getImage());
      }
      catch(Exception e){
        System.err.println("JFrameComponent::image loading failed:"+e);
      }
  }

  class MainPanel extends JPanel{

    public MainPanel(){

      setBorder(BorderFactory.createEmptyBorder(2,0,2,2));
      setLayout(new BorderLayout());
	    //setBorder(BorderFactory.createEtchedBorder());
      tp=new JTabbedPane();
      tp.setTabPlacement(JTabbedPane.LEFT);

      tp.addTab("Connect", /*Images.getIcon("connectTabIcon"),*/ new ConPanel());

      tp.addTab("Services", /*Images.getIcon("servicesTabIcon"),*/ new ServicesPanel());

      tp.addTab("GNutella", new JPanel());
      tp.setEnabledAt(2,false);

      tp.addTab("Chat", new JPanel());

      //org.is.html.TopManager.init();
      //org.is.html.GHTMLEditorPanel editor=new org.is.html.GHTMLEditorPanel();
      tp.addTab("HTML editor",/* editor*/new JPanel());
      tp.setEnabledAt(4,false);

      tp.addTab("Logs", new LogPanel());

      add(tp);
/*
      editor = new GHTMLEditor();

	    JScrollPane htmlScroller = new JScrollPane(editor); //editor
      add(htmlScroller, BorderLayout.CENTER);
      */
    }

  }

  public void setServiceStarted(boolean started){

    int indOfServicesTab=1;
    if(started){
      tp.setIconAt(indOfServicesTab, Images.getIcon("/images/green.gif"));
    }
    else{
      tp.setIconAt(indOfServicesTab, null/*Images.getIcon("/images/gray.gif")*/);
    }
  }

  public MainFrameMenuBar getMainFrameMenuBar(){

     return menuBar;
  }

  public void setMainFrameMenuBar(MainFrameMenuBar menuBar){

     this.menuBar=menuBar;
  }

  public StatusBar getStatusBar(){

     return statusBar;
  }

  public void setStatusBar(StatusBar statusBar){

     this.statusBar=statusBar;
  }

/**
 * Entry point of the application
 */
  public static void main(String args[]) {

    try{
       TopManager.init();
       MainFrame mf=new MainFrame();

    }catch (Throwable t) {
       System.out.println("uncaught exception: " + t);
       t.printStackTrace();
       try{Thread.sleep(50000);}catch(InterruptedException ie){}
    }
  }


}
