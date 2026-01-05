module com.example.bb_mario { // Executes: module com.example.bb_mario {
    requires javafx.controls; // Executes: requires javafx.controls;
    requires javafx.fxml; // Executes: requires javafx.fxml;

    requires com.almasb.fxgl.all; // Executes: requires com.almasb.fxgl.all;

    opens com.example.bb_mario to javafx.fxml; // Executes: opens com.example.bb_mario to javafx.fxml;
    exports game.core; // Executes: exports game.core;
    exports game.systems; // Executes: exports game.systems;
    exports game.utils; // Executes: exports game.utils;
} // Closes a code block.
