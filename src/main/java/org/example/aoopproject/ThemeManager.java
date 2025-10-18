package org.example.aoopproject;

import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import java.util.Objects;

public class ThemeManager {

    private static Scene currentScene;
    private static boolean isDark = false; // start with light theme by default

    public static void setScene(Scene scene) {
        currentScene = scene;
        applyCurrentTheme();
    }

    public static void toggleTheme() {
        isDark = !isDark;
        applyCurrentTheme();
    }

    private static void applyCurrentTheme() {
        if (currentScene == null) return;

        // clear old CSS
        currentScene.getStylesheets().clear();

        // choose correct CSS
        String cssPath = isDark ? "/themes/dark.css" : "/themes/light.css";
        currentScene.getStylesheets().add(Objects.requireNonNull(
                ThemeManager.class.getResource(cssPath)).toExternalForm());

        // change background image
        setBackgroundImage();
    }

    private static void setBackgroundImage() {
        if (currentScene == null || currentScene.getRoot() == null) return;

        String bgPath = isDark ? "/pictures/darkHome.png" : "/pictures/lightHome.png";

        Image bgImage = new Image(Objects.requireNonNull(
                ThemeManager.class.getResourceAsStream(bgPath)));

        BackgroundImage backgroundImage = new BackgroundImage(
                bgImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true)
        );

        ((Region) currentScene.getRoot()).setBackground(new Background(backgroundImage));
    }

    public static boolean isDark() {
        return isDark;
    }
}
