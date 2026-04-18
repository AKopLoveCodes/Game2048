package com.cz.game2048super;

import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BoardAnimationPlanTest {

    @Test
    void plannerDelaysSpawnUntilAfterSlideAndMerge() {
        Object engine = newEngine();
        setBoard(engine, new int[][]{
                {2, 2, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0},
                {0, 0, 0, 0}
        });
        Object result = move(engine, "LEFT", true);

        Object tokens = defaultTokens();
        Object plan = buildPlan(result, tokens);

        Duration slide = (Duration) invoke(plan, "slideDuration");
        Duration merge = (Duration) invoke(plan, "mergeDuration");
        Duration spawnDelay = (Duration) invoke(plan, "spawnDelay");
        Duration total = (Duration) invoke(plan, "totalDuration");

        assertEquals(slide.plus(merge), spawnDelay);
        assertEquals(spawnDelay.plus((Duration) invoke(plan, "spawnDuration")), total);
        assertEquals(1, ((List<?>) invoke(plan, "mergeEvents")).size());
        assertNotNull(invoke(plan, "spawnEvent"));
    }

    @Test
    void plannerUsesFailureDurationForBlockedMoves() {
        Object engine = newEngine();
        setBoard(engine, new int[][]{
                {2, 4, 2, 4},
                {4, 2, 4, 2},
                {2, 4, 2, 4},
                {4, 2, 4, 2}
        });
        Object result = move(engine, "LEFT", true);

        Object plan = buildPlan(result, defaultTokens());

        assertTrue((boolean) invoke(plan, "isFailureFeedbackOnly"));
        assertEquals(Duration.ZERO, invoke(plan, "slideDuration"));
        assertEquals(invoke(plan, "failureDuration"), invoke(plan, "totalDuration"));
    }

    private Object newEngine() {
        try {
            return Class.forName("com.cz.game2048super.GameEngine")
                    .getDeclaredConstructor()
                    .newInstance();
        } catch (ClassNotFoundException e) {
            throw new AssertionError("GameEngine should exist for animation planning", e);
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
            throw new AssertionError("MoveDirection enum should exist for animation planning", e);
        } catch (NoSuchMethodException e) {
            throw new AssertionError("GameEngine should expose move(MoveDirection, boolean)", e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new AssertionError("Unable to execute GameEngine move", e);
        }
    }

    private Object defaultTokens() {
        try {
            Class<?> tokensClass = Class.forName("com.cz.game2048super.MotionTokens");
            Method method = tokensClass.getMethod("defaults");
            return method.invoke(null);
        } catch (ClassNotFoundException e) {
            throw new AssertionError("MotionTokens should exist for animation planning", e);
        } catch (NoSuchMethodException e) {
            throw new AssertionError("MotionTokens should expose defaults()", e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new AssertionError("Unable to create MotionTokens defaults", e);
        }
    }

    private Object buildPlan(Object result, Object tokens) {
        try {
            Class<?> plannerClass = Class.forName("com.cz.game2048super.BoardAnimationPlanner");
            Method method = plannerClass.getMethod("plan", result.getClass(), tokens.getClass());
            return method.invoke(null, result, tokens);
        } catch (ClassNotFoundException e) {
            throw new AssertionError("BoardAnimationPlanner should exist", e);
        } catch (NoSuchMethodException e) {
            throw new AssertionError("BoardAnimationPlanner should expose plan(MoveResult, MotionTokens)", e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new AssertionError("Unable to build animation plan", e);
        }
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
}
