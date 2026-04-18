package com.cz.game2048super;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameEngine {
    private final Random random;
    private BoardState boardState;
    private int nextTileId;

    public GameEngine() {
        this(new Random());
    }

    public GameEngine(Random random) {
        this.random = random;
        this.boardState = BoardState.empty();
        this.nextTileId = 1;
    }

    public void setBoard(int[][] grid) {
        List<TileDescriptor> tiles = new ArrayList<>();
        nextTileId = 1;
        for (int row = 0; row < BoardState.SIZE; row++) {
            for (int col = 0; col < BoardState.SIZE; col++) {
                int value = grid[row][col];
                if (value != 0) {
                    tiles.add(new TileDescriptor(nextTileId++, value, new BoardPosition(row, col)));
                }
            }
        }
        boardState = new BoardState(tiles);
    }

    public BoardState boardState() {
        return boardState;
    }

    public MoveResult move(MoveDirection direction, boolean spawnAfterMove) {
        BoardState before = boardState;
        List<TileMove> tileMoves = new ArrayList<>();
        List<MergeEvent> mergeEvents = new ArrayList<>();
        List<TileDescriptor> resolvedTiles = new ArrayList<>();
        int[] scoreDelta = {0};

        for (int lineIndex = 0; lineIndex < BoardState.SIZE; lineIndex++) {
            resolveLine(direction, lineIndex, before, tileMoves, mergeEvents, resolvedTiles, scoreDelta);
        }

        BoardState afterMove = new BoardState(resolvedTiles);
        boolean moveChanged = !tileMoves.isEmpty() || !mergeEvents.isEmpty();
        boolean win = afterMove.isWin();

        SpawnEvent spawnEvent = null;
        BoardState after = afterMove;
        if (moveChanged && spawnAfterMove && !win) {
            spawnEvent = createSpawn(afterMove);
            if (spawnEvent != null) {
                after = afterMove.withTile(new TileDescriptor(
                        spawnEvent.tileId(),
                        spawnEvent.value(),
                        spawnEvent.position()));
            }
        }

        boolean gameOver = after.isGameOver();
        boardState = after;
        return new MoveResult(before, afterMove, after, tileMoves, mergeEvents, spawnEvent, scoreDelta[0], moveChanged, win, gameOver);
    }

    public SpawnEvent spawnRandomTile() {
        SpawnEvent spawnEvent = createSpawn(boardState);
        if (spawnEvent != null) {
            boardState = boardState.withTile(new TileDescriptor(
                    spawnEvent.tileId(),
                    spawnEvent.value(),
                    spawnEvent.position()));
        }
        return spawnEvent;
    }

    private SpawnEvent createSpawn(BoardState currentState) {
        List<BoardPosition> emptyPositions = currentState.emptyPositions();
        if (emptyPositions.isEmpty()) {
            return null;
        }
        BoardPosition position = emptyPositions.get(random.nextInt(emptyPositions.size()));
        int value = random.nextInt(2) == 0 ? 2 : 4;
        return new SpawnEvent(nextTileId++, value, position);
    }

    private void resolveLine(
            MoveDirection direction,
            int lineIndex,
            BoardState state,
            List<TileMove> tileMoves,
            List<MergeEvent> mergeEvents,
            List<TileDescriptor> resolvedTiles,
            int[] scoreDelta
    ) {
        List<BoardPosition> segmentPositions = new ArrayList<>();
        List<TileDescriptor> segmentTiles = new ArrayList<>();

        for (int offset = 0; offset < BoardState.SIZE; offset++) {
            BoardPosition position = targetPosition(direction, lineIndex, offset);
            TileDescriptor tile = state.tileAt(position).orElse(null);

            if (tile != null && tile.value() == 1) {
                resolveSegment(segmentPositions, segmentTiles, tileMoves, mergeEvents, resolvedTiles, scoreDelta);
                resolvedTiles.add(tile);
                segmentPositions.clear();
                segmentTiles.clear();
                continue;
            }

            segmentPositions.add(position);
            if (tile != null) {
                segmentTiles.add(tile);
            }
        }

        resolveSegment(segmentPositions, segmentTiles, tileMoves, mergeEvents, resolvedTiles, scoreDelta);
    }

    private void resolveSegment(
            List<BoardPosition> segmentPositions,
            List<TileDescriptor> segmentTiles,
            List<TileMove> tileMoves,
            List<MergeEvent> mergeEvents,
            List<TileDescriptor> resolvedTiles,
            int[] scoreDelta
    ) {
        if (segmentPositions.isEmpty()) {
            return;
        }

        List<PlacedTile> placedTiles = new ArrayList<>();
        for (TileDescriptor tile : segmentTiles) {
            if (!placedTiles.isEmpty()) {
                PlacedTile lastPlaced = placedTiles.get(placedTiles.size() - 1);
                if (!lastPlaced.merged && canMerge(lastPlaced.tile.value(), tile.value())) {
                    BoardPosition target = lastPlaced.tile.position();
                    if (!tile.position().equals(target)) {
                        tileMoves.add(new TileMove(tile.id(), tile.value(), tile.position(), target));
                    }

                    TileDescriptor mergedTile = lastPlaced.tile.withValue(lastPlaced.tile.value() * 2);
                    placedTiles.set(placedTiles.size() - 1, new PlacedTile(mergedTile, true));
                    mergeEvents.add(new MergeEvent(lastPlaced.tile.id(), tile.id(), target, mergedTile.value()));
                    scoreDelta[0] += mergedTile.value();
                    continue;
                }
            }

            BoardPosition target = segmentPositions.get(placedTiles.size());
            if (!tile.position().equals(target)) {
                tileMoves.add(new TileMove(tile.id(), tile.value(), tile.position(), target));
            }
            placedTiles.add(new PlacedTile(tile.withPosition(target), false));
        }

        for (PlacedTile placedTile : placedTiles) {
            resolvedTiles.add(placedTile.tile);
        }
    }

    private BoardPosition targetPosition(MoveDirection direction, int lineIndex, int offset) {
        return switch (direction) {
            case LEFT -> new BoardPosition(lineIndex, offset);
            case RIGHT -> new BoardPosition(lineIndex, BoardState.SIZE - 1 - offset);
            case UP -> new BoardPosition(offset, lineIndex);
            case DOWN -> new BoardPosition(BoardState.SIZE - 1 - offset, lineIndex);
        };
    }

    private boolean canMerge(int first, int second) {
        return first == second && first != 1;
    }

    private record PlacedTile(TileDescriptor tile, boolean merged) {
    }
}
