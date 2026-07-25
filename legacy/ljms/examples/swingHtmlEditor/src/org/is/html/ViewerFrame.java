package org.is.html;

import java.awt.event.*;
import javax.swing.*;
import java.net.URL;
import java.awt.*;
import java.io.IOException;
import java.util.Locale;
import javax.swing.event.*;
import javax.swing.text.html.*;
import javax.swing.text.*;

import org.is.util.Utils;
import org.is.gui.StatusBar;

/**
 * Main viewer frame
 *
 * @since jdk1.2
 */
public class ViewerFrame extends JFrame{

    //protected MainFrameMenuBar menuBar;
    protected StatusBar statusBar;
    protected HTMLPanel htmlPanel;

    ViewerFrame(){

	    //setTitle("Viewer");

      Container contentPane = getContentPane();
      contentPane.setLayout(new BorderLayout());

      htmlPanel = new HTMLPanel();

	    JScrollPane htmlScroller = new JScrollPane(); //editor
      htmlScroller.setViewportView(htmlPanel);
      contentPane.add(htmlScroller, BorderLayout.CENTER);

	    //setJMenuBar(menuBar = new MainFrameMenuBar(this));

      //StatusBar:
      statusBar=new StatusBar();
      contentPane.add(statusBar, BorderLayout.SOUTH);

      pack();

      //get Dimensions of our frame from properties
      try{
         int f_width=Resources.getInteger("MainFrame.size.x");
         int f_height=Resources.getInteger("MainFrame.size.y");
	       this.setSize(new Dimension(f_width,f_height));
      }
      catch(Exception nfe){this.setSize(new Dimension(720,575));} //if not defined in Bundles

      Utils.setCentalizedLocation(this);

	    show();
  }

  public void setText(String text){
  
     htmlPanel.set(text);
  }

  class HTMLPanel extends JTextPane{

  public HTMLPanel(){

     setContentType("html/text");
     //setEditorKitForContentType("html/text", new HTMLEditorKit());
     setEditorKit(new HTMLEditorKit());
     this.setEditable(false);
     
     addHyperlinkListener(new HyperlinkListener() {
				public void hyperlinkUpdate(HyperlinkEvent evt) {
					if (evt.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
						URL thisPage = getPage();

            java.awt.Frame f=JOptionPane.getRootFrame();
						Cursor oldCursor = f.getCursor();

						try{
							f.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
							setPage(evt.getURL());
						}catch (IOException e) {
							System.out.println("GHTMLEditor::Page switch failed - revert to old page");
							try{
								setPage(thisPage);
							}catch (IOException ioexc) {
								System.out.println("GHTMLEditor::Failed to revert to old page");
							}
						}finally{
							f.setCursor(oldCursor);
						}
					}
				}
			});
  }

  /**
   * Workaround a bug in DefaultStyledDocument.remove, which is
   * invoked when setText is invoked.
   */
  public void set(String text){

     clearAllText();
     try{
        getEditorKit().read(/*new BufferedReader(*/new java.io.StringReader(text)/*)*/,this.getDocument(),0);
     }
     catch(Exception e){
        e.printStackTrace();
     }
     //setCaretPosition(0);
  }

  public void clearAllText() {

    Document doc=getEditorKit().createDefaultDocument();
    doc.putProperty("IgnoreCharsetDirective", new Boolean(true));
		setDocument(doc);
  }

  }

 /**
  * Entry point of the application
  */
  public static void main(String args[]) {

    //check java version:
    String vers = System.getProperty("java.version");
    if(vers.compareTo("1.3") < 0){
       System.out.println("VM must be 1.3 or higher - exiting");
       System.exit(1);
    }

    try{
       ViewerFrame viewer=new ViewerFrame();
       viewer.setText(new TextProducer().getText());
      // viewer.setText("<html><body>asdf adsf das f</body></html>");
    }catch (Throwable t) {
       System.out.println("uncaught exception: " + t);
       t.printStackTrace();
       try{Thread.sleep(50000);}catch(InterruptedException ie){}
    }
  }

}
