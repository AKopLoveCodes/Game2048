package com.cz.game2048super;

import java.time.Duration;

public record MotionTokens(
        Duration slideDuration,
        Duration mergeDuration,
        Duration spawnDuration,
        Duration failureDuration,
        double mergeScale,
        double spawnInitialScale,
        double failurePressedScale
) {
    public static MotionTokens defaults() {
        return new MotionTokens(
                Duration.ofMillis(160),
                Duration.ofMillis(105),
                Duration.ofMillis(125),
                Duration.ofMillis(80),
                1.1,
                0.82,
                0.985
        );
    }
}
