package org.is.gui.bundles;

import java.util.ListResourceBundle;

/**
 * ResourceBundle base class (at least this class must exist, other
 * ResourceBundles are optional and override it). Contains all required
 * modifyable properties
 *
 * @since jdk1.2
 */
public class Bundle extends ListResourceBundle{

  public static final Object[][] contents={

      {"MainFrame.title", "Message Portal"},
      {"MainFrame.size.x", "640"},    //initial frame width
      {"MainFrame.size.y", "480"},    //initial frame height
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

      //menu labels:
      {"menu.file","File"},
      {"menu.preferences","Preferences"},
      {"menu.exit","Exit"},
      {"menu.help","Help"},

      //button images:
      {"image.0","images/0.gif"},
      {"image.logo","images/logo.gif"},

      {"image.export","images/export.gif"},
      {"image.import","images/import.gif"},
      {"image.undo","images/undo.gif"},
      {"image.help","images/help.gif"},

      //buttons actions:
      {"action.export","save"},
      {"action.import","open"},
      {"action.undo","Undo"},
      {"action.help","help"},

      {"tip.export","Upload"},
      {"tip.import","Import"},
      {"tip.undo","Undo"},
      {"tip.help","Help"}

  };

  public Object[][] getContents(){

     return contents;
  }

}
