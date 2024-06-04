package com.cz.game2048super;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameData implements Serializable{
    @Serial
    private static final long serialVersionUID = 1L;
    private static String username;
    private int scoreLast;
    private int TimerLast;
    private int[][] gridsLast;
    //以下两个变量为记录型变量 应该始终跟随账号
    private int scoreBest;
    private boolean ifHaveWon;

    public GameData(String username){
        GameData.username = username;
    }

    public void loadGameData() throws IOException{
        //在用户名下已有保存数据的情况下加载数据（continue选项）
        String[] newData = getLastData();
        this.scoreLast = Integer.parseInt(newData[1]);
        this.scoreBest = Integer.parseInt(newData[2]);
        this.TimerLast = Integer.parseInt(newData[3]);
        this.gridsLast = new int[4][4];
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                this.gridsLast[i][j] = Integer.parseInt(newData[4 + i * 4 + j]);
            }
        }
        this.ifHaveWon = Boolean.parseBoolean(newData[20]);
    }

    public void loadGameRecord() throws IOException{
        //在已有存档时选择新游戏 但仍保留该账号的游戏记录
        String[] newData = getLastData();
        this.scoreBest = Integer.parseInt(newData[2]);
        this.ifHaveWon = Boolean.parseBoolean(newData[20]);
    }

    private static String[] getLastData() throws IOException {
        //将用户名下的数据提取出来 找到最新的存档并加载
        List<String> dataList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/assets/data/GameData.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                dataList.add(line);
            }
        }
        String[] newData = new String[21];
        for (String line : dataList) {
            String[] data;
            try {
                data = line.split(",");
                if (data.length!=21){
                    continue;
                }
                for (int i = 1; i < 20; i++) {
                    int ifint = Integer.parseInt(data[i]);
                }
                boolean ifboolean = Boolean.parseBoolean(data[20]);
                if (data[0].equals(username)) {
                    newData = data;
                }
            } catch (Exception e){
                System.out.println("data format error");
            }
        }
        return newData;
    }

    public boolean ifFoundUserData() throws IOException{
        //确认该用户是已经注册且有存档 还是新注册未有存档
        boolean ifFound = false;
        try (BufferedReader reader = new BufferedReader(new FileReader("src/main/resources/assets/data/GameData.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data;
                try {
                    data = line.split(",");
                    if (data.length!=21){
                        continue;
                    }
                    for (int i = 1; i < 20; i++) {
                        int ifint = Integer.parseInt(data[i]);
                    }
                    boolean ifboolean = Boolean.parseBoolean(data[20]);
                    if (data[0].equals(username)) {
                        ifFound = true;
                        break;
                    }
                } catch (Exception e){
                    System.out.println("data format error");
                }
            }
        }
        //true代表用户已有存档 false代表用户新注册
        return ifFound;
    }

    public void setIfHaveWon(boolean ifHaveWon){
        //当游戏通关时 设置该用户已通关
        this.ifHaveWon = ifHaveWon;
    }

    public void initGameData(int choice) throws IOException{
        //将新注册用户或游客的游戏数据初始化
        this.scoreLast = 0;
        this.TimerLast = 0;
        //initial grids
        gridsLast=new int[4][4];
        for (int i=0;i<4;i++){
            for (int j=0;j<4;j++){
                gridsLast[i][j]=0;
            }
        }
        //生成两个随机的2方块
        int num1,num2;
        do {
            Random random = new Random();
            // 生成两个1到16的随机整数
            num1 = random.nextInt(16) ; // nextInt(16)生成0到15的整数
            num2 = random.nextInt(16) ;
        } while (num1==num2);
        int x1,x2,y1,y2;
        x1=num1%4;
        x2=num2%4;
        y1=num1/4;
        y2=num2/4;
        if (choice==1){
            gridsLast[x1][y1]=1;
        } else {
            gridsLast[x1][y1]=4;
        }
        gridsLast[x2][y2]=2;
        this.ifHaveWon = false;
    }

    public void saveGameData() throws IOException{
        //将现有的游戏数据保存至本地文档
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/assets/data/GameData.txt", true))) {
            writer.write(username + "," + this.scoreLast + "," + this.scoreBest + "," + this.TimerLast + ",");
            for (int i = 0; i < 4; i++) {
                for (int j = 0; j < 4; j++) {
                    writer.write(this.gridsLast[i][j] + ",");
                }
            }
            writer.write(this.ifHaveWon+"");
            writer.newLine();
        }
        System.out.println("save success");
    }

    public void updateGameData(int score, int Timer, int[][] grids){
        //更新游戏数据
        this.scoreLast = score;
        this.TimerLast = Timer;
        this.gridsLast = grids;
        if (score > scoreBest){
            this.scoreBest = score;
        }
    }

    public void updateTimer(int Timer){
        //更新游戏时间
        this.TimerLast = Timer;
    }

    public int getScoreLast() {
        return scoreLast;
    }

    public int getScoreBest() {
        return scoreBest;
    }

    public int getTimerLast() {
        return TimerLast;
    }

    public int[][] getGridsLast() {
        return gridsLast;
    }

    public boolean getIfHaveWon() {
        return ifHaveWon;
    }
}
