package org.is.gui;

import javax.swing.table.*;
import javax.swing.tree.TreePath;
import javax.swing.event.*;
import javax.swing.*;
import java.util.Vector;
import java.util.Hashtable;
import java.awt.*;
import java.awt.event.*;

import org.is.net.HostPort;

public class ServicesTable extends JTable{

  private BasicTableModel model;

  private static final String STATUS_STARTED = "Started";
  private static final String STATUS_IDLE = "Idle";

  private static final int SERVICE_COL = 0;
  private static final int PORT_COL = 1;
  private static final int STATUS_COL = 2;
  private static final int START_STOP_COL = 3;
  
  /**
   * This hashtable has been used here as Set
   */
  private Hashtable portsTaken=new Hashtable();

  public ServicesTable(){

      super();

      //setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

      BasicColumnModel cm=new BasicColumnModel();
      setColumnModel(cm);

      model=new BasicTableModel();
      setModel(model);

      TableColumn col=cm.getColumn(PORT_COL);
      col.setMaxWidth(80);
      col.setMinWidth(70);

      col=cm.getColumn(START_STOP_COL);
      col.setMaxWidth(80);
      col.setMinWidth(100);
      //col.sizeWidthToFit();

      col.setCellRenderer(new BooleanRenderer());
      col.setCellEditor(new BooleanEditor());

      //setPreferredScrollableViewportSize(getPreferredSize());
      //getTableHeader().setReorderingAllowed(false);
      setColumnSelectionAllowed(false);
      setShowGrid(false);
      //setRowSelectionAllowed(false);
      //this.setCellSelectionEnabled(false);
      //setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
  }

  /**
   * Returns true if port is available and there are no services running on it.
   * This method is used by ServicePanel only
   */
  boolean portIsAvailable(String portAsString){

    return portsTaken.get(portAsString)==null;
  }

  /**
   * Adds service to the service table. The service has not been started
   * automatically being idle
   */
  void addService(Service s){

    portsTaken.put(Integer.toString(s.getPort()),"");

    String status=null;
    if(s.getRunning()){
       status=STATUS_STARTED;
    }
    else{
       status=STATUS_IDLE;
    }
      //service       port      status      
    Object[] rowData=new Object[]{
       s.getName(),   new Integer(s.getPort()),    status
    };
    model.addRow(rowData);
  }

  /**
   * Removes service from the table. If server is active - dialog is poped
   * up.
   */
  void removeService(Service s){


    portsTaken.remove(Integer.toString(s.getPort()));
  }



  class BasicTableModel extends DefaultTableModel{

    String[] names = {"Service", "Port", "Status", "Start/Stop"};

    public BasicTableModel(){
    }

    public int getColumnCount() { return names.length; }
    //public int getRowCount() { return 5;}
    /*
    public Object getValueAt(int row, int col) {
      return data[row][col];
    } */
    public String getColumnName(int column) {return names[column];}

    public Class getColumnClass(int col){

     if(col==START_STOP_COL){
       return Boolean.class;
     }

     return super.getColumnClass(col);
    }

    public boolean isCellEditable(int row, int col) {

      if(col==START_STOP_COL)return true;
      return false;
    }

    /*
    public void setValueAt(Object aValue, int row, int column) {
       data[row][column] = aValue;
    }
    */
  }

  class BasicColumnModel extends DefaultTableColumnModel{

  }

  static class BooleanRenderer extends JCheckBox implements TableCellRenderer{

    BooleanRenderer(){
      super();
      setHorizontalAlignment(JLabel.CENTER);

      Icon icon1=Images.getIcon("/images/gray.gif");
      if(icon1!=null) setIcon(icon1);

      Icon icon2=Images.getIcon("/images/green.gif");
      if(icon2!=null) setSelectedIcon(icon2);
    }

	  public Component getTableCellRendererComponent(JTable table, Object value,
						 boolean isSelected, boolean isFocused,
						 int row, int column){

	    if (isSelected) {
	        setForeground(table.getSelectionForeground());
	        setBackground(table.getSelectionBackground());
          
	    }
	    else {
	        setForeground(table.getForeground());
	        setBackground(table.getBackground());
	    }
      setSelected((value != null && ((Boolean)value).booleanValue()));
      return this;
    }

 }

 static class BooleanEditor extends DefaultCellEditor{

	  public BooleanEditor() {
	    super(new JCheckBox());
	    JCheckBox checkBox = (JCheckBox)getComponent();
	    checkBox.setHorizontalAlignment(JCheckBox.CENTER);

      Icon icon1=Images.getIcon("/images/gray.gif");
      if(icon1!=null) checkBox.setIcon(icon1);

      Icon icon2=Images.getIcon("/images/green.gif");
      if(icon2!=null) checkBox.setSelectedIcon(icon2);

	  }
 }





}









