package com.cz.game2048super;

import javafx.application.Application;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage){
        //设置图标
        Image icon = new Image("file:src/main/resources/assets.icon/Icon.png");
        stage.getIcons().add(icon);
        //进入登录界面
        LoginSystem.LoadLogin(stage);
    }
}
