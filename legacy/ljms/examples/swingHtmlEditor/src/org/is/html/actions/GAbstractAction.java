package org.is.html.actions;

import javax.swing.text.TextAction;
import javax.swing.Action;
import java.net.URL;
import javax.swing.ImageIcon;

/**
 * Convenience Action Base for all application-aware Actions
 * In addition to AbstractAction this class contains convenience methods
 * for getting main Frame, current Editor, reloading procedure (forcing parser
 * to parse a piece of Text)
 *
 * @since jdk1.2
 */
public abstract class GAbstractAction extends TextAction{

    public GAbstractAction(String nm){

      super(nm);

     	// Register key binding
      /*
      String binding = Resources.getKeyBinding(nm);
		  if(binding != null){
			   inputHandler.addKeyBinding(binding,action);
      }
      */
  	}

    //
    // These next public methods may belong in the AbstractAction class.
    //

    /** 
     * Gets the value from the key Action.ACTION_COMMAND_KEY
     */
    public String getActionCommand(){

      return (String)getValue(Action.ACTION_COMMAND_KEY);
    }

    /**
     * Gets the value from the key Action.SHORT_DESCRIPTION
     */
    public String getShortDescription(){

      return (String)getValue(Action.SHORT_DESCRIPTION);
    }

    /**
     * Gets the value from the key Action.LONG_DESCRIPTION
     */
    public String getLongDescription(){

      return (String)getValue(Action.LONG_DESCRIPTION);
    }
    
    /* Should finish the implementation and add get/set methods for all the 
     * javax.swing.Action keys:
        
        Action.NAME
        Action.SMALL_ICON
        ActionConstants.LARGE_ICON
        Action.MNEMONIC_KEY
     */
    

    // ActionListener registration and invocation.

    /**
     * Forwards the ActionEvent to the registered listener.
     */    /*
    public void actionPerformed(ActionEvent evt){

      if(listeners != null){

          Object[] listenerList = listeners.getListenerList();

          // Recreate the ActionEvent and stuff the value of the ACTION_COMMAND_KEY
          ActionEvent e = new ActionEvent(evt.getSource(), evt.getID(),
                                          (String)getValue(Action.ACTION_COMMAND_KEY));
          for (int i = 0; i <= listenerList.length-2; i += 2) {
             ((ActionListener)listenerList[i+1]).actionPerformed(e);
          }
      }
    }

    public void addActionListener(ActionListener l)  {

      if (listeners == null) {
         listeners = new EventListenerList();
	    }
      listeners.add(ActionListener.class, l);
    }

    public void removeActionListener(ActionListener l)  {

	    if (listeners == null) {
	      return;
    	}
      listeners.remove(ActionListener.class, l);
    }
             */
    /**
     * Currently it is not used
     */
    public ImageIcon getIcon(String name)  {

      String imagePath = "images/" + name;
      URL url = this.getClass().getResource(imagePath);
      if (url != null)  {
         return new ImageIcon(url);
      }
      return null;
    }


}

