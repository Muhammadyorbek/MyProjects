package com.example.movie_ticket.service;

import java.math.BigDecimal;

public class UserSession {
    private static UserSession instance;

    private int    userId;
    private String username;
    private double coinsBalance;
    private String firstName;
    private String lastName;
    private String phone;
    private String email;

    private String     role;
    private String     status;
    private int        bookedTickets;
    private String     loginFl;
    private BigDecimal balance;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) instance = new UserSession();
        return instance;
    }

    public void loginUser(int userId, String username, double coinsBalance,
                          String firstName, String lastName,
                          String phone, String email) {
        this.userId       = userId;
        this.username     = username;
        this.coinsBalance = coinsBalance;
        this.firstName    = firstName;
        this.lastName     = lastName;
        this.phone        = phone;
        this.email        = email;
    }

    public void loginUser(int id, String name, String surname, String role,
                          String phone, BigDecimal balance, String status,
                          int bookedTickets, String loginFl, String email) {
        this.userId        = id;
        this.username      = loginFl;
        this.firstName     = name;
        this.lastName      = surname;
        this.role          = role;
        this.phone         = phone;
        this.balance       = balance != null ? balance : BigDecimal.ZERO;
        this.coinsBalance  = balance != null ? balance.doubleValue() : 0.0;
        this.status        = status;
        this.bookedTickets = bookedTickets;
        this.loginFl       = loginFl;
        this.email         = email;
    }

    public void cleanUserSession() {
        this.userId        = 0;
        this.username      = "";
        this.firstName     = "";
        this.lastName      = "";
        this.phone         = "";
        this.email         = "";
        this.coinsBalance  = 0.0;
        this.role          = "user";
        this.status        = "active";
        this.bookedTickets = 0;
        this.loginFl       = "";
        this.balance       = BigDecimal.ZERO;
    }

    public int    getUserId()       { return userId; }
    public String getUsername()     { return username != null ? username : ""; }
    public double getCoinsBalance() { return coinsBalance; }
    public String getFirstName()    { return firstName != null ? firstName : ""; }
    public String getLastName()     { return lastName  != null ? lastName  : ""; }
    public String getPhone()        { return phone     != null ? phone     : ""; }
    public String getEmail()        { return email     != null ? email     : ""; }
    public boolean isLoggedIn()     { return userId > 0; }

    public String     getRole()          { return role          != null ? role          : "user"; }
    public String     getStatus()        { return status        != null ? status        : "active"; }
    public int        getBookedTickets() { return bookedTickets; }
    public String     getLoginFl()       { return loginFl       != null ? loginFl       : ""; }
    public BigDecimal getBalance()       { return balance       != null ? balance       : BigDecimal.ZERO; }

    public String getFullName() {
        String fn = getFirstName();
        String ln = getLastName();
        return (fn + " " + ln).trim();
    }

    public String getName()    { return getFirstName(); }
    public String getSurname() { return getLastName(); }

    public void setCoinsBalance(double coinsBalance) {
        this.coinsBalance = coinsBalance;
        this.balance = BigDecimal.valueOf(coinsBalance);
    }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName)   { this.lastName  = lastName; }
    public void setPhone(String phone)         { this.phone     = phone; }
    public void setEmail(String email)         { this.email     = email; }

    public void setName(String name)           { this.firstName = name; }
    public void setSurname(String surname)     { this.lastName  = surname; }
    public void setBalance(BigDecimal balance) {
        this.balance      = balance;
        this.coinsBalance = balance != null ? balance.doubleValue() : 0.0;
    }
    public void setBookedTickets(int count)    { this.bookedTickets = count; }
    public void setLoginFl(String loginFl)     { this.loginFl = loginFl; }
    public void setRole(String role)           { this.role = role; }
}