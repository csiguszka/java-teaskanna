package com.example.viewer.utils;

import com.example.viewer.model.VaseParameters;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Window;

import java.io.*;
import java.nio.file.Files;

public class SceneExporter {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public static void exportToFile(VaseParameters params, Window owner) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Exportalas");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON fajl", "*.json"));
        chooser.setInitialFileName("vaza.json");

        File file = chooser.showSaveDialog(owner);
        if (file == null) return;

        JsonObject obj = new JsonObject();
        obj.addProperty("height",              params.height.get());
        obj.addProperty("wallThickness",       params.wallThickness.get());
        obj.addProperty("bellyAmount",         params.bellyAmount.get());
        obj.addProperty("neckTaper",           params.neckTaper.get());
        obj.addProperty("baseRadius",          params.baseRadius.get());
        obj.addProperty("radialSegments",      params.radialSegments.get());
        obj.addProperty("spoutLength",         params.spoutLength.get());
        obj.addProperty("spoutWidth",          params.spoutWidth.get());
        obj.addProperty("spoutLift",           params.spoutLift.get());
        obj.addProperty("lidHeight",           params.lidHeight.get());
        obj.addProperty("knobHeight",          params.knobHeight.get());
        obj.addProperty("knobRadius",          params.knobRadius.get());
        obj.addProperty("handleSize",          params.handleSize.get());
        obj.addProperty("handleThickness",     params.handleThickness.get());
        obj.addProperty("handlePos",           params.handlePos.get());
        obj.addProperty("bodyColor",           toHex(params.bodyColor.get()));
        obj.addProperty("handleColor",         toHex(params.handleColor.get()));
        obj.addProperty("lidDomeColor",        toHex(params.lidDomeColor.get()));
        obj.addProperty("lidKnobColor",        toHex(params.lidKnobColor.get()));
        obj.addProperty("ambientLightEnabled", params.ambientLightEnabled.get());
        obj.addProperty("pointLightEnabled",   params.pointLightEnabled.get());
        obj.addProperty("bodyVisible",         params.bodyVisible.get());
        obj.addProperty("spoutVisible",        params.spoutVisible.get());
        obj.addProperty("handleVisible",       params.handleVisible.get());
        obj.addProperty("lidVisible",          params.lidVisible.get());

        try (Writer writer = new FileWriter(file)) {
            GSON.toJson(obj, writer);
        } catch (IOException ex) {
            showError("Export hiba: " + ex.getMessage());
        }
    }

    public static void importFromFile(VaseParameters params, Window owner) {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Importalas");
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("JSON fajl", "*.json"));

        File file = chooser.showOpenDialog(owner);
        if (file == null) return;

        try {
            String json = Files.readString(file.toPath());
            JsonObject obj = JsonParser.parseString(json).getAsJsonObject();

            // Parse everything into locals first — if anything throws, params is left untouched
            double       height              = obj.has("height")              ? obj.get("height").getAsDouble()              : params.height.get();
            double       wallThickness       = obj.has("wallThickness")       ? obj.get("wallThickness").getAsDouble()       : params.wallThickness.get();
            double       bellyAmount         = obj.has("bellyAmount")         ? obj.get("bellyAmount").getAsDouble()         : params.bellyAmount.get();
            double       neckTaper           = obj.has("neckTaper")           ? obj.get("neckTaper").getAsDouble()           : params.neckTaper.get();
            double       baseRadius          = obj.has("baseRadius")          ? obj.get("baseRadius").getAsDouble()          : params.baseRadius.get();
            int          radialSegments      = obj.has("radialSegments")      ? obj.get("radialSegments").getAsInt()         : params.radialSegments.get();
            double       spoutLength         = obj.has("spoutLength")         ? obj.get("spoutLength").getAsDouble()         : params.spoutLength.get();
            double       spoutWidth          = obj.has("spoutWidth")          ? obj.get("spoutWidth").getAsDouble()          : params.spoutWidth.get();
            double       spoutLift           = obj.has("spoutLift")           ? obj.get("spoutLift").getAsDouble()           : params.spoutLift.get();
            double       lidHeight           = obj.has("lidHeight")           ? obj.get("lidHeight").getAsDouble()           : params.lidHeight.get();
            double       knobHeight          = obj.has("knobHeight")          ? obj.get("knobHeight").getAsDouble()          : params.knobHeight.get();
            double       knobRadius          = obj.has("knobRadius")          ? obj.get("knobRadius").getAsDouble()          : params.knobRadius.get();
            double       handleSize          = obj.has("handleSize")          ? obj.get("handleSize").getAsDouble()          : params.handleSize.get();
            double       handleThickness     = obj.has("handleThickness")     ? obj.get("handleThickness").getAsDouble()     : params.handleThickness.get();
            double       handlePos           = obj.has("handlePos")           ? obj.get("handlePos").getAsDouble()           : params.handlePos.get();
            Color        bodyColor           = obj.has("bodyColor")           ? Color.web(obj.get("bodyColor").getAsString())           : params.bodyColor.get();
            Color        handleColor         = obj.has("handleColor")         ? Color.web(obj.get("handleColor").getAsString())         : params.handleColor.get();
            Color        lidDomeColor        = obj.has("lidDomeColor")        ? Color.web(obj.get("lidDomeColor").getAsString())        : params.lidDomeColor.get();
            Color        lidKnobColor        = obj.has("lidKnobColor")        ? Color.web(obj.get("lidKnobColor").getAsString())        : params.lidKnobColor.get();
            boolean      ambientLightEnabled = obj.has("ambientLightEnabled") ? obj.get("ambientLightEnabled").getAsBoolean() : params.ambientLightEnabled.get();
            boolean      pointLightEnabled   = obj.has("pointLightEnabled")   ? obj.get("pointLightEnabled").getAsBoolean()   : params.pointLightEnabled.get();
            boolean      bodyVisible         = obj.has("bodyVisible")         ? obj.get("bodyVisible").getAsBoolean()         : params.bodyVisible.get();
            boolean      spoutVisible        = obj.has("spoutVisible")        ? obj.get("spoutVisible").getAsBoolean()        : params.spoutVisible.get();
            boolean      handleVisible       = obj.has("handleVisible")       ? obj.get("handleVisible").getAsBoolean()       : params.handleVisible.get();
            boolean      lidVisible          = obj.has("lidVisible")          ? obj.get("lidVisible").getAsBoolean()          : params.lidVisible.get();

            // All values parsed successfully — apply atomically
            params.height.set(height);
            params.wallThickness.set(wallThickness);
            params.bellyAmount.set(bellyAmount);
            params.neckTaper.set(neckTaper);
            params.baseRadius.set(baseRadius);
            params.radialSegments.set(radialSegments);
            params.spoutLength.set(spoutLength);
            params.spoutWidth.set(spoutWidth);
            params.spoutLift.set(spoutLift);
            params.lidHeight.set(lidHeight);
            params.knobHeight.set(knobHeight);
            params.knobRadius.set(knobRadius);
            params.handleSize.set(handleSize);
            params.handleThickness.set(handleThickness);
            params.handlePos.set(handlePos);
            params.bodyColor.set(bodyColor);
            params.handleColor.set(handleColor);
            params.lidDomeColor.set(lidDomeColor);
            params.lidKnobColor.set(lidKnobColor);
            params.ambientLightEnabled.set(ambientLightEnabled);
            params.pointLightEnabled.set(pointLightEnabled);
            params.bodyVisible.set(bodyVisible);
            params.spoutVisible.set(spoutVisible);
            params.handleVisible.set(handleVisible);
            params.lidVisible.set(lidVisible);
        } catch (Exception ex) {
            showError("Import hiba: " + ex.getMessage());
        }
    }

    private static String toHex(Color c) {
        return String.format("#%02X%02X%02X",
                (int) Math.round(c.getRed()   * 255),
                (int) Math.round(c.getGreen() * 255),
                (int) Math.round(c.getBlue()  * 255));
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

