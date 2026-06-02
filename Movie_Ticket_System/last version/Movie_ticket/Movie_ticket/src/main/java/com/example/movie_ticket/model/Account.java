package com.example.movie_ticket.model;

import java.time.LocalDate;

public class Account {
    private String login;
    private String password;
    private String email;
    private String registeredDate;
    public Account(String login, String password, String email) {
        this.login = login;
        this.password = password;
        this.email = email;
        registeredDate = LocalDate.now().toString();
    }
    public String getLogin() {return login;}
    public String getPassword() {return password;}
    public String getEmail() {return email;}
    public String getRegisteredDate() {return registeredDate;}
}
