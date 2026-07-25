package org.is.html.actions;

import javax.swing.text.html.HTML;

/**
 *
 * @since jdk1.2
 */
public class ListUnorderedAction extends ListActionBase{

  private static String INSERT_HTML="<ul><p></p></ul>";
  private static String INSERT_LI_HTML="<ul><li><p></p></li></ul>";
  private static HTML.Tag tag=HTML.Tag.UL;

	public ListUnorderedAction(){

	  super("ul", INSERT_LI_HTML, INSERT_HTML, tag);
	}

}

