package com.aureobank.theme;

import javafx.scene.Scene;

public class AureoTheme {
    public static void apply(Scene scene) {
        scene.getStylesheets().add(AureoTheme.class.getResource("/theme/aureo.css").toExternalForm());
    }
}
