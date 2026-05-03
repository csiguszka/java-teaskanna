package com.example.viewer.utils;

import com.example.viewer.model.VaseParameters;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple JSON-based export/import for VaseParameters.
 * No external libraries required – uses hand-written serialization.
 */
public class SceneExporter {

    // ------------------------------------------------------------------ export

    public static void exportToFile(VaseParameters params, Window owner) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Exportálás");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON fájl", "*.json"));
        chooser.setInitialFileName("vaza.json");

        File file = chooser.showSaveDialog(owner);
        if (file == null) return;

        try (PrintWriter pw = new PrintWriter(new FileWriter(file))) {
            pw.println("{");
            pw.println("  \"height\": " + params.height.get() + ",");
            pw.println("  \"wallThickness\": " + params.wallThickness.get() + ",");
            pw.println("  \"bellyAmount\": " + params.bellyAmount.get() + ",");
            pw.println("  \"neckTaper\": " + params.neckTaper.get() + ",");
            pw.println("  \"baseRadius\": " + params.baseRadius.get() + ",");
            pw.println("  \"radialSegments\": " + params.radialSegments.get() + ",");
            pw.println("  \"spoutLength\": " + params.spoutLength.get() + ",");
            pw.println("  \"spoutWidth\": " + params.spoutWidth.get() + ",");
            pw.println("  \"spoutLift\": " + params.spoutLift.get() + ",");
            pw.println("  \"lidHeight\": " + params.lidHeight.get() + ",");
            pw.println("  \"knobHeight\": " + params.knobHeight.get() + ",");
            pw.println("  \"knobRadius\": " + params.knobRadius.get() + ",");
            pw.println("  \"handleSize\": " + params.handleSize.get() + ",");
            pw.println("  \"handleThickness\": " + params.handleThickness.get() + ",");
            pw.println("  \"handlePos\": " + params.handlePos.get() + ",");
            pw.println("  \"bodyColor\": \"" + toHex(params.bodyColor.get()) + "\",");
            pw.println("  \"handleColor\": \"" + toHex(params.handleColor.get()) + "\",");
            pw.println("  \"lidDomeColor\": \"" + toHex(params.lidDomeColor.get()) + "\",");
            pw.println("  \"lidKnobColor\": \"" + toHex(params.lidKnobColor.get()) + "\",");
            pw.println("  \"ambientLightEnabled\": " + params.ambientLightEnabled.get() + ",");
            pw.println("  \"pointLightEnabled\": " + params.pointLightEnabled.get());
            pw.println("}");
        } catch (IOException ex) {
            showError("Export hiba: " + ex.getMessage());
        }
    }

    // ------------------------------------------------------------------ import

    public static void importFromFile(VaseParameters params, Window owner) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Importálás");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON fájl", "*.json"));

        File file = chooser.showOpenDialog(owner);
        if (file == null) return;

        try {
            String json = readFile(file);
            params.height.set(readDouble(json, "height", params.height.get()));
            params.wallThickness.set(readDouble(json, "wallThickness", params.wallThickness.get()));
            params.bellyAmount.set(readDouble(json, "bellyAmount", params.bellyAmount.get()));
            params.neckTaper.set(readDouble(json, "neckTaper", params.neckTaper.get()));
            params.baseRadius.set(readDouble(json, "baseRadius", params.baseRadius.get()));
            params.radialSegments.set((int) Math.round(readDouble(json, "radialSegments", params.radialSegments.get())));
            params.spoutLength.set(readDouble(json, "spoutLength", params.spoutLength.get()));
            params.spoutWidth.set(readDouble(json, "spoutWidth", params.spoutWidth.get()));
            params.spoutLift.set(readDouble(json, "spoutLift", params.spoutLift.get()));
            params.lidHeight.set(readDouble(json, "lidHeight", params.lidHeight.get()));
            params.knobHeight.set(readDouble(json, "knobHeight", params.knobHeight.get()));
            params.knobRadius.set(readDouble(json, "knobRadius", params.knobRadius.get()));
            params.handleSize.set(readDouble(json, "handleSize", params.handleSize.get()));
            params.handleThickness.set(readDouble(json, "handleThickness", params.handleThickness.get()));
            params.handlePos.set(readDouble(json, "handlePos", params.handlePos.get()));
            params.bodyColor.set(readColor(json, "bodyColor", params.bodyColor.get()));
            params.handleColor.set(readColor(json, "handleColor", params.handleColor.get()));
            params.lidDomeColor.set(readColor(json, "lidDomeColor", params.lidDomeColor.get()));
            params.lidKnobColor.set(readColor(json, "lidKnobColor", params.lidKnobColor.get()));
            params.ambientLightEnabled.set(readBoolean(json, "ambientLightEnabled", params.ambientLightEnabled.get()));
            params.pointLightEnabled.set(readBoolean(json, "pointLightEnabled", params.pointLightEnabled.get()));
        } catch (IOException ex) {
            showError("Import hiba: " + ex.getMessage());
        }
    }

    // ----------------------------------------------------------------- helpers

    private static String toHex(Color c) {
        return String.format("#%02X%02X%02X",
                (int) Math.round(c.getRed() * 255),
                (int) Math.round(c.getGreen() * 255),
                (int) Math.round(c.getBlue() * 255));
    }

    private static String readFile(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) sb.append(line).append('\n');
        }
        return sb.toString();
    }

    private static double readDouble(String json, String key, double fallback) {
        Matcher m = Pattern.compile("\"" + key + "\"\\s*:\\s*(-?[\\d.eE+\\-]+)").matcher(json);
        return m.find() ? Double.parseDouble(m.group(1)) : fallback;
    }

    private static boolean readBoolean(String json, String key, boolean fallback) {
        Matcher m = Pattern.compile("\"" + key + "\"\\s*:\\s*(true|false)").matcher(json);
        return m.find() ? Boolean.parseBoolean(m.group(1)) : fallback;
    }

    private static Color readColor(String json, String key, Color fallback) {
        Matcher m = Pattern.compile("\"" + key + "\"\\s*:\\s*\"(#[0-9A-Fa-f]{6})\"").matcher(json);
        if (!m.find()) return fallback;
        try { return Color.web(m.group(1)); } catch (Exception e) { return fallback; }
    }

    private static void showError(String message) {
        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.ERROR, message,
                    javafx.scene.control.ButtonType.OK);
            alert.setHeaderText(null);
            alert.showAndWait();
        });
    }
}
