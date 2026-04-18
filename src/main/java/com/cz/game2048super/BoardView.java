package com.cz.game2048super;

import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BoardView extends StackPane {
    private static final double BOARD_SIZE = 600;
    private static final double PADDING = 18;
    private static final double GAP = 12;
    private static final double CELL_SIZE = (BOARD_SIZE - (PADDING * 2) - (GAP * 3)) / 4;

    private final Pane boardPane;
    private final Pane tileLayer;
    private final Map<Integer, TileView> tileViews;
    private BoardState currentState;

    public BoardView() {
        this.tileViews = new LinkedHashMap<>();
        this.currentState = BoardState.empty();
        this.boardPane = new Pane();
        this.tileLayer = new Pane();

        setAlignment(Pos.CENTER);
        setPrefSize(BOARD_SIZE, BOARD_SIZE);
        setMinSize(BOARD_SIZE, BOARD_SIZE);
        setMaxSize(BOARD_SIZE, BOARD_SIZE);

        boardPane.setPrefSize(BOARD_SIZE, BOARD_SIZE);
        boardPane.setMinSize(BOARD_SIZE, BOARD_SIZE);
        boardPane.setMaxSize(BOARD_SIZE, BOARD_SIZE);

        Rectangle boardShell = new Rectangle(BOARD_SIZE, BOARD_SIZE);
        boardShell.setArcWidth(52);
        boardShell.setArcHeight(52);
        boardShell.setFill(new LinearGradient(
                0,
                0,
                1,
                1,
                true,
                CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.web("#7a6453")),
                new Stop(0.5, Color.web("#6b5648")),
                new Stop(1.0, Color.web("#4f4037"))
        ));

        DropShadow outer = new DropShadow();
        outer.setRadius(32);
        outer.setOffsetY(18);
        outer.setColor(Color.web("#140d09", 0.28));

        InnerShadow inner = new InnerShadow();
        inner.setRadius(16);
        inner.setOffsetY(-8);
        inner.setColor(Color.web("#fff6ea", 0.18));
        outer.setInput(inner);
        boardShell.setEffect(outer);

        boardPane.getChildren().add(boardShell);
        boardPane.getChildren().addAll(createSlots());
        boardPane.getChildren().add(tileLayer);

        getChildren().add(boardPane);
    }

    public void syncToState(BoardState state) {
        Set<Integer> targetIds = state.tiles().stream().map(TileDescriptor::id).collect(java.util.stream.Collectors.toSet());

        List<Integer> toRemove = new ArrayList<>();
        for (Integer tileId : tileViews.keySet()) {
            if (!targetIds.contains(tileId)) {
                toRemove.add(tileId);
            }
        }
        for (Integer tileId : toRemove) {
            removeTile(tileId);
        }

        for (TileDescriptor tile : state.tiles()) {
            TileView tileView = tileViews.get(tile.id());
            if (tileView == null) {
                tileView = addTile(tile);
            } else {
                tileView.applyTile(tile);
            }
            snapTile(tile.id(), tile.position());
            tileView.resetVisualState();
        }

        currentState = state;
    }

    public TileView tileView(int tileId) {
        return tileViews.get(tileId);
    }

    public TileView addTile(TileDescriptor tile) {
        TileView tileView = new TileView(tile, CELL_SIZE);
        tileViews.put(tile.id(), tileView);
        tileLayer.getChildren().add(tileView);
        snapTile(tile.id(), tile.position());
        return tileView;
    }

    public void removeTile(int tileId) {
        TileView tileView = tileViews.remove(tileId);
        if (tileView != null) {
            tileLayer.getChildren().remove(tileView);
        }
    }

    public void snapTile(int tileId, BoardPosition position) {
        TileView tileView = tileViews.get(tileId);
        if (tileView == null) {
            return;
        }
        Point2D point = pointFor(position);
        tileView.moveTo(position);
        tileView.relocate(point.getX(), point.getY());
        tileView.setTranslateX(0);
        tileView.setTranslateY(0);
    }

    public Point2D pointFor(BoardPosition position) {
        return new Point2D(
                PADDING + position.col() * (CELL_SIZE + GAP),
                PADDING + position.row() * (CELL_SIZE + GAP)
        );
    }

    public BoardState currentState() {
        return currentState;
    }

    public double cellSize() {
        return CELL_SIZE;
    }

    public Node animatedNode() {
        return this;
    }

    private List<Node> createSlots() {
        List<Node> slots = new ArrayList<>();
        for (int row = 0; row < BoardState.SIZE; row++) {
            for (int col = 0; col < BoardState.SIZE; col++) {
                Rectangle slot = new Rectangle(CELL_SIZE, CELL_SIZE);
                slot.setArcWidth(CELL_SIZE * 0.24);
                slot.setArcHeight(CELL_SIZE * 0.24);
                slot.setFill(Color.web("#d4c1af", 0.42));

                InnerShadow slotDepth = new InnerShadow();
                slotDepth.setRadius(10);
                slotDepth.setOffsetY(3);
                slotDepth.setColor(Color.web("#3b2f28", 0.18));
                slot.setEffect(slotDepth);

                Point2D point = pointFor(new BoardPosition(row, col));
                slot.relocate(point.getX(), point.getY());
                slots.add(slot);
            }
        }
        return slots;
    }
}
