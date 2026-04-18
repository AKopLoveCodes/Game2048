package com.cz.game2048super;

import java.time.Duration;

public final class BoardAnimationPlanner {
    private BoardAnimationPlanner() {
    }

    public static AnimationPlan plan(MoveResult result, MotionTokens tokens) {
        if (!result.moveChanged()) {
            return new AnimationPlan(
                    result.tileMoves(),
                    result.mergeEvents(),
                    result.spawnEvent(),
                    Duration.ZERO,
                    Duration.ZERO,
                    Duration.ZERO,
                    tokens.failureDuration(),
                    Duration.ZERO,
                    tokens.failureDuration(),
                    true
            );
        }

        Duration slide = result.tileMoves().isEmpty() ? Duration.ZERO : tokens.slideDuration();
        Duration merge = result.mergeEvents().isEmpty() ? Duration.ZERO : tokens.mergeDuration();
        Duration spawn = result.spawnEvent() == null ? Duration.ZERO : tokens.spawnDuration();
        Duration spawnDelay = slide.plus(merge);
        Duration total = spawnDelay.plus(spawn);

        return new AnimationPlan(
                result.tileMoves(),
                result.mergeEvents(),
                result.spawnEvent(),
                slide,
                merge,
                spawn,
                tokens.failureDuration(),
                spawnDelay,
                total,
                false
        );
    }
}
