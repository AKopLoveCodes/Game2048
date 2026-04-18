package com.cz.game2048super;

public record MergeEvent(int survivorTileId, int absorbedTileId, BoardPosition position, int mergedValue) {
}
