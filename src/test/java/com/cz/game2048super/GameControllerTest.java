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
}
