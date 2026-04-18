package com.cz.game2048super;

import java.util.List;

public record MoveResult(
        BoardState boardBefore,
        BoardState boardAfterMove,
        BoardState boardAfter,
        List<TileMove> tileMoves,
        List<MergeEvent> mergeEvents,
        SpawnEvent spawnEvent,
        int scoreDelta,
        boolean moveChanged,
        boolean win,
        boolean gameOver
) {
    public MoveResult {
        tileMoves = List.copyOf(tileMoves);
        mergeEvents = List.copyOf(mergeEvents);
    }
}
