package com.aureobank.theme;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

public class UI {
    public static Label h1(String text) { Label l = new Label(text); l.getStyleClass().add("h1"); return l; }
    public static Label h2(String text) { Label l = new Label(text); l.getStyleClass().add("h2"); return l; }
    public static Label h3(String text) { Label l = new Label(text); l.getStyleClass().add("h3"); return l; }
    public static Label caption(String text) { Label l = new Label(text); l.getStyleClass().add("caption"); return l; }
    public static Label errorLabel() { Label l = new Label(""); l.getStyleClass().add("error"); return l; }

    public static Button primaryButton(String text) { Button b = new Button(text); b.getStyleClass().add("btn-primary"); return b; }
    public static Button secondaryButton(String text) { Button b = new Button(text); b.getStyleClass().add("btn-secondary"); return b; }
    public static Button dangerButton(String text) { Button b = new Button(text); b.getStyleClass().add("btn-danger"); return b; }

    public static VBox card(double width) {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(24));
        box.setPrefWidth(width);
        box.getStyleClass().add("card");
        box.setEffect(new DropShadow(10, Color.gray(0, 0.2)));
        return box;
    }

    public static Node cardWrap(Node inner) {
        VBox box = card(800);
        box.getChildren().add(inner);
        return box;
    }

    public static Button navButton(String text, Runnable action) {
        Button b = new Button(text);
        b.getStyleClass().add("nav-btn");
        b.setMaxWidth(Double.MAX_VALUE);
        b.setOnAction(e -> action.run());
        return b;
    }

    public static Node navCard(Node inner) {
        VBox box = new VBox(inner);
        box.setPadding(new Insets(16));
        box.setPrefWidth(220);
        box.getStyleClass().add("nav-card");
        return box;
    }

    public static TextField keypadField() {
        TextField tf = new TextField();
        tf.setPromptText("0.00");
        tf.setEditable(false);
        GridPane grid = new GridPane();
        grid.setHgap(8); grid.setVgap(8);
        String[] keys = {"1","2","3","4","5","6","7","8","9",".","0","<"};
        int k=0; for (int r=0;r<4;r++) for (int c=0;c<3;c++) {
            String key = keys[k++];
            Button b = new Button(key); b.getStyleClass().add("key"); b.setPrefSize(60,60);
            b.setOnAction(e -> {
                if ("<".equals(key)) { String t = tf.getText(); if (!t.isEmpty()) tf.setText(t.substring(0,t.length()-1)); }
                else tf.setText(tf.getText()+key);
            });
            grid.add(b, c, r);
        }
        VBox container = new VBox(8, tf, grid);
        return tf; // The caller should layout its own keypad; for simplicity we only provide the field here
    }
}
