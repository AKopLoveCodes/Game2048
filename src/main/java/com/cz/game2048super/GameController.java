package com.cz.game2048super;

public class GameController {
    private final GameEngine engine;
    private int[][] gridnums;
    private boolean lastMoveChanged;
    private MoveResult lastMoveResult;

    public GameController() {
        this.engine = new GameEngine();
        this.gridnums = new int[BoardState.SIZE][BoardState.SIZE];
        this.lastMoveChanged = false;
    }

    public int AStep(int direction) {
        return move(MoveDirection.fromLegacyCode(direction), false).scoreDelta();
    }

    public MoveResult move(MoveDirection direction, boolean spawn) {
        lastMoveResult = engine.move(direction, spawn);
        gridnums = lastMoveResult.boardAfter().toGridValues();
        lastMoveChanged = lastMoveResult.moveChanged();
        return lastMoveResult;
    }

    public int[][] getGridnums() {
        return copyGrid(gridnums);
    }

    public boolean isGameOver() {
        return engine.boardState().isGameOver();
    }

    public boolean isWin() {
        return engine.boardState().isWin();
    }

    public void createNewGrid() {
        spawnRandomTile();
    }

    public SpawnEvent spawnRandomTile() {
        SpawnEvent spawnEvent = engine.spawnRandomTile();
        gridnums = engine.boardState().toGridValues();
        return spawnEvent;
    }

    public void setGridnums(int[][] grids) {
        gridnums = copyGrid(grids);
        engine.setBoard(gridnums);
        lastMoveChanged = false;
        lastMoveResult = null;
    }

    public boolean wasLastMoveChanged() {
        return lastMoveChanged;
    }

    public MoveResult getLastMoveResult() {
        return lastMoveResult;
    }

    public BoardState getBoardState() {
        return engine.boardState();
    }

    private int[][] copyGrid(int[][] source) {
        int[][] copy = new int[BoardState.SIZE][BoardState.SIZE];
        for (int row = 0; row < BoardState.SIZE; row++) {
            System.arraycopy(source[row], 0, copy[row], 0, BoardState.SIZE);
        }
        return copy;
    }
}
