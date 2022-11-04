package net.biancheng.www.bean;

import java.sql.PreparedStatement;

public class user {
    private String name;
    private String iphone;
    private String account;
    private String password;
    private int position;
    public String last_login_time;

    public user() {
    }

    public user(String name, String iphone, String account, String password, int position, String last_login_time) {
        this.name = name;
        this.iphone = iphone;
        this.account = account;
        this.password = password;
        this.position = position;
        this.last_login_time = last_login_time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIphone() {
        return iphone;
    }

    public void setIphone(String iphone) {
        this.iphone = iphone;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getLast_login_time() {
        return last_login_time;
    }

    public void setLast_login_time(String last_login_time) {
        this.last_login_time = last_login_time;
    }

    @Override
    public String toString() {
        return "user{" +
                "name='" + name + '\'' +
                ", phone='" + iphone + '\'' +
                ", account='" + account + '\'' +
                ", password='" + password + '\'' +
                ", position=" + position +
                ", last_login_time='" + last_login_time + '\'' +
                '}';
    }
}
