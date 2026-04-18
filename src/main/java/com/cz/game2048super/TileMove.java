package com.cz.game2048super;

public record TileMove(int tileId, int value, BoardPosition from, BoardPosition to) {
}
