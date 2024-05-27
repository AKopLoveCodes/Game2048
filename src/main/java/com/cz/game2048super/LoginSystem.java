package com.cz.game2048super;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LoginSystem {

    public static void LoadLogin(Stage stage){
        //重设窗口关闭逻辑，防止因为注册系统的设定而无法关闭窗口
        stage.setOnCloseRequest(_ -> {});
        // 创建StackPane
        StackPane stackPane = new StackPane();
        // 创建GridPane
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(15);
        gridPane.setVgap(20);
        // 创建组件
        Label userNameLabel = new Label("用户名:");
        TextField userNameField = new TextField();
        Label passwordLabel = new Label("密码:");
        PasswordField passwordField = new PasswordField();
        Button loginButton = new Button("登录");
        Button registerButton = new Button("注册");
        Button visitorButton = new Button("游客登录");
        HBox buttonBox = new HBox(15);
        buttonBox.getChildren().addAll(registerButton, visitorButton);
        // 将组件添加到GridPane中
        gridPane.add(userNameLabel, 1, 1);
        gridPane.add(userNameField, 2, 1);
        gridPane.add(passwordLabel, 1, 2);
        gridPane.add(passwordField, 2, 2);
        gridPane.add(loginButton, 1, 3);
        gridPane.add(buttonBox, 2, 3);
        // 将GridPane添加到StackPane中
        stackPane.getChildren().add(gridPane);
        // 创建Scene并设置到Stage
        Scene Loginscene = new Scene(stackPane, 400, 300);
        stage.setTitle("登录界面");
        stage.setScene(Loginscene);
        stage.show();
        // 处理登录按钮的点击事件
        loginButton.setOnAction(_ -> {
            //检测是否存在该账号
            try{
                if (CheckUser(userNameField.getText(), passwordField.getText())){
                    // 登录成功，跳转到游戏界面
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("");
                    alert.setHeaderText("登录成功");
                    alert.setContentText("欢迎进入游戏");
                    alert.showAndWait();
                    stage.close();
                    MainGame.LoadGame(stage,new User(userNameField.getText(),passwordField.getText()));
                } else {
                    // 登录失败，弹出提示框
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("");
                    alert.setHeaderText("登录失败");
                    alert.setContentText("您的账号不存在或密码错误");
                    alert.showAndWait();
                }
            } catch (IOException ex){
                ex.printStackTrace();
            }
        });
        // 处理注册按钮的点击事件
        registerButton.setOnAction(_ -> LoginSystem.LoadRegister(stage));
        // 处理游客登录按钮的点击事件
        visitorButton.setOnAction(_ -> {
            // 游客登录，跳转到游戏界面
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("");
            alert.setHeaderText("您将以游客身份进入游戏");
            alert.setContentText("您游玩过程中的数据将不会被保存");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // 用户点击了“确定”
                stage.close();
                MainGame.LoadGame(stage,new User());
            }
        });
    }

    public static void LoadRegister(Stage stage){
        // 创建StackPane
        StackPane stackPane = new StackPane();
        // 创建GridPane
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(10);
        gridPane.setVgap(10);
        // 创建组件
        Label hintmessage = new Label("请输入您的用户名和密码进行注册");
        Label userNameLabel = new Label("用户名:");
        TextField userNameField = new TextField();
        Label passwordLabel = new Label("密码:");
        PasswordField passwordField = new PasswordField();
        Button registerButton = new Button("确定注册");
        // 将组件添加到GridPane中
        gridPane.add(userNameLabel, 0, 1);
        gridPane.add(userNameField, 1, 1);
        gridPane.add(passwordLabel, 0, 2);
        gridPane.add(passwordField, 1, 2);
        gridPane.add(hintmessage, 0, 0);
        gridPane.add(registerButton, 0, 3);
        // 将GridPane添加到StackPane中
        stackPane.getChildren().add(gridPane);
        // 创建Scene并设置到Stage
        Scene registerScene = new Scene(stackPane, 400, 300);
        stage.setTitle("注册界面");
        stage.setScene(registerScene);
        stage.show();
        // 处理注册按钮的点击事件
        registerButton.setOnAction(_ -> {
            if (userNameField.getText().isEmpty() || passwordField.getText().isEmpty()){
                // 提示用户输入用户名和密码
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("");
                alert.setHeaderText("警告");
                alert.setContentText("请输入用户名和密码");
                alert.showAndWait();
            } else {
                // 检查用户名是否已存在
                try {
                    if (CheckUser(userNameField.getText(),passwordField.getText())){
                        //用户名已存在
                        Alert alert = new Alert(Alert.AlertType.WARNING);
                        alert.setTitle("");
                        alert.setHeaderText("警告");
                        alert.setContentText("您所输入的用户名已存在");
                        alert.showAndWait();
                    } else {
                        //用户名不存在 可以注册
                        // 注册成功，保存用户名和密码到本地文件或数据库中
                        String username = userNameField.getText();
                        String password = passwordField.getText();
                        User user = new User(username, password);
                        try {
                            SaveUserToRegistry(user);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("");
                        alert.setHeaderText(username+"，您已注册成功");
                        alert.setContentText("请点击确定登录游戏");
                        alert.showAndWait();
                        stage.close();
                        // 打开登录界面
                        LoginSystem.LoadLogin(stage);
                    }
                } catch(IOException e){
                    e.printStackTrace();
                }
            }
        });
        stage.setOnCloseRequest(event -> {
            event.consume(); // 阻止默认关闭行为
            LoginSystem.LoadLogin(stage);
        });
    }

    public static void SaveUserToRegistry(User user) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/assets/data/UserRegistry.txt", true))) {
            writer.write(user.toString());
            writer.newLine();
        }
    }

    public static boolean CheckUser(String username,String password) throws IOException {
        boolean ifExist=false;
        List<User> users = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/assets/data/UserRegistry.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                users.add(User.getUserRegistry(line));
            }
        }
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                ifExist = true;
                break;
            }
        }
        return ifExist;
    }
}

