package com.cz.game2048super;

import javafx.scene.input.KeyCode;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MainGameTest {

    @Test
    void blockedMoveOnDeadBoardShouldTriggerGameOver() {
        GameController controller = new GameController();
        controller.setGridnums(new int[][]{
                {2, 4, 2, 4},
                {4, 2, 4, 2},
                {2, 4, 2, 4},
                {4, 2, 4, 2}
        });

        controller.AStep(3);

        assertTrue(shouldTriggerGameOverAfterInput(KeyCode.LEFT, controller.wasLastMoveChanged(), controller));
    }

    @Test
    void blockedMoveOnPlayableBoardShouldNotTriggerGameOver() {
        GameController controller = new GameController();
        controller.setGridnums(new int[][]{
                {2, 2, 4, 8},
                {16, 32, 64, 128},
                {256, 512, 1024, 2048},
                {4, 8, 16, 32}
        });

        controller.AStep(1);

        assertFalse(shouldTriggerGameOverAfterInput(KeyCode.UP, controller.wasLastMoveChanged(), controller));
    }

    @Test
    void nonMoveKeyShouldNotTriggerGameOverFallback() {
        GameController controller = new GameController();
        controller.setGridnums(new int[][]{
                {2, 4, 2, 4},
                {4, 2, 4, 2},
                {2, 4, 2, 4},
                {4, 2, 4, 2}
        });

        assertFalse(shouldTriggerGameOverAfterInput(KeyCode.ENTER, false, controller));
    }

    @Test
    void startedDeadBoardShouldPromptEndDialogWhenBoardBecomesReady() {
        GameController controller = new GameController();
        controller.setGridnums(new int[][]{
                {2, 4, 2, 4},
                {4, 2, 4, 2},
                {2, 4, 2, 4},
                {4, 2, 4, 2}
        });

        assertTrue(shouldPromptEndDialogWhenBoardReady(true, false, controller));
    }

    @Test
    void notStartedBoardShouldNotPromptEndDialog() {
        GameController controller = new GameController();
        controller.setGridnums(new int[][]{
                {2, 4, 2, 4},
                {4, 2, 4, 2},
                {2, 4, 2, 4},
                {4, 2, 4, 2}
        });

        assertFalse(shouldPromptEndDialogWhenBoardReady(false, false, controller));
    }

    private boolean shouldTriggerGameOverAfterInput(KeyCode keyCode, boolean moveChanged, GameController controller) {
        try {
            Method method = MainGame.class.getDeclaredMethod(
                    "shouldTriggerGameOverAfterInput",
                    KeyCode.class,
                    boolean.class,
                    GameController.class
            );
            method.setAccessible(true);
            return (boolean) method.invoke(null, keyCode, moveChanged, controller);
        } catch (NoSuchMethodException e) {
            throw new AssertionError("MainGame should expose shouldTriggerGameOverAfterInput for move fallback logic", e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new AssertionError("Unable to invoke MainGame fallback logic", e);
        }
    }

    private boolean shouldPromptEndDialogWhenBoardReady(boolean started, boolean over, GameController controller) {
        try {
            Method method = MainGame.class.getDeclaredMethod(
                    "shouldPromptEndDialogWhenBoardReady",
                    boolean.class,
                    boolean.class,
                    GameController.class
            );
            method.setAccessible(true);
            return (boolean) method.invoke(null, started, over, controller);
        } catch (NoSuchMethodException e) {
            throw new AssertionError("MainGame should expose readiness end-state logic for terminal boards", e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new AssertionError("Unable to invoke MainGame readiness end-state logic", e);
        }
    }
}
