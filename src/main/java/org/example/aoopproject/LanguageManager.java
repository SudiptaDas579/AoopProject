package org.example.aoopproject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageManager {

    private static LanguageManager instance;
    private ResourceBundle bundle;
    private boolean isEnglish = true;
    private final List<LanguageListener> listeners = new ArrayList<>();

    private LanguageManager() {
        loadLanguage("en");
    }

    public static LanguageManager getInstance() {
        if (instance == null) instance = new LanguageManager();
        return instance;
    }

    public void toggleLanguage() {
        if (isEnglish) loadLanguage("bn");
        else loadLanguage("en");
        isEnglish = !isEnglish;

        listeners.forEach(LanguageListener::refreshLanguage);
    }

    private void loadLanguage(String lang) {
        bundle = ResourceBundle.getBundle("bundle", new Locale(lang));
    }

    public ResourceBundle getBundle() { return bundle; }

    public boolean isEnglish() { return isEnglish; }

    public void register(LanguageListener listener) {
        if (!listeners.contains(listener)) listeners.add(listener);
    }

    public interface LanguageListener {
        void refreshLanguage(); // called on language change
    }
}
