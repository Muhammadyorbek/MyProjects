package com.example.movie_ticket.model;

import java.util.ArrayList;

public class Admin extends Person {
    private ArrayList<Movie> movieArrayList = new ArrayList<>();
    public Admin(Person person) {
        super(person.getID(), person.getName(), person.getSurname(), Role.ADMIN, person.getAccount(), person.getPhone());
    }
    public Admin(String ID, String name, String surname, String phone, Account account) {
        super(ID, name, surname, Role.ADMIN, account, phone);
    }
    public void addMovie(Movie movie, ArrayList<Movie> movieArrayList) {
        movieArrayList.add(movie);
    }
    public void cancelMovie(Movie movie, ArrayList<Movie> movieArrayList) {
        for (int i = 0; i < movieArrayList.size(); i++) {
            if (movieArrayList.get(i).equals(movie)) {
                movieArrayList.remove(i);
                break;
            }
        }
    }
    public void blockCustomer(Customer customer) {
        customer.block(true);
    }
    public void unblockCustomer(Customer customer) {
        customer.block(false);
    }
}
