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
import javafx.stage.Stage;

public class MainApp extends Application {
    private double anchorX;
    private double anchorY;
    private double anchorAngleX;
    private double anchorAngleY;

    @Override
    public void start(Stage stage) {
        Group world = new Group();
        Group objectGroup = new Group();

        VaseParameters params = new VaseParameters();

        MeshView vase = createVaseMesh(params);
        vase.setMaterial(new PhongMaterial(Color.rgb(210, 130, 80)));
        objectGroup.getChildren().add(vase);
        world.getChildren().add(objectGroup);

        var ambientLight = new javafx.scene.AmbientLight(Color.color(0.35, 0.35, 0.35));
        var pointLight = new javafx.scene.PointLight(Color.WHITE);
        pointLight.setTranslateX(-300);
        pointLight.setTranslateY(-200);
        pointLight.setTranslateZ(-400);
        world.getChildren().addAll(ambientLight, pointLight);

        Rotate rotateX = new Rotate(-20, Rotate.X_AXIS);
        Rotate rotateY = new Rotate(-20, Rotate.Y_AXIS);
        objectGroup.getTransforms().addAll(rotateX, rotateY);

        PerspectiveCamera camera = new PerspectiveCamera(true);
        camera.setTranslateZ(-650);
        camera.setNearClip(0.1);
        camera.setFarClip(5000);

        SubScene subScene = new SubScene(world, 1000, 700, true, SceneAntialiasing.BALANCED);
        subScene.setFill(Color.rgb(20, 20, 26));
        subScene.setCamera(camera);

        setupMouseControls(subScene, rotateX, rotateY);

        StackPane subSceneHolder = new StackPane(subScene);
        BorderPane root = new BorderPane();
        root.setCenter(subSceneHolder);
        root.setRight(createControlPanel(params, vase));
        root.setStyle("-fx-background-color: #1a1a22;");
        Scene scene = new Scene(root, 1000, 700, true);
        scene.setFill(Color.rgb(20, 20, 26));

        stage.setTitle("3D Objektum Nezegeto");
        stage.setScene(scene);
        stage.setMinWidth(900);
        stage.setMinHeight(620);
        stage.show();

        subScene.widthProperty().bind(subSceneHolder.widthProperty());
        subScene.heightProperty().bind(subSceneHolder.heightProperty());
    }

    private void setupMouseControls(SubScene scene, Rotate rotateX, Rotate rotateY) {
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
    }

    private ScrollPane createControlPanel(VaseParameters params, MeshView vase) {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(14));
        panel.setPrefWidth(300);
        panel.setMinWidth(300);
        panel.setMaxWidth(300);
        panel.setStyle("-fx-background-color: #2c2c36; -fx-border-color: #4a4a58; -fx-border-width: 0 0 0 1;");

        Label title = new Label("Vaza parameterek");
        title.setStyle("-fx-text-fill: #f2f2f2; -fx-font-size: 15px; -fx-font-weight: bold;");

        panel.getChildren().add(title);
        panel.getChildren().add(createSliderControl("Magassag", params.height, 170, 360, vase, params));
        panel.getChildren().add(createSliderControl("Falvastagsag", params.wallThickness, 4, 28, vase, params));
        panel.getChildren().add(createSliderControl("Hasasodas", params.bellyAmount, 6, 46, vase, params));
        panel.getChildren().add(createSliderControl("Nyakszukules", params.neckTaper, 0, 22, vase, params));
        panel.getChildren().add(createSliderControl("Also sugar", params.baseRadius, 18, 62, vase, params));
        panel.getChildren().add(createSliderControl("Felbontas", params.radialSegments, 24, 128, vase, params));

        ScrollPane scrollPane = new ScrollPane(panel);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefWidth(300);
        scrollPane.setMinWidth(300);
        scrollPane.setMaxWidth(300);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setStyle("-fx-background: #2c2c36; -fx-background-color: #2c2c36;");
        return scrollPane;
    }

    private VBox createSliderControl(
            String labelText,
            DoubleProperty property,
            double min,
            double max,
            MeshView vase,
            VaseParameters params
    ) {
        Slider slider = new Slider(min, max, property.get());
        slider.setBlockIncrement((max - min) / 40.0);
        slider.valueProperty().addListener((obs, oldVal, newVal) -> property.set(newVal.doubleValue()));

        Label label = new Label();
        label.setStyle("-fx-text-fill: #e7e7e7;");
        label.textProperty().bind(property.asString(labelText + ": %.1f"));

        property.addListener((obs, oldVal, newVal) -> vase.setMesh(createVaseTriangleMesh(params)));

        return new VBox(4, label, slider);
    }

    private VBox createSliderControl(
            String labelText,
            IntegerProperty property,
            int min,
            int max,
            MeshView vase,
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

        property.addListener((obs, oldVal, newVal) -> vase.setMesh(createVaseTriangleMesh(params)));

        return new VBox(4, label, slider);
    }

    private MeshView createVaseMesh(VaseParameters params) {
        MeshView meshView = new MeshView(createVaseTriangleMesh(params));
        meshView.setCullFace(CullFace.BACK);
        return meshView;
    }

    private TriangleMesh createVaseTriangleMesh(VaseParameters params) {
        System.out.println("Creating vase triangle mesh with params: " + params);
        int radialSegments = params.radialSegments.get();
        int verticalSegments = 64;
        float totalHeight = (float) params.height.get();
        float wallThickness = (float) params.wallThickness.get();
        float yStart = -totalHeight / 2f;

        float[] outerRadius = new float[verticalSegments + 1];
        float[] innerRadius = new float[verticalSegments + 1];
        float[] yValues = new float[verticalSegments + 1];

        for (int i = 0; i <= verticalSegments; i++) {
            float t = (float) i / verticalSegments;
            yValues[i] = yStart + t * totalHeight;

            // Sima, kifelé domborodó váza profil (forgástest), paraméterezve
            float r = (float) params.baseRadius.get()
                    + (float) params.bellyAmount.get() * (float) Math.sin(Math.PI * t)
                    + 9f * (float) Math.sin(2.3 * Math.PI * t + 0.4)
                    - (float) params.neckTaper.get() * (float) Math.pow(t, 1.4);
            outerRadius[i] = r;
            innerRadius[i] = Math.max(10f, r - wallThickness);
        }

        TriangleMesh mesh = new TriangleMesh();
        mesh.getTexCoords().addAll(0f, 0f);

        int ringSize = radialSegments + 1;

        // Kulso fal pontok
        for (int yi = 0; yi <= verticalSegments; yi++) {
            float y = yValues[yi];
            float radius = outerRadius[yi];
            for (int ri = 0; ri <= radialSegments; ri++) {
                double angle = 2.0 * Math.PI * ri / radialSegments;
                float x = (float) (radius * Math.cos(angle));
                float z = (float) (radius * Math.sin(angle));
                mesh.getPoints().addAll(x, y, z);
            }
        }

        int innerOffset = mesh.getPoints().size() / 3;

        // Belso fal pontok (kulon gyuru, ugyanazzal a mintavetelezessel)
        for (int yi = 0; yi <= verticalSegments; yi++) {
            float y = yValues[yi];
            float radius = innerRadius[yi];
            for (int ri = 0; ri <= radialSegments; ri++) {
                double angle = 2.0 * Math.PI * ri / radialSegments;
                float x = (float) (radius * Math.cos(angle));
                float z = (float) (radius * Math.sin(angle));
                mesh.getPoints().addAll(x, y, z);
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

        // Also zaro perem (felul nyitott marad)
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
        System.out.println("Created vase triangle mesh with " + mesh.getPoints().size() + " points and " + mesh.getFaces().size() + " faces");
        return mesh;
    }

    private static class VaseParameters {
        final DoubleProperty height = new SimpleDoubleProperty(260);
        final DoubleProperty wallThickness = new SimpleDoubleProperty(10);
        final DoubleProperty bellyAmount = new SimpleDoubleProperty(22);
        final DoubleProperty neckTaper = new SimpleDoubleProperty(4);
        final DoubleProperty baseRadius = new SimpleDoubleProperty(38);
        final IntegerProperty radialSegments = new SimpleIntegerProperty(72);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
