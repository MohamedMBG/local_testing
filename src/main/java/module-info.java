module com.example.bb_mario {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.almasb.fxgl.all;

    opens com.example.bb_mario to javafx.fxml;
    exports com.example.bb_mario;
}