package com.xera.notetify.Model;

public class UserModel {

    private String accessToken;
    private String idToken;
    private String name;
    private String email;

    public UserModel() {
        this.accessToken = "";
        this.idToken = "";
        this.name = "";
        this.email = "";
    }

    public UserModel(String accessToken, String idToken, String name, String email) {
        this.accessToken = accessToken;
        this.idToken = idToken;
        this.name = name;
        this.email = email;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getIdToken() {
        return idToken;
    }

    public void setIdToken(String idToken) {
        this.idToken = idToken;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
