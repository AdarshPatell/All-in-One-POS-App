module org.example.newchronopos {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires java.sql;
    requires java.desktop;
    requires jbcrypt;

    opens org.example.newchronopos to javafx.fxml;
    opens org.example.newchronopos.controller to javafx.fxml;
    exports org.example.newchronopos;
    exports org.example.newchronopos.controller to javafx.fxml;
}