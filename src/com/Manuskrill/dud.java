package com.Manuskrill;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class dud extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        Group root = new Group();
        Scene scene = new Scene(root, 50, 50);

        Stage stage = new Stage();

        stage.setScene(scene);
        stage.show();

    }
}
