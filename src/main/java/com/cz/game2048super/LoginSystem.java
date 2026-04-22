package com.cz.game2048super;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LoginSystem {
    private static final String USER_REGISTRY_FILE_NAME = "UserRegistry.txt";
    private static Path userRegistryPath = AppPaths.applicationDataFile(USER_REGISTRY_FILE_NAME);

    public static void LoadLogin(Stage stage) {
        stage.setOnCloseRequest(_ -> {});

        StackPane stackPane = new StackPane();
        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().addAll("login-grid", "form-card");
        gridPane.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        Label titleLabel = new Label("2048 Super");
        titleLabel.getStyleClass().add("form-title");
        GridPane.setHalignment(titleLabel, javafx.geometry.HPos.CENTER);

        Label userNameLabel = new Label("用户名：");
        userNameLabel.getStyleClass().add("form-label");
        TextField userNameField = new TextField();
        userNameField.getStyleClass().add("form-text-field");
        Label passwordLabel = new Label("密码:");
        passwordLabel.getStyleClass().add("form-label");
        PasswordField passwordField = new PasswordField();
        passwordField.getStyleClass().add("form-text-field");
        StackPane loginPasswordBox = createPasswordBox(passwordField);
        Button loginButton = new Button("登录");
        Button registerButton = new Button("注册");
        Button visitorButton = new Button("游客登录");
        UIAnimations.addHoverScale(loginButton);
        UIAnimations.addHoverScale(registerButton);
        UIAnimations.addHoverScale(visitorButton);

        HBox buttonBox = new HBox();
        buttonBox.getStyleClass().add("login-button-box");
        buttonBox.getChildren().addAll(registerButton, visitorButton);

        gridPane.add(titleLabel, 1, 0, 2, 1);
        gridPane.add(userNameLabel, 1, 1);
        gridPane.add(userNameField, 2, 1);
        gridPane.add(passwordLabel, 1, 2);
        gridPane.add(loginPasswordBox, 2, 2);
        gridPane.add(loginButton, 1, 3);
        gridPane.add(buttonBox, 2, 3);

        stackPane.getChildren().add(gridPane);
        Image image = new Image(ResourceLoader.resourceUrl("/pictures/wallhaven-l8wvzl.jpg").toExternalForm());
        stackPane.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(100, 100, true, true, false, true))));

        Scene loginscene = new Scene(stackPane, 768, 432);
        loginscene.getStylesheets().add(ResourceLoader.stylesheet("/css/style.css"));
        stage.setTitle("登录界面");
        stage.setScene(loginscene);
        stage.show();
        stage.centerOnScreen();
        UIAnimations.addFadeIn(stackPane);

        loginButton.setOnAction(_ -> {
            try {
                if (CheckUser(userNameField.getText(), passwordField.getText())) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    UIUtils.styleDialog(alert);
                    alert.setTitle("");
                    alert.setHeaderText("登录成功");
                    alert.setContentText("欢迎进入游戏");
                    alert.showAndWait();
                    stage.close();
                    Choice.ChooseModel(stage, new User(userNameField.getText(), passwordField.getText()));
                } else {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    UIUtils.styleDialog(alert);
                    alert.setTitle("");
                    alert.setHeaderText("登录失败");
                    alert.setContentText("您的账号不存在或密码错误");
                    alert.showAndWait();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });

        registerButton.setOnAction(_ -> LoginSystem.LoadRegister(stage));

        visitorButton.setOnAction(_ -> {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            UIUtils.styleDialog(alert);
            alert.setTitle("");
            alert.setHeaderText("您将以游客身份进入游戏");
            alert.setContentText("您游玩过程中的数据将不会被保存");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                stage.close();
                Choice.ChooseModel(stage, createVisitorUser());
            }
        });
    }

    public static void LoadRegister(Stage stage) {
        StackPane stackPane = new StackPane();
        GridPane gridPane = new GridPane();
        gridPane.getStyleClass().addAll("register-grid", "form-card");
        gridPane.setMaxSize(Region.USE_PREF_SIZE, Region.USE_PREF_SIZE);

        Label titleLabel = new Label("注册新用户");
        titleLabel.getStyleClass().add("form-title");
        GridPane.setHalignment(titleLabel, javafx.geometry.HPos.CENTER);

        Label hintmessage = new Label("请输入用户名和密码");
        hintmessage.getStyleClass().add("form-label");
        Label userNameLabel = new Label("用户名：");
        userNameLabel.getStyleClass().add("form-label");
        TextField userNameField = new TextField();
        userNameField.getStyleClass().add("form-text-field");
        Label passwordLabel = new Label("密码:");
        passwordLabel.getStyleClass().add("form-label");
        PasswordField passwordField = new PasswordField();
        passwordField.getStyleClass().add("form-text-field");
        StackPane registerPasswordBox = createPasswordBox(passwordField);
        Button registerButton = new Button("确定注册");
        Button backButton = new Button("返回登录");
        UIAnimations.addHoverScale(registerButton);
        UIAnimations.addHoverScale(backButton);

        HBox buttonBox = new HBox();
        buttonBox.setSpacing(15);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);
        buttonBox.getChildren().addAll(registerButton, backButton);

        gridPane.add(titleLabel, 0, 0, 2, 1);
        gridPane.add(hintmessage, 0, 1, 2, 1);
        GridPane.setHalignment(hintmessage, javafx.geometry.HPos.CENTER);
        gridPane.add(userNameLabel, 0, 2);
        gridPane.add(userNameField, 1, 2);
        gridPane.add(passwordLabel, 0, 3);
        gridPane.add(registerPasswordBox, 1, 3);
        gridPane.add(buttonBox, 0, 4, 2, 1);

        stackPane.getChildren().add(gridPane);
        Image image = new Image(ResourceLoader.resourceUrl("/pictures/wallhaven-l8wvzl.jpg").toExternalForm());
        stackPane.setBackground(new Background(new BackgroundImage(image, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(100, 100, true, true, false, true))));
        Scene registerScene = new Scene(stackPane, 768, 432);
        registerScene.getStylesheets().add(ResourceLoader.stylesheet("/css/style.css"));
        stage.setTitle("注册界面");
        stage.setScene(registerScene);
        stage.show();
        stage.centerOnScreen();
        UIAnimations.addFadeIn(stackPane);

        backButton.setOnAction(_ -> LoginSystem.LoadLogin(stage));

        registerButton.setOnAction(_ -> {
            if (userNameField.getText().isEmpty() || passwordField.getText().isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                UIUtils.styleDialog(alert);
                alert.setTitle("");
                alert.setHeaderText("警告");
                alert.setContentText("请输入用户名和密码");
                alert.showAndWait();
                return;
            }

            try {
                if (CheckUser(userNameField.getText())) {
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    UIUtils.styleDialog(alert);
                    alert.setTitle("");
                    alert.setHeaderText("警告");
                    alert.setContentText("您所输入的用户名已存在");
                    alert.showAndWait();
                    return;
                }

                String username = userNameField.getText();
                String password = passwordField.getText();
                User user = new User(username, password);
                SaveUserToRegistry(user);

                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                UIUtils.styleDialog(alert);
                alert.setTitle("");
                alert.setHeaderText(username + "，您已注册成功");
                alert.setContentText("请点击确定登录游戏");
                alert.showAndWait();
                stage.close();
                LoginSystem.LoadLogin(stage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        stage.setOnCloseRequest(event -> {
            event.consume();
            LoginSystem.LoadLogin(stage);
        });
    }

    public static User createVisitorUser() {
        return User.visitor();
    }

    public static void setUserRegistryPath(Path path) {
        userRegistryPath = path;
    }

    public static void SaveUserToRegistry(User user) throws IOException {
        String storedPassword = encodePassword(user.getPassword());
        String line = user.getUsername() + "," + storedPassword;
        Path parent = userRegistryPath.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        try (BufferedWriter writer = Files.newBufferedWriter(
                userRegistryPath,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND)) {
            writer.write(line);
            writer.newLine();
        }
    }

    public static boolean CheckUser(String username) throws IOException {
        for (User user : loadUsers()) {
            if (user != null && user.getUsername().equals(username)) {
                return true;
            }
        }
        return false;
    }

    public static boolean CheckUser(String username, String password) throws IOException {
        for (User user : loadUsers()) {
            if (user == null) {
                continue;
            }
            if (user.getUsername().equals(username) && passwordMatches(user.getPassword(), password)) {
                return true;
            }
        }
        return false;
    }

    private static List<User> loadUsers() throws IOException {
        List<User> users = new ArrayList<>();
        if (!Files.exists(userRegistryPath)) {
            return users;
        }
        try (BufferedReader reader = Files.newBufferedReader(userRegistryPath, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                users.add(User.getUserRegistry(line));
            }
        }
        return users;
    }

    private static String encodePassword(String password) {
        return sha256(password);
    }

    private static boolean passwordMatches(String storedPassword, String inputPassword) {
        return storedPassword.equals(inputPassword) || storedPassword.equals(encodePassword(inputPassword));
    }

    private static String sha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder(hashed.length * 2);
            for (byte b : hashed) {
                builder.append(String.format("%02x", b));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 is unavailable", e);
        }
    }

    private static StackPane createPasswordBox(PasswordField passwordField) {
        TextField visiblePasswordField = new TextField();
        visiblePasswordField.getStyleClass().add("form-text-field");
        visiblePasswordField.setVisible(false);
        visiblePasswordField.setManaged(false);
        visiblePasswordField.textProperty().bindBidirectional(passwordField.textProperty());

        passwordField.setStyle("-fx-padding: 8 30 8 12;");
        visiblePasswordField.setStyle("-fx-padding: 8 30 8 12;");

        Button toggleButton = new Button("👁");
        String baseStyle = "-fx-background-color: transparent; -fx-cursor: hand; -fx-text-fill: #333; -fx-font-size: 14px; -fx-padding: 0 8 0 0;";
        toggleButton.setStyle(baseStyle + " -fx-strikethrough: true;");

        toggleButton.setOnAction(e -> {
            if (passwordField.isVisible()) {
                passwordField.setVisible(false);
                passwordField.setManaged(false);
                visiblePasswordField.setVisible(true);
                visiblePasswordField.setManaged(true);
                toggleButton.setStyle(baseStyle + " -fx-strikethrough: false;");
            } else {
                passwordField.setVisible(true);
                passwordField.setManaged(true);
                visiblePasswordField.setVisible(false);
                visiblePasswordField.setManaged(false);
                toggleButton.setStyle(baseStyle + " -fx-strikethrough: true;");
            }
        });

        StackPane fieldsPane = new StackPane(passwordField, visiblePasswordField, toggleButton);
        StackPane.setAlignment(toggleButton, javafx.geometry.Pos.CENTER_RIGHT);
        return fieldsPane;
    }
}
