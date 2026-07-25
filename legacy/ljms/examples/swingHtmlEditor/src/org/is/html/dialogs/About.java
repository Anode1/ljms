package org.is.html.dialogs;

import javax.swing.JDialog;
import java.awt.Frame;

/**
 * About dialog
 *
 * @since jdk1.2
 */
public class About extends JDialog {

    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JTextField jTextField1;

    public About(Frame parent) {

        super (parent, true);
        initComponents ();
        pack();
    }

    private void initComponents () {

        jMenuBar1 = new javax.swing.JMenuBar ();
        jMenu1 = new javax.swing.JMenu ();
        jMenuItem1 = new javax.swing.JMenuItem ();
        jTextField1 = new javax.swing.JTextField ();

        jMenu1.setText ("About");
        jMenuItem1.setText ("Close");
        jMenuItem1.addActionListener (new java.awt.event.ActionListener () {
                public void actionPerformed (java.awt.event.ActionEvent evt) {
                    jMenuItem1ActionPerformed (evt);
                }
        }
        );

        jMenu1.add(jMenuItem1);
        jMenuBar1.add(jMenu1);
        setTitle ("About");
        addWindowListener (new java.awt.event.WindowAdapter () {
            public void windowClosing (java.awt.event.WindowEvent evt) {
                closeDialog (evt);
            }
        }
        );

        jTextField1.setEditable (false);
        jTextField1.setText ("2000");


        getContentPane().add(jTextField1, java.awt.BorderLayout.CENTER);

        setJMenuBar(jMenuBar1);
    }

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {
        // Add your handling code here:
        closeDialog(null);
    }


    /** Closes the dialog */
    private void closeDialog(java.awt.event.WindowEvent evt) {
        setVisible (false);
        dispose ();
    }


}
