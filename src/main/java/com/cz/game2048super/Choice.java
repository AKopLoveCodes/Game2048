package com.cz.game2048super;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.util.Optional;

public class Choice {
    public static void ChooseModel(Stage stage,User user){
        VBox root = new VBox(30);
        Image image = new Image("file:src/main/resources/pictures/wallhaven-kx53om.jpg");
        root.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(100, 100, true, true, false, true))));
        root.setAlignment(Pos.TOP_CENTER);
        Font font = new Font("华文中宋",40);
        Label labelTitle = new Label("请选择游戏模式:");
        Label labelSingle = new Label("传统模式");
        Label labelBlock = new Label("障碍模式");
        Label labelTime = new Label("限时模式\n(60s)");
        labelTitle.setFont(font);
        labelSingle.setFont(font);
        labelBlock.setFont(font);
        labelTime.setFont(font);
        root.getChildren().addAll(labelTitle,labelSingle,labelBlock,labelTime);
        VBox.setMargin(labelTitle, new Insets(40));
        Scene scene = new Scene(root,460,820);
        stage.setScene(scene);
        stage.show();
        labelSingle.setOnMouseClicked(_ -> {
            stage.close();
            MainGame.LoadGame(stage,user,0);}
        );
        labelBlock.setOnMouseClicked(_ -> {
            stage.close();
            MainGame.LoadGame(stage,user,1);
        });
        labelTime.setOnMouseClicked(_ -> {
            stage.close();
            MainGame.LoadGame(stage,user,2);
        });
        stage.setOnCloseRequest(e -> {
            e.consume(); // 阻止默认关闭行为
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
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
