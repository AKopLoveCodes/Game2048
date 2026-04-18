package com.cz.game2048super;

import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.Stop;

public record TileSkin(Paint fill, Paint highlight, Color textColor, double fontSize) {
    public static TileSkin forValue(int value) {
        return switch (value) {
            case 0 -> new TileSkin(
                    Color.web("#d4c8ba"),
                    Color.web("#efe8de", 0.55),
                    Color.TRANSPARENT,
                    44
            );
            case 1 -> new TileSkin(
                    gradient("#3f444b", "#252a31"),
                    Color.web("#7c8793", 0.35),
                    Color.web("#eef2f5"),
                    38
            );
            case 2 -> new TileSkin(
                    gradient("#f8e5b6", "#efc978"),
                    Color.web("#fff7df", 0.55),
                    Color.web("#5d472d"),
                    52
            );
            case 4 -> new TileSkin(
                    gradient("#fff58a", "#ebe836"),
                    Color.web("#fff6bf", 0.9),
                    Color.web("#5a4023"),
                    52
            );
            case 8 -> new TileSkin(
                    gradient("#f5b487", "#e47432"),
                    Color.web("#ffd8bd", 0.5),
                    Color.web("#fff9f4"),
                    50
            );
            case 16 -> new TileSkin(
                    gradient("#ee8d74", "#e1180a"),
                    Color.web("#ffc1b6", 0.45),
                    Color.web("#fff8f7"),
                    47
            );
            case 32 -> new TileSkin(
                    gradient("#da698d", "#cf2b85"),
                    Color.web("#ffc1d8", 0.4),
                    Color.web("#fff7fb"),
                    47
            );
            case 64 -> new TileSkin(
                    gradient("#b76fd5", "#8141ca"),
                    Color.web("#ead6ff", 0.38),
                    Color.web("#fff8ff"),
                    47
            );
            case 128 -> new TileSkin(
                    gradient("#61a8ef", "#2f7ee4"),
                    Color.web("#d6ebff", 0.38),
                    Color.web("#f8fbff"),
                    43
            );
            case 256 -> new TileSkin(
                    gradient("#53c8c2", "#2da79d"),
                    Color.web("#d6fff8", 0.35),
                    Color.web("#f7fffe"),
                    43
            );
            case 512 -> new TileSkin(
                    gradient("#68d39d", "#3bae70"),
                    Color.web("#dfffe8", 0.35),
                    Color.web("#f6fff9"),
                    43
            );
            case 1024 -> new TileSkin(
                    gradient("#67c7f6", "#8f83ff", "#ff8eb0"),
                    Color.web("#f0e9ff", 0.32),
                    Color.web("#ffffff"),
                    38
            );
            case 2048 -> new TileSkin(
                    gradient("#5bd6ff", "#9c82ff", "#ff7f7a"),
                    Color.web("#ffffff", 0.3),
                    Color.web("#ffffff"),
                    36
            );
            default -> new TileSkin(
                    gradient("#f2b8d5", "#dc7aa4"),
                    Color.web("#fff0f8", 0.3),
                    Color.web("#fff7fb"),
                    34
            );
        };
    }

    private static Paint gradient(String start, String end) {
        return new LinearGradient(
                0.0,
                0.0,
                0.0,
                1.0,
                true,
                CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.web(start)),
                new Stop(1.0, Color.web(end))
        );
    }

    private static Paint gradient(String start, String middle, String end) {
        return new LinearGradient(
                0.0,
                0.0,
                1.0,
                1.0,
                true,
                CycleMethod.NO_CYCLE,
                new Stop(0.0, Color.web(start)),
                new Stop(0.55, Color.web(middle)),
                new Stop(1.0, Color.web(end))
        );
    }
}
