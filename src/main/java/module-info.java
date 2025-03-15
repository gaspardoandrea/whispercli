module it.andreag.whispercli {
    requires javafx.controls;
    requires javafx.fxml;

    requires kotlin.stdlib;
    requires kotlinx.datetime;

    requires org.controlsfx.controls;
    requires org.kordamp.ikonli.javafx;
    requires java.prefs;
    requires java.desktop;
    requires kotlinx.serialization.core;
    requires kotlinx.serialization.json;
    requires org.apache.commons.codec;
    requires io.github.microutils.kotlinlogging;
    requires org.slf4j;
    requires org.slf4j.simple;

    opens it.andreag.whispercli to javafx.fxml;
    opens it.andreag.whispercli.components to javafx.fxml;
    opens it.andreag.whispercli.components.stack to javafx.fxml;
    opens it.andreag.whispercli.components.result to javafx.fxml;

    exports it.andreag.whispercli;
}