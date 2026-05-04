package com.example.viewer.controller;

import com.example.viewer.model.VaseParameters;
import com.example.viewer.geometry.*;
import com.example.viewer.utils.SceneExporter;
import com.example.viewer.view.ControlPanel;
import com.example.viewer.view.HelpDialog;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.CullFace;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;

public class MainController {
    private double anchorX;
    private double anchorY;
    private double anchorAngleX;
    private double anchorAngleY;
    private VaseParameters currentParams;
    private javafx.scene.AmbientLight ambientLight;
    private javafx.scene.PointLight pointLight;
    private Group worldGroup;
    private MeshView vaseMesh;
    private MeshView spoutMesh;
    private MeshView handleMesh;
    private MeshView lidDomeMesh;
    private MeshView lidKnobMesh;

    public MainController() {
        currentParams = new VaseParameters();
        currentParams.ambientLightEnabled.addListener((obs, oldVal, newVal) -> updateLightsVisibility());
        currentParams.pointLightEnabled.addListener((obs, oldVal, newVal) -> updateLightsVisibility());
    }

    public void start(Stage stage) {
        worldGroup = new Group();
        Group objectGroup = new Group();

        vaseMesh = createVaseMesh(currentParams);
        PhongMaterial vaseMaterial = new PhongMaterial(currentParams.bodyColor.get());
        vaseMesh.setMaterial(vaseMaterial);
        
        handleMesh = createHandleMesh(currentParams);
        PhongMaterial handleMaterial = new PhongMaterial(currentParams.handleColor.get());
        handleMesh.setMaterial(handleMaterial);
        Rotate handleRotation = new Rotate(-90, Rotate.Z_AXIS);
        handleMesh.getTransforms().add(handleRotation);

        lidDomeMesh = createLidDomeMesh(currentParams);
        PhongMaterial lidDomeMaterial = new PhongMaterial(currentParams.lidDomeColor.get());
        lidDomeMesh.setMaterial(lidDomeMaterial);
        
        lidKnobMesh = createLidKnobMesh(currentParams);
        PhongMaterial lidKnobMaterial = new PhongMaterial(currentParams.lidKnobColor.get());
        lidKnobMesh.setMaterial(lidKnobMaterial);
        
        spoutMesh = createSpoutMesh(currentParams);
        PhongMaterial spoutMaterial = new PhongMaterial(currentParams.bodyColor.get());
        spoutMesh.setMaterial(spoutMaterial);

        objectGroup.getChildren().addAll(vaseMesh, spoutMesh, handleMesh, lidDomeMesh, lidKnobMesh);
        worldGroup.getChildren().add(objectGroup);

        ambientLight = new javafx.scene.AmbientLight(Color.color(0.35, 0.35, 0.35));
        pointLight = new javafx.scene.PointLight(Color.WHITE);
        pointLight.setTranslateX(-300);
        pointLight.setTranslateY(-200);
        pointLight.setTranslateZ(-400);
        worldGroup.getChildren().addAll(ambientLight, pointLight);

        Rotate rotateX = new Rotate(160, Rotate.X_AXIS);
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
        
        ScrollPane controlPanel = ControlPanel.createControlPanel(currentParams, vaseMesh, spoutMesh, handleMesh, lidDomeMesh, lidKnobMesh);
        root.setRight(controlPanel);
        root.setStyle("-fx-background-color: #1a1a22;");
        
        Scene scene = new Scene(root, 1000, 700, true);
        scene.setFill(Color.rgb(20, 20, 26));
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        stage.setTitle("3D Objektum Nezegeto");
        stage.setScene(scene);
        stage.setMinWidth(700);
        stage.setMinHeight(500);
        stage.setMaximized(true);
        
        controlPanel.prefWidthProperty().bind(scene.widthProperty().multiply(0.3));
        controlPanel.maxWidthProperty().bind(scene.widthProperty().multiply(0.4));
        controlPanel.setMinWidth(220);
        stage.show();

        subScene.widthProperty().bind(subSceneHolder.widthProperty());
        subScene.heightProperty().bind(subSceneHolder.heightProperty());
        
        randomizeParameters();
    }

    private MenuBar createMenuBar() {
        MenuBar menuBar = new MenuBar();
        menuBar.setStyle("-fx-background-color: #1a1a22; -fx-border-color: #5e5e70; -fx-border-width: 0 0 1 0; -fx-pref-height: 30;");
        
        Menu fileMenu = new Menu("Fájl");
        fileMenu.setStyle("-fx-text-fill: #f5f5ff; -fx-background-color: transparent;");
        
        MenuItem newItem = new MenuItem("Új");
        newItem.setStyle("-fx-text-fill: #f5f5ff; -fx-background-color: transparent;");
        newItem.setOnAction(e -> randomizeParameters());

        MenuItem deleteItem = new MenuItem("Törlés");
        deleteItem.setStyle("-fx-text-fill: #f5f5ff; -fx-background-color: transparent;");
        deleteItem.setOnAction(e -> resetToDefaults());

        MenuItem exportItem = new MenuItem("Exportálás...");
        exportItem.setStyle("-fx-text-fill: #f5f5ff; -fx-background-color: transparent;");
        exportItem.setOnAction(e -> SceneExporter.exportToFile(currentParams, menuBar.getScene().getWindow()));

        MenuItem importItem = new MenuItem("Importálás...");
        importItem.setStyle("-fx-text-fill: #f5f5ff; -fx-background-color: transparent;");
        importItem.setOnAction(e -> SceneExporter.importFromFile(currentParams, menuBar.getScene().getWindow()));

        fileMenu.getItems().addAll(newItem, deleteItem, new javafx.scene.control.SeparatorMenuItem(), exportItem, importItem);
        menuBar.getMenus().add(fileMenu);

        Menu helpMenu = new Menu("Súgó");
        helpMenu.setStyle("-fx-text-fill: #f5f5ff; -fx-background-color: transparent;");

        MenuItem helpItem = new MenuItem("Súgó megjelenítése");
        helpItem.setStyle("-fx-text-fill: #f5f5ff; -fx-background-color: transparent;");
        helpItem.setOnAction(e -> HelpDialog.show(menuBar.getScene().getWindow()));

        helpMenu.getItems().add(helpItem);
        menuBar.getMenus().add(helpMenu);
        
        return menuBar;
    }
    
    private void randomizeParameters() {
        VaseParameters params = currentParams;
        
        params.height.set(170 + Math.random() * 110);
        params.wallThickness.set(4 + Math.random() * 6);
        params.bellyAmount.set(6 + Math.random() * 40);
        params.neckTaper.set(Math.random() * 22);
        params.baseRadius.set(30 + Math.random() * 32);
        params.radialSegments.set(24 + (int)(Math.random() * 104));
        
        params.spoutLength.set(4 + Math.random() * 26);
        params.spoutWidth.set(15 + Math.random() * 105);
        params.spoutLift.set(-20 + Math.random() * 38);
        
        params.lidHeight.set(20 + Math.random() * 36);
        params.knobHeight.set(8 + Math.random() * 34);
        params.knobRadius.set(10 + Math.random() * 10);
        
        params.handleSize.set(20 + Math.random() * 60);
        params.handlePos.set(-140 + Math.random() * 280);
        params.handleThickness.set(4 + Math.random() * 12);
        
        params.bodyColor.set(Color.color(
            0.6 + Math.random() * 0.4,
            0.3 + Math.random() * 0.4,
            0.2 + Math.random() * 0.3
        ));
        
        params.handleColor.set(Color.color(
            0.5 + Math.random() * 0.4,
            0.3 + Math.random() * 0.4,
            0.1 + Math.random() * 0.3
        ));
        
        params.lidDomeColor.set(Color.color(
            0.6 + Math.random() * 0.4,
            0.3 + Math.random() * 0.4,
            0.2 + Math.random() * 0.3
        ));
        
        params.lidKnobColor.set(Color.color(
            0.7 + Math.random() * 0.3,
            0.4 + Math.random() * 0.4,
            0.2 + Math.random() * 0.3
        ));
        
        params.ambientLightEnabled.set(true);
        params.pointLightEnabled.set(Math.random() > 0.3);
        
        updateMeshColorsAfterRandomization();
        updateLightsVisibility();
    }
    
    private void resetToDefaults() {
        VaseParameters params = currentParams;

        params.height.set(260);
        params.wallThickness.set(10);
        params.bellyAmount.set(22);
        params.neckTaper.set(4);
        params.baseRadius.set(38);
        params.radialSegments.set(72);

        params.spoutLength.set(12);
        params.spoutWidth.set(70);
        params.spoutLift.set(7);

        params.lidHeight.set(30);
        params.knobHeight.set(20);
        params.knobRadius.set(12);

        params.handleSize.set(40);
        params.handleThickness.set(8);
        params.handlePos.set(0);

        params.bodyColor.set(Color.rgb(210, 130, 80));
        params.handleColor.set(Color.rgb(180, 100, 60));
        params.lidDomeColor.set(Color.rgb(196, 116, 72));
        params.lidKnobColor.set(Color.rgb(220, 140, 80));

        params.ambientLightEnabled.set(true);
        params.pointLightEnabled.set(true);

        updateMeshColorsAfterRandomization();
        updateLightsVisibility();
    }

    private void updateMeshColorsAfterRandomization() {
        ((PhongMaterial) vaseMesh.getMaterial()).setDiffuseColor(currentParams.bodyColor.get());
        ((PhongMaterial) spoutMesh.getMaterial()).setDiffuseColor(currentParams.bodyColor.get());
        ((PhongMaterial) handleMesh.getMaterial()).setDiffuseColor(currentParams.handleColor.get());
        ((PhongMaterial) lidDomeMesh.getMaterial()).setDiffuseColor(currentParams.lidDomeColor.get());
        ((PhongMaterial) lidKnobMesh.getMaterial()).setDiffuseColor(currentParams.lidKnobColor.get());
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
        
        scene.setOnScroll(event -> {
            double zoomFactor = event.getDeltaY() > 0 ? 0.9 : 1.1;
            double currentZ = camera.getTranslateZ();
            double newZ = currentZ * zoomFactor;
            
            if (newZ > -200) newZ = -200;
            if (newZ < -2000) newZ = -2000;
            
            camera.setTranslateZ(newZ);
        });
    }
    
    private void updateLightsVisibility() {
        worldGroup.getChildren().remove(ambientLight);
        worldGroup.getChildren().remove(pointLight);
        
        if (currentParams.ambientLightEnabled.get()) {
            worldGroup.getChildren().add(ambientLight);
        }
        if (currentParams.pointLightEnabled.get()) {
            worldGroup.getChildren().add(pointLight);
        }
    }

    private MeshView createVaseMesh(VaseParameters params) {
        MeshView meshView = new MeshView(VaseMeshGenerator.createVaseTriangleMesh(params));
        meshView.setCullFace(CullFace.NONE);
        return meshView;
    }

    private MeshView createHandleMesh(VaseParameters params) {
        MeshView meshView = new MeshView(HandleMeshGenerator.createHandleTriangleMesh(params));
        meshView.setCullFace(CullFace.NONE);
        return meshView;
    }

    private MeshView createSpoutMesh(VaseParameters params) {
        MeshView meshView = new MeshView(SpoutMeshGenerator.createSpoutTriangleMesh(params));
        meshView.setCullFace(CullFace.NONE);
        return meshView;
    }

    private MeshView createLidDomeMesh(VaseParameters params) {
        MeshView meshView = new MeshView(LidDomeMeshGenerator.createLidDomeTriangleMesh(params));
        meshView.setCullFace(CullFace.NONE);
        return meshView;
    }

    private MeshView createLidKnobMesh(VaseParameters params) {
        MeshView meshView = new MeshView(LidKnobMeshGenerator.createLidKnobTriangleMesh(params));
        meshView.setCullFace(CullFace.NONE);
        return meshView;
    }
}
