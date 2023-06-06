module com.example.finalprojectfantasyfootball {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.example.fffController to javafx.fxml;
    exports com.example.fffController;
}