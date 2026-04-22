package com.cz.game2048super;

import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Button;
import javafx.scene.Node;

import javafx.scene.paint.Color;
import javafx.stage.StageStyle;

public class UIUtils {
    public static void styleDialog(Alert alert) {
        alert.initStyle(StageStyle.TRANSPARENT);
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getScene().setFill(Color.TRANSPARENT);
        
        dialogPane.getStylesheets().add(ResourceLoader.stylesheet("/css/style.css"));
        dialogPane.getStyleClass().add("custom-alert");
        
        // Add styling class to buttons in the dialog
        for (ButtonType buttonType : dialogPane.getButtonTypes()) {
            Node button = dialogPane.lookupButton(buttonType);
            if (button instanceof Button) {
                button.getStyleClass().add("alert-button");
            }
        }
    }
}
