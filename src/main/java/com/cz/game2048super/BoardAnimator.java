package com.cz.game2048super;

import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class BoardAnimator {
    private final BoardView boardView;
    private final MotionTokens tokens;
    private Animation activeAnimation;

    public BoardAnimator(BoardView boardView) {
        this(boardView, MotionTokens.defaults());
    }

    public BoardAnimator(BoardView boardView, MotionTokens tokens) {
        this.boardView = boardView;
        this.tokens = tokens;
    }

    public void play(MoveResult result, Runnable onFinished) {
        stop(result.boardBefore());
        AnimationPlan plan = BoardAnimationPlanner.plan(result, tokens);

        if (plan.isFailureFeedbackOnly()) {
            Animation failure = createFailureFeedback(plan.failureDuration());
            activeAnimation = failure;
            failure.setOnFinished(_ -> {
                activeAnimation = null;
                boardView.syncToState(result.boardAfter());
                if (onFinished != null) {
                    onFinished.run();
                }
            });
            failure.play();
            return;
        }

        List<Animation> phases = new ArrayList<>();
        Animation slidePhase = createSlidePhase(plan.slideMoves(), plan.slideDuration());
        if (slidePhase != null) {
            phases.add(slidePhase);
        }
        Animation mergePhase = createMergePhase(plan.mergeEvents(), plan.mergeDuration());
        if (mergePhase != null) {
            phases.add(mergePhase);
        }
        Animation spawnPhase = createSpawnPhase(plan.spawnEvent(), plan.spawnDuration());
        if (spawnPhase != null) {
            phases.add(spawnPhase);
        }

        SequentialTransition sequence = new SequentialTransition();
        sequence.getChildren().addAll(phases);
        activeAnimation = sequence;
        sequence.setOnFinished(_ -> {
            activeAnimation = null;
            boardView.syncToState(result.boardAfter());
            if (onFinished != null) {
                onFinished.run();
            }
        });
        sequence.play();
    }

    public void stop(BoardState fallbackState) {
        if (activeAnimation != null) {
            activeAnimation.stop();
            activeAnimation = null;
        }
        boardView.setScaleX(1);
        boardView.setScaleY(1);
        boardView.syncToState(fallbackState);
    }

    private Animation createFailureFeedback(Duration duration) {
        ScaleTransition scale = new ScaleTransition(toFx(duration), boardView.animatedNode());
        scale.setToX(tokens.failurePressedScale());
        scale.setToY(tokens.failurePressedScale());
        scale.setCycleCount(2);
        scale.setAutoReverse(true);
        scale.setInterpolator(Interpolator.EASE_BOTH);
        return scale;
    }

    private Animation createSlidePhase(List<TileMove> moves, Duration duration) {
        if (moves.isEmpty()) {
            return null;
        }

        ParallelTransition slidePhase = new ParallelTransition();
        for (TileMove move : moves) {
            TileView tileView = boardView.tileView(move.tileId());
            if (tileView == null) {
                continue;
            }
            var from = boardView.pointFor(tileView.descriptor().position());
            var to = boardView.pointFor(move.to());
            TranslateTransition transition = new TranslateTransition(toFx(duration), tileView);
            transition.setByX(to.getX() - from.getX());
            transition.setByY(to.getY() - from.getY());
            transition.setInterpolator(Interpolator.SPLINE(0.22, 0.84, 0.24, 1.0));
            slidePhase.getChildren().add(transition);
        }

        slidePhase.setOnFinished(_ -> {
            for (TileMove move : moves) {
                boardView.snapTile(move.tileId(), move.to());
            }
        });
        return slidePhase;
    }

    private Animation createMergePhase(List<MergeEvent> mergeEvents, Duration duration) {
        if (mergeEvents.isEmpty()) {
            return null;
        }

        ParallelTransition mergePhase = new ParallelTransition();
        for (MergeEvent mergeEvent : mergeEvents) {
            TileView survivor = boardView.tileView(mergeEvent.survivorTileId());
            TileView absorbed = boardView.tileView(mergeEvent.absorbedTileId());
            if (survivor == null) {
                continue;
            }

            survivor.updateValue(mergeEvent.mergedValue());
            ScaleTransition pulse = new ScaleTransition(toFx(duration), survivor);
            pulse.setToX(tokens.mergeScale());
            pulse.setToY(tokens.mergeScale());
            pulse.setCycleCount(2);
            pulse.setAutoReverse(true);
            pulse.setInterpolator(Interpolator.SPLINE(0.18, 0.88, 0.18, 1.0));

            ParallelTransition mergeTransition = new ParallelTransition();
            mergeTransition.getChildren().add(pulse);

            if (absorbed != null) {
                FadeTransition fade = new FadeTransition(toFx(duration), absorbed);
                fade.setToValue(0);
                ScaleTransition shrink = new ScaleTransition(toFx(duration), absorbed);
                shrink.setToX(0.9);
                shrink.setToY(0.9);
                mergeTransition.getChildren().addAll(fade, shrink);
            }

            mergePhase.getChildren().add(mergeTransition);
        }

        mergePhase.setOnFinished(_ -> {
            for (MergeEvent mergeEvent : mergeEvents) {
                boardView.removeTile(mergeEvent.absorbedTileId());
                TileView survivor = boardView.tileView(mergeEvent.survivorTileId());
                if (survivor != null) {
                    survivor.setScaleX(1);
                    survivor.setScaleY(1);
                    survivor.setOpacity(1);
                }
            }
        });
        return mergePhase;
    }

    private Animation createSpawnPhase(SpawnEvent spawnEvent, Duration duration) {
        if (spawnEvent == null) {
            return null;
        }

        TileView tileView = boardView.addTile(new TileDescriptor(
                spawnEvent.tileId(),
                spawnEvent.value(),
                spawnEvent.position()));
        tileView.setScaleX(tokens.spawnInitialScale());
        tileView.setScaleY(tokens.spawnInitialScale());
        tileView.setOpacity(0);

        FadeTransition fade = new FadeTransition(toFx(duration), tileView);
        fade.setFromValue(0);
        fade.setToValue(1);

        ScaleTransition scale = new ScaleTransition(toFx(duration), tileView);
        scale.setFromX(tokens.spawnInitialScale());
        scale.setFromY(tokens.spawnInitialScale());
        scale.setToX(1);
        scale.setToY(1);
        scale.setInterpolator(Interpolator.SPLINE(0.16, 0.94, 0.22, 1.0));

        ParallelTransition spawnPhase = new ParallelTransition(fade, scale);
        spawnPhase.setOnFinished(_ -> tileView.resetVisualState());
        return spawnPhase;
    }

    private javafx.util.Duration toFx(Duration duration) {
        return javafx.util.Duration.millis(duration.toMillis());
    }
}
