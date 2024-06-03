module com.cz.game2048super {
    requires javafx.controls;
    requires javafx.fxml;

    opens com.cz.game2048super to javafx.fxml;
    exports com.cz.game2048super;
}