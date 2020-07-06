package com.example.flybird.Models;

public class Account {
    private String name;
    private String account;
    private String password;
    private String other;
    private String id;

    public Account(){
    }

    public Account(String id, String name, String account, String password, String other){
        this.name = name;
        this.account = account;
        this.password = password;
        this.other = other;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getPassword() {
        return password;
    }

}
