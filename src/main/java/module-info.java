module com {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires java.desktop;
    requires java.sql;
    requires java.naming;
    requires mysql.connector.j;

    opens com.accountantapp to javafx.fxml;
    exports com.accountantapp;

    opens com.databaseclient to javafx.fxml;
    exports com.databaseclient;
}

