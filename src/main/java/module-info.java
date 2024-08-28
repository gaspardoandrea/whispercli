module it.andreag.whispercli {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires kotlin.stdlib;
    requires kotlinx.datetime;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires java.prefs;
    requires java.desktop;
    requires kotlinx.serialization.core;
    requires kotlinx.serialization.json;
    requires org.apache.commons.codec;
    requires kotlin.logging.jvm;

    opens it.andreag.whispercli to javafx.fxml;
    opens it.andreag.whispercli.components to javafx.fxml;
    opens it.andreag.whispercli.components.stack to javafx.fxml;
    opens it.andreag.whispercli.components.result to javafx.fxml;
    exports it.andreag.whispercli;
}