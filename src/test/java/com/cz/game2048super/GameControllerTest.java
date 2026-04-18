package com.cz.game2048super;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameControllerTest {

    @Test
    void isGameOverReturnsFalseWhenBoardIsFullButMergeIsStillPossible() {
        GameController controller = new GameController();
        controller.setGridnums(new int[][]{
                {2, 2, 4, 8},
                {16, 32, 64, 128},
                {256, 512, 1024, 2048},
                {4, 8, 16, 32}
        });

        assertFalse(controller.isGameOver());
    }

    @Test
    void lastMoveChangedReturnsFalseForNoOpMove() {
        GameController controller = new GameController();
        int[][] initial = {
                {2, 0, 0, 0},
                {4, 8, 16, 32},
                {64, 128, 256, 512},
                {1024, 2048, 4, 8}
        };
        controller.setGridnums(initial);

        assertEquals(0, controller.AStep(3));
        assertArrayEquals(initial, controller.getGridnums());
        assertFalse(lastMoveChanged(controller));
    }

    @Test
    void lastMoveChangedReturnsTrueForARealMove() {
        GameController controller = new GameController();
        int[][] initial = {
                {0, 2, 0, 0},
                {4, 8, 16, 32},
                {64, 128, 256, 512},
                {1024, 2048, 4, 8}
        };
        controller.setGridnums(initial);

        assertEquals(0, controller.AStep(3));

        assertArrayEquals(new int[][]{
                {2, 0, 0, 0},
                {4, 8, 16, 32},
                {64, 128, 256, 512},
                {1024, 2048, 4, 8}
        }, controller.getGridnums());
        assertTrue(lastMoveChanged(controller));
    }

    @Test
    void moveApiReturnsStructuredResultForAnimatedFlow() {
        GameController controller = new GameController();
        controller.setGridnums(new int[][]{
                {0, 2, 2, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        });

        Object result = move(controller, "LEFT", false);

        assertTrue(lastMoveChanged(controller));
        assertEquals(4, scoreDelta(result));
        assertArrayEquals(new int[][]{
                {4, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        }, controller.getGridnums());
    }

    @Test
    void gameOverShouldConsiderBlockedBoardsEvenWhenObstacleModeStillHasEmptyCells() {
        GameController controller = new GameController();
        controller.setGridnums(new int[][]{
                {2, 1, 4, 1},
                {1, 8, 1, 16},
                {32, 1, 64, 1},
                {1, 0, 1, 2}
        });

        assertTrue(controller.isGameOver());
    }

    private boolean lastMoveChanged(GameController controller) {
        try {
            Method method = GameController.class.getMethod("wasLastMoveChanged");
            return (boolean) method.invoke(controller);
        } catch (NoSuchMethodException e) {
            throw new AssertionError("GameController should expose wasLastMoveChanged() after a move", e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new AssertionError("Unable to read last move state", e);
        }
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Object move(GameController controller, String directionName, boolean spawn) {
        try {
            Class directionClass = Class.forName("com.cz.game2048super.MoveDirection");
            Object direction = Enum.valueOf(directionClass, directionName);
            Method method = GameController.class.getMethod("move", directionClass, boolean.class);
            return method.invoke(controller, direction, spawn);
        } catch (ClassNotFoundException e) {
            throw new AssertionError("MoveDirection should exist for the controller animation flow", e);
        } catch (NoSuchMethodException e) {
            throw new AssertionError("GameController should expose move(MoveDirection, boolean)", e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new AssertionError("Unable to execute GameController move API", e);
        }
    }

    private int scoreDelta(Object result) {
        try {
            Method method = result.getClass().getMethod("scoreDelta");
            return (int) method.invoke(result);
        } catch (NoSuchMethodException e) {
            throw new AssertionError("MoveResult should expose scoreDelta()", e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new AssertionError("Unable to read MoveResult score", e);
        }
    }
}
