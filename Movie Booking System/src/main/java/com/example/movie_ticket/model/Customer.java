package com.example.movie_ticket.model;

public class Customer extends Person {
    private boolean isBlocked;
    private int bookedTickets;
    public Customer(Person person) {
        super(person.getID(), person.getName(), person.getSurname(), Role.CUSTOMER, person.getAccount(), person.getPhone());
        isBlocked = false;
        bookedTickets = 0;
    }
    public Customer(String ID, String name, String surname, String phone, Account account) {
        super(ID, name, surname, Role.CUSTOMER, account, phone);
        isBlocked = false;
        bookedTickets = 0;
    }
    public boolean getBlocked() {return isBlocked;}
    public void block(boolean isBlocked) {this.isBlocked = isBlocked;}
    public void cancelTicket(Ticket ticket) {ticket.cancel();}
    public void bookTicket(Ticket ticket) {ticket.book();}
    public void setBookedTickets(int bookedTickets) {this.bookedTickets += bookedTickets;}
    public int getBookedTickets() {return bookedTickets;}
}
