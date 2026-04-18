package com.cz.game2048super;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoardMoveResultTest {

    @Test
    void moveLeftWithoutSpawnTracksSlidesMergeAndScore() {
        Object engine = newEngine();
        setBoard(engine, new int[][]{
                {0, 2, 2, 4},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        });

        Object result = move(engine, "LEFT", false);

        assertTrue((boolean) invoke(result, "moveChanged"));
        assertEquals(4, invoke(result, "scoreDelta"));
        assertArrayEquals(new int[][]{
                {4, 4, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        }, toGrid(result, "boardAfterMove"));
        assertEquals(3, ((List<?>) invoke(result, "tileMoves")).size());
        assertEquals(1, ((List<?>) invoke(result, "mergeEvents")).size());
        assertNull(invoke(result, "spawnEvent"));
    }

    @Test
    void moveWithSpawnCreatesOneNewTileOnAnEmptyCell() {
        Object engine = newEngine();
        setBoard(engine, new int[][]{
                {2, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        });

        Object result = move(engine, "RIGHT", true);

        assertTrue((boolean) invoke(result, "moveChanged"));
        assertArrayEquals(new int[][]{
                {0, 0, 0, 2},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        }, toGrid(result, "boardAfterMove"));

        Object spawnEvent = invoke(result, "spawnEvent");
        assertNotNull(spawnEvent);

        int spawnValue = (int) invoke(spawnEvent, "value");
        assertTrue(spawnValue == 2 || spawnValue == 4);

        int[][] afterMove = toGrid(result, "boardAfterMove");
        int[][] afterSpawn = toGrid(result, "boardAfter");
        BoardCell spawnCell = toCell(spawnEvent, "position");
        assertEquals(0, afterMove[spawnCell.row][spawnCell.col]);
        assertEquals(spawnValue, afterSpawn[spawnCell.row][spawnCell.col]);
        assertEquals(2, countNonZero(afterSpawn));
    }

    @Test
    void moveLeftOnlyMergesOncePerPair() {
        Object engine = newEngine();
        setBoard(engine, new int[][]{
                {2, 2, 2, 2},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        });

        Object result = move(engine, "LEFT", false);

        assertArrayEquals(new int[][]{
                {4, 4, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        }, toGrid(result, "boardAfterMove"));
        assertEquals(8, invoke(result, "scoreDelta"));
        assertEquals(2, ((List<?>) invoke(result, "mergeEvents")).size());
        assertFalse((boolean) invoke(result, "win"));
    }

    @Test
    void obstaclesShouldBlockSlidingAcrossSegments() {
        Object engine = newEngine();
        setBoard(engine, new int[][]{
                {2, 1, 2, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        });

        Object result = move(engine, "LEFT", false);

        assertFalse((boolean) invoke(result, "moveChanged"));
        assertArrayEquals(new int[][]{
                {2, 1, 2, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        }, toGrid(result, "boardAfterMove"));
    }

    @Test
    void moveCanReportGameOverAfterSpawnCreatesTheFinalDeadBoard() {
        Object engine = newEngine(new FixedRandom(0, 0));
        setBoard(engine, new int[][]{
                {0, 8, 16, 32},
                {64, 128, 256, 512},
                {1024, 2, 4, 8},
                {16, 32, 64, 128}
        });

        Object result = move(engine, "LEFT", true);

        assertTrue((boolean) invoke(result, "moveChanged"));
        assertNotNull(invoke(result, "spawnEvent"));
        assertTrue((boolean) invoke(result, "gameOver"));
    }

    private Object newEngine() {
        return newEngine(null);
    }

    private Object newEngine(Random random) {
        try {
            Class<?> engineClass = Class.forName("com.cz.game2048super.GameEngine");
            if (random == null) {
                return engineClass.getDeclaredConstructor().newInstance();
            }
            return engineClass.getDeclaredConstructor(Random.class).newInstance(random);
        } catch (ClassNotFoundException e) {
            throw new AssertionError("GameEngine should exist for the animated board refactor", e);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError("Unable to create GameEngine", e);
        }
    }

    private void setBoard(Object engine, int[][] grid) {
        invoke(engine, "setBoard", (Object) grid);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private Object move(Object engine, String directionName, boolean spawn) {
        try {
            Class directionClass = Class.forName("com.cz.game2048super.MoveDirection");
            Object direction = Enum.valueOf(directionClass, directionName);
            Method method = engine.getClass().getMethod("move", directionClass, boolean.class);
            return method.invoke(engine, direction, spawn);
        } catch (ClassNotFoundException e) {
            throw new AssertionError("MoveDirection enum should exist for the new board engine", e);
        } catch (NoSuchMethodException e) {
            throw new AssertionError("GameEngine should expose move(MoveDirection, boolean)", e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new AssertionError("Unable to execute GameEngine move", e);
        }
    }

    private int[][] toGrid(Object result, String accessor) {
        Object boardState = invoke(result, accessor);
        return (int[][]) invoke(boardState, "toGridValues");
    }

    private BoardCell toCell(Object owner, String accessor) {
        Object position = invoke(owner, accessor);
        int row = (int) invoke(position, "row");
        int col = (int) invoke(position, "col");
        return new BoardCell(row, col);
    }

    private Object invoke(Object target, String methodName, Object... args) {
        try {
            Method method = findMethod(target.getClass(), methodName, args);
            return method.invoke(target, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new AssertionError("Unable to call " + methodName, e);
        }
    }

    private Method findMethod(Class<?> type, String methodName, Object[] args) {
        for (Method method : type.getMethods()) {
            if (!method.getName().equals(methodName) || method.getParameterCount() != args.length) {
                continue;
            }
            Class<?>[] parameterTypes = method.getParameterTypes();
            boolean matches = true;
            for (int index = 0; index < parameterTypes.length; index++) {
                Object arg = args[index];
                if (arg == null) {
                    continue;
                }
                if (!wrap(parameterTypes[index]).isInstance(arg)) {
                    matches = false;
                    break;
                }
            }
            if (matches) {
                return method;
            }
        }
        throw new AssertionError(type.getSimpleName() + " should expose " + methodName);
    }

    private Class<?> wrap(Class<?> type) {
        if (!type.isPrimitive()) {
            return type;
        }
        return switch (type.getName()) {
            case "boolean" -> Boolean.class;
            case "int" -> Integer.class;
            case "long" -> Long.class;
            case "double" -> Double.class;
            default -> type;
        };
    }

    private int countNonZero(int[][] grid) {
        int count = 0;
        for (int[] row : grid) {
            for (int value : row) {
                if (value != 0) {
                    count++;
                }
            }
        }
        return count;
    }

    private record BoardCell(int row, int col) {
    }

    private static final class FixedRandom extends Random {
        private final int[] values;
        private int index;

        private FixedRandom(int... values) {
            this.values = values;
        }

        @Override
        public int nextInt(int bound) {
            return Math.floorMod(values[index++ % values.length], bound);
        }
    }
}
