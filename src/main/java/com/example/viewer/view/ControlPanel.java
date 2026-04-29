package com.example.viewer.view;

import com.example.viewer.model.VaseParameters;
import com.example.viewer.geometry.*;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.MeshView;
import javafx.scene.paint.PhongMaterial;

public class ControlPanel {
    
    public static ScrollPane createControlPanel(VaseParameters params, MeshView vase, MeshView spout, MeshView handle, MeshView lidDome, MeshView lidKnob) {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(14, 14, 40, 14));
        panel.setPrefWidth(300);
        panel.setMinWidth(250);
        panel.setMaxWidth(350);
        panel.setStyle("-fx-background-color: #2c2c36; -fx-border-color: #4a4a58; -fx-border-width: 0 0 0 1;");

        Label title = new Label("Vaza parameterek");
        title.setStyle("-fx-text-fill: #f2f2f2; -fx-font-size: 15px; -fx-font-weight: bold;");
        panel.getChildren().add(title);
        
        // Color controls section
        Label colorTitle = new Label("Szín beállítások");
        colorTitle.setStyle("-fx-text-fill: #f2f2f2; -fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 10px 0px 5px 0px;");
        panel.getChildren().add(colorTitle);
        
        panel.getChildren().add(createColorControl("Test színe", params.bodyColor, vase, spout, handle, lidDome, lidKnob, params));
        panel.getChildren().add(createColorControl("Fogo színe", params.handleColor, vase, spout, handle, lidDome, lidKnob, params));
        panel.getChildren().add(createColorControl("Teto félgömb színe", params.lidDomeColor, vase, spout, handle, lidDome, lidKnob, params));
        panel.getChildren().add(createColorControl("Teto fogó színe", params.lidKnobColor, vase, spout, handle, lidDome, lidKnob, params));
        
        // Light controls section
        Label lightTitle = new Label("Fények");
        lightTitle.setStyle("-fx-text-fill: #f2f2f2; -fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 10px 0px 5px 0px;");
        panel.getChildren().add(lightTitle);
        
        panel.getChildren().add(createToggleControl("Környezeti fény", params.ambientLightEnabled));
        panel.getChildren().add(createToggleControl("Pont fényforrás", params.pointLightEnabled));
        panel.getChildren().add(createSliderControl("Magassag", params.height, 170, 280, vase, spout, handle, lidDome, lidKnob, params));
        panel.getChildren().add(createSliderControl("Falvastagsag", params.wallThickness, 4, 10, vase, spout, handle, lidDome, lidKnob, params));
        panel.getChildren().add(createSliderControl("Hasasodas", params.bellyAmount, 6, 46, vase, spout, handle, lidDome, lidKnob, params));
        panel.getChildren().add(createSliderControl("Nyakszukules", params.neckTaper, 0, 22, vase, spout, handle, lidDome, lidKnob, params));
        panel.getChildren().add(createSliderControl("Also sugar", params.baseRadius, 30, 62, vase, spout, handle, lidDome, lidKnob, params));
        panel.getChildren().add(createSliderControl("Felbontas", params.radialSegments, 24, 128, vase, spout, handle, lidDome, lidKnob, params));

        Label spoutTitle = new Label("Kionto parameterek");
        spoutTitle.setStyle("-fx-text-fill: #f2f2f2; -fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 10px 0px 5px 0px;");
        panel.getChildren().add(spoutTitle);
        panel.getChildren().add(createSliderControl("Kionto hossza", params.spoutLength, 4, 30, vase, spout, handle, lidDome, lidKnob, params));
        panel.getChildren().add(createSliderControl("Kionto szelesseg", params.spoutWidth, 15, 120, vase, spout, handle, lidDome, lidKnob, params));
        panel.getChildren().add(createSliderControl("Ajak emeles", params.spoutLift, -20, 18, vase, spout, handle, lidDome, lidKnob, params));

        Label lidTitle = new Label("Teto parameterek");
        lidTitle.setStyle("-fx-text-fill: #f2f2f2; -fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 10px 0px 5px 0px;");
        panel.getChildren().add(lidTitle);
        panel.getChildren().add(createSliderControl("Teto magassag", params.lidHeight, 20, 56, vase, spout, handle, lidDome, lidKnob, params));
        panel.getChildren().add(createSliderControl("Fogo magassag", params.knobHeight, 8, 42, vase, spout, handle, lidDome, lidKnob, params));
        panel.getChildren().add(createSliderControl("Fogo sugar", params.knobRadius, 10, 20, vase, spout, handle, lidDome, lidKnob, params));
        
        Label handleTitle = new Label("Fogo parameterek");
        handleTitle.setStyle("-fx-text-fill: #f2f2f2; -fx-font-size: 15px; -fx-font-weight: bold; -fx-padding: 10px 0px 5px 0px;");
        panel.getChildren().add(handleTitle);
        panel.getChildren().add(createSliderControl("Fogo meret", params.handleSize, 20, 80, vase, spout, handle, lidDome, lidKnob, params));
        panel.getChildren().add(createSliderControl("Fogo pozicio", params.handlePos, -140, 140, vase, spout, handle, lidDome, lidKnob, params));
        panel.getChildren().add(createSliderControl("Fogo vastagsag", params.handleThickness, 4, 16, vase, spout, handle, lidDome, lidKnob, params));
        
        ScrollPane scrollPane = new ScrollPane(panel);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefWidth(300);
        scrollPane.setMinWidth(250);
        scrollPane.setMaxWidth(350);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPannable(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: #2c2c36; -fx-background-color: #2c2c36; -fx-padding: 0;");
        return scrollPane;
    }

    private static HBox createColorControl(
            String labelText,
            javafx.beans.property.ObjectProperty<Color> colorProperty,
            MeshView vase,
            MeshView spout,
            MeshView handle,
            MeshView lidDome,
            MeshView lidKnob,
            VaseParameters params
    ) {
        Label label = new Label(labelText);
        label.setStyle("-fx-text-fill: #e7e7e7; -fx-font-size: 14px;");
        label.setPrefWidth(100);
        
        ColorPicker colorPicker = new ColorPicker(colorProperty.get());
        colorPicker.setStyle("-fx-background-color: #3c3c46; -fx-border-color: #5a5a68;");
        colorPicker.setPrefWidth(120);
        colorPicker.valueProperty().bindBidirectional(colorProperty);
        colorProperty.addListener((obs, oldVal, newVal) -> updateMeshColors(vase, spout, handle, lidDome, lidKnob, params));
        
        HBox hbox = new HBox(10, label, colorPicker);
        hbox.setStyle("-fx-alignment: center-left;");
        return hbox;
    }
    
    private static void updateMeshColors(MeshView vase, MeshView spout, MeshView handle, MeshView lidDome, MeshView lidKnob, VaseParameters params) {
        ((PhongMaterial) vase.getMaterial()).setDiffuseColor(params.bodyColor.get());
        ((PhongMaterial) spout.getMaterial()).setDiffuseColor(params.bodyColor.get());
        ((PhongMaterial) handle.getMaterial()).setDiffuseColor(params.handleColor.get());
        ((PhongMaterial) lidDome.getMaterial()).setDiffuseColor(params.lidDomeColor.get());
        ((PhongMaterial) lidKnob.getMaterial()).setDiffuseColor(params.lidKnobColor.get());
    }

    private static VBox createSliderControl(
            String labelText,
            javafx.beans.property.DoubleProperty property,
            double min,
            double max,
            MeshView vase,
            MeshView spout,
            MeshView handle,
            MeshView lidDome,
            MeshView lidKnob,
            VaseParameters params
    ) {
        Slider slider = new Slider(min, max, property.get());
        slider.setBlockIncrement((max - min) / 40.0);
        slider.valueProperty().addListener((obs, oldVal, newVal) -> property.set(newVal.doubleValue()));

        Label label = new Label();
        label.setStyle("-fx-text-fill: #e7e7e7;");
        label.textProperty().bind(property.asString(labelText + ": %.1f"));

        property.addListener((obs, oldVal, newVal) -> {
            enforceHandleInsideBounds(params);
            vase.setMesh(VaseMeshGenerator.createVaseTriangleMesh(params));
            spout.setMesh(SpoutMeshGenerator.createSpoutTriangleMesh(params));
            handle.setMesh(HandleMeshGenerator.createHandleTriangleMesh(params));
            lidDome.setMesh(LidDomeMeshGenerator.createLidDomeTriangleMesh(params));
            lidKnob.setMesh(LidKnobMeshGenerator.createLidKnobTriangleMesh(params));
        });

        return new VBox(4, label, slider);
    }

    private static VBox createSliderControl(
            String labelText,
            javafx.beans.property.IntegerProperty property,
            int min,
            int max,
            MeshView vase,
            MeshView spout,
            MeshView handle,
            MeshView lidDome,
            MeshView lidKnob,
            VaseParameters params
    ) {
        Slider slider = new Slider(min, max, property.get());
        slider.setMajorTickUnit(16);
        slider.setMinorTickCount(3);
        slider.setSnapToTicks(true);
        slider.valueProperty().addListener((obs, oldVal, newVal) -> property.set((int) Math.round(newVal.doubleValue())));

        Label label = new Label();
        label.setStyle("-fx-text-fill: #e7e7e7;");
        label.textProperty().bind(property.asString(labelText + ": %d"));

        property.addListener((obs, oldVal, newVal) -> {
            enforceHandleInsideBounds(params);
            vase.setMesh(VaseMeshGenerator.createVaseTriangleMesh(params));
            spout.setMesh(SpoutMeshGenerator.createSpoutTriangleMesh(params));
            handle.setMesh(HandleMeshGenerator.createHandleTriangleMesh(params));
            lidDome.setMesh(LidDomeMeshGenerator.createLidDomeTriangleMesh(params));
            lidKnob.setMesh(LidKnobMeshGenerator.createLidKnobTriangleMesh(params));
        });

        return new VBox(4, label, slider);
    }

    private static HBox createToggleControl(String labelText, javafx.beans.property.BooleanProperty property) {
        Label label = new Label(labelText);
        label.setStyle("-fx-text-fill: #e7e7e7; -fx-font-size: 14px;");
        label.setPrefWidth(120);
        
        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(property.get());
        checkBox.setStyle("-fx-background-color: #3c3c46; -fx-border-color: #5a5a68;");
        
        checkBox.selectedProperty().bindBidirectional(property);
        
        HBox hbox = new HBox(10, label, checkBox);
        hbox.setStyle("-fx-alignment: center-left;");
        return hbox;
    }
    
    private static void enforceHandleInsideBounds(VaseParameters params) {
        float totalHeight = (float) params.height.get();
        float yStart = -totalHeight / 2f;
        float yEnd = totalHeight / 2f;

        float attachRadius = com.example.viewer.utils.MathUtils.findMaxVaseRadius(params);
        float maxHandleSize = Math.max(20f, totalHeight * 0.5f - 2f);
        float constrainedSize = (float) Math.min(params.handleSize.get(), maxHandleSize);
        if (Math.abs(params.handleSize.get() - constrainedSize) > 1e-6) {
            params.handleSize.set(constrainedSize);
        }

        float handleSize = (float) params.handleSize.get();
        float minPos = yStart + attachRadius + handleSize;
        float maxPos = yEnd + attachRadius - handleSize;

        if (minPos > maxPos) {
            float mid = (minPos + maxPos) * 0.5f;
            params.handlePos.set(mid);
        } else {
            double clampedPos = Math.max(minPos, Math.min(maxPos, params.handlePos.get()));
            if (Math.abs(params.handlePos.get() - clampedPos) > 1e-6) {
                params.handlePos.set(clampedPos);
            }
        }
    }
}
