package com.cz.game2048super;

import java.util.Random;

public class GameController {
    //TODO: implement game logic here
    private int[][] gridnums;

    public GameController(){
        gridnums = new int[4][4];
    }

    public int AStep(int direction){
        //direction 1 UP 2 DOWN 3 LEFT 4 RIGHT
        int addScore=0;
        //根据方向对gridnums进行操作
        switch (direction){
            case 1:
                for (int time = 3; time>0 ; time--) {
                    for (int j = 0; j < gridnums.length; j++) {
                        for (int i = 0; i<gridnums.length  ; i++) {
                            if (gridnums[i][j]==0&&i<=2){
                                gridnums[i][j]=gridnums[i+1][j];
                                gridnums[i+1][j]=0;
                            }
                        }
                    }
                }
                for(int i = 0; i < gridnums.length; i++) {
                    for (int j =0;j<gridnums.length; j++) {
                        if (i<=2&&gridnums[i][j]==gridnums[i+1][j]){
                            gridnums[i][j]=2*gridnums[i][j];
                            gridnums[i+1][j]=0;
                            addScore+=gridnums[i][j];
                        }
                    }

                }
                for (int time = 2; time>0 ; time--) {
                    for (int j = 0; j < gridnums.length; j++) {
                        for (int i = 0; i<gridnums.length  ; i++) {
                            if (gridnums[i][j]==0&&i<=2){
                                gridnums[i][j]=gridnums[i+1][j];
                                gridnums[i+1][j]=0;
                            }
                        }
                    }
                }
                break;
            case 2:
                for (int time = 3; time>0 ; time--) {
                    for (int j = 0; j < gridnums.length; j++) {
                        for (int i =gridnums.length-1; i>=0  ; i--) {
                            if (gridnums[i][j]==0&&i-1>=0){
                                gridnums[i][j]=gridnums[i-1][j];
                                gridnums[i-1][j]=0;
                            }
                        }
                    }
                }
                for(int i =gridnums.length-1; i >=0; i--) {
                    for (int j =0;j<gridnums.length; j++) {
                        if (i>=1&&gridnums[i][j]==gridnums[i-1][j]){
                            gridnums[i][j]=2*gridnums[i][j];
                            gridnums[i-1][j]=0;
                            addScore+=gridnums[i][j];
                        }
                    }

                }
                for (int time = 2; time>0 ; time--) {
                    for (int j = 0; j < gridnums.length; j++) {
                        for (int i =gridnums.length-1; i>=0  ; i--) {
                            if (gridnums[i][j]==0&&i-1>=0){
                                gridnums[i][j]=gridnums[i-1][j];
                                gridnums[i-1][j]=0;
                            }
                        }
                    }
                }
                break;
            case 3:
                for (int time = 3; time>0 ; time--) {
                    for (int i = 0; i < gridnums.length; i++) {
                        for (int j = 0; j<gridnums.length  ; j++) {
                            if (gridnums[i][j]==0&&j+1<4) {
                                gridnums[i][j] = gridnums[i][j+1];
                                gridnums[i][j+1] = 0;
                            }
                        }
                    }
                }
                for (int i = 0; i < gridnums.length; i++) {
                    for (int j =0;j<gridnums.length; j++) {
                        if (j<=2&&gridnums[i][j]==gridnums[i][j+1]){
                            gridnums[i][j]=2*gridnums[i][j];
                            gridnums[i][j+1]=0;
                            addScore+=gridnums[i][j];
                        }
                    }

                }
                for (int time = 2; time>0 ; time--) {
                    for (int i = 0; i < gridnums.length; i++) {
                        for (int j = 0; j<gridnums.length  ; j++) {
                            if (gridnums[i][j]==0&&j+1<4) {
                                gridnums[i][j] = gridnums[i][j+1];
                                gridnums[i][j+1] = 0;
                            }
                        }
                    }
                }
                break;
            case 4:
                for (int time = 3; time > 0; time--) {
                    for (int i = 0; i < 4; i++) {
                        for (int j = 3; j >= 0; j--) {
                            if (gridnums[i][j] == 0 && j - 1 >= 0) {
                                gridnums[i][j] = gridnums[i][j - 1];
                                gridnums[i][j - 1] = 0;
                            }
                        }
                    }
                }
                for (int i = 0; i < 4; i++) {
                    for (int j = 3; j>=0 ; j--) {
                        if (j>0&&gridnums[i][j]==gridnums[i][j-1]){
                            gridnums[i][j]=2*gridnums[i][j];
                            gridnums[i][j-1]=0;
                            addScore+=gridnums[i][j];
                        }
                    }

                }
                for (int time = 2; time > 0; time--) {
                    for (int i = 0; i <4; i++) {
                        for (int j = 3; j >= 0; j--) {
                            if (gridnums[i][j] == 0 && j - 1 >= 0) {
                                gridnums[i][j] = gridnums[i][j - 1];
                                gridnums[i][j - 1] = 0;
                            }
                        }
                    }
                }
                break;
        }
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                System.out.print(gridnums[i][j]+" ");
            }
        }
        System.out.println(" ");
        return addScore;
    }

    public int[][] getGridnums() {
        return gridnums;
    }

    public boolean isGameOver(){
        boolean ifGameOver=true;
        for (int i=0;i<4;i++){
            for (int j=0;j<4;j++){
                if (gridnums[i][j]==0){
                    ifGameOver=false;
                    break;
                }
            }
        }
        return ifGameOver;
    }

    public boolean isWin(){
        boolean ifWin = false;
        for (int i=0;i<4;i++){
            for (int j=0;j<4;j++){
                if (gridnums[i][j]==2048){
                    ifWin=true;
                    break;
                }
            }
        }
        return ifWin;
    }

    public void createNewGrid(){
        Random random = new Random();
        boolean ifempty = false;
        int i,j;
        do {
            i = random.nextInt(4);
            j = random.nextInt(4);
            if (gridnums[i][j] == 0) {
                ifempty = true ;
            }
        } while (!ifempty);
        System.out.println("随机生成的数的坐标为：列:"+j+" 行:"+i);
        if (random.nextInt(2)==0){
            gridnums[i][j]=2;
        } else {
            gridnums[i][j]=4;
        }
    }

    public void setGridnums(int[][] grids) {
        for (int i = 0; i < 4; i++) {
            System.arraycopy(grids[i], 0, gridnums[i], 0, 4);
        }
    }
}
