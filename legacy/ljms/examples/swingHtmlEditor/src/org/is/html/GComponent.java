package org.is.html;

import java.util.List;
import java.util.ArrayList;
import javax.swing.text.*;

/**
 * Substitute for Sun's Elements tree.
 * Workaround for impossibility to modify Sun's parsing tree
 * directly in JDK1.2. We create our own tree replicating the structure
 * of Sun's tree, modify attributes, flatten it into String and replace
 * selected text in JTextPane by this string through insertHTML() method. Simple!
 *
 * Not used in current version. Currently used for debuggin (printing) of
 * hierarchies only
 *
 * @version 1.0
 * @since jdk1.2
 */
public class GComponent{

  /**
   * List of children which can be either composite or leaf components
   */
  public List children;

  /**
   * Ref to the parent of this component
   */
  protected GComponent p;

  /**
   * Ref to original element.
   */
  protected Element elem;

  protected Attributes attrs=new Attributes();

  /**
   * Default constructor used by GComponent class loader
   */
  public GComponent(Element elem){

    this.elem=elem;
  }

  /**
   * Gets the parent of this component.
   */
  public GComponent getParent() {

    return p;
  }

  /**
   * Convenience method for accessing the root element.
   * If root has been accessed frequently, it is worth to make direct ref
   * from GComponent (for reference cost)
   */
  public GComponent getRootGComponent(){

    GComponent comp=this;
    while((comp=comp.getParent())!=null){
    }
    return comp;
  }

  /**
   * Creates a tree of GComponents from Sun's element tree begining from the node passed
   */
  public static GComponent cloneTree(Element element){

    GComponent runner=new GComponent(null);
    return runner.bind(null, element, null);
  }

  /**
   * Recursively creates GComponents tree
   */
  protected final GComponent bind(GComponent p, Element initialElement, List brothers){

    if(initialElement == null) return null;  //just for sure

    GComponent anotherMe = new GComponent(initialElement);

    if(!(anotherMe.isLeaf())){

       anotherMe.children=new ArrayList();  //do it only if composite element

       for (int i = 0; i < initialElement.getElementCount(); i++){
           Element child=initialElement.getElement(i);
           bind(anotherMe, initialElement.getElement(i),anotherMe.children);
       }
    }

    anotherMe.p=p;  file://set Parent
    if(brothers!=null)brothers.add(anotherMe);   //add me as a Component

    return anotherMe;
  }

  public boolean isLeaf(){

     return elem.isLeaf();
  }

  public Attributes getAttributes(){

     return attrs;
  }

  public void addAttribute(String key, String value){

    attrs.addAttribute(key, value);
  }

  public String getAttribute(String key){

    return attrs.getAttribute(key);
  }

  public String getElementName(){

    return elem.getAttributes().getAttribute(StyleConstants.NameAttribute).toString();
  }

  public String getAllAttributesAsString(){

    return attrs.getAllAttributesAsString();
  }

  public String getOriginalAttributesAsString(){

    return HTMLUtils.attr2String(elem.getAttributes());
  }

  /**
   * For debugging purposes only
   */
  public String toString(){

      //delegate to Element:

      String name=getElementName()+getAllAttributesAsString();
      return name;
  }

  /**
   * Prints string representation of GComponents tree (this class as the root)
   * For debugging purposes only.   
   */
  public String printPreorder(){

      StringBuffer sb=new StringBuffer("TREE PREORDER:\n");
      printPreorder(sb,0);
      return sb.toString();
  }

  /**
   * Prints string representation of GComponents tree (this class as the root)
   * For debugging purposes only.
   */
  public void printPreorder(StringBuffer buffer, int level){

      String CR=System.getProperty("line.separator");

      buffer.append(CR);
      doIndent(buffer,level);
      buffer.append("<"+this.toString()+"> ");
      buffer.append("["+elem.getStartOffset()+","+elem.getEndOffset()+"]");
      buffer.append(getOriginalAttributesAsString());

      if(children==null)return;

      doIndent(buffer,level);
      int size=children.size();
      for(int i=0; i<size; i++){

         Object o=children.get(i);

         if(o instanceof GComponent){
              ((GComponent)o).printPreorder(buffer, level+1); //recursively
         }
         else buffer.append("Unknown object:"+o.toString());

      }//for
      
      buffer.append(CR);
      doIndent(buffer,level);
      buffer.append("</"+this.toString()+">");

  }

  protected static void doIndent(StringBuffer buffer, int level){

    for(int j=-1; j<level; j++)buffer.append("  ");
  }

  public static void main(String[] args) {

      try{

        //Document doc=parser.parse(Config.getInstance().getResourcesDir()+File.separator+"test_awt.xml");

      //  System.out.println(tree.printPreorder());

        try{Thread.sleep(60000);}catch(Exception ie){};
        System.exit(0);

      }catch (Exception e) {
        e.printStackTrace(System.err);
      }
  }

}

