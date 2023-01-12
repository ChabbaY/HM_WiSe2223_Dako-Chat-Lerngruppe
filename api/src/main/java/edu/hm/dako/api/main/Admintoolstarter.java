package edu.hm.dako.api.main;

import java.awt.Desktop;

import java.net.URI;
import java.net.URL;

public class Admintoolstarter {
    public static void main(String[] args) {
        openInBrowser("http://localhost:63342/dako/dako.api.main/WEB-INF/Startseite.html?_ijt=7d866ptr2kacpje7b6puf41o50&_ij_reload=RELOAD_ON_SAVE");
    }

    public static void openInBrowser(String url) {
        try {
            URI uri = new URL(url).toURI();
            Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
            // prüfung ob aufruf funktioniert
            if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE))
                desktop.browse(uri);
        } catch (Exception e) {
            // einfache Fehlerbehandlung, da desktop angeblich nicht auf allen rechnern funktioniert.
            System.out.println("Leider ist ein Fehler aufgetreten. Bitte öffnen sie die HTML Datei manuell.");
            System.out.println("Sie finden Sie im Ordner API unter webapp/WEB-INF/Startseite.html");
            System.out.println("Ich bitte Sie die Unanehmlichkeit zu entschuldigen.");
        }
    }
}