package com.example.proyecto_agenda.entities;

public class UserEntity {

    private String uid;
    private String email;
    private String names;
    private String password;

    public UserEntity() {}

    public UserEntity(String uid, String email, String names, String password) {
        this.uid = uid;
        this.email = email;
        this.names = names;
        this.password = password;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNames() {
        return names;
    }

    public void setNames(String names) {
        this.names = names;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
