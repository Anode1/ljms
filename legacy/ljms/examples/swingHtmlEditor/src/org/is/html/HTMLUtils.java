package org.is.html;

import java.awt.event.ActionEvent;
import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;
import javax.swing.text.html.*;
import java.util.Enumeration;

/**
 * Set of useful utilites which are used from different classes.
 *
 * @version 1.0
 * @since jdk1.2
 */
public class HTMLUtils{

  public static boolean matchNameAttribute(Element e, HTML.Tag tag) {

	  Object o = e.getAttributes().getAttribute(StyleConstants.NameAttribute);
	  if (o instanceof HTML.Tag){
	    HTML.Tag name = (HTML.Tag)o;
	    if(name == tag) {
		    return true;
	    }
	  }
	  return false;
  }

  public static boolean firstInElement(Element charE, int offset, HTML.Tag tag){

    Element wrapper=getWrappingElementByTag(charE, tag);
    if(wrapper==null)return false;

    int wrapperStart=wrapper.getStartOffset();
    //System.out.println("Wrapper's start pos="+offset);
    return wrapperStart==offset;
  }

  public static Element getWrappingElementByTag(Element e, HTML.Tag tag){

	  while (e != null && !matchNameAttribute(e, tag)){
      //System.out.println(e.getAttributes().getAttribute(StyleConstants.NameAttribute).toString()+" "+Utils.attr2String(e.getAttributes()));
	    e = e.getParentElement();
	  }
    return e;
  }

  public static boolean isWrappedInTag(Element e, HTML.Tag tag) {

	  return getWrappingElementByTag(e, tag)!=null;
  }

  /**
   * Returns wrapping Element if position specified by coordinates: <x,y>
   * (in EditorPane coordinates) is wrapped by tag passed
   * or null otherwise
   */
  public static Element getWrappingElementByTag(int offset, HTMLDocument doc, HTML.Tag tag) {

	  Element e = doc.getCharacterElement(offset);
	  while (e != null && !matchNameAttribute(e, tag)) {
	    e = e.getParentElement();
	  }
	  return e;
  }

  public static Element getWrappingElementByTag(JEditorPane ep, HTML.Tag tag) {

    HTMLDocument doc=(HTMLDocument)ep.getDocument();
    int offset = ep.getCaret().getDot();

	  return getWrappingElementByTag(offset, doc, tag);
  }

  public static boolean isWrappedInTag(JEditorPane ep, HTML.Tag tag) {

	  return getWrappingElementByTag(ep, tag)!=null;
  }

  public static Element getWrappingElementByAttribute(Element e, HTML.Tag tag){

	  while (e != null){

      AttributeSet a=(SimpleAttributeSet)e.getAttributes().getAttribute(tag);
      if(a!=null){
         return e;
      }
	    e = e.getParentElement();
	  }
    return e;
  }

  public static String elementSpec2String(DefaultStyledDocument.ElementSpec[] es){

     if(es==null){
        return "<null>";
     }
     StringBuffer sb=new StringBuffer();
     for(int i=0; i<es.length; i++){
        if(es[i]!=null){
          sb.append(es[i].toString());
          sb.append(" ");
          sb.append(attr2String(es[i].getAttributes()));
        }
        sb.append("\n");
     }
     return sb.toString();
  }

  public static String element2String(Element e, boolean showAttr){

    if(e!=null){
       String result=e.getAttributes().getAttribute(StyleConstants.NameAttribute).toString();
       if(showAttr){
          return result+" "+attr2String(e.getAttributes());
       }
       return result;
    }
    else{
       return "<null>";
    }
  }

  public static String element2String(Element e){

    return element2String(e, true);
  }

  public static String attr2String(AttributeSet a){

    StringBuffer sb=new StringBuffer();
    if(a==null)return "Attribute set is null";
		int attrs = a.getAttributeCount();
		if (attrs == 0) {
			sb.append("{ No attributes }");
		}else{
      sb.append("{");
			Enumeration enum = a.getAttributeNames();
			while (enum.hasMoreElements()) {
				Object o = enum.nextElement();
				sb.append(o.toString() + "="+a.getAttribute(o).toString());
        if(enum.hasMoreElements())sb.append(",");
			}
      sb.append("}");
		}
    return sb.toString();
  }
        /*
  public static Element findNextElement(HTMLDocument doc, HTML.Tag tag){

    Element el=null;
	  ElementIterator iter = doc.getIterator();
	  for (el = iter.next(); el != null; el = iter.next()) {

      System.out.println("Element:"+el.getName());
	    AttributeSet as = el.getAttributes();
      System.out.println(attr2String(as));

      if(e.getAttributes().getAttribute(StyleConstants.NameAttribute)!=tag){
    }
    return el;
  }
          */

  /**
   * Returns true if position specified by coordinates: <x,y>
   * (in EditorPane coordinates) is inside tag passed.
   *
   * this method returns more precise result (it does not return true if the cursor
   * is at the line break symbol)
   */
  public static boolean isWrappedInTagPrecisely(JEditorPane ep, int x, int y, HTML.Tag tag) {

    // Determine the offset for the passed in x, y location
  	Position.Bias bias[] = new Position.Bias[1];
  	int offset = ep.getUI().viewToModel(ep, new Point(x, y), bias);
    // A backward bias indicates an end of line condition. The passed in point was
    // beyond the visible region of the line. In which case the backward
    // bias indicates the location is at the end offset of the character
    // element. Since we will be using getCharacterElement followed by
    // a check of the bounds we subtract one from the offset so that
    // getCharacterElement returns the Element representing the end of
    // line and NOT the next line.
	  if(offset > 0 && bias[0] == Position.Bias.Backward) {
	    offset--;
	  }

    HTMLDocument doc=(HTMLDocument)ep.getDocument();

    // Get the character Element at that location, and find the
    // corresponding element
	  Element e = doc.getCharacterElement(offset);
	  while (e != null && e.getAttributes().getAttribute(StyleConstants.NameAttribute) != tag) {
	    e = e.getParentElement();
	  }

    if(e!=null){
      // Check that the location is really inside the table cell.
/*    Rectangle bounds;
      try {
         bounds = ep.getUI().modelToView(ep, e.getStartOffset(), Position.Bias.Forward);
         bounds = bounds.union(ep.getUI().modelToView(ep, e.getEndOffset(), Position.Bias.Backward));
         if (bounds.contains(x, y)){
            //determine something like:
             Object boardLocation = e.getAttributes().getAttribute(HTML.Attribute.ID);
             if (boardLocation != null) {
                try{
                   return true;
                }catch (NumberFormatException nfe) {}
             }

          }
      }catch (BadLocationException ble) {}
  */
      return true;
	  }
	  return false;
  }

  /**
   * Returns wrapping Element if position specified by coordinates: <x,y>
   * (in EditorPane coordinates) is wrapped by tag passed
   * or null otherwise
   */
  public static Element getWrappingElementPrecisely(JEditorPane ep, HTMLDocument doc, int x, int y, HTML.Tag tag) {

  	Position.Bias bias[] = new Position.Bias[1];
  	int offset = ep.getUI().viewToModel(ep, new Point(x, y), bias);

	  if(offset > 0 && bias[0] == Position.Bias.Backward) {
	    offset--;
	  }

	  Element e = doc.getCharacterElement(offset);
	  while (e != null && e.getAttributes().getAttribute(StyleConstants.NameAttribute) != tag) {
	    e = e.getParentElement();
	  }
	  return e;
  }

  /**
   * Scans and return Element by tag
   */  /*
  public static Element findElementByTag(JEditorPane editor, HTMLDocument doc, HTML.Tag tag) {

    HTMLDocument.Iterator ei = hdoc.getIterator(tag);

    int currentOffset = editor.getCaretPosition();

		while (ei.isValid()){
		   if (currentOffset >= ei.getStartOffset() && currentOffset <= ei.getEndOffset()) {
			    return ei.;
			 }
			 ei.next();
		}
    return null;
  }      */

  /*
  public static String getLink(ActionEvent e) {

    GHTMLEditor editor=getGEditor();
    HTMLDocument hdoc=(HTMLDocument)editor.getDocument();

    HTMLDocument.Iterator ei = hdoc.getIterator(HTML.Tag.A);

    int currentOffset = editor.getCaretPosition();

	  String urlString = null;
		while (ei.isValid()) {
			AttributeSet anchor = ei.getAttributes();
			if(anchor != null) {
			    String href=(String)anchor.getAttribute(HTML.Attribute.HREF);
			    if (href != null) {
				    if (currentOffset >= ei.getStartOffset() &&
				      currentOffset <= ei.getEndOffset()) {
				      urlString = href;
				      break;
				    }
			    }
			}
			ei.next();
		}
    if(urlString==null)return "";
    return urlString;
  }
  */

  public static String extractBody(String text){

    int beg=text.indexOf("<body>");
    if(beg==-1)beg=text.indexOf("<BODY>");
    if(beg==-1)beg=text.toLowerCase().indexOf("<body>"); //to be sure - not sould be happening

    int end=text.indexOf("</body>");
    if(end==-1)end=text.indexOf("</BODY>");
    if(end==-1)end=text.toLowerCase().indexOf("</body>"); //to be sure - not sould be happening

    if(beg==-1 || end==-1){
       System.err.println("Utils::extractBody:<body> tag (opened or closed) not found and is not stripped");
       return text; //should not happened if there is no bug
    }

    return text.substring(beg+6, end);
  }

  public static String wrapBody(String body){

    StringBuffer sb=new StringBuffer("<html><head></head><body>");
    sb.append(body);
    sb.append("</body></html>");
    return sb.toString();
  }

  /**
   * Converts a type Color to a hex string
   * in the format "#RRGGBB"
   */
  public static String colorToHex(Color color) {

    String colorstr = new String("#");

    // Red
    String str = Integer.toHexString(color.getRed());
    if (str.length() > 2)
      str = str.substring(0, 2);
    else if (str.length() < 2)
      colorstr += "0" + str;
    else
    	colorstr += str;

    // Green
    str = Integer.toHexString(color.getGreen());
    if (str.length() > 2)
    	str = str.substring(0, 2);
    else if (str.length() < 2)
     	colorstr += "0" + str;
    else
     	colorstr += str;

    // Blue
    str = Integer.toHexString(color.getBlue());
    if (str.length() > 2)
     	str = str.substring(0, 2);
    else if (str.length() < 2)
     	colorstr += "0" + str;
    else
     	colorstr += str;

    return colorstr;
  }

   /**
    * Convert a "#FFFFFF" hex string to a Color.
    * If the color specification is bad, an attempt
    * will be made to fix it up.
    */
  private static final Color hexToColor(String value) {

    	String digits;
    	int n = value.length();
    	if (value.startsWith("#")) {
  	    digits = value.substring(1, Math.min(value.length(), 7));
	    } else {
	      digits = value;
    	}
	    String hstr = "0x" + digits;
	    Color c;
	    try {
	      c = Color.decode(hstr);
	    } catch (NumberFormatException nfe) {
	      c = null;
    	}
	    return c;
  }

  /**
   * Convert a color string such as "RED" or "#NNNNNN" to a Color.
   * Note: This will only convert the HTML3.2 color strings
   *       or a string of length 7;
   *       otherwise, it will return null.
   */
  public static Color stringToColor(String str) {

      Color color = null;

      if (str.charAt(0) == '#')
        color = hexToColor(str);
      else if (str.equalsIgnoreCase("Black"))
        color = hexToColor("#000000");
      else if(str.equalsIgnoreCase("Silver"))
        color = hexToColor("#C0C0C0");
      else if(str.equalsIgnoreCase("Gray"))
        color = hexToColor("#808080");
      else if(str.equalsIgnoreCase("White"))
        color = hexToColor("#FFFFFF");
      else if(str.equalsIgnoreCase("Maroon"))
        color = hexToColor("#800000");
      else if(str.equalsIgnoreCase("Red"))
        color = hexToColor("#FF0000");
      else if(str.equalsIgnoreCase("Purple"))
        color = hexToColor("#800080");
      else if(str.equalsIgnoreCase("Fuchsia"))
        color = hexToColor("#FF00FF");
      else if(str.equalsIgnoreCase("Green"))
        color = hexToColor("#008000");
      else if(str.equalsIgnoreCase("Lime"))
        color = hexToColor("#00FF00");
      else if(str.equalsIgnoreCase("Olive"))
        color = hexToColor("#808000");
      else if(str.equalsIgnoreCase("Yellow"))
        color = hexToColor("#FFFF00");
      else if(str.equalsIgnoreCase("Navy"))
        color = hexToColor("#000080");
      else if(str.equalsIgnoreCase("Blue"))
        color = hexToColor("#0000FF");
      else if(str.equalsIgnoreCase("Teal"))
        color = hexToColor("#008080");
      else if(str.equalsIgnoreCase("Aqua"))
        color = hexToColor("#00FFFF");
      else
	      color = hexToColor(str); // sometimes get specified without leading #
      return color;
  }


  public static void propagate(Element element, AttributeSet newSet) {

    Document document = element.getDocument();
    if (document instanceof StyledDocument) {
      StyledDocument styled = (StyledDocument)document;
      int start = element.getStartOffset();
      int end = element.getEndOffset();
      AttributeSet set = element.getAttributes();
      if (element.getElementCount() > 0) {
        AttributeSet as = styled.getParagraphElement(start).getAttributes();
        Enumeration enum = ((AbstractDocument.AbstractElement)element).children();
        while (enum.hasMoreElements()) {
          propagate((Element)enum.nextElement(), newSet);
        }
      }
      else{
        styled.setCharacterAttributes(start, end-start, newSet, true);
      }
    }
  }

}

