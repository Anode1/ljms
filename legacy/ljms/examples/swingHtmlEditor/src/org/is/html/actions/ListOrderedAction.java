package org.is.html.actions;

import javax.swing.text.html.HTML;

/**
 *
 * @since jdk1.2
 */
public class ListOrderedAction extends ListActionBase{

  private static String INSERT_HTML="<ol><p></p></ol>";
  private static String INSERT_LI_HTML="<ol><li><p></p></li></ol>";
  private static HTML.Tag tag=HTML.Tag.OL;

	public ListOrderedAction(){

	  super("ol", INSERT_LI_HTML, INSERT_HTML, tag);
	}

}

