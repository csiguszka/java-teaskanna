package com.example.viewer;

import com.example.viewer.controller.MainController;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {
    
    private MainController controller;

    @Override
    public void start(Stage stage) {
        controller = new MainController();
        controller.start(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
