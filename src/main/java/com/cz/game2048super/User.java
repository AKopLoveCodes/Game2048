package com.cz.game2048super;

import java.io.Serial;
import java.io.Serializable;

public class User implements Serializable{
    //Save user registry
    @Serial
    private static final long serialVersionUID = 1L;
    private final String username;
    private final String password;

    public User(){
        //游客登录
        this.username = "";
        this.password = "";
    }

    public User(String username, String password) {
        this.password = password;
        this.username = username;
    }

    @Override
    public String toString() {
        return username + "," + password ;
    }

    public static User getUserRegistry(String str) {
        try{
            String[] strs = str.split(",");
            if (strs.length==2){
                return new User(strs[0], strs[1]);
            } else {
                return null;
            }
        } catch (Exception e){
            System.out.println("读取本地文件错误");
            return null;
        }
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
