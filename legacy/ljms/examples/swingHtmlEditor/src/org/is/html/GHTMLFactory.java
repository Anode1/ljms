package org.is.html;

import javax.swing.text.html.*;
import javax.swing.text.*;

/**
 * Views factory
 *
 * @version 1.0
 * @since jdk1.2
 */
public class GHTMLFactory extends HTMLEditorKit.HTMLFactory{

 /**
  * Creates a view for an element
  */
  public View create(Element elem){

    Object o = elem.getAttributes().getAttribute(StyleConstants.NameAttribute);
    if (o instanceof HTML.Tag){
       HTML.Tag kind = (HTML.Tag)o;
       if(kind == HTML.Tag.HEAD ||
            kind == HTML.Tag.META ||
            kind == HTML.Tag.TITLE ||
            kind == HTML.Tag.COMMENT) {
         return new GInvisibleView(elem);
         //return super.create(elem);
   		}
      else if (kind == HTML.Tag.TABLE) {
        //View tv=super.create(elem);

       //TableView tv=new TableView(elem);
		    //return new GTableView(elem); //DOES NOT WORK IN CURRENT SUN IMPLEMENTATION
        return super.create(elem);
   		}
      else return super.create(elem);
    }//o instance HTML.Tag

    return super.create(elem);
  }

     /*
		      if (kind == HTML.Tag.CONTENT) {
		        return new InlineView(elem);
		      } else if (kind == HTML.Tag.IMPLIED) {
		        String ws = (String) elem.getAttributes().getAttribute(
			      CSS.Attribute.WHITE_SPACE);
		        if ((ws != null) && ws.equals("pre")) {
			        //return new LineView(elem);
              return HTMLEditorKit.HTMLFactory.cre
		        }
		        return new javax.swing.text.html.ParagraphView(elem);
		      } else if ((kind == HTML.Tag.P) ||
			        (kind == HTML.Tag.H1) ||
			        (kind == HTML.Tag.H2) ||
			        (kind == HTML.Tag.H3) ||
			        (kind == HTML.Tag.H4) ||
			        (kind == HTML.Tag.H5) ||
			        (kind == HTML.Tag.H6) ||
			        (kind == HTML.Tag.DT)) {
		          // paragraph
		          return new javax.swing.text.html.ParagraphView(elem);
		      } else if ((kind == HTML.Tag.MENU) ||
			        (kind == HTML.Tag.DIR) ||
			        (kind == HTML.Tag.UL)   ||
			        (kind == HTML.Tag.OL)) {
		          return new ListView(elem);
		      } else if (kind == HTML.Tag.BODY) {
		          // reimplement major axis requirements to indicate that the
		          // block is flexible for the body element... so that it can
		          // be stretched to fill the background properly.
		          return
                new BlockView(elem, View.Y_AXIS) {
                  protected SizeRequirements calculateMajorAxisRequirements(int axis, SizeRequirements r) {
                    r = super.calculateMajorAxisRequirements(axis, r);
			              r.maximum = Integer.MAX_VALUE;
			              return r;
          			  }
	              };
		      } else if ((kind == HTML.Tag.LI) ||
			        (kind == HTML.Tag.CENTER) ||
			        (kind == HTML.Tag.DL) ||
			        (kind == HTML.Tag.DD) ||
			        (kind == HTML.Tag.HTML) ||
			        (kind == HTML.Tag.DIV) ||
			        (kind == HTML.Tag.BLOCKQUOTE) ||
			        (kind == HTML.Tag.PRE)) {
      		    // vertical box
      		    return new BlockView(elem, View.Y_AXIS);
      		} else if (kind == HTML.Tag.NOFRAMES) {
		          return new NoFramesView(elem, View.Y_AXIS);
      		} else if ((kind == HTML.Tag.TH) ||
                     (kind == HTML.Tag.TD)) {
              return new javax.swing.text.html.TableView.CellView(elem);
      		} else if (kind==HTML.Tag.IMG) {
      		    return new ImageView(elem);
		      } else if (kind == HTML.Tag.ISINDEX) {
              return new IsindexView(elem);
		      } else if (kind == HTML.Tag.HR) {
		          return new HRuleView(elem);
      		} else if (kind == HTML.Tag.BR) {
		          return new BRView(elem);
      		} else if (kind == HTML.Tag.TABLE) {
		          return new javax.swing.text.html.TableView(elem);
      		} else if ((kind == HTML.Tag.INPUT) ||
			         (kind == HTML.Tag.SELECT) ||
			         (kind == HTML.Tag.TEXTAREA)) {
      		    return new FormView(elem);
		      } else if (kind == HTML.Tag.OBJECT) {
		          return new ObjectView(elem);
      		} else if (kind == HTML.Tag.FRAMESET) {
              if (elem.getAttributes().isDefined(HTML.Attribute.ROWS)) {
                  return new FrameSetView(elem, View.Y_AXIS);
              } else if (elem.getAttributes().isDefined(HTML.Attribute.COLS)) {
                  return new FrameSetView(elem, View.X_AXIS);
              }
              throw new Error("Can't build a"  + kind + ", " + elem + ":" +
                                     "no ROWS or COLS defined.");
          } else if (kind == HTML.Tag.FRAME) {
 		          return new FrameView(elem);
          } else if (kind instanceof HTML.UnknownTag) {
	      	    return new HiddenTagView(elem);
		      } else if (kind == HTML.Tag.COMMENT) {
             //put here logic how to handle HTML comments

		          return new CommentView(elem);
		      } else if ((kind == HTML.Tag.HEAD) ||
			        (kind == HTML.Tag.TITLE) ||
			        (kind == HTML.Tag.META) ||
			        (kind == HTML.Tag.LINK) ||
			        (kind == HTML.Tag.STYLE) ||
			        (kind == HTML.Tag.SCRIPT) ||
			        (kind == HTML.Tag.AREA) ||
			        (kind == HTML.Tag.MAP) ||
			        (kind == HTML.Tag.PARAM) ||
			        (kind == HTML.Tag.APPLET)) {
		         return new HiddenTagView(elem);
      		}
		      // don't know how to build this....
		      throw new Error("Can't build a " + kind + ", " + elem);
	        }

	    // don't know how to build this....
	    throw new Error("Can't build a " + elem);
	 }//create
  }
       */


}
