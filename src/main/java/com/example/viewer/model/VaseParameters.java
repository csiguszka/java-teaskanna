package com.example.viewer.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.paint.Color;

public class VaseParameters {
    // Basic vase parameters
    public final DoubleProperty height = new SimpleDoubleProperty(260);
    public final DoubleProperty wallThickness = new SimpleDoubleProperty(10);
    public final DoubleProperty bellyAmount = new SimpleDoubleProperty(22);
    public final DoubleProperty neckTaper = new SimpleDoubleProperty(4);
    public final DoubleProperty baseRadius = new SimpleDoubleProperty(38);
    public final IntegerProperty radialSegments = new SimpleIntegerProperty(72);

    // Spout parameters
    public final DoubleProperty spoutLength = new SimpleDoubleProperty(12);
    public final DoubleProperty spoutWidth = new SimpleDoubleProperty(70);
    public final DoubleProperty spoutLift = new SimpleDoubleProperty(7);

    // Lid parameters
    public final DoubleProperty lidHeight = new SimpleDoubleProperty(30);
    public final DoubleProperty knobHeight = new SimpleDoubleProperty(20);
    public final DoubleProperty knobRadius = new SimpleDoubleProperty(12);
    
    // Handle parameters
    public final DoubleProperty handleSize = new SimpleDoubleProperty(40);
    public final DoubleProperty handleThickness = new SimpleDoubleProperty(8);
    
    // Handle position parameter (constrained to vase height)
    public final DoubleProperty handlePos = new SimpleDoubleProperty(0) {
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
    public final javafx.beans.property.ObjectProperty<Color> bodyColor = new javafx.beans.property.SimpleObjectProperty<>(Color.rgb(210, 130, 80));
    public final javafx.beans.property.ObjectProperty<Color> handleColor = new javafx.beans.property.SimpleObjectProperty<>(Color.rgb(180, 100, 60));
    public final javafx.beans.property.ObjectProperty<Color> lidDomeColor = new javafx.beans.property.SimpleObjectProperty<>(Color.rgb(196, 116, 72));
    public final javafx.beans.property.ObjectProperty<Color> lidKnobColor = new javafx.beans.property.SimpleObjectProperty<>(Color.rgb(220, 140, 80));
    
    // Light toggle properties
    public final javafx.beans.property.BooleanProperty ambientLightEnabled = new javafx.beans.property.SimpleBooleanProperty(true);
    public final javafx.beans.property.BooleanProperty pointLightEnabled = new javafx.beans.property.SimpleBooleanProperty(true);
    
    // Visibility properties for each component
    public final javafx.beans.property.BooleanProperty bodyVisible = new javafx.beans.property.SimpleBooleanProperty(false);
    public final javafx.beans.property.BooleanProperty spoutVisible = new javafx.beans.property.SimpleBooleanProperty(true);
    public final javafx.beans.property.BooleanProperty handleVisible = new javafx.beans.property.SimpleBooleanProperty(true);
    public final javafx.beans.property.BooleanProperty lidVisible = new javafx.beans.property.SimpleBooleanProperty(true);
}
