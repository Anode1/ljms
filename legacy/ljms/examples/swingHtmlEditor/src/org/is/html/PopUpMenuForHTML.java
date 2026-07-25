package org.is.html;

import java.awt.Component;
import java.awt.Dimension;
import javax.swing.text.html.HTML;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Element;
import javax.swing.*;

import org.is.html.actions.*;
import org.is.util.*;

/**
 * Popup menu for HTMLEditor
 *
 * @since jdk1.2
 */
public class PopUpMenuForHTML extends PopUpMenu{

  int x,y; //coordinates where mouse event occured (to determine exact View)

  public PopUpMenuForHTML(Component origin, int x, int y){

    super(origin);
    this.x=x;
    this.y=y;
  }

  protected void addProperties(){

    super.addProperties();

    JTextPane editor=(JTextPane)origin;

    Element te=HTMLUtils.getWrappingElementByTag(editor, HTML.Tag.TABLE);

    if(te!=null)addTableStuff(te);

  }

  private void addTableStuff(Element te){

	    JMenuItem menuItem = add(new JMenuItem(Resources.getString("menu.table_properties")));
      menuItem.addActionListener(new EditTableAction(te));

      menuItem = add(new JMenuItem(Resources.getString("menu.cell_properties")));
      menuItem.addActionListener(new EditCellAction(te));

      Dimension tableDim=TableUtils.getTableDimensions(te);

      this.addSeparator();

      JMenu menu=new JMenu(Resources.getString("menu.insert_column"));
      add(menu);
      menuItem=menu.add(new JMenuItem("Before"));
      menuItem.addActionListener(Actions.getAction("insert-table-column-before"));
      menuItem=menu.add(new JMenuItem("After"));
      menuItem.addActionListener(Actions.getAction("insert-table-column"));

      menu=new JMenu(Resources.getString("menu.insert_row"));
      add(menu);
      menuItem=menu.add(new JMenuItem("Before"));
      menuItem.addActionListener(Actions.getAction("insert-table-row-before"));
      menuItem=menu.add(new JMenuItem("After"));
      menuItem.addActionListener(Actions.getAction("insert-table-row"));

      menuItem=add(new JMenuItem(Resources.getString("menu.delete_column")));
      if(tableDim.width<2){
        menuItem.setEnabled(false);
      }
      else{
        menuItem.addActionListener(Actions.getAction("delete-table-column"));
      }

      menuItem=add(new JMenuItem(Resources.getString("menu.delete_row")));
      if(tableDim.height<2){
        menuItem.setEnabled(false);
      }
      else{
        menuItem.addActionListener(Actions.getAction("delete-table-row"));
      }

      menuItem = add(new JMenuItem(Resources.getString("menu.delete-table")));
      menuItem.addActionListener(Actions.getAction("table-delete"));


   }


}