package com.example.survivalgame;

public class User {
    private String username;
    private String email;
    private int puntuacion;
    public User() {

    }
    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setPuntuacion(int puntuacion){this.puntuacion = puntuacion;}
    public int getPuntuacion(){return puntuacion;}
}
