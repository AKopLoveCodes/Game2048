package com.cz.game2048super;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.media.Media;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.io.IOException;
import java.util.Optional;

public class MainGame{
    private static GridPane grids;//方块网格
    private static int counter;//游戏开始的秒数
    private static Timeline timeline;//游戏计时器
    private static int Score;//游戏得分
    private static GameController model;//游戏控制器
    private static boolean ifStart;//游戏是否开始
    private static Label ScoreLabel;//得分标签
    private static Label Timer;//计时器标签
    private static BorderPane borderPane;//游戏主界面
    private static User user;//游戏账号
    private static GameData gameData;//游戏数据库
    private static boolean ifSave;//游戏是否保存至本地
    private static boolean ifVisitor;//是否为游客
    private static boolean ifOver;//游戏是否结束
    private static Stage stage;//游戏舞台
    private static Scene gameScene;//游戏场景
    private static int choice;//游戏模式
    private static MediaPlayer bgm;//游戏背景音乐
    private static boolean ifSoundOpen;//游戏是否开启音乐

    public static void LoadGame(Stage theStage,User theUser,int theChoice){
        choice=theChoice;
        stage=theStage;
        user=theUser;
        ifVisitor = user.getUsername().isEmpty();
        initVars();
        initGameData();
        initSettings();
        initGame();
    }

    public static void initVars(){
        //initial Variables 初始化变量和游戏数据
        ifOver = false;
        counter = 0;
        Score = 0;
        ifStart = false;
        ifSave = false;
        ifSoundOpen = true;
    }

    public static void initSettings(){
        //重设窗口关闭逻辑 防止因Choice界面的设定而干扰游戏界面退出
        stage.setOnCloseRequest(_ -> {});
        //initial settings 主要用于设置游戏UI和主界面
        model=new GameController();
        Timer = new Label("用时：0");
        Timer.setTextFill(Color.WHITE);
        ScoreLabel = new Label("得分：0");
        ScoreLabel.setTextFill(Color.WHITE);
        Font LabelFont = new Font("华文中宋", 35);
        Timer.setFont(LabelFont);
        ScoreLabel.setFont(LabelFont);
        timeline = new Timeline(new KeyFrame(Duration.seconds(1), _ -> {
            if (!ifOver && ifStart){
                counter++;
                if (choice==2 && counter==60){
                    System.out.println("时间到了");
                    ifOver=true;
                    Platform.runLater(MainGame::gameOver);
                }
            }
            gameData.updateTimer(counter);
            Timer.setText("用时：" + counter);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);
        grids = new GridPane(10,10);
        grids.setPrefSize(600, 600);
        grids.setAlignment(Pos.CENTER);
        borderPane=new BorderPane();
        borderPane.setCenter(grids);
        VBox vBox=new VBox(15);
        borderPane.setLeft(vBox);
        vBox.getChildren().addAll(Timer,ScoreLabel);
        vBox.setAlignment(Pos.TOP_CENTER);
        VBox.setMargin(Timer, new Insets(40));
        vBox.setPrefWidth(300);
        Button startButton = new Button("开始游戏");
        startButton.setMinWidth(100);
        startButton.setMinHeight(50);
        startButton.setAlignment(Pos.CENTER);
        Button restartButton = new Button("重新开始");
        restartButton.setMinWidth(100);
        restartButton.setMinHeight(50);
        restartButton.setAlignment(Pos.CENTER);
        StackPane ButtonPane = new StackPane();
        ButtonPane.getChildren().add(startButton);
        // 设置StackPane的对齐方式
        StackPane.setAlignment(startButton, Pos.CENTER);
        // 使用边距来进一步微调位置
        StackPane.setMargin(startButton, new Insets(0, 0, 50, 0)); // 向上偏移
        borderPane.setBottom(ButtonPane);
        ImageView exitOption = new ImageView(new Image("file:src/main/resources/pictures/exit.png"));
        exitOption.setFitWidth(60);
        exitOption.setFitHeight(60);
        ImageView soundOption = new ImageView(new Image("file:src/main/resources/pictures/SoundOpen.png"));
        soundOption.setFitWidth(60);
        soundOption.setFitHeight(60);
        ImageView informationOption = new ImageView(new Image("file:src/main/resources/pictures/Information.png"));
        informationOption.setFitWidth(60);
        informationOption.setFitHeight(60);
        VBox options = new VBox(15);
        options.getChildren().addAll(exitOption,soundOption,informationOption);
        borderPane.setRight(options);
        gameScene = new Scene(borderPane, 1050, 600);
        if (choice==0){
            Image backgroundImage = new Image("file:src/main/resources/pictures/wallhaven-2yxp16.jpg");
            borderPane.setBackground(new Background(new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(100, 100, true, true, false, true))));
        } else if (choice==1){
            Image backgroundImage = new Image("file:src/main/resources/pictures/wallhaven-wekp5x.jpg");
            borderPane.setBackground(new Background(new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(100, 100, true, true, false, true))));
        } else if (choice==2){
            Image backgroundImage = new Image("file:src/main/resources/pictures/wallhaven-d6z98o.jpg");
            borderPane.setBackground(new Background(new BackgroundImage(backgroundImage, BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.CENTER, new BackgroundSize(100, 100, true, true, false, true))));
        }
        startButton.setOnAction(_ -> {
            timeline.play();
            ifStart = true;
            ButtonPane.getChildren().remove(startButton);
            ButtonPane.getChildren().add(restartButton);
            StackPane.setAlignment(restartButton, Pos.CENTER);
            StackPane.setMargin(restartButton, new Insets(0, 0, 50, 0)); // 向上偏移
        });
        restartButton.setOnAction(_ -> restartGame());
        if (!ifVisitor && choice==0){
            Button saveButton = new Button("保存");
            saveButton.setMinWidth(80);
            saveButton.setMinHeight(40);
            saveButton.setAlignment(Pos.CENTER);
            StackPane saveButtonPane = new StackPane();
            saveButtonPane.getChildren().add(saveButton);
            StackPane.setAlignment(saveButtonPane,Pos.CENTER);
            StackPane.setMargin(saveButtonPane, new Insets(0, 0, -50, 0));
            borderPane.setTop(saveButtonPane);
            saveButton.setOnMouseClicked(_ -> {
                try {
                    gameData.saveGameData();
                    ifSave = true;
                    ifStart = false;
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("保存成功");
                    alert.setHeaderText("游戏存档已成功保存");
                    alert.setContentText("");
                    Optional <ButtonType> result = alert.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.OK) {
                        ifStart = true;
                        playGame();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            if (!ifSave){
                stage.setOnCloseRequest(event -> {
                    event.consume(); // 阻止默认关闭行为
                    try {
                        if (!ifVisitor && choice==0 && !ifSave){
                            giveSaveWarning();
                        } else {
                            bgm.stop();
                            stage.close();
                        }
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
        exitOption.setOnMouseClicked(_ -> {
            if (!ifVisitor && choice==0 && !ifSave){
                try {
                    giveSaveWarning();
                    Choice.ChooseModel(stage,user);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                bgm.stop();
                stage.close();
                Choice.ChooseModel(stage,user);
            }
        });
        soundOption.setOnMouseClicked(_ -> {
            if (ifSoundOpen){
                soundOption.setImage(new Image("file:src/main/resources/pictures/SoundClose.png"));
                bgm.setVolume(0);
                ifSoundOpen = false;
            } else {
                soundOption.setImage(new Image("file:src/main/resources/pictures/SoundOpen.png"));
                bgm.setVolume(1);
                ifSoundOpen = true;
            }
        });
        informationOption.setOnMouseClicked(_ -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("游戏信息");
            switch (choice){
                case 0:
                    alert.setHeaderText("传统模式");
                    alert.setContentText("在无限长时间内，玩家通过合并数字获取得分，直至得到2048或无法操作为止");
                    break;
                case 1:
                    alert.setHeaderText("障碍模式");
                    alert.setContentText("玩家会遇到一个无法合并的方块，你必须在剩余区域完成得分");
                    break;
                case 2:
                    alert.setHeaderText("限时模式");
                    alert.setContentText("玩家在规定时间内完成得分(默认为60s)");
                    break;
            }
            alert.showAndWait();
        });
        Media media;
        if (choice == 0) {
            media = new Media(new File("src/main/resources/music/SkinnyLove.mp3").toURI().toString());
        } else {
            media = new Media(new File("src/main/resources/music/TheTrunk.mp3").toURI().toString());
        }
        bgm = new MediaPlayer(media);
        bgm.setCycleCount(MediaPlayer.INDEFINITE); // 循环播放
        bgm.play(); // 播放音乐
        stage.setTitle("2048");
        stage.setScene(gameScene);
        stage.show();
    }

    public static void initGameData(){
        gameData = new GameData(user.getUsername());
        //确认该用户是已经注册且有存档 还是新注册未有存档
        try{
            if (!ifVisitor && gameData.ifFoundUserData() && choice==0){
                //非游客登录 且有存档 可以选择以前存档或开始新游戏
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("游戏选项");
                alert.setHeaderText(user.getUsername()+",检测到您已有存档，请选择要执行的操作：");
                alert.setContentText("注意：开始新游戏会覆盖原有存档，但不会影响您的记录");
                // 定义“开始新游戏”按钮
                ButtonType startNewGameButton = new ButtonType("开始新游戏");
                // 定义“载入存档”按钮
                ButtonType loadGameButton = new ButtonType("载入存档");
                // 将按钮添加到对话框中
                alert.getButtonTypes().setAll(startNewGameButton, loadGameButton);
                // 显示对话框并等待用户响应
                Optional<ButtonType> result = alert.showAndWait();
                // 根据用户的选择执行相应的操作
                if (result.isPresent() && result.get() == startNewGameButton) {
                    // 用户选择开始新游戏，执行相关操作
                    gameData.initGameData(choice);
                    gameData.loadGameRecord();
                } else if (result.isPresent() && result.get() == loadGameButton) {
                    // 用户选择载入存档，执行相关操作
                    gameData.loadGameData();
                }
            } else {
                //无存档或是游客 直接开始新游戏
                gameData.initGameData(choice);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void initGame(){
        //载入游戏数据
        model.setGridnums(gameData.getGridsLast());
        Score = gameData.getScoreLast();
        counter = gameData.getTimerLast();
        updateGridsByNums(model.getGridnums());
        ScoreLabel.setText("得分："+Score);
        Timer.setText("用时："+counter);
        playGame();
        borderPane.requestFocus();
    }

    public static void playGame(){
        //Game starts
        //键盘事件逻辑
        // 创建并注册全局键盘事件过滤器
        EventHandler<KeyEvent> keyEventHandler = event -> {
            // 处理键盘事件
            if(!ifStart) return;
            switch(event.getCode()) {
                case UP, W:
                    doMoveUp();
                    break;
                case DOWN, S:
                    doMoveDown();
                    break;
                case LEFT, A:
                    doMoveLeft();
                    break;
                case RIGHT, D:
                    doMoveRight();
                    break;
            }
            afterMove();
        };
        // 将事件过滤器添加到场景中
        gameScene.addEventFilter(KeyEvent.KEY_PRESSED, keyEventHandler);
    }

    public static void restartGame(){
        //confirm the option
        if (!ifVisitor && choice==0){
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("");
            alert.setHeaderText("您确定要重新开始吗？");
            alert.setContentText("您游玩过程中的数据可能尚未保存");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK){
                //restart the game
                initVars();
                ifStart = true;
                try {
                    gameData.initGameData(choice);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                initGame();
            }
        } else{
            initVars();
            ifStart = true;
            try {
                gameData.initGameData(choice);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            initGame();
        }
    }

    public static void giveSaveWarning() throws IOException {
        //confirm the option
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("");
        alert.setHeaderText("您确定要退出吗");
        alert.setContentText("您游玩过程中的数据将会被自动保存");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK){
            //save the game
            gameData.saveGameData();
            bgm.stop();
            stage.close();
        }
    }

    public static void changeGrid(int x,int y,int index){
        grids.getChildren().removeIf(node ->
                GridPane.getColumnIndex(node) == x && GridPane.getRowIndex(node) == y
        );
        grids.add(new Diamond(index).getRoot(),x,y);
    }

    /*上下左右的执行逻辑：
     * 1.合并方块（先判断是否有动作）
     * 2.计算得分
     * 3.生成新的方块
     */
    public static void doMoveUp(){
        //完成gridsnum的更新 和 计算得分
        Score+=model.AStep(1);
    }

    public static void doMoveDown(){
        Score+=model.AStep(2);
    }

    public static void doMoveLeft(){
        Score+=model.AStep(3);
    }

    public static void doMoveRight(){
        Score+=model.AStep(4);
    }

    public static void updateGridsByNums(int[][] nums){
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                changeGrid(j,i,nums[i][j]);
            }
        }
    }

    public static void gameWin(){
        ifOver = true;
        //先将最高分数保存 在随后的操作里清零
        if (!ifVisitor && choice==0){
            try {
                gameData.saveGameData();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        //记录此账号的通关记录
        gameData.setIfHaveWon(true);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("游戏通关");
        String text = "恭喜你成功通关了此模式:\n您的本次得分为："+Score+"\n您的本次用时为："+counter+"秒\n您的历史最高得分为："+gameData.getScoreBest()+"\n";
        alert.setHeaderText(text);
        alert.setContentText("继续你的旅程吗？");
        ButtonType startNewGameButton = new ButtonType("再来一次");
        ButtonType outGameButton = new ButtonType("就此退出");
        alert.getButtonTypes().setAll(startNewGameButton, outGameButton);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == startNewGameButton) {
            // 用户选择再来一次，执行相关操作
            initVars();
            ifStart = true;
            try {
                gameData.initGameData(choice);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            initGame();
        } else if (result.isPresent() && result.get() == outGameButton)  {
            //将状态信息归零
            if (!ifVisitor && choice==0){
                try {
                    gameData.initGameData(choice);
                    gameData.saveGameData();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            bgm.stop();
            stage.close();
        }
    }

    public static void gameOver(){
        ifOver=true;//停止时间
        //先将最高分数保存 在随后的操作里清零
        if (!ifVisitor && choice==0){
            try {
                gameData.saveGameData();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("游戏结算");
        String text = "本次游戏结束了：\n您的本次得分为："+Score+"\n您的本次用时为："+counter+"秒\n您的历史最高得分为："+gameData.getScoreBest()+"\n";
        if (gameData.getIfHaveWon()){
            text+="您已经成功通关过此模式";
        } else {
            text+="您尚未成功通关过此模式";
        }
        alert.setHeaderText(text);
        alert.setContentText("继续你的旅程吗？");
        ButtonType startNewGameButton = new ButtonType("再来一次");
        ButtonType outGameButton = new ButtonType("就此退出");
        alert.getButtonTypes().setAll(startNewGameButton, outGameButton);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == startNewGameButton) {
            // 用户选择再来一次，执行相关操作
            initVars();
            ifStart = true;
            try {
                gameData.initGameData(choice);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            initGame();
        } else if (result.isPresent() && result.get() == outGameButton)  {
            //将状态信息归零
            if (!ifVisitor && choice==0){
                try {
                    gameData.initGameData(choice);
                    gameData.saveGameData();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            bgm.stop();
            stage.close();
        }
    }

    public static void afterMove(){
        ScoreLabel.setText("得分: "+Score);
        gameData.updateGameData(Score,counter,model.getGridnums());
        //Judge if the player wins
        if (model.isWin()){
            updateGridsByNums(model.getGridnums());
            gameWin();
        } else if (model.isGameOver()) {
            updateGridsByNums(model.getGridnums());
            gameOver();
        } else {
            model.createNewGrid();
            updateGridsByNums(model.getGridnums());
            gameData.updateGameData(Score,counter,model.getGridnums());
            ifSave = false;
        }
    }
}
