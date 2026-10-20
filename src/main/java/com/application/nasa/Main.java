package com.application.nasa;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.awt.Taskbar;
import java.io.IOException;
import java.util.Objects;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        Image icon = new Image(Objects.requireNonNull(Main.class.getResource("images/nasa-logo.png")).toExternalForm());
        stage.getIcons().add(icon);

        // macOS uses a separate application icon in the Dock.
        if (Taskbar.isTaskbarSupported()) {
            Taskbar taskbar = Taskbar.getTaskbar();
            if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) {
                taskbar.setIconImage(SwingFXUtils.fromFXImage(icon, null));
            }
        }


        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1000, 800);
        fxmlLoader.<APIController>getController().setExplanationLinkOpener(getHostServices()::showDocument);
        scene.getStylesheets().add(Main.class.getResource("styles.css").toExternalForm());
        stage.setMinWidth(480);
        stage.setMinHeight(480);
        stage.setTitle("Nasa API Experiments");
        stage.setScene(scene);
        stage.show();
    }
}
