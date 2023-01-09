import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import java.net.URI;
import java.net.URL;

public class Admintoolstarter {

    public static void main(String[] args) {
        openInBrowser("http://localhost:63342/dako/dako.api.main/WEB-INF/Startseite.html?_ijt=7d866ptr2kacpje7b6puf41o50&_ij_reload=RELOAD_ON_SAVE");

    }





    public static void openInBrowser(String url)
    {
        try
        {
            URI uri = new URL(url).toURI();
            Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE))
                desktop.browse(uri);
        }
        catch (Exception e)
        {
            /*
             *  I know this is bad practice
             *  but we don't want to do anything clever for a specific error
             */
            e.printStackTrace();

            // Copy URL to the clipboard so the user can paste it into their browser
            StringSelection stringSelection = new StringSelection(url);
            Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
            clpbrd.setContents(stringSelection, null);
            // Notify the user of the failure
            /*WindowTools.informationWindow("This program just tried to open a webpage." + "\n"
                            + "The URL has been copied to your clipboard, simply paste into your browser to access.",
                    "Webpage: " + url);*/
        }
    }}
