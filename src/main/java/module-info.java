module com.example.project_caroz {
    requires javafx.controls;
    requires javafx.fxml;

    requires javafx.graphics;
    requires java.sql;
    requires mysql.connector.j;
    requires com.google.gson;
    requires jbcrypt;
    requires ports.javaxmail;
    requires org.controlsfx.controls;


    opens com.example.project_caroz to javafx.fxml;
    exports com.example.project_caroz;
    exports com.example.project_caroz.stage;
    opens com.example.project_caroz.stage to javafx.fxml;

    opens com.example.project_caroz.emoji to com.google.gson;
}