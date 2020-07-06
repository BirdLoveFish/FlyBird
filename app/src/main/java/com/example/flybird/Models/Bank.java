package com.example.flybird.Models;


public class Bank {
    private String id;
    private String fullName;
    private String simpleName;
    private String owner;
    private String password;
    private String other;

    public Bank(){

    }

    public Bank(String id, String fullName, String simpleName, String owner, String password, String other) {
        this.id = id;
        this.fullName = fullName;
        this.simpleName = simpleName;
        this.owner = owner;
        this.password = password;
        this.other = other;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getSimpleName() {
        return simpleName;
    }

    public void setSimpleName(String simpleName) {
        this.simpleName = simpleName;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }
}
