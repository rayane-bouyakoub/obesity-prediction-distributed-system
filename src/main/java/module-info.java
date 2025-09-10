module com.example.obesitypredictorrmi {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.rmi;
    requires java.sql;
    requires weka;

    opens GUI to javafx.fxml;
    exports Client;
    opens Client to javafx.fxml;

    exports Server.Interfaces to java.rmi;
    exports Server.Implementations to java.rmi;
    exports Enums;
    exports Server.Entities;

}