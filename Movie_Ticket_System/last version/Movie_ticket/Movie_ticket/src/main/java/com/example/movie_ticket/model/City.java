package com.example.movie_ticket.model;


import java.util.ArrayList;
import java.util.List;

public class City {
    private int id;
    private String name;
    private List<Cinema> cinemas;

    public City(String name) {
        this.name = name;
        this.cinemas = new ArrayList<>();
    }

    public City(int id, String name) {
        this.id = id;
        this.name = name;
        this.cinemas = new ArrayList<>();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public List<Cinema> getCinemas() { return cinemas; }
    public void addCinema(Cinema cinema) { this.cinemas.add(cinema); }

    @Override
    public String toString() {
        return name;
    }
}