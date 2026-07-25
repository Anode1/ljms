package org.is.html;

import java.io.IOException;
import java.io.Reader;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import javax.swing.text.html.parser.DocumentParser;
import javax.swing.text.html.parser.DTD;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLEditorKit.*;
import javax.swing.text.MutableAttributeSet;

/**
 * HTML parser
 * HTMLEditorKit.ParserCallback callback =
 *    new HTMLEditorKit.ParserCallback () {
 *      public void handleText(char[] data, int pos) {
 *          System.out.println(data);
 *      }
 *    };
 *  Reader reader = new FileReader("myFile.html");
 *  new ParserDelegator().parse(reader, callback, false);
 *
 * @version 1.0
 * @since jdk1.2
 */
public class GParserDelegator extends ParserDelegator{
  /*
  private DocumentParser parser=new DocumentParser(getDTD());

  private DTD getDTD() {

    try{
      return javax.swing.text.html.parser.DTD.getDTD("HTML");
    }
    catch (IOException e) {
      System.err.println("HTMLParser::getDTD:"+e);
    }
    return null;
  }
    */

    /**
     //implement this for new html DTD

  protected static DTD createDTD(DTD dtd, String name) {
    InputStream in = null;
    boolean debug = true;
    try {
      String path = name + ".bdtd";
      in = getResourceAsStream(path);
      if (in != null) {
        dtd.read(new DataInputStream(in));
        dtd.putDTDHash(name, dtd);
      }
    } catch (Exception e) {
      System.out.println(e);
    }
    return dtd;
  }
       */

  class Callback extends HTMLEditorKit.ParserCallback{

    public Callback(){}
    public void flush(){}
    public void handleComment( char[] data, int pos ){}
    public void handleEndTag( HTML.Tag tag, int pos ){}
    public void handleError( String errorMsg, int pos ){}
    public void handleSimpleTag( HTML.Tag t, MutableAttributeSet a, int pos ){}
    public void handleStartTag( HTML.Tag tag, MutableAttributeSet attr, int pos){}

    public void handleText( char[] data, int pos ){
      System.out.println( "Text-String \"" + new String( data ) +
                          "\" found at Position " + pos );
    }

  }
  

  /*
  public void parse(Reader r, HTMLEditorKit.ParserCallback cb, boolean ignoreCharSet)throws IOException{

    super.parse(r, cb, true);
  }
    */
}
