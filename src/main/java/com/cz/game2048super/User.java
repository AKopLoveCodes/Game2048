package com.cz.game2048super;

import java.io.Serial;
import java.io.Serializable;

public class User implements Serializable {
    // Save user registry
    @Serial
    private static final long serialVersionUID = 1L;

    private final String username;
    private final String password;
    private final boolean visitor;

    public User() {
        this.username = "";
        this.password = "";
        this.visitor = true;
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.visitor = false;
    }

    public static User visitor() {
        return new User();
    }

    @Override
    public String toString() {
        return username + "," + password;
    }

    public static User getUserRegistry(String str) {
        try {
            String[] strs = str.split(",", 2);
            if (strs.length == 2) {
                return new User(strs[0], strs[1]);
            }
            return null;
        } catch (Exception e) {
            System.out.println("璇诲彇鏈湴鏂囦欢閿欒");
            return null;
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isVisitor() {
        return visitor;
    }
}
