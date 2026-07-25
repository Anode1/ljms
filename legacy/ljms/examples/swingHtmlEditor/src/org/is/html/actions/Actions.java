package org.is.html.actions;

import javax.swing.Action;
import java.awt.event.ActionEvent;
import javax.swing.JTextPane;
import javax.swing.text.TextAction;
import java.util.HashMap;
import java.util.Iterator;

import org.is.html.GHTMLEditorKit;
import org.is.html.GHTMLEditor;

/**
 * All actions repository. This class is a container of all application events
 *
 * @since jdk1.2
 */
public class Actions{

    private static HashMap actions=new HashMap(64);
 //   private HashMap actionsNames=new HashMap(64);

    /**
     * Constructor
     */
    public Actions(GHTMLEditorKit kit){

	    Action[] kitActions = kit.getActions();

	    for (int i = 0; i < kitActions.length; i++) {
	      Action a = kitActions[i];
	      actions.put(a.getValue(Action.NAME), a);
        //System.out.print(a.getValue(Action.NAME)+" ");
	    }

      //System.out.println("All actions:"+this.toString());
    }

    public static Action getAction(String actionName){

      Action action=(Action)actions.get(actionName);
      if(action==null)System.err.println("Actions::getAction: no action for key:"+actionName);
      return action;
    }

    /*
    protected Action getAction(String actionName){

      Action action=(Action)actions.get(actionName);
      if(action==null){  //try to create
         action=createAction(actionName);
                         //and put into cache
         actions.put(action.getValue(Action.NAME),action);
      }
	    return action;
    }
    */

  /**
   * Creates new Action
   */ /*
  private Action createAction(String actionName) {

    Action action = null;

    try{
       String className=(String)actions.get(actionName);
       if(className!=null){
         action = (Action)Class.forName(className).newInstance();
         action.putValue(Action.NAME, actionName);
         if(action implements IAttachedToEditor)action.setEditor(pane);
       }
       else{
         //action = new GComponent(el);
         //create anonimous action here
         System.err.println("Actions::createAction:There is no class for tag:"+actionName+" in Actions map!");
       }
    }
    catch(Exception e) {
       //action = new GComponent(el);
       System.err.println("GComponent::createComponent:"+e);
    }
    return action;
  }
        */

  /**
   * For debugging purposes only
   */
  public String toString(){

    StringBuffer sb=new StringBuffer();

    Iterator myIterator = actions.keySet().iterator();

    while (myIterator.hasNext()){
       String key=(String)myIterator.next();
       sb.append("* ");
       sb.append(key);
       sb.append(":   ");
       Object o=actions.get(key);
       sb.append(o.toString());
       sb.append(System.getProperty("line.separator"));
    }
    return sb.toString();
  }

  /**
   * Method simulating JButton/JMenu behaviour (firing of Action) to share
   * the same Actions delivery mechanism with buttons/menus
   */
  public static void fireAction(Object source, String actionPK){

    Action action=Actions.getAction(actionPK);
    if(action==null)return;

    action.actionPerformed(new ActionEvent(source, ActionEvent.ACTION_PERFORMED, actionPK));
  }

  /**
   * Method simulating JButton/JMenu behaviour (firing of Action) to share
   * the same Actions delivery mechanism with buttons/menus
   */  /*
  public static void fireAction(Object source, Action action){

    if(action==null)return;
    action.actionPerformed(new ActionEvent(source, ActionEvent.ACTION_PERFORMED, (String)action.getValue(Action.NAME)));
  }      */

  /**
   * Testing entry point. Prints all Actions available in our EditorKit 
   */
  public static void main(String[] args) {

      try{

        Actions a=new Actions(new GHTMLEditorKit());

        System.out.println(a.toString());

        Thread.sleep(10000);
      }
      catch(Exception e){
        e.printStackTrace();
      }
  }

}
