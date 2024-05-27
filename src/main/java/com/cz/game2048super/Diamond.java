package com.cz.game2048super;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class Diamond {
    private static StackPane root;

    public Diamond(){
        //空方块的设定
        root=new StackPane();
        Rectangle rect=new Rectangle(100,100);
        rect.setArcWidth(30);  // 设置圆角矩形的水平圆角弧度
        rect.setArcHeight(30); // 设置圆角矩形的垂直圆角弧度
        rect.setFill(Color.web("#cccccc"));
        root.getChildren().add(rect);
    }

    public Diamond(int num){
        //特定数字方块的设定
        root=new StackPane();
        Rectangle rect=new Rectangle(100,100);
        rect.setArcWidth(30);  // 设置圆角矩形的水平圆角弧度
        rect.setArcHeight(30); // 设置圆角矩形的垂直圆角弧度
        Text text = new Text();
        Font font = Font.font("Berlin Sans FB Demi", FontWeight.findByName("Bold"),50);
        text.setFont(font);
        text.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        switch (num){
            case 0:
                rect.setFill(Color.web("#cccccc"));
                text.setText("");//空方块
                break;
            case 2:
                rect.setFill(Color.web("#ffcc99"));
                text.setText("2");
                break;
            case 4:
                rect.setFill(Color.web("#ffff66"));
                text.setText("4");
                break;
            case 8:
                rect.setFill(Color.web("#ff9966"));
                text.setText("8");
                break;
            case 16:
                rect.setFill(Color.web("#ff6666"));
                text.setText("16");
                text.setFont(Font.font("Berlin Sans FB Demi", FontWeight.findByName("Bold"),47));
                break;
            case 32:
                rect.setFill(Color.web("#cc3399"));
                text.setText("32");
                text.setFont(Font.font("Berlin Sans FB Demi", FontWeight.findByName("Bold"),47));
                break;
            case 64:
                rect.setFill(Color.web("#9933cc"));
                text.setText("64");
                text.setFont(Font.font("Berlin Sans FB Demi", FontWeight.findByName("Bold"),47));
                break;
            case 128:
                rect.setFill(Color.web("#0099ff"));
                text.setText("128");
                text.setFont(Font.font("Berlin Sans FB Demi", FontWeight.findByName("Bold"),45));
                break;
            case 256:
                rect.setFill(Color.web("#66cc66"));
                text.setText("256");
                text.setFont(Font.font("Berlin Sans FB Demi", FontWeight.findByName("Bold"),45));
                break;
            case 512:
                rect.setFill(Color.web("#66cc99"));
                text.setText("512");
                text.setFont(Font.font("Berlin Sans FB Demi", FontWeight.findByName("Bold"),45));
                break;
            case 1024:
                rect.setFill(Color.web("#ff99cc"));
                text.setText("1024");
                text.setFont(Font.font("Berlin Sans FB Demi", FontWeight.findByName("Bold"),43));
                break;
            case 2048:
                rect.setFill(new LinearGradient(
                        0.0,0.0,1.0,0.0,true, CycleMethod.NO_CYCLE,
                        new Stop(0.0,new Color( 0.14, 0.82, 0.95, 1.0)),
                        new Stop(0.5,new Color( 0.84, 0.57, 0.98, 1.0)),
                        new Stop(1.0,new Color( 1.0, 0.48, 0.52, 1.0))));
                text.setText("2048");
                text.setFont(Font.font("Berlin Sans FB Demi", FontWeight.findByName("Bold"),43));
                break;
            default:
                System.out.println("代码中出现错误：方块的值不在规定范围内");
        }
        root.getChildren().addAll(rect,text);
    }

    public StackPane getRoot(){
        return root;
    }

}
