package com.cz.game2048super;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class BoardState {
    public static final int SIZE = 4;

    private final List<TileDescriptor> tiles;
    private final Map<BoardPosition, TileDescriptor> positions;
    private final int[][] gridValues;

    public BoardState(List<TileDescriptor> tiles) {
        List<TileDescriptor> sortedTiles = new ArrayList<>(tiles);
        sortedTiles.sort(Comparator
                .comparingInt((TileDescriptor tile) -> tile.position().row())
                .thenComparingInt(tile -> tile.position().col())
                .thenComparingInt(TileDescriptor::id));
        this.tiles = List.copyOf(sortedTiles);
        this.positions = new HashMap<>();
        this.gridValues = new int[SIZE][SIZE];

        for (TileDescriptor tile : sortedTiles) {
            BoardPosition position = tile.position();
            positions.put(position, tile);
            gridValues[position.row()][position.col()] = tile.value();
        }
    }

    public static BoardState empty() {
        return new BoardState(List.of());
    }

    public List<TileDescriptor> tiles() {
        return tiles;
    }

    public Optional<TileDescriptor> tileAt(BoardPosition position) {
        return Optional.ofNullable(positions.get(position));
    }

    public int[][] toGridValues() {
        int[][] copy = new int[SIZE][SIZE];
        for (int row = 0; row < SIZE; row++) {
            System.arraycopy(gridValues[row], 0, copy[row], 0, SIZE);
        }
        return copy;
    }

    public List<BoardPosition> emptyPositions() {
        List<BoardPosition> empties = new ArrayList<>();
        for (int row = 0; row < SIZE; row++) {
            for (int col = 0; col < SIZE; col++) {
                if (gridValues[row][col] == 0) {
                    empties.add(new BoardPosition(row, col));
                }
            }
        }
        return empties;
    }

    public boolean isWin() {
        return tiles.stream().anyMatch(tile -> tile.value() >= 2048);
    }

    public boolean isGameOver() {
        for (MoveDirection direction : MoveDirection.values()) {
            if (canMove(direction)) {
                return false;
            }
        }
        return true;
    }

    public BoardState withTile(TileDescriptor tileDescriptor) {
        List<TileDescriptor> updated = new ArrayList<>(tiles);
        updated.add(tileDescriptor);
        return new BoardState(updated);
    }

    private boolean canMerge(int first, int second) {
        return first == second && first != 1;
    }

    private boolean canMove(MoveDirection direction) {
        for (int lineIndex = 0; lineIndex < SIZE; lineIndex++) {
            int[] original = line(direction, lineIndex);
            int[] resolved = resolveLineRespectingObstacles(original);
            if (!java.util.Arrays.equals(original, resolved)) {
                return true;
            }
        }
        return false;
    }

    private int[] line(MoveDirection direction, int lineIndex) {
        int[] values = new int[SIZE];
        for (int offset = 0; offset < SIZE; offset++) {
            BoardPosition position = switch (direction) {
                case LEFT -> new BoardPosition(lineIndex, offset);
                case RIGHT -> new BoardPosition(lineIndex, SIZE - 1 - offset);
                case UP -> new BoardPosition(offset, lineIndex);
                case DOWN -> new BoardPosition(SIZE - 1 - offset, lineIndex);
            };
            values[offset] = gridValues[position.row()][position.col()];
        }
        return values;
    }

    private int[] resolveLineRespectingObstacles(int[] original) {
        int[] resolved = new int[SIZE];
        int segmentStart = 0;

        while (segmentStart < SIZE) {
            if (original[segmentStart] == 1) {
                resolved[segmentStart] = 1;
                segmentStart++;
                continue;
            }

            int segmentEnd = segmentStart;
            while (segmentEnd < SIZE && original[segmentEnd] != 1) {
                segmentEnd++;
            }

            List<Integer> compacted = new ArrayList<>();
            for (int index = segmentStart; index < segmentEnd; index++) {
                if (original[index] != 0) {
                    compacted.add(original[index]);
                }
            }

            int writeIndex = segmentStart;
            for (int index = 0; index < compacted.size(); index++) {
                int value = compacted.get(index);
                if (index + 1 < compacted.size() && canMerge(value, compacted.get(index + 1))) {
                    resolved[writeIndex++] = value * 2;
                    index++;
                } else {
                    resolved[writeIndex++] = value;
                }
            }

            segmentStart = segmentEnd;
        }
        return resolved;
    }
}
