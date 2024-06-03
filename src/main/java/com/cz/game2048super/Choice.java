package com.cz.game2048super;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.stage.Stage;

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
        Label labelTime = new Label("限时模式\n(200s)");
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
    }
}
