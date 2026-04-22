package com.cz.game2048super;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.Optional;

public class Choice {
    public static void ChooseModel(Stage stage,User user){
        VBox root = new VBox();
        root.getStyleClass().add("choice-root");
        Image image = new Image(ResourceLoader.resourceUrl("/pictures/wallhaven-kx53om.jpg").toExternalForm());
        root.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(100, 100, true, true, false, true))));
        Label labelTitle = new Label("请选择游戏模式:");
        Label labelSingle = new Label("传统模式");
        Label labelBlock = new Label("障碍模式");
        Label labelTime = new Label("限时模式\n(60s)");
        UIAnimations.addHoverScale(labelSingle);
        UIAnimations.addHoverScale(labelBlock);
        UIAnimations.addHoverScale(labelTime);
        labelTitle.getStyleClass().add("choice-label");
        labelSingle.getStyleClass().add("choice-label");
        labelBlock.getStyleClass().add("choice-label");
        labelTime.getStyleClass().add("choice-label");
        root.getChildren().addAll(labelTitle,labelSingle,labelBlock,labelTime);
        VBox.setMargin(labelTitle, new Insets(40));
        Scene scene = new Scene(root,460,820);
        scene.getStylesheets().add(ResourceLoader.stylesheet("/css/style.css"));
        stage.setScene(scene);
        stage.show();
        stage.centerOnScreen();
        UIAnimations.addFadeIn(root);
        labelSingle.setOnMouseClicked(_ -> {
            MainGame.LoadGame(stage,user,0);}
        );
        labelBlock.setOnMouseClicked(_ -> {
            MainGame.LoadGame(stage,user,1);
        });
        labelTime.setOnMouseClicked(_ -> {
            MainGame.LoadGame(stage,user,2);
        });
        stage.setOnCloseRequest(e -> {
            e.consume(); // 阻止默认关闭行为
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            UIUtils.styleDialog(alert);
            alert.setTitle("退出确认");
            alert.setHeaderText("确定要退出吗？");
            ButtonType logOutButton = new ButtonType("回到登录界面");
            ButtonType exitButton = new ButtonType("离开游戏");
            alert.getButtonTypes().setAll(logOutButton, exitButton);
            Optional<ButtonType> result = alert.showAndWait();
            // 根据用户的选择执行相应的操作
            if (result.isPresent() && result.get() == logOutButton)  {
                stage.close();
                LoginSystem.LoadLogin(stage);
            } else if (result.isPresent() && result.get() == exitButton) {
                stage.close();
            }
        });
    }
}
