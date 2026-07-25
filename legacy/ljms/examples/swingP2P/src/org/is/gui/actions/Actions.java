package org.is.gui.actions;

import javax.swing.Action;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Container of all application events
 *
 * @version 1.0
 * @since jdk1.2
 */
public class Actions{

    private static HashMap actions=new HashMap(64);

    /**
     * Constructor
     */
    public Actions(){

	    Action[] definedHere = defaultActions;

	    for (int i = 0; i < defaultActions.length; i++) {
	      Action a = definedHere[i];
	      actions.put(a.getValue(Action.NAME), a);
	    }

      //System.out.println("All actions:"+this.toString());
    }

    public static Action getAction(String actionName){

      Action action=(Action)actions.get(actionName);
      if(action==null)System.err.println("Actions::getAction: no action for key:"+actionName);
      return action;
    }


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
   * Testing entry point. Prints all Actions available in the current EditorKit
   */
  public static void main(String[] args) {

      try{

        Actions a=new Actions();

        System.out.println(a.toString());

        Thread.sleep(10000);
      }
      catch(Exception e){
        e.printStackTrace();
      }
  }

  private static final Action[] defaultActions = {
      new org.is.gui.actions.ExitAction()
  };

}
