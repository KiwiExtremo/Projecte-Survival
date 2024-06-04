package com.example.survivalgame;

/**
 * The User class is used as to create a user template before saving it to the Firebase database.
 */
public class User {
    private String username;
    private String email;
    private int score;
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

    public void setScore(int score){this.score = score;}

    public int getScore(){return score;}
}
