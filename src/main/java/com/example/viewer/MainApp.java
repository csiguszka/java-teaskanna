package com.example.viewer;

import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.TriangleMesh;
import javafx.scene.transform.Rotate;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class MainApp extends Application {
    private double anchorX;
    private double anchorY;
    private double anchorAngleX;
    private double anchorAngleY;
    private VaseParameters currentParams;
    private javafx.scene.AmbientLight ambientLight;
    private javafx.scene.PointLight pointLight;
    private Group worldGroup;

    @Override
    public void start(Stage stage) {
        worldGroup = new Group();
        Group objectGroup = new Group();

        currentParams = new VaseParameters();

        MeshView vase = createVaseMesh(currentParams);
        PhongMaterial vaseMaterial = new PhongMaterial(currentParams.bodyColor.get());
        vase.setMaterial(vaseMaterial);
        
        MeshView handle = createHandleMesh(currentParams);
        PhongMaterial handleMaterial = new PhongMaterial(currentParams.handleColor.get());
        handle.setMaterial(handleMaterial);
        Rotate handleRotation = new Rotate(-90, Rotate.Z_AXIS);
        handle.getTransforms().add(handleRotation);

        // Create separate meshes for lid dome and knob
        MeshView lidDome = createLidDomeMesh(currentParams);
        PhongMaterial lidDomeMaterial = new PhongMaterial(currentParams.lidDomeColor.get());
        lidDome.setMaterial(lidDomeMaterial);
        
        MeshView lidKnob = createLidKnobMesh(currentParams);
        PhongMaterial lidKnobMaterial = new PhongMaterial(currentParams.lidKnobColor.get());
        lidKnob.setMaterial(lidKnobMaterial);
        
        // Create separate mesh for spout part
        MeshView spout = createSpoutMesh(currentParams);
        PhongMaterial spoutMaterial = new PhongMaterial(currentParams.bodyColor.get());
        spout.setMaterial(spoutMaterial);

        objectGroup.getChildren().addAll(vase, spout, handle, lidDome, lidKnob);
        worldGroup.getChildren().add(objectGroup);

        ambientLight = new javafx.scene.AmbientLight(Color.color(0.35, 0.35, 0.35));
        pointLight = new javafx.scene.PointLight(Color.WHITE);
        pointLight.setTranslateX(-300);
        pointLight.setTranslateY(-200);
        pointLight.setTranslateZ(-400);
        worldGroup.getChildren().addAll(ambientLight, pointLight);

        Rotate rotateX = new Rotate(-20, Rotate.X_AXIS);
        Rotate rotateY = new Rotate(-20, Rotate.Y_AXIS);
        objectGroup.getTransforms().addAll(rotateX, rotateY);

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-650);
        camera.setNearClip(0.1);
        camera.setFarClip(5000);

        SubScene subScene = new SubScene(worldGroup, 1000, 700, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.rgb(20, 20, 26));
        subScene.setCamera(camera);

        setupMouseControls(subScene, rotateX, rotateY, camera);

        StackPane subSceneHolder = new StackPane(subScene);
        BorderPane root = new BorderPane();
        root.setTop(createMenuBar());
        root.setCenter(subSceneHolder);
        
        ScrollPane controlPanel = createControlPanel(currentParams, vase, spout, handle, lidDome, lidKnob);
        root.setRight(controlPanel);
        root.setStyle("-fx-background-color: #1a1a22;");
        
        Scene scene = new Scene(root, 1000, 700, true);
        scene.setFill(Color.rgb(20, 20, 26));

        stage.setTitle("3D Objektum Nezegeto");
        stage.setScene(scene);
        stage.setMinWidth(700); // Reduced minimum width
        stage.setMinHeight(500); // Reduced minimum height
        stage.setMaximized(true);
        
        // Make the layout responsive
        controlPanel.prefWidthProperty().bind(scene.widthProperty().multiply(0.3));
        controlPanel.maxWidthProperty().bind(scene.widthProperty().multiply(0.4));
        controlPanel.minWidthProperty().bind(scene.widthProperty().multiply(0.2));
        stage.show();

        subScene.widthProperty().bind(subSceneHolder.widthProperty());
        subScene.heightProperty().bind(subSceneHolder.heightProperty());
        
        // Initialize with random parameters on startup
        randomizeParameters();
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.setStyle("-fx-background-color: #2c2c36; -fx-border-color: #4a4a58; -fx-border-width: 0 0 1 0; -fx-pref-height: 30;");
        
        Menu fileMenu = new Menu("Fájl");
        fileMenu.setStyle("-fx-text-fill: #ffffff; -fx-background-color: transparent;");
        
        MenuItem newItem = new MenuItem("Új");
        newItem.setStyle("-fx-text-fill: #ffffff; -fx-background-color: transparent;");
        newItem.setOnAction(e -> randomizeParameters());
        
        fileMenu.getItems().add(newItem);
        menuBar.getMenus().add(fileMenu);
        
        return menuBar;
    }
    
    private void randomizeParameters() {
        VaseParameters params = getCurrentParameters();
        
        // Generate random values within the specified ranges
        params.height.set(170 + Math.random() * 110); // 170-280
        params.wallThickness.set(4 + Math.random() * 6); // 4-10
        params.bellyAmount.set(6 + Math.random() * 40); // 6-46
        params.neckTaper.set(Math.random() * 22); // 0-22
        params.baseRadius.set(30 + Math.random() * 32); // 30-62
        params.radialSegments.set(24 + (int)(Math.random() * 104)); // 24-128
        
        params.spoutLength.set(4 + Math.random() * 26); // 4-30
        params.spoutWidth.set(15 + Math.random() * 105); // 15-120
        params.spoutLift.set(-20 + Math.random() * 38); // -20-18
        
        params.lidHeight.set(20 + Math.random() * 36); // 20-56
        params.knobHeight.set(8 + Math.random() * 34); // 8-42
        params.knobRadius.set(10 + Math.random() * 10); // 10-20
        
        params.handleSize.set(20 + Math.random() * 60); // 20-80
        params.handlePos.set(-140 + Math.random() * 280); // -140 to 140
        params.handleThickness.set(4 + Math.random() * 12); // 4-16
        
        // Generate random colors
        params.bodyColor.set(Color.color(
            0.6 + Math.random() * 0.4,  // R: 0.6-1.0
            0.3 + Math.random() * 0.4,  // G: 0.3-0.7
            0.2 + Math.random() * 0.3   // B: 0.2-0.5
        ));
        
        params.handleColor.set(Color.color(
            0.5 + Math.random() * 0.4,  // R: 0.5-0.9
            0.3 + Math.random() * 0.4,  // G: 0.3-0.7
            0.1 + Math.random() * 0.3   // B: 0.1-0.4
        ));
        
        params.lidDomeColor.set(Color.color(
            0.6 + Math.random() * 0.4,  // R: 0.6-1.0
            0.3 + Math.random() * 0.4,  // G: 0.3-0.7
            0.2 + Math.random() * 0.3   // B: 0.2-0.5
        ));
        
        params.lidKnobColor.set(Color.color(
            0.7 + Math.random() * 0.3,  // R: 0.7-1.0
            0.4 + Math.random() * 0.4,  // G: 0.4-0.8
            0.2 + Math.random() * 0.3   // B: 0.2-0.5
        ));
        
        // Randomize light states
        params.ambientLightEnabled.set(Math.random() > 0.3); // 70% chance of being enabled
        params.pointLightEnabled.set(Math.random() > 0.3);  // 70% chance of being enabled
    }
    
    private VaseParameters getCurrentParameters() {
        return currentParams;
    }

    private void setupMouseControls(SubScene scene, Rotate rotateX, Rotate rotateY, PerspectiveCamera camera) {
        scene.addEventHandler(MouseEvent.MOUSE_PRESSED, event -> {
            anchorX = event.getSceneX();
            anchorY = event.getSceneY();
            anchorAngleX = rotateX.getAngle();
            anchorAngleY = rotateY.getAngle();
        });

        scene.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            rotateX.setAngle(anchorAngleX - (event.getSceneY() - anchorY) * 0.45);
            rotateY.setAngle(anchorAngleY + (event.getSceneX() - anchorX) * 0.45);
        });
        
        // Add zoom functionality with mouse wheel
        scene.setOnScroll(event -> {
            double zoomFactor = event.getDeltaY() > 0 ? 0.9 : 1.1;
            double currentZ = camera.getTranslateZ();
            double newZ = currentZ * zoomFactor;
            
            // Constrain zoom limits
            if (newZ > -200) newZ = -200;  // Max zoom in
            if (newZ < -2000) newZ = -2000; // Max zoom out
            
            camera.setTranslateZ(newZ);
        });
    }

    private ScrollPane createControlPanel(VaseParameters params, MeshView vase, MeshView spout, MeshView handle, MeshView lidDome, MeshView lidKnob) {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(14, 14, 40, 14)); // Add more bottom padding for proper scrolling
        panel.setPrefWidth(300);
        panel.setMinWidth(250); // Allow responsive shrinking
        panel.setMaxWidth(350); // Allow some expansion
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
        scrollPane.setMinWidth(250); // Allow responsive shrinking
        scrollPane.setMaxWidth(350); // Allow some expansion
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setPannable(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background: #2c2c36; -fx-background-color: #2c2c36; -fx-padding: 0;");
        return scrollPane;
    }

    private HBox createColorControl(
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
        
        colorPicker.setOnAction(e -> {
            colorProperty.set(colorPicker.getValue());
            updateMeshColors(vase, spout, handle, lidDome, lidKnob, params);
        });
        
        HBox hbox = new HBox(10, label, colorPicker);
        hbox.setStyle("-fx-alignment: center-left;");
        return hbox;
    }
    
    private void updateMeshColors(MeshView vase, MeshView spout, MeshView handle, MeshView lidDome, MeshView lidKnob, VaseParameters params) {
        ((PhongMaterial) vase.getMaterial()).setDiffuseColor(params.bodyColor.get());
        ((PhongMaterial) spout.getMaterial()).setDiffuseColor(params.bodyColor.get()); // Spout uses body color
        ((PhongMaterial) handle.getMaterial()).setDiffuseColor(params.handleColor.get());
        ((PhongMaterial) lidDome.getMaterial()).setDiffuseColor(params.lidDomeColor.get());
        ((PhongMaterial) lidKnob.getMaterial()).setDiffuseColor(params.lidKnobColor.get());
    }

    private VBox createSliderControl(
            String labelText,
            DoubleProperty property,
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
            vase.setMesh(createVaseTriangleMesh(params));
            spout.setMesh(createSpoutTriangleMesh(params));
            handle.setMesh(createHandleTriangleMesh(params));
            lidDome.setMesh(createLidDomeTriangleMesh(params));
            lidKnob.setMesh(createLidKnobTriangleMesh(params));
        });

        return new VBox(4, label, slider);
    }

    private HBox createToggleControl(String labelText, javafx.beans.property.BooleanProperty property) {
        Label label = new Label(labelText);
        label.setStyle("-fx-text-fill: #e7e7e7; -fx-font-size: 14px;");
        label.setPrefWidth(120);
        
        CheckBox checkBox = new CheckBox();
        checkBox.setSelected(property.get());
        checkBox.setStyle("-fx-background-color: #3c3c46; -fx-border-color: #5a5a68;");
        
        checkBox.selectedProperty().bindBidirectional(property);
        checkBox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            updateLightsVisibility();
        });
        
        HBox hbox = new HBox(10, label, checkBox);
        hbox.setStyle("-fx-alignment: center-left;");
        return hbox;
    }
    
    private void updateLightsVisibility() {
        // Remove existing lights
        worldGroup.getChildren().remove(ambientLight);
        worldGroup.getChildren().remove(pointLight);
        
        // Add lights back based on their enabled state
        if (currentParams.ambientLightEnabled.get()) {
            worldGroup.getChildren().add(ambientLight);
        }
        if (currentParams.pointLightEnabled.get()) {
            worldGroup.getChildren().add(pointLight);
        }
    }

    private VBox createSliderControl(
            String labelText,
            IntegerProperty property,
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
            vase.setMesh(createVaseTriangleMesh(params));
            spout.setMesh(createSpoutTriangleMesh(params));
            handle.setMesh(createHandleTriangleMesh(params));
            lidDome.setMesh(createLidDomeTriangleMesh(params));
            lidKnob.setMesh(createLidKnobTriangleMesh(params));
        });

        return new VBox(4, label, slider);
    }

    private MeshView createVaseMesh(VaseParameters params) {
        MeshView meshView = new MeshView(createVaseTriangleMesh(params));
        meshView.setCullFace(CullFace.NONE);
        return meshView;
    }

    private MeshView createHandleMesh(VaseParameters params) {
        MeshView meshView = new MeshView(createHandleTriangleMesh(params));
        meshView.setCullFace(CullFace.NONE);
        return meshView;
    }

    private MeshView createSpoutMesh(VaseParameters params) {
        MeshView meshView = new MeshView(createSpoutTriangleMesh(params));
        meshView.setCullFace(CullFace.NONE);
        return meshView;
    }

    private MeshView createLidDomeMesh(VaseParameters params) {
        MeshView meshView = new MeshView(createLidDomeTriangleMesh(params));
        meshView.setCullFace(CullFace.NONE);
        return meshView;
    }

    private MeshView createLidKnobMesh(VaseParameters params) {
        MeshView meshView = new MeshView(createLidKnobTriangleMesh(params));
        meshView.setCullFace(CullFace.NONE);
        return meshView;
    }

    private TriangleMesh createVaseTriangleMesh(VaseParameters params) {
        int radialSegments = params.radialSegments.get();
        int verticalSegments = 64;
        float totalHeight = (float) params.height.get();
        float wallThickness = (float) params.wallThickness.get();
        float yStart = -totalHeight / 2f;

        float[] baseOuterRadius = new float[verticalSegments + 1];
        float[] yValues = new float[verticalSegments + 1];

        for (int i = 0; i <= verticalSegments; i++) {
            float t = (float) i / verticalSegments;
            yValues[i] = yStart + t * totalHeight;

            baseOuterRadius[i] = vaseProfileRadius(t, params);
        }

        TriangleMesh mesh = new TriangleMesh();
        mesh.getTexCoords().addAll(0f, 0f);

        int ringSize = radialSegments + 1;

        // Kulso fal pontok
        for (int yi = 0; yi <= verticalSegments; yi++) {
            float t = (float) yi / verticalSegments;
            float y = yValues[yi];
            float baseRadius = baseOuterRadius[yi];
            for (int ri = 0; ri <= radialSegments; ri++) {
                double angle = 2.0 * Math.PI * ri / radialSegments;
                // Remove spout geometry from vase body - it's now a separate mesh
                float radius = baseRadius;
                float liftedY = y;
                float x = (float) (radius * Math.cos(angle));
                float z = (float) (radius * Math.sin(angle));
                mesh.getPoints().addAll(x, liftedY, z);
            }
        }

        int innerOffset = mesh.getPoints().size() / 3;

        // Belso fal pontok (kulon gyuru, ugyanazzal a mintavetelezessel)
        for (int yi = 0; yi <= verticalSegments; yi++) {
            float t = (float) yi / verticalSegments;
            float y = yValues[yi];
            float baseRadius = baseOuterRadius[yi];
            for (int ri = 0; ri <= radialSegments; ri++) {
                double angle = 2.0 * Math.PI * ri / radialSegments;
                // Remove spout geometry from inner wall - it's now a separate mesh
                float radius = Math.max(10f, baseRadius - wallThickness);
                float liftedY = y;
                float x = (float) (radius * Math.cos(angle));
                float z = (float) (radius * Math.sin(angle));
                mesh.getPoints().addAll(x, liftedY, z);
            }
        }

        // Kulso oldalfeluletek
        for (int yi = 0; yi < verticalSegments; yi++) {
            for (int ri = 0; ri < radialSegments; ri++) {
                int p0 = yi * ringSize + ri;
                int p1 = p0 + 1;
                int p2 = p0 + ringSize;
                int p3 = p2 + 1;
                mesh.getFaces().addAll(p0, 0, p2, 0, p1, 0);
                mesh.getFaces().addAll(p1, 0, p2, 0, p3, 0);
            }
        }

        // Belso oldalfeluletek (forditott irany)
        for (int yi = 0; yi < verticalSegments; yi++) {
            for (int ri = 0; ri < radialSegments; ri++) {
                int p0 = innerOffset + yi * ringSize + ri;
                int p1 = p0 + 1;
                int p2 = p0 + ringSize;
                int p3 = p2 + 1;
                mesh.getFaces().addAll(p0, 0, p1, 0, p2, 0);
                mesh.getFaces().addAll(p1, 0, p3, 0, p2, 0);
            }
        }

        // Also zaro perem (falvastagsag kozti kitoltes)
        int outerBottom = 0;
        int innerBottom = innerOffset;
        for (int ri = 0; ri < radialSegments; ri++) {
            int ob0 = outerBottom + ri;
            int ob1 = outerBottom + ri + 1;
            int ib0 = innerBottom + ri;
            int ib1 = innerBottom + ri + 1;
            mesh.getFaces().addAll(ob0, 0, ib0, 0, ob1, 0);
            mesh.getFaces().addAll(ob1, 0, ib0, 0, ib1, 0);
        }

        // Felso zaro perem (tetejen a lyuk megmarad, csak a falvastagsag lesz lefedve)
        int outerTop = verticalSegments * ringSize;
        int innerTop = innerOffset + verticalSegments * ringSize;
        for (int ri = 0; ri < radialSegments; ri++) {
            int ot0 = outerTop + ri;
            int ot1 = outerTop + ri + 1;
            int it0 = innerTop + ri;
            int it1 = innerTop + ri + 1;
            mesh.getFaces().addAll(ot0, 0, ot1, 0, it0, 0);
            mesh.getFaces().addAll(ot1, 0, it1, 0, it0, 0);
        }

        // Teljes also alaplemez: a belso gyuru korlapos lezarsa
        float bottomCenterY = yValues[0];
        int bottomCenterIndex = mesh.getPoints().size() / 3;
        mesh.getPoints().addAll(0f, bottomCenterY, 0f);
        for (int ri = 0; ri < radialSegments; ri++) {
            int ib0 = innerBottom + ri;
            int ib1 = innerBottom + ri + 1;
            mesh.getFaces().addAll(bottomCenterIndex, 0, ib1, 0, ib0, 0);
        }

        return mesh;
    }

    private TriangleMesh createSpoutTriangleMesh(VaseParameters params) {
        int radialSegments = params.radialSegments.get();
        int verticalSegments = 64;
        float totalHeight = (float) params.height.get();
        float wallThickness = (float) params.wallThickness.get();
        float yStart = -totalHeight / 2f;

        float[] baseOuterRadius = new float[verticalSegments + 1];
        float[] yValues = new float[verticalSegments + 1];

        for (int i = 0; i <= verticalSegments; i++) {
            float t = (float) i / verticalSegments;
            yValues[i] = yStart + t * totalHeight;
            baseOuterRadius[i] = vaseProfileRadius(t, params);
        }

        TriangleMesh mesh = new TriangleMesh();
        mesh.getTexCoords().addAll(0f, 0f);

        int ringSize = radialSegments + 1;

        // Create spout geometry with smooth blending
        for (int yi = 0; yi <= verticalSegments; yi++) {
            float t = (float) yi / verticalSegments;
            float y = yValues[yi];
            float baseRadius = baseOuterRadius[yi];
            for (int ri = 0; ri <= radialSegments; ri++) {
                double angle = 2.0 * Math.PI * ri / radialSegments;
                float spoutWeight = computeSpoutWeight(t, angle, params);
                
                // Always add points, but use spout weight for smooth blending
                float radius = baseRadius + (float) params.spoutLength.get() * spoutWeight;
                float liftedY = y + (float) params.spoutLift.get() * spoutWeight;
                float x = (float) (radius * Math.cos(angle));
                float z = (float) (radius * Math.sin(angle));
                mesh.getPoints().addAll(x, liftedY, z);
            }
        }

        // Create faces for spout surface
        for (int yi = 0; yi < verticalSegments; yi++) {
            for (int ri = 0; ri < radialSegments; ri++) {
                int p0 = yi * ringSize + ri;
                int p1 = p0 + 1;
                int p2 = p0 + ringSize;
                int p3 = p2 + 1;
                
                mesh.getFaces().addAll(p0, 0, p2, 0, p1, 0);
                mesh.getFaces().addAll(p1, 0, p2, 0, p3, 0);
            }
        }

        return mesh;
    }

    private float vaseProfileRadius(float t, VaseParameters params) {
        return (float) params.baseRadius.get()
                + (float) params.bellyAmount.get() * (float) Math.sin(Math.PI * t)
                + 9f * (float) Math.sin(2.3 * Math.PI * t + 0.4)
                - (float) params.neckTaper.get() * (float) Math.pow(t, 1.4);
    }

    private float computeSpoutWeight(float t, double angle, VaseParameters params) {
        float topMask = smoothStep(0.68f, 1.0f, t);
        float widthRad = (float) Math.toRadians(params.spoutWidth.get() * 0.5);
        widthRad = Math.max(0.12f, widthRad);
        float centeredAngle = (float) Math.atan2(Math.sin(angle - Math.PI), Math.cos(angle - Math.PI));
        float angularMask = (float) Math.exp(-Math.pow(centeredAngle / widthRad, 2.0));
        return topMask * angularMask;
    }

    private float smoothStep(float edge0, float edge1, float x) {
        float t = (x - edge0) / (edge1 - edge0);
        t = Math.max(0f, Math.min(1f, t));
        return t * t * (3f - 2f * t);
    }

    private TriangleMesh createHandleTriangleMesh(VaseParameters params) {
        int segments = 32;
        int thicknessSegments = 8;
        
        float handleSize = (float) params.handleSize.get();
        float handleThickness = (float) params.handleThickness.get();
        float posX = (float) params.handlePos.get();
        float totalHeight = (float) params.height.get();
        float baseRadius = (float) params.baseRadius.get();
        float bellyAmount = (float) params.bellyAmount.get();
        float neckTaper = (float) params.neckTaper.get();
        
        // Find the actual vase edge point where handle should attach
        float yStart = -totalHeight / 2f;
        float attachRadius = 0;
        
        // Find the point where vase has maximum radius (the "edge")
        float maxRadius = 0;
        float maxRadiusY = yStart;
        
        for (int i = 0; i <= 100; i++) {
            float t = (float) i / 100;
            float y = yStart + t * totalHeight;
            float radius = vaseProfileRadius(t, params);
            
            if (radius > maxRadius) {
                maxRadius = radius;
                maxRadiusY = y;
            }
        }
        
        attachRadius = maxRadius;

        // The handle is rotated around Z_AXIS by -90 degrees in start().
        // After that rotation, the handle's local Y coordinate becomes the radial distance from the vase's Y axis.
        // So we compute an attachY value (radial distance) that makes both arc endpoints match the vase outer radius
        // at the corresponding Y positions (as a function of the arc's x/y placement).
        float arcX0 = attachRadius + handleSize - posX;      // angle=0 -> cos=1
        float arcX1 = attachRadius - handleSize - posX;      // angle=pi -> cos=-1

        // Rotation: newY = -oldX (Rotate(-90, Z_AXIS))
        float vaseY0 = -arcX0;
        float vaseY1 = -arcX1;

        float t0 = (vaseY0 - yStart) / totalHeight;
        float t1 = (vaseY1 - yStart) / totalHeight;
        t0 = Math.max(0f, Math.min(1f, t0));
        t1 = Math.max(0f, Math.min(1f, t1));

        float r0 = vaseProfileRadius(t0, params);
        float r1 = vaseProfileRadius(t1, params);

        // Both arc endpoints (angle=0 and angle=pi) share the same local arcY term at the moment.
        // If the vase radius differs at the two endpoint heights, only one side will match.
        // We keep one endpoint correct and apply a smooth radial correction across the other half.
        // Weight: w(0)=0, w(pi)=1 so the correction is 0 at one end and full at the other.
        float d0 = Math.abs((t0 * totalHeight + yStart) - maxRadiusY);
        float d1 = Math.abs((t1 * totalHeight + yStart) - maxRadiusY);
        boolean baseIs0 = d0 <= d1;

        float attachYBase = baseIs0 ? r0 : r1;
        float deltaY = baseIs0 ? (r1 - r0) : (r0 - r1);
        
        TriangleMesh mesh = new TriangleMesh();
        mesh.getTexCoords().addAll(0f, 0f);
        
        // Create simple arc handle points
        for (int i = 0; i <= segments; i++) {
            float arcT = (float) i / segments;
            float angle = (float) Math.PI * arcT; // Half circle arc
            
            // Arc path: half circle in the handle's local X/Y plane.
            // The attachY value is chosen so the two arc endpoints connect to the vase outer surface.
            float arcX = attachRadius + handleSize * (float) Math.cos(angle) - posX;

            float w = 0.5f * (1f - (float) Math.cos(angle)); // 0 at angle=0, 1 at angle=pi
            float wForOtherHalf = baseIs0 ? w : (1f - w);
            float arcY = attachYBase + deltaY * wForOtherHalf + handleSize * (float) Math.sin(angle);
            float arcZ = 0; // Z position fixed at 0
            
            // Create circular cross-section at each arc point
            for (int j = 0; j <= thicknessSegments; j++) {
                float thicknessAngle = 2.0f * (float) Math.PI * j / thicknessSegments;
                
                // Thickness in X-Z plane (perpendicular to arc direction)
                float thicknessX = handleThickness * 0.5f * (float) Math.cos(thicknessAngle);
                float thicknessZ = handleThickness * 0.5f * (float) Math.sin(thicknessAngle);
                
                // Keep cross-section thickness centered on the arc centerline.
                // This avoids asymmetric vertical/radial offsets after the handle's Z rotation.
                float x = arcX + thicknessX;
                float y = arcY;
                float z = arcZ + thicknessZ;
                
                mesh.getPoints().addAll(x, y, z);
            }
        }
        
        // Create faces for the handle
        int ringSize = thicknessSegments + 1;
        for (int i = 0; i < segments; i++) {
            for (int j = 0; j < thicknessSegments; j++) {
                int p0 = i * ringSize + j;
                int p1 = p0 + 1;
                int p2 = p0 + ringSize;
                int p3 = p2 + 1;
                
                mesh.getFaces().addAll(p0, 0, p2, 0, p1, 0);
                mesh.getFaces().addAll(p1, 0, p2, 0, p3, 0);
            }
        }
        
        return mesh;
    }

    private TriangleMesh createLidDomeTriangleMesh(VaseParameters params) {
        int radialSegments = Math.max(24, params.radialSegments.get());
        int domeRings = 18;
        int ringSize = radialSegments + 1;

        float totalHeight = (float) params.height.get();
        float yStart = -totalHeight / 2f;
        float topY = yStart + totalHeight;

        float topRadius = vaseProfileRadius(1f, params);
        float lidBaseRadius = topRadius;
        float lidHeight = (float) params.lidHeight.get();
        float lidBottomY = topY + 0.6f;

        TriangleMesh mesh = new TriangleMesh();
        mesh.getTexCoords().addAll(0f, 0f);

        // Dome rings: gently stepped profile, richer than a plain truncated hemisphere.
        for (int yi = 0; yi <= domeRings; yi++) {
            float t = (float) yi / domeRings;
            float y = lidBottomY + t * lidHeight;
            float profile = (float) Math.pow(Math.max(0f, 1f - t * t), 0.62);
            float ridge = 1.0f + 0.08f * (float) Math.sin(4.0 * Math.PI * t) * (1f - t);
            float radius = Math.max(lidBaseRadius * 0.22f, lidBaseRadius * profile * ridge);

            for (int ri = 0; ri <= radialSegments; ri++) {
                double angle = 2.0 * Math.PI * ri / radialSegments;
                float x = (float) (radius * Math.cos(angle));
                float z = (float) (radius * Math.sin(angle));
                mesh.getPoints().addAll(x, y, z);
            }
        }

        for (int yi = 0; yi < domeRings; yi++) {
            for (int ri = 0; ri < radialSegments; ri++) {
                int p0 = yi * ringSize + ri;
                int p1 = p0 + 1;
                int p2 = p0 + ringSize;
                int p3 = p2 + 1;
                mesh.getFaces().addAll(p0, 0, p2, 0, p1, 0);
                mesh.getFaces().addAll(p1, 0, p2, 0, p3, 0);
            }
        }

        return mesh;
    }

    private TriangleMesh createLidKnobTriangleMesh(VaseParameters params) {
        int radialSegments = Math.max(24, params.radialSegments.get());
        int domeRings = 18;
        int ringSize = radialSegments + 1;

        float totalHeight = (float) params.height.get();
        float yStart = -totalHeight / 2f;
        float topY = yStart + totalHeight;

        float topRadius = vaseProfileRadius(1f, params);
        float lidBaseRadius = topRadius;
        float lidHeight = (float) params.lidHeight.get();
        float knobHeight = (float) params.knobHeight.get();
        float knobRadius = (float) params.knobRadius.get();
        float lidBottomY = topY + 0.6f;

        TriangleMesh mesh = new TriangleMesh();
        mesh.getTexCoords().addAll(0f, 0f);

        int domeTopStart = domeRings * ringSize;
        float knobBottomY = lidBottomY + lidHeight - 1.2f;
        float knobTopY = knobBottomY + knobHeight;
        float knobTopRadius = knobRadius * 0.78f;
        int knobStart = mesh.getPoints().size() / 3;

        // Truncated hexagonal knob.
        for (int ring = 0; ring <= 1; ring++) {
            float y = ring == 0 ? knobBottomY : knobTopY;
            float r = ring == 0 ? knobRadius : knobTopRadius;
            for (int i = 0; i <= 6; i++) {
                double angle = 2.0 * Math.PI * i / 6.0 + Math.PI / 6.0;
                float x = (float) (r * Math.cos(angle));
                float z = (float) (r * Math.sin(angle));
                mesh.getPoints().addAll(x, y, z);
            }
        }

        int hexRingSize = 7;
        for (int i = 0; i < 6; i++) {
            int b0 = knobStart + i;
            int b1 = knobStart + i + 1;
            int t0 = knobStart + hexRingSize + i;
            int t1 = knobStart + hexRingSize + i + 1;
            mesh.getFaces().addAll(b0, 0, t0, 0, b1, 0);
            mesh.getFaces().addAll(b1, 0, t0, 0, t1, 0);
        }

        int topCenter = mesh.getPoints().size() / 3;
        mesh.getPoints().addAll(0f, knobTopY, 0f);
        for (int i = 0; i < 6; i++) {
            int t0 = knobStart + hexRingSize + i;
            int t1 = knobStart + hexRingSize + i + 1;
            mesh.getFaces().addAll(topCenter, 0, t0, 0, t1, 0);
        }

        return mesh;
    }

    private static class VaseParameters {
        final DoubleProperty height = new SimpleDoubleProperty(260);
        final DoubleProperty wallThickness = new SimpleDoubleProperty(10);
        final DoubleProperty bellyAmount = new SimpleDoubleProperty(22);
        final DoubleProperty neckTaper = new SimpleDoubleProperty(4);
        final DoubleProperty baseRadius = new SimpleDoubleProperty(38);
        final IntegerProperty radialSegments = new SimpleIntegerProperty(72);

        final DoubleProperty spoutLength = new SimpleDoubleProperty(12);
        final DoubleProperty spoutWidth = new SimpleDoubleProperty(70);
        final DoubleProperty spoutLift = new SimpleDoubleProperty(7);

        final DoubleProperty lidHeight = new SimpleDoubleProperty(30);
        final DoubleProperty knobHeight = new SimpleDoubleProperty(20);
        final DoubleProperty knobRadius = new SimpleDoubleProperty(12);
        
        // Handle parameters
        final DoubleProperty handleSize = new SimpleDoubleProperty(40);
        final DoubleProperty handleThickness = new SimpleDoubleProperty(8);
        
        // Handle position parameter (constrained to vase height)
        final DoubleProperty handlePos = new SimpleDoubleProperty(0) {
            @Override
            public void set(double value) {
                // Constrain to vase height range
                double minHeight = -height.get() / 2;
                double maxHeight = height.get() / 2;
                double constrainedValue = Math.max(minHeight, Math.min(maxHeight, value));
                super.set(constrainedValue);
            }
        };
        
        // Color properties for each part
        final javafx.beans.property.ObjectProperty<Color> bodyColor = new javafx.beans.property.SimpleObjectProperty<>(Color.rgb(210, 130, 80));
        final javafx.beans.property.ObjectProperty<Color> handleColor = new javafx.beans.property.SimpleObjectProperty<>(Color.rgb(180, 100, 60));
        final javafx.beans.property.ObjectProperty<Color> lidDomeColor = new javafx.beans.property.SimpleObjectProperty<>(Color.rgb(196, 116, 72));
        final javafx.beans.property.ObjectProperty<Color> lidKnobColor = new javafx.beans.property.SimpleObjectProperty<>(Color.rgb(220, 140, 80));
        
        // Light toggle properties
        final javafx.beans.property.BooleanProperty ambientLightEnabled = new javafx.beans.property.SimpleBooleanProperty(true);
        final javafx.beans.property.BooleanProperty pointLightEnabled = new javafx.beans.property.SimpleBooleanProperty(true);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
