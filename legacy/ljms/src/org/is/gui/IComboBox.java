package org.is.gui;

import javax.swing.*;

/**
 * JComboBox implementing IInterface
 *
 * @see IInterface
 * @since jdk1.2   
 */
public class IComboBox extends JComboBox implements IInterface{

  public boolean userInputIsValid(){
  
    if(getSelectedItem()==null) return false;
    return true;
  }

/**
 * If string already exists - just select it, if no - add it and select.
 */
    public void sendWithoutDuplicates(String string){

       for(int i=0; i<getItemCount(); i++){    //to prevent duplication
           if(((String)getItemAt(i)).equals(string)){
              setSelectedItem(string);
              return;
           }
       }
       addItem(string);
       setSelectedItem(string);
    }

    /**
     * workaround against problems with selection
     */
    public void sendData(String string){
      addItem(makeObj(string));
    }

   /**
    * Allows duplication but workaround against problems with selection
    */
   private Object makeObj(final String item)  {
     return new Object() { public String toString() { return item; } };
   }


/**
 * Safe version of JComboBox.setSelectedIndex().
 */
    public void setSelectedIndex(int i){
    
       if(i<getItemCount() && i!=-1) super.setSelectedIndex(i);
      // else System.out.println("Trying to select index "+i+" in "+this.toString());
    }

    public void removeAll(){
        super.removeAll();
    }

    /**
     * Populates this combo from String array
     */
    public void populate(String[] strings){
      for(int i=0; i<strings.length; i++){
        sendData(strings[i]);
      }
    }


  /**
   * For backward compatibility
   */
    public String getData(){

       return getSelected();
    }

/**
 * Return selected item if selected or try to select the first item
 */    
    public String getSelected(){

       if(getSelectedItem()==null){
          if(getItemCount()<1)return null;
          else{
               setSelectedIndex(0); //try to select the first item
          }
       }
       return getSelectedItem().toString();
    }


}