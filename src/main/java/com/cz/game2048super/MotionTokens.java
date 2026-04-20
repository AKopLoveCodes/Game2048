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
                Duration.ofMillis(110),
                Duration.ofMillis(70),
                Duration.ofMillis(80),
                Duration.ofMillis(80),
                1.12,
                0.85,
                0.985
        );
    }
}
