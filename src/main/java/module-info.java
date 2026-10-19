module com.application.nasa {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.swing;

    requires org.controlsfx.controls;
    requires java.net.http;
    requires com.fasterxml.jackson.annotation;
    requires tools.jackson.databind;
    requires java.desktop;
    requires javafx.media;

    opens com.application.nasa to javafx.fxml;
    exports com.application.nasa;
}
