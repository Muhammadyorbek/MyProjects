package com.example.movie_ticket.model;

public class Ticket {
    private boolean isBooked;
    private boolean isCancelled;
    public void book() {isBooked = true;}
    public void cancel() {isCancelled = true;}
}