package com.cz.game2048super;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serial;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class GameData implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private static final int GRID_SIZE = 4;

    private final String username;
    private final int choice;
    private final Path dataFile;
    private int scoreLast;
    private int TimerLast;
    private int[][] gridsLast;
    private int scoreBest;
    private boolean ifHaveWon;

    public GameData(String username, int choice) {
        this(username, choice, AppPaths.gameDataFile());
    }

    public GameData(String username, int choice, Path dataFile) {
        this.username = Objects.requireNonNull(username, "username");
        this.choice = choice;
        this.dataFile = Objects.requireNonNull(dataFile, "dataFile").toAbsolutePath().normalize();
    }

    public void loadGameData() throws IOException {
        String[] newData = requireLastData();
        applyLoadedData(newData);
    }

    public void loadGameRecord() throws IOException {
        String[] newData = requireLastData();
        this.scoreBest = Integer.parseInt(newData[2]);
        this.ifHaveWon = Boolean.parseBoolean(newData[20]);
    }

    public boolean ifFoundUserData() throws IOException {
        return getLastData() != null;
    }

    public void setIfHaveWon(boolean ifHaveWon) {
        this.ifHaveWon = ifHaveWon;
    }

    public void initGameData() {
        this.scoreLast = 0;
        this.TimerLast = 0;
        this.gridsLast = new int[GRID_SIZE][GRID_SIZE];
        int num1;
        int num2;
        do {
            Random random = new Random();
            num1 = random.nextInt(GRID_SIZE * GRID_SIZE);
            num2 = random.nextInt(GRID_SIZE * GRID_SIZE);
        } while (num1 == num2);
        int x1 = num1 % GRID_SIZE;
        int x2 = num2 % GRID_SIZE;
        int y1 = num1 / GRID_SIZE;
        int y2 = num2 / GRID_SIZE;
        if (choice == 1) {
            gridsLast[x1][y1] = 1;
        } else {
            gridsLast[x1][y1] = 4;
        }
        gridsLast[x2][y2] = 2;
        this.ifHaveWon = false;
    }

    public void saveGameData() throws IOException {
        Path parent = dataFile.getParent();
        if (parent != null) {
            Files.createDirectories(parent);
        }
        List<String> records = Files.exists(dataFile)
                ? new ArrayList<>(Files.readAllLines(dataFile, StandardCharsets.UTF_8))
                : new ArrayList<>();
        String currentRecord = serializeCurrentState();
        boolean replaced = false;

        for (int index = 0; index < records.size(); index++) {
            String line = records.get(index);
            String[] data = line.split(",");
            if (data.length != 22) {
                continue;
            }
            try {
                parseRecord(data);
                if (data[0].equals(username) && choice == Integer.parseInt(data[21])) {
                    records.set(index, currentRecord);
                    replaced = true;
                    break;
                }
            } catch (RuntimeException ex) {
                System.out.println("data format error");
            }
        }

        if (!replaced) {
            records.add(currentRecord);
        }

        try (BufferedWriter writer = Files.newBufferedWriter(dataFile, StandardCharsets.UTF_8)) {
            for (String record : records) {
                writer.write(record);
                writer.newLine();
            }
        }
    }

    public void updateGameData(int score, int Timer, int[][] grids) {
        this.scoreLast = score;
        this.TimerLast = Timer;
        this.gridsLast = copyGrid(grids);
        if (score > scoreBest) {
            this.scoreBest = score;
        }
    }

    public void updateTimer(int Timer) {
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

    private String[] requireLastData() throws IOException {
        String[] lastData = getLastData();
        if (lastData == null) {
            throw new IOException("No game data for " + username + " and choice " + choice);
        }
        return lastData;
    }

    private void applyLoadedData(String[] data) {
        this.scoreLast = Integer.parseInt(data[1]);
        this.scoreBest = Integer.parseInt(data[2]);
        this.TimerLast = Integer.parseInt(data[3]);
        this.gridsLast = new int[GRID_SIZE][GRID_SIZE];
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                this.gridsLast[row][col] = Integer.parseInt(data[4 + row * GRID_SIZE + col]);
            }
        }
        this.ifHaveWon = Boolean.parseBoolean(data[20]);
    }

    private String[] getLastData() throws IOException {
        if (Files.notExists(dataFile)) {
            return null;
        }
        String[] newData = null;
        try (BufferedReader reader = Files.newBufferedReader(dataFile, StandardCharsets.UTF_8)) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length != 22) {
                    continue;
                }
                try {
                    parseRecord(data);
                    if (data[0].equals(username) && choice == Integer.parseInt(data[21])) {
                        newData = data;
                    }
                } catch (RuntimeException ex) {
                    System.out.println("data format error");
                }
            }
        }
        return newData;
    }

    private static void parseRecord(String[] data) {
        for (int index = 1; index < 20; index++) {
            Integer.parseInt(data[index]);
        }
        Boolean.parseBoolean(data[20]);
        Integer.parseInt(data[21]);
    }

    private String serializeCurrentState() {
        StringBuilder builder = new StringBuilder();
        builder.append(username)
                .append(',')
                .append(scoreLast)
                .append(',')
                .append(scoreBest)
                .append(',')
                .append(TimerLast)
                .append(',');
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                builder.append(gridsLast[row][col]).append(',');
            }
        }
        builder.append(ifHaveWon).append(',').append(choice);
        return builder.toString();
    }

    private int[][] copyGrid(int[][] source) {
        int[][] copy = new int[GRID_SIZE][GRID_SIZE];
        for (int row = 0; row < GRID_SIZE; row++) {
            System.arraycopy(source[row], 0, copy[row], 0, GRID_SIZE);
        }
        return copy;
    }
}
