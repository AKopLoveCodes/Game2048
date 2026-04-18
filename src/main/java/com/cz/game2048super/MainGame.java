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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;
import java.util.Optional;

public class MainGame {
    private final Stage stage;
    private final User user;
    private final boolean visitor;
    private final int choice;

    private BoardView boardView;
    private BoardAnimator boardAnimator;
    private int counter;
    private Timeline timeline;
    private int score;
    private GameController model;
    private boolean started;
    private Label scoreLabel;
    private Label timerLabel;
    private BorderPane borderPane;
    private GameData gameData;
    private boolean saved;
    private boolean over;
    private Scene gameScene;
    private MediaPlayer bgm;
    private boolean soundOpen;
    private StackPane buttonPane;
    private Button startButton;
    private Button restartButton;
    private boolean boardBusy;

    private final EventHandler<KeyEvent> keyEventHandler = event -> {
        if (!started || over || boardBusy) {
            return;
        }

        KeyCode code = event.getCode();
        MoveResult moveResult = handleMove(code);
        if (moveResult == null) {
            return;
        }

        boardBusy = true;
        if (!moveResult.moveChanged()) {
            boardAnimator.play(moveResult, () -> {
                boardBusy = false;
                if (shouldTriggerGameOverAfterInput(code, false, model)) {
                    gameOver();
                } else {
                    requestGameFocus();
                }
            });
            return;
        }

        score += moveResult.scoreDelta();
        boardAnimator.play(moveResult, () -> finishMove(moveResult));
    };

    private MainGame(Stage stage, User user, int choice) {
        this.stage = stage;
        this.user = user;
        this.choice = choice;
        this.visitor = user.isVisitor();
    }

    public static void LoadGame(Stage stage, User user, int choice) {
        new MainGame(stage, user, choice).load();
    }

    private void load() {
        initVars();
        initGameData();
        initSettings();
        initGame();
    }

    private void initVars() {
        over = false;
        counter = 0;
        score = 0;
        started = false;
        saved = false;
        soundOpen = true;
        boardBusy = false;
    }

    private void initSettings() {
        stage.setOnCloseRequest(_ -> {});

        model = new GameController();
        timerLabel = new Label("用时: 0");
        timerLabel.setTextFill(Color.WHITE);
        scoreLabel = new Label("得分: 0");
        scoreLabel.setTextFill(Color.WHITE);
        Font labelFont = new Font("华文中宋", 35);
        timerLabel.setFont(labelFont);
        scoreLabel.setFont(labelFont);

        timeline = new Timeline(new KeyFrame(Duration.seconds(1), _ -> {
            if (!over && started) {
                counter++;
                if (choice == 2 && counter == 60) {
                    over = true;
                    timeline.stop();
                    Platform.runLater(this::gameOver);
                }
                if (model.isGameOver()) {
                    over = true;
                    timeline.stop();
                    Platform.runLater(this::gameOver);
                }
            }
            gameData.updateTimer(counter);
            timerLabel.setText("用时: " + counter);
        }));
        timeline.setCycleCount(Timeline.INDEFINITE);

        boardView = new BoardView();
        boardAnimator = new BoardAnimator(boardView);

        borderPane = new BorderPane();
        borderPane.setCenter(boardView);
        borderPane.setLeft(buildInfoPane());
        borderPane.setBottom(buildBottomButtons());
        borderPane.setRight(buildOptionsPane());
        if (!visitor) {
            borderPane.setTop(buildSavePane());
        }

        applyBackground();

        gameScene = new Scene(borderPane, 1050, 600);
        gameScene.addEventFilter(KeyEvent.KEY_PRESSED, keyEventHandler);

        bgm = new MediaPlayer(selectBackgroundMusic());
        bgm.setCycleCount(MediaPlayer.INDEFINITE);
        bgm.play();

        stage.setTitle("2048");
        stage.setScene(gameScene);
        stage.show();
        requestGameFocus();
    }

    private VBox buildInfoPane() {
        VBox infoPane = new VBox(15);
        infoPane.getChildren().addAll(timerLabel, scoreLabel);
        infoPane.setAlignment(Pos.TOP_CENTER);
        VBox.setMargin(timerLabel, new Insets(40));
        infoPane.setPrefWidth(300);
        return infoPane;
    }

    private StackPane buildBottomButtons() {
        startButton = new Button("开始游戏");
        startButton.setMinWidth(100);
        startButton.setMinHeight(50);
        restartButton = new Button("重新开始");
        restartButton.setMinWidth(100);
        restartButton.setMinHeight(50);

        buttonPane = new StackPane(startButton);
        StackPane.setAlignment(startButton, Pos.CENTER);
        StackPane.setMargin(startButton, new Insets(0, 0, 50, 0));

        startButton.setOnAction(_ -> {
            timeline.play();
            started = true;
            buttonPane.getChildren().setAll(restartButton);
            StackPane.setAlignment(restartButton, Pos.CENTER);
            StackPane.setMargin(restartButton, new Insets(0, 0, 50, 0));
            if (shouldPromptEndDialogWhenBoardReady(started, over, model)) {
                if (model.isWin()) {
                    gameWin();
                } else {
                    gameOver();
                }
            } else {
                requestGameFocus();
            }
        });
        restartButton.setOnAction(_ -> restartGame());
        return buttonPane;
    }

    private StackPane buildSavePane() {
        Button saveButton = new Button("保存");
        saveButton.setMinWidth(80);
        saveButton.setMinHeight(40);

        StackPane saveButtonPane = new StackPane(saveButton);
        StackPane.setAlignment(saveButtonPane, Pos.CENTER);
        StackPane.setMargin(saveButtonPane, new Insets(0, 0, -50, 0));

        saveButton.setOnAction(_ -> {
            try {
                stopBoardAnimation();
                gameData.saveGameData();
                saved = true;
                started = false;
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("保存成功");
                alert.setHeaderText("游戏存档已成功保存");
                alert.setContentText("");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    started = true;
                    requestGameFocus();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        stage.setOnCloseRequest(event -> {
            event.consume();
            try {
                if (!visitor && !saved) {
                    giveSaveWarning();
                } else {
                    shutdown();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        return saveButtonPane;
    }

    private VBox buildOptionsPane() {
        ImageView exitOption = createIcon("/pictures/exit.png");
        ImageView soundOption = createIcon("/pictures/SoundOpen.png");
        ImageView informationOption = createIcon("/pictures/Information.png");

        exitOption.setOnMouseClicked(_ -> exitToChoice());
        soundOption.setOnMouseClicked(_ -> toggleSound(soundOption));
        informationOption.setOnMouseClicked(_ -> showModeInformation());

        VBox options = new VBox(15);
        options.getChildren().addAll(exitOption, soundOption, informationOption);
        return options;
    }

    private ImageView createIcon(String resourcePath) {
        ImageView imageView = new ImageView(new Image(ResourceLoader.resourceUrl(resourcePath).toExternalForm()));
        imageView.setFitWidth(60);
        imageView.setFitHeight(60);
        return imageView;
    }

    private void applyBackground() {
        String resourcePath = switch (choice) {
            case 1 -> "/pictures/wallhaven-wekp5x.jpg";
            case 2 -> "/pictures/wallhaven-d6z98o.jpg";
            default -> "/pictures/wallhaven-2yxp16.jpg";
        };
        Image backgroundImage = new Image(ResourceLoader.resourceUrl(resourcePath).toExternalForm());
        borderPane.setBackground(new Background(new BackgroundImage(
                backgroundImage,
                BackgroundRepeat.NO_REPEAT,
                BackgroundRepeat.NO_REPEAT,
                BackgroundPosition.CENTER,
                new BackgroundSize(100, 100, true, true, false, true))));
    }

    private Media selectBackgroundMusic() {
        String resourcePath = choice == 0 ? "/music/SkinnyLove.mp3" : "/music/TheTrunk.mp3";
        return new Media(ResourceLoader.resourceUrl(resourcePath).toExternalForm());
    }

    private void initGameData() {
        gameData = new GameData(user.getUsername(), choice);
        try {
            if (!visitor && gameData.ifFoundUserData()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("游戏选项");
                alert.setHeaderText(user.getUsername() + "，检测到您已有存档，请选择要执行的操作：");
                alert.setContentText("注意：开始新游戏会覆盖原有存档，但不会影响您的记录");
                ButtonType startNewGameButton = new ButtonType("开始新游戏");
                ButtonType loadGameButton = new ButtonType("载入存档");
                alert.getButtonTypes().setAll(startNewGameButton, loadGameButton);
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == startNewGameButton) {
                    gameData.initGameData();
                    gameData.loadGameRecord();
                } else if (result.isPresent() && result.get() == loadGameButton) {
                    gameData.loadGameData();
                }
            } else {
                gameData.initGameData();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void initGame() {
        model.setGridnums(gameData.getGridsLast());
        score = gameData.getScoreLast();
        counter = gameData.getTimerLast();
        boardBusy = false;
        boardView.syncToState(model.getBoardState());
        scoreLabel.setText("得分: " + score);
        timerLabel.setText("用时: " + counter);
        if (shouldPromptEndDialogWhenBoardReady(started, over, model)) {
            if (model.isWin()) {
                gameWin();
            } else {
                gameOver();
            }
        } else {
            requestGameFocus();
        }
    }

    private MoveResult handleMove(KeyCode code) {
        MoveDirection direction = switch (code) {
            case UP, W -> MoveDirection.UP;
            case DOWN, S -> MoveDirection.DOWN;
            case LEFT, A -> MoveDirection.LEFT;
            case RIGHT, D -> MoveDirection.RIGHT;
            default -> null;
        };
        if (direction == null) {
            return null;
        }
        return model.move(direction, true);
    }

    private static boolean shouldTriggerGameOverAfterInput(KeyCode code, boolean moveChanged, GameController controller) {
        return isMoveKey(code) && !moveChanged && controller.isGameOver();
    }

    private static boolean shouldPromptEndDialogWhenBoardReady(boolean started, boolean over, GameController controller) {
        return started && !over && (controller.isWin() || controller.isGameOver());
    }

    private static boolean isMoveKey(KeyCode code) {
        return switch (code) {
            case UP, W, DOWN, S, LEFT, A, RIGHT, D -> true;
            default -> false;
        };
    }

    private void restartGame() {
        stopBoardAnimation();
        if (!visitor && choice == 0) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("");
            alert.setHeaderText("您确定要重新开始吗？");
            alert.setContentText("您游玩过程中的数据可能尚未保存");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isEmpty() || result.get() != ButtonType.OK) {
                requestGameFocus();
                return;
            }
        }

        initVars();
        started = true;
        timeline.playFromStart();
        gameData.initGameData();
        initGame();
        buttonPane.getChildren().setAll(restartButton);
        StackPane.setAlignment(restartButton, Pos.CENTER);
        StackPane.setMargin(restartButton, new Insets(0, 0, 50, 0));
    }

    private void giveSaveWarning() throws IOException {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("");
        alert.setHeaderText("您确定要退出吗");
        alert.setContentText("您游玩过程中的数据将会被自动保存");
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            gameData.saveGameData();
            shutdown();
        } else {
            requestGameFocus();
        }
    }

    private void gameWin() {
        over = true;
        timeline.stop();
        if (!visitor && choice == 0) {
            try {
                gameData.saveGameData();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        gameData.setIfHaveWon(true);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("游戏通关");
        String text = "恭喜你成功通关了此模式:\n您的本次得分为：" + score
                + "\n您的本次用时为：" + counter + "秒\n您的历史最高得分为：" + gameData.getScoreBest() + "\n";
        alert.setHeaderText(text);
        alert.setContentText("继续你的旅程吗？");
        ButtonType startNewGameButton = new ButtonType("再来一次");
        ButtonType outGameButton = new ButtonType("就此退出");
        alert.getButtonTypes().setAll(startNewGameButton, outGameButton);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == startNewGameButton) {
            restartAfterEnd();
        } else if (result.isPresent() && result.get() == outGameButton) {
            if (!visitor && choice == 0) {
                try {
                    gameData.initGameData();
                    gameData.saveGameData();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            shutdown();
        }
    }

    private void gameOver() {
        over = true;
        timeline.stop();
        if (!visitor && choice == 0) {
            try {
                gameData.saveGameData();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("游戏结算");
        String text = "本次游戏结束了：\n您的本次得分为：" + score
                + "\n您的本次用时为：" + counter + "秒\n您的历史最高得分为：" + gameData.getScoreBest() + "\n";
        text += gameData.getIfHaveWon() ? "您已经成功通关过此模式" : "您尚未成功通关过此模式";
        alert.setHeaderText(text);
        alert.setContentText("继续你的旅程吗？");
        ButtonType startNewGameButton = new ButtonType("再来一次");
        ButtonType outGameButton = new ButtonType("就此退出");
        alert.getButtonTypes().setAll(startNewGameButton, outGameButton);
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == startNewGameButton) {
            restartAfterEnd();
        } else if (result.isPresent() && result.get() == outGameButton) {
            if (!visitor && choice == 0) {
                try {
                    gameData.initGameData();
                    gameData.saveGameData();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            shutdown();
        }
    }

    private void restartAfterEnd() {
        stopBoardAnimation();
        initVars();
        started = true;
        timeline.playFromStart();
        gameData.initGameData();
        initGame();
        buttonPane.getChildren().setAll(restartButton);
        StackPane.setAlignment(restartButton, Pos.CENTER);
        StackPane.setMargin(restartButton, new Insets(0, 0, 50, 0));
    }

    private void finishMove(MoveResult moveResult) {
        boardBusy = false;
        scoreLabel.setText("得分: " + score);
        gameData.updateGameData(score, counter, model.getGridnums());

        if (moveResult.win()) {
            gameWin();
            return;
        }

        if (moveResult.gameOver()) {
            gameOver();
            return;
        }

        saved = false;
        requestGameFocus();
    }

    private void exitToChoice() {
        stopBoardAnimation();
        over = true;
        if (!visitor && !saved) {
            try {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("");
                alert.setHeaderText("您确定要退出吗");
                alert.setContentText("您游玩过程中的数据将会被自动保存");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    gameData.saveGameData();
                    shutdown();
                    Choice.ChooseModel(stage, user);
                } else {
                    over = false;
                    requestGameFocus();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        shutdown();
        Choice.ChooseModel(stage, user);
    }

    private void toggleSound(ImageView soundOption) {
        if (soundOpen) {
            soundOption.setImage(new Image(ResourceLoader.resourceUrl("/pictures/SoundClose.png").toExternalForm()));
            bgm.setVolume(0);
            soundOpen = false;
        } else {
            soundOption.setImage(new Image(ResourceLoader.resourceUrl("/pictures/SoundOpen.png").toExternalForm()));
            bgm.setVolume(1);
            soundOpen = true;
        }
        requestGameFocus();
    }

    private void showModeInformation() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("游戏信息");
        switch (choice) {
            case 0 -> {
                alert.setHeaderText("传统模式");
                alert.setContentText("在无限长时间内，玩家通过合并数字获取得分，直至得到 2048 或无法操作为止。");
            }
            case 1 -> {
                alert.setHeaderText("障碍模式");
                alert.setContentText("玩家会遇到一个无法合并的方块，你必须在剩余区域完成得到 2048。");
            }
            case 2 -> {
                alert.setHeaderText("限时模式");
                alert.setContentText("玩家需要在规定时间内完成得分，当前限制为 60 秒。");
            }
            default -> throw new IllegalStateException("Unexpected choice: " + choice);
        }
        alert.showAndWait();
        requestGameFocus();
    }

    private void requestGameFocus() {
        if (gameScene != null) {
            gameScene.getRoot().requestFocus();
        }
    }

    private void stopBoardAnimation() {
        boardBusy = false;
        if (boardAnimator != null && model != null) {
            boardAnimator.stop(model.getBoardState());
        }
    }

    private void shutdown() {
        stopBoardAnimation();
        if (timeline != null) {
            timeline.stop();
        }
        if (bgm != null) {
            bgm.stop();
        }
        stage.close();
    }
}
