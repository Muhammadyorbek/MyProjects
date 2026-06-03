package com.example.movie_ticket.model;


public class FrontDeskOfficer extends Person {
    private Ticket ticket;
    public FrontDeskOfficer(Person person) {
        super(person.getID(), person.getName(), person.getSurname(), Role.FRONT_DESK_OFFICER, person.getAccount(), person.getPhone());
    }
    public FrontDeskOfficer(String ID, String name, String surname, String phone, Account account) {
        super(ID, name, surname, Role.FRONT_DESK_OFFICER, account, phone);
    }
    public void cancelTicket(Ticket ticket) {
        ticket.cancel();
    }
}
