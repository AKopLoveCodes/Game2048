package com.cz.game2048super;

import javafx.geometry.Pos;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class TileView extends StackPane {
    private final double size;
    private final Rectangle body;
    private final Rectangle highlight;
    private final Text label;
    private TileDescriptor descriptor;

    public TileView(TileDescriptor descriptor, double size) {
        this.size = size;
        this.body = new Rectangle(size, size);
        this.highlight = new Rectangle(size - 18, size * 0.44);
        this.label = new Text();
        this.label.setMouseTransparent(true);

        setAlignment(Pos.CENTER);
        setPrefSize(size, size);
        setMinSize(size, size);
        setMaxSize(size, size);

        body.setArcWidth(size * 0.28);
        body.setArcHeight(size * 0.28);

        highlight.setArcWidth(size * 0.2);
        highlight.setArcHeight(size * 0.2);
        highlight.setTranslateY(-size * 0.18);
        highlight.setMouseTransparent(true);

        getChildren().addAll(body, highlight, label);
        applyTile(descriptor);
    }

    public TileDescriptor descriptor() {
        return descriptor;
    }

    public void applyTile(TileDescriptor descriptor) {
        this.descriptor = descriptor;
        TileSkin skin = TileSkin.forValue(descriptor.value());
        body.setFill(skin.fill());
        body.setStroke(Color.web("#ffffff", descriptor.value() >= 1024 ? 0.22 : 0.12));
        body.setStrokeWidth(descriptor.value() >= 1024 ? 1.8 : 1.2);
        highlight.setFill(skin.highlight());

        label.setText(descriptor.value() == 0 || descriptor.value() == 1 ? "" : Integer.toString(descriptor.value()));
        label.setFill(skin.textColor());
        label.setFont(Font.font("Berlin Sans FB Demi", FontWeight.EXTRA_BOLD, skin.fontSize()));

        DropShadow liftShadow = new DropShadow();
        liftShadow.setRadius(descriptor.value() >= 1024 ? 26 : 18);
        liftShadow.setOffsetY(descriptor.value() >= 1024 ? 10 : 7);
        liftShadow.setColor(Color.web("#2f2117", descriptor.value() >= 1024 ? 0.30 : 0.18));

        InnerShadow innerShadow = new InnerShadow();
        innerShadow.setRadius(10);
        innerShadow.setOffsetY(-4);
        innerShadow.setColor(Color.web("#fff8ef", 0.18));

        liftShadow.setInput(innerShadow);
        body.setEffect(liftShadow);
    }

    public void moveTo(BoardPosition position) {
        descriptor = descriptor.withPosition(position);
    }

    public void updateValue(int value) {
        applyTile(descriptor.withValue(value));
    }

    public void resetVisualState() {
        setTranslateX(0);
        setTranslateY(0);
        setScaleX(1);
        setScaleY(1);
        setOpacity(1);
    }
}
