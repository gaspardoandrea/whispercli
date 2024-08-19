module it.andreag.whispercli {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires kotlin.stdlib;
    requires kotlinx.datetime;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires io.github.oshai.kotlinlogging;
    requires java.prefs;
    requires java.desktop;
    requires kotlinx.serialization.core;
    requires kotlinx.serialization.json;
    requires org.apache.commons.codec;

    opens it.andreag.whispercli to javafx.fxml;
    opens it.andreag.whispercli.components to javafx.fxml;
    exports it.andreag.whispercli;
}