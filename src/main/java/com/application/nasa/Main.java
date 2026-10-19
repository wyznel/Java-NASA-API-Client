package com.application.nasa;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 800);
        scene.getStylesheets().add(Main.class.getResource("styles.css").toExternalForm());
        stage.setMinWidth(480);
        stage.setMinHeight(480);
        stage.setTitle("Nasa API Experiments");
        stage.setScene(scene);
        stage.show();
    }
}
