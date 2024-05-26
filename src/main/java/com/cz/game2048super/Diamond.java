package com.cz.game2048super;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class Diamond {
    private static int index;
    private static StackPane root;

    public Diamond(){
        //空方块的设定
        index=0;
        root=new StackPane();
        Rectangle rect=new Rectangle(100,100);
        rect.setArcWidth(30);  // 设置圆角矩形的水平圆角弧度
        rect.setArcHeight(30); // 设置圆角矩形的垂直圆角弧度
        rect.setFill(Color.web("#cccccc"));
        root.getChildren().add(rect);
    }

    public Diamond(int num){
        //特定数字方块的设定
        index=num;
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
                break;
            case 32:
                rect.setFill(Color.web("#cc3333"));
                text.setText("32");
                break;
            case 64:
                rect.setFill(Color.web("#ff0033"));
                text.setText("64");
                break;
            case 128:
                rect.setFill(Color.web("#cc0033"));
                text.setText("128");
                break;
            case 256:
                rect.setFill(Color.web("#ffff99"));
                text.setText("256");
                break;
            case 512:
                rect.setFill(Color.web("#ffff00"));
                text.setText("512");
                break;
            case 1024:
                rect.setFill(Color.web("#ccffff"));
                text.setText("1024");
                break;
            case 2048:
                rect.setFill(Color.web("#0099cc"));
                text.setText("2048");
                break;
            default:
                System.out.println("代码中出现错误：方块的值不在规定范围内");
        }
        root.getChildren().addAll(rect,text);
    }

    public StackPane getRoot(){
        return root;
    }

    public int getIndex(){
        return index;
    }
}
