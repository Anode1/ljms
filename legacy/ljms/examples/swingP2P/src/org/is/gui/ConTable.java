package org.is.gui;

import javax.swing.table.*;
import javax.swing.tree.TreePath;
import javax.swing.event.*;
import javax.swing.*;
import java.util.Vector;
import java.awt.*;
import java.awt.event.*;

import org.is.net.HostPort;

public class ConTable extends JPanel{

 // protected BasicTableModel model=new BasicTableModel();
  protected JTable tableView;

  public ConTable(){

    setLayout(new BorderLayout());

    tableView=new TableView();

    //wrap into ScrollPane to see the header
    JScrollPane sp=new JScrollPane();
    sp.getViewport().add(tableView);
    sp.setMinimumSize(new Dimension(30,50));
    sp.setPreferredSize(new Dimension(530,50));

    add(sp, BorderLayout.CENTER);

  }

  class TableView extends JTable{

    TableView(){

      super();

      setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

      BasicColumnModel cm=new BasicColumnModel();
      setColumnModel(cm);

      BasicTableModel model=new BasicTableModel();
      setModel(model);

      model.addColumn("Host", new String[]{});
      model.addColumn("Status", new String[]{});
      model.addColumn("Info", new String[]{});

      TableColumn col=cm.getColumn(0);
      col.setPreferredWidth(200);

      //setPreferredScrollableViewportSize(getPreferredSize());
      //getTableHeader().setReorderingAllowed(false);
      setColumnSelectionAllowed(false);

      //setAutoResizeMode( JTable.AUTO_RESIZE_OFF );
    }
  }

  void addHost(HostPort hostPort){

  }

  void removeHostPort(HostPort hostPort){

  }

/**
 * Due to this method we can edit cells
 */
 
  public boolean isCellEditable(int row, int col){

    if(col==0)return false;
    return true;
  }

  class BasicTableModel extends DefaultTableModel{
   /*
    String[] names = {"Host", "Status", "Info"};

    public BasicTableModel(){
    }

    Object[][] data = {
	    {"host port", "Connecting", ""},
	    {"host port 2", "", ""},
	    {"host port 3", "", ""},
	    {"host port 4", "", ""},
	    {"host port 5", "", ""}
    };

    public int getColumnCount() { return names.length; }
    public int getRowCount() { return 5;}
    public Object getValueAt(int row, int col) {
      return data[row][col];
    }
    public String getColumnName(int column) {return names[column];}
    //public Class getColumnClass(int col) {return getValueAt(0,col).getClass();}
    public boolean isCellEditable(int row, int col) {return (col!=0);}

    public void setValueAt(Object aValue, int row, int column) {
       data[row][column] = aValue;
    }
     */
  }

  class BasicColumnModel extends DefaultTableColumnModel{

  }

  /**
   * For testing purposes only
   */
  public static void main(String args[]) {

    try{

       ConTable form=new ConTable();

       //wrap into frame for testing:
       JFrame frame = new JFrame("Form testing frame");
	     frame.addWindowListener(new WindowAdapter() {
	        public void windowClosing(WindowEvent e) {System.exit(0);}
    	 });
	     frame.getContentPane().add(form, BorderLayout.CENTER);
       frame.pack();

	     frame.show();

    }catch (Throwable t) {
       System.out.println("uncaught exception: " + t);
       t.printStackTrace();
       try{Thread.sleep(50000);}catch(InterruptedException ie){}
    }
  }


}









