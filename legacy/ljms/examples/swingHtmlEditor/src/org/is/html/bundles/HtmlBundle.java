package org.is.html.bundles;

import java.util.ListResourceBundle;

/**
 * ResourceBundle base class (at least this class must exist, other
 * ResourceBundles are optional and override it). Contains all required
 * modifyable properties
 *
 * @since jdk1.2
 */
public class HtmlBundle extends ListResourceBundle{

  public static final Object[][] contents={

      {"MainFrame.title", "HTML Editor"},
      {"MainFrame.size.x", "400"},    //initial width
      {"MainFrame.size.y", "250"},    //initial height
      {"look_and_feel_class","javax.swing.plaf.metal.MetalLookAndFeel"}, //initial l&f

      {"Locales.size", "3"},          //number of locales been supported
      {"Locales.Country.0", "CA"},
      {"Locales.Language.0", "en"},
      {"Locales.Country.1", "CA"},
      {"Locales.Language.1", "fr"},
      {"Locales.Country.2", "US"},
      {"Locales.Language.2", "en"},
      {"Defaults.language", "en"},
      {"Defaults.country", "CA"},

      {"html_chunk", "true"}, //if true -  we have not valid HTML (although it is well formed): it is taken from inside body

      //From this point we have mainly labels strings been specified:

      {"Preferences.Title", "HTML Editor Dialog"},
      {"Preferences.Frame_Title", "Preferences"},
      {"PreferencesFrame.size.x", "600"},
      {"PreferencesFrame.size.y", "400"},
      {"Preferences.labels.Language_label", "Language"},
      {"Preferences.labels.Look_and_feel", "Look&Feel"},
      {"Preferences.Tab.Profile", "Preferences"},

      {"Dialogs.save", "Do you want to save your changes before exit?"},
      {"Dialogs.link", "URL:"},
      {"Dialogs.src", "HTML Source"},

      //dialog and not toolMenu's buttons
      {"Buttons.Cancel_button","Cancel"},
      {"Buttons.Prev_button","<< Prev"},
      {"Buttons.Next_button","Next >>"},
      {"Buttons.Ok_button","Ok"},
      {"Buttons.Redo_button","Redo"},
      {"Buttons.Undo_button","Undo"},

      //menu labels:
      {"menu.file","File"},
      {"menu.preferences","Preferences"},
      {"menu.exit","Exit"},
      {"menu.undo","Undo"},
      {"menu.redo","Redo"},
      {"menu.delete","Delete"},
      {"menu.selectAll","Select All"},
      {"menu.linkTo","Link to ..."},
      {"menu.removeLink","Remove Link"},
      {"menu.find","Find"},
      {"menu.fn","Find Next"},
      {"menu.fp","Find Previous"},
      {"menu.rfn","Replace and Find Next"},
      {"menu.view","View"},
      {"menu.src","Source"},
      {"menu.insert","Insert"},
      {"menu.spacer","Spacer"},
      {"menu.break","Break"},
      {"menu.frame","Frame"},
      {"menu.page","Page"},
      {"menu.cut","Cut"},
      {"menu.copy","Copy"},
      {"menu.paste","Paste"},
      {"menu.bold","Bold"},
      {"menu.italic","Italic"},
      {"menu.underline","Underline"},
      {"menu.ul","Unordered List"},
      {"menu.ol","Ordered List"},
      {"menu.fg","Foreground color"},
      {"menu.left","Left"},
      {"menu.center","Center"},
      {"menu.right","Right"},
      {"menu.properties","Page Properties..."},
      {"menu.table_properties","Table Properties..."},
      {"menu.cell_properties","Cell Properties..."},
      {"menu.insert_column","Insert Column"},
      {"menu.insert_row","Insert Row"},
      {"menu.delete_column","Delete Column"},
      {"menu.delete_row","Delete Row"},
      {"menu.delete-table","Delete Table"},
      {"menu.save","Save"},
      {"menu.toolbars","Toolbars"},
      {"menu.hr","Horizontal Rule"},
      {"menu.help","Help"},

      //button images:
      {"image.new","images/html/new.gif"},
      {"image.export","images/html/export.gif"},
      {"image.import","images/html/import.gif"},
      {"image.cut","images/html/cut.gif"},
      {"image.copy","images/html/copy.gif"},
      {"image.paste","images/html/paste.gif"},
      {"image.undo","images/html/undo.gif"},
      {"image.redo","images/html/redo.gif"},
      {"image.bold","images/html/bold.gif"},
      {"image.italic","images/html/italic.gif"},
      {"image.underline","images/html/underline.gif"},
      {"image.fg","images/html/fg.gif"},
      {"image.hr","images/html/hr.gif"},
      {"image.br","images/html/br.gif"},
      {"image.left","images/html/left.gif"},
      {"image.right","images/html/right.gif"},
      {"image.center","images/html/center.gif"},
      {"image.linkto","images/html/link.gif"},
      {"image.ul","images/html/ul.gif"},
      {"image.ol","images/html/ol.jpg"},
      {"image.image","images/html/image.gif"},
      {"image.photo","images/html/photo.gif"},
      {"image.table","images/html/table.gif"},
      {"image.table_delete","images/html/table_delete.gif"},
      {"image.row_insert_before","images/html/row_insert_before.gif"},
      {"image.row_insert","images/html/row_insert.gif"},
      {"image.row_delete","images/html/row_delete.gif"},
      {"image.column_insert_before","images/html/column_insert_before.gif"},
      {"image.column_insert","images/html/column_insert.gif"},
      {"image.column_delete","images/html/column_delete.gif"},
      {"image.source","images/html/source.gif"},
      {"image.objects","images/html/objects.gif"},
      {"image.help","images/html/help.gif"},
      {"image.bg_color","images/html/bg_color.gif"},

      {"image.logo","images/logo.gif"},

      //buttons actions:
      {"action.new","new-document"},
      {"action.export","save"},
      {"action.import","open"},
      {"action.cut","cut-to-clipboard"},
      {"action.copy","copy-to-clipboard"},
      {"action.paste","paste-from-clipboard"},
      {"action.undo","Undo"},
      {"action.redo","Redo"},
      {"action.bold","font-bold"},
      {"action.italic","font-italic"},
      {"action.underline","font-underline"},
      {"action.linkto","link-action"},
      {"action.hr","InsertHR"},
      {"action.br","break-action"},      
      {"action.fg","fg-color"},
      {"action.left","left-justify"},
      {"action.center","center-justify"},
      {"action.right","right-justify"},
      {"action.ul","ul"},
      {"action.ol","ol"},
      {"action.image","insert-image"},
      {"action.photo","photo"},
      {"action.table","insert-table"},
      {"action.table_delete","table-delete"},
      {"action.row_insert_before","insert-table-row-before"},
      {"action.row_insert","insert-table-row"},
      {"action.row_delete","delete-table-row"},
      {"action.column_insert_before","insert-table-column-before"},
      {"action.column_insert","insert-table-column"},
      {"action.column_delete","delete-table-column"},
      {"action.source","show-source"},
      {"action.objects","show-model"},
      {"action.help","help"},

      {"tip.new","New Document"},
      {"tip.export","Upload"},
      {"tip.import","Import"},
      {"tip.cut","Cut"},
      {"tip.copy","Copy"},
      {"tip.paste","Paste"},
      {"tip.undo","Undo"},
      {"tip.redo","Redo"},
      {"tip.bold","Bold"},
      {"tip.italic","Italic"},
      {"tip.underline","Underline text"},
      {"tip.linkto","Link"},
      {"tip.hr","Horizontal Rule"},
      {"tip.br","Line Break"},
      {"tip.fg","Foreground color"},
      {"tip.left","Left alignment"},
      {"tip.center","Center alignment (for the whole current paragraph)"},
      {"tip.right","Right alignment (for the whole current paragraph)"},
      {"tip.ul","Insert Unordered List"},
      {"tip.ol","Insert Ordered List"},
      {"tip.image","Insert image"},
      {"tip.photo","Insert photo"},
      {"tip.table","Insert Table"},
      {"tip.table_delete","Delete Table"},
      {"tip.row_insert_before","Insert Row into Table BEFORE current row"},
      {"tip.row_insert","Insert Row into Table AFTER current row"},
      {"tip.row_delete","Delete Row from Table"},
      {"tip.column_insert_before","Insert Column into Table BEFORE current column"},
      {"tip.column_insert","Insert Column into Table AFTER current column"},
      {"tip.column_delete","Delete current Column from Table"},
      {"tip.source","HTML Source"},
      {"tip.objects","Document Model"},
      {"tip.help","Help"}

  };

  public Object[][] getContents(){

     return contents;
  }

}
