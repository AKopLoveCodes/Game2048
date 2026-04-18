package com.cz.game2048super;

public record TileDescriptor(int id, int value, BoardPosition position) {

    public TileDescriptor withValue(int newValue) {
        return new TileDescriptor(id, newValue, position);
    }

    public TileDescriptor withPosition(BoardPosition newPosition) {
        return new TileDescriptor(id, value, newPosition);
    }
}
