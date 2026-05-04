package com.example.viewer.view;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class HelpDialog {

    public static void show(Window owner) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initOwner(owner);
        dialog.setTitle("Súgó");
        dialog.setResizable(false);

        VBox root = new VBox(14);
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #2c2c36;");
        root.setPrefWidth(480);

        root.getChildren().addAll(
            sectionTitle("🖱  Nézet vezérlés"),
            item("Forgatás",       "Bal egérgomb + húzás"),
            item("Nagyítás/kicsinyítés", "Egérgörgő"),

            sectionTitle("⚙  Paraméterek"),
            item("Csúszkák",       "Valós idejű módosítás: a 3D modell azonnal frissül"),
            item("Színválasztók",  "Minden alkatrész (test, fedő, fogantyú) színe külön állítható"),
            item("Fények",         "Környezeti fény és pontfényforrás be-/kikapcsolható"),

            sectionTitle("📂  Fájl menü"),
            item("Új",             "Véletlenszerű paraméterek generálása"),
            item("Törlés",         "Paraméterek visszaállítása alapértékekre"),
            item("Exportálás…",    "Jelenlegi paraméterek mentése JSON fájlba"),
            item("Importálás…",    "Korábban elmentett JSON fájl betöltése"),

            sectionTitle("📐  Alkatrészek"),
            item("Váza test",      "Magasság, falvastagság, hasasodás, nyakszűkület, alapsugár"),
            item("Kiöntő",         "Hossz, szélesség, ajak-emelés"),
            item("Tető",           "Félgömb magassága; fogantyú magassága és sugara"),
            item("Fogantyú",       "Méret, vastagság, függőleges pozíció (váza határain belül tartva)"),

            sectionTitle("ℹ  Névjegy"),
            plain("JavaFX 3D Teáskanna Néző  –  Java 17 + JavaFX 21")
        );

        ScrollPane scroll = new ScrollPane(root);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background: #2c2c36; -fx-background-color: #2c2c36;");

        Scene scene = new Scene(scroll, 500, 460);
        scene.setFill(javafx.scene.paint.Color.rgb(44, 44, 54));
        dialog.setScene(scene);
        dialog.showAndWait();
    }

    // ----------------------------------------------------------------- helpers

    private static Label sectionTitle(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: #f2c060; -fx-font-size: 13px; -fx-font-weight: bold; -fx-padding: 6 0 0 0;");
        return l;
    }

    private static VBox item(String key, String value) {
        Label keyLabel = new Label(key + ":  ");
        keyLabel.setStyle("-fx-text-fill: #b0b0c8; -fx-font-size: 12px; -fx-font-weight: bold;");
        keyLabel.setMinWidth(170);

        Label valLabel = new Label(value);
        valLabel.setStyle("-fx-text-fill: #e7e7e7; -fx-font-size: 12px;");
        valLabel.setWrapText(true);

        javafx.scene.layout.HBox row = new javafx.scene.layout.HBox(keyLabel, valLabel);
        row.setPadding(new Insets(0, 0, 0, 12));
        return new VBox(row);
    }

    private static Label plain(String text) {
        Label l = new Label(text);
        l.setStyle("-fx-text-fill: #9090a8; -fx-font-size: 11px; -fx-padding: 4 0 0 0;");
        return l;
    }
}
