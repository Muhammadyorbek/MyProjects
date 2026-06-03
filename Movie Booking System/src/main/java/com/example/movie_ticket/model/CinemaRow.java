package com.example.movie_ticket.model;

public class CinemaRow {
    private final String cityName;
    private final Cinema cinema;

    public CinemaRow(String cityName, Cinema cinema) {
        this.cityName = cityName;
        this.cinema = cinema;
    }

    public String cityName() {
        return cityName;
    }

    public Cinema cinema() {
        return cinema;
    }


}