package com.cz.game2048super;

import java.time.Duration;
import java.util.List;

public record AnimationPlan(
        List<TileMove> slideMoves,
        List<MergeEvent> mergeEvents,
        SpawnEvent spawnEvent,
        Duration slideDuration,
        Duration mergeDuration,
        Duration spawnDuration,
        Duration failureDuration,
        Duration spawnDelay,
        Duration totalDuration,
        boolean failureFeedbackOnly
) {
    public AnimationPlan {
        slideMoves = List.copyOf(slideMoves);
        mergeEvents = List.copyOf(mergeEvents);
    }

    public boolean isFailureFeedbackOnly() {
        return failureFeedbackOnly;
    }
}
