package org.is.html;

/**
 * Class producing HTMLEditor by default example HTML
 *
 */
public class TextProducer{

  private static String defaultString=null;

  static{

     defaultString=//<HTML><HEAD><TITLE>title</TITLE></HEAD><BODY BGCOLOR=\"#FFFFFF\">
     //"<P><TABLE BORDER=\"0\" "+
     //"CELLPADDING=\"0\" CELLSPACING=\"0\"><TR><TD VALIGN=\"TOP\"><FONT SIZE=\"6\" FACE=\"Arial, Helvetica\"><STRONG>PLML </STRONG></FONT><FONT SIZE=\"5\" FACE=\"Arial, Helvetica\"><STRONG>(Page Layout Manipulating Language)</STRONG></FONT></TD><TD ALIGN=\"LEFT\" VALIGN=\"BOTTOM\"></TD></TR></TABLE></P><P><TABLE BORDER=\"0\" CELLPADDING=\"0\" CELLSPACING=\"0\" HEIGHT=\"161\" COLS=\"2\"><TR><TD WIDTH=\"567\" VALIGN=\"TOP\">&nbsp;<BR><STRONG>Version 0.9 for Java 2 and higher.<BR><BR>November 12, 2000</STRONG></TD></TR></TABLE><BR><A HREF=\"general.html\" target=\"default\"><FONT SIZE=\"2\">General</FONT></A><BR><A HREF=\"layout.html\" target=\"default\"><FONT SIZE=\"2\">Layout</FONT></A><BR><A HREF=\"text.html\" target=\"default\"><FONT SIZE=\"2\">Text</FONT></A><BR><A HREF=\"http://java.sun.com\" "+
     //"target=\"default\"><FONT SIZE=\"2\">Layout DTD</FONT></A><BR><A HREF=\"text_dtd.html\" target=\"default\"><FONT SIZE=\"2\">Text DTD</FONT></A><BR><A HREF=\"details.html\" target=\"default\"><FONT SIZE=\"2\">Details</FONT></A><BR><A HREF=\"tables.html\" target=\"default\"><FONT SIZE=\"2\">Tables</FONT></A></P><P><A HREF=\"tables.html\" target=\"default\"><FONT SIZE=\"2\"><BR></FONT></A><FONT FACE=\"Arial, Helvetica\"><B>Other resources:</B></FONT><TABLE BORDER=\"0\" WIDTH=\"100%\" COLS=\"2\"><TR><TD WIDTH=\"100\" VALIGN=\"TOP\"><A HREF=\"./javadoc/overview-summary.html\">Java API</A></TD><TD VALIGN=\"TOP\">Framework API</TD></TR>"+
     //"</TABLE>&nbsp;"+
     "<form method=\"get\" name=\"person\" action=\"http://java.sun.com/\">"+
     "<table border=\"1\" width=\"100%\">"+
        "<tr><td><select size=1 name=\"salutation\">"+
        "<option selected>Mr.</option> <option>Ms.</option> </select> Name:</td>"+
         " <td width=\"40%\"><input type=\"text\" size=\"20\" name=\"name\"></td>"+
         " <td>Firstname:</td>"+
          "<td width=\"40%\"><input type=\"text\" size=\"20\" name=\"firstname\"></td>"+
        "</tr><tr><td>Voice phone:</td><td width=\"40%\"><input type=\"text\" size=\"20\" "+
        "name=\"phone\"></td><td>Fax:</td>"+
        "<td width=\"40%\"><input type=\"text\" size=\"20\" name=\"fax\"></td>"+
        "</tr></table><p align=\"center\">"+
 "<input type=\"submit\"  name=\"ok\"     value=\"Confirm\">"+
 " <input type=\"reset\"   name=\"cancel\" value=\"Cancel\">"+
 "<input type=\"submit\"  name=\"delete\" value=\"Delete\">"+
     " </p> </form>"+
    "<p><object classid=\"javax.swing.JLabel\">"+
    "  <param name=\"text\" value=\"text1\">"+
   " </object>"+
   "<object classid=\"javax.swing.JTextField\">"+
      "<param name=\"text\" value=\"text2\">"+
    "</object></p>"+
   "<p><object classid=\"javax.swing.JTextArea\">"+
      "<param name=\"text\" value=\"text3\">"+
    "</object></p>";
//";//</BODY></HTML>";

    // defaultString="foo";

   /*
     try{
        defaultString=FileUtils.fileToString(System.getProperty("user.dir")+java.io.File.separator+"test.html");
     }
     catch(java.io.IOException ie){
        System.err.println("TextProducer:"+ie);
     }
    */
  }

  public TextProducer(){  }

  public String getText(){

     return defaultString;
  }


}
