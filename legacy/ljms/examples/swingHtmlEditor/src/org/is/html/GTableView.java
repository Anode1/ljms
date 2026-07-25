package org.is.html;

import java.awt.*;
import java.util.BitSet;
import java.util.Vector;
import javax.swing.SizeRequirements;
import javax.swing.event.DocumentEvent;
import javax.swing.text.*;
import javax.swing.text.html.*;

/**
 * Currently extending of TableView does not work (too many private and
 * package protected things - almost everything has to be copied from Sun'sclasses)
 *
 * If with future Sun's version it will work, we'll implement our own view here ...
 */
class GTableView  extends /*javax.swing.text.html.*/TableView {

    /**
     * Constructs a TableView for the given element.
     *
     * @param elem the element that this view is responsible for
     */
    public GTableView(Element elem) {

      super(elem);
    }


}


