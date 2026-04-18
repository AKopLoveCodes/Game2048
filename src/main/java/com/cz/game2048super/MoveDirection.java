package com.cz.game2048super;

public enum MoveDirection {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    public static MoveDirection fromLegacyCode(int direction) {
        return switch (direction) {
            case 1 -> UP;
            case 2 -> DOWN;
            case 3 -> LEFT;
            case 4 -> RIGHT;
            default -> throw new IllegalArgumentException("Unknown move direction: " + direction);
        };
    }
}
